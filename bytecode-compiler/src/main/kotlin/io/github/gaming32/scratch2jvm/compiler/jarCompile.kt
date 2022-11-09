package io.github.gaming32.scratch2jvm.compiler

import io.github.gaming32.scratch2jvm.parser.ScratchProjectFile
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.util.CheckClassAdapter
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder
import java.io.File
import java.io.PrintWriter
import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.createDirectories
import kotlin.io.path.createDirectory
import kotlin.io.path.outputStream
import kotlin.io.path.writer
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.DurationUnit

public enum class FatnessLevel {
    NONE, RUNTIME, LWJGL, NATIVES
}

private val LOGGER = getLogger()

public fun compileToJar(inFile: File, outFile: File, fatness: FatnessLevel = FatnessLevel.RUNTIME) {
    val mainStart = System.nanoTime()
    outFile.delete()
    ScratchProjectFile.open(inFile).use { project ->
        LOGGER.info("Compiling {}...", inFile.nameWithoutExtension)
        val start = System.nanoTime()
        val result = ScratchCompiler.compile(inFile.nameWithoutExtension, project.project)
        val end = System.nanoTime()
        LOGGER.info("Finished compilation in {}ms", (end - start).nanoseconds.toDouble(DurationUnit.MILLISECONDS))
        FileSystems.newFileSystem(URI("jar:" + outFile.toURI()), mapOf("create" to "true")).use { outJar ->
            LOGGER.info("Writing assets")
            for (entry in project.scratchZip.entries()) {
                if (entry.name == "project.json") continue
                project.scratchZip.getInputStream(entry).use {
                    Files.copy(it, outJar.getPath(entry.name), StandardCopyOption.REPLACE_EXISTING)
                }
            }
            if (System.getProperty("scratch2jvm.verify").toBoolean()) {
                LOGGER.info("Verifying classes")
                val bytecodes = mutableMapOf<String, ByteArray>()
                for ((name, clazz) in result.classes) {
                    val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
                    clazz.accept(writer)
                    bytecodes[name] = writer.toByteArray()
                }
                val verifyClassLoader = object : ClassLoader() {
                    override fun findClass(name: String): Class<*> {
                        val bytecode = bytecodes[name.replace('.', '/')] ?:
                            throw ClassNotFoundException(name)
                        return defineClass(name, bytecode, 0, bytecode.size)
                    }
                }
                val verifyDir = File("verify")
                verifyDir.mkdirs()
                for ((name, bytecode) in bytecodes) {
                    PrintWriter(File(verifyDir, name.substringAfterLast('/') + ".verify.txt")).use {
                        CheckClassAdapter.verify(ClassReader(bytecode), verifyClassLoader, true, it)
                    }
                }
            }
            LOGGER.info("Writing classes")
            for ((name, clazz) in result.classes) {
                val writer = ClassWriter(ClassWriter.COMPUTE_FRAMES)
                clazz.accept(writer)
                val destPath = outJar.getPath("$name.class")
                destPath.parent.createDirectories()
                destPath.outputStream().use {
                    it.write(writer.toByteArray())
                }
            }
            LOGGER.info("Writing resources")
            for ((name, data) in result.resources) {
                val destPath = outJar.getPath(name)
                destPath.writer(Charsets.UTF_8).use {
                    it.write(data)
                }
            }
            LOGGER.info("Writing data")
            outJar.getPath("META-INF").createDirectory()
            outJar.getPath("META-INF/MANIFEST.MF").writer(Charsets.UTF_8).use {
                it.write("Manifest-Version: 1.0\n")
                it.write("Main-Class: ${result.mainClass.replace('/', '.')}\n")
            }
            if (fatness >= FatnessLevel.RUNTIME) {
                LOGGER.info("Writing runtime")
                copyPackageToJar("io.github.gaming32.scratch2jvm.runtime", outJar)
                if (fatness >= FatnessLevel.LWJGL) {
                    LOGGER.info("Writing LWJGL")
                    copyPackageToJar("org.lwjgl", outJar)
                    copyPackageToJar("org.joml", outJar)
                    if (fatness >= FatnessLevel.NATIVES) {
                        LOGGER.info("Writing LWJGL natives")
                        Reflections(Scanners.Resources).getAll(Scanners.Resources).forEach { resource ->
                            if (!resource.contains("/org/lwjgl/")) return@forEach
                            val path = outJar.getPath(resource)
                            path.parent.createDirectories()
                            FatnessLevel::class.java.getResourceAsStream("/$resource")?.use {
                                Files.copy(it, path, StandardCopyOption.REPLACE_EXISTING)
                            } ?: throw Error("Resource not found: $resource")
                        }
                    }
                }
            }
            LOGGER.info("Building final JAR")
        }
    }
    val mainEnd = System.nanoTime()
    LOGGER.info("Finished in {}ms", (mainEnd - mainStart).nanoseconds.toDouble(DurationUnit.MILLISECONDS))
}

private fun copyPackageToJar(pkg: String, jar: FileSystem) =
    Reflections(ConfigurationBuilder().apply {
        addClassLoaders(FatnessLevel::class.java.classLoader)
        forPackage(pkg)
        filterInputsBy(FilterBuilder().includePackage(pkg))
        setScanners(Scanners.SubTypes.filterResultsBy { true })
    }).getAll(Scanners.SubTypes).forEach { clazz ->
        if (!clazz.startsWith("$pkg.")) return@forEach
        val fileName = "/${clazz.replace('.', '/')}.class"
        val path = jar.getPath(fileName)
        path.parent.createDirectories()
        FatnessLevel::class.java.getResourceAsStream(fileName)?.use {
            Files.copy(it, path, StandardCopyOption.REPLACE_EXISTING)
        } ?: throw Error("Class file not found: $clazz")
    }

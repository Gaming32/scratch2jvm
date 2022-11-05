package io.github.gaming32.scratch2jvm.compiler

import io.github.gaming32.scratch2jvm.parser.ScratchProjectFile
import org.objectweb.asm.ClassWriter
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder
import java.io.File
import java.net.URI
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.createDirectories
import kotlin.io.path.createDirectory
import kotlin.io.path.outputStream
import kotlin.io.path.writer

public enum class FatnessLevel {
    NONE, RUNTIME, LWJGL, NATIVES
}

public fun compileToJar(inFile: File, outFile: File, fatness: FatnessLevel = FatnessLevel.RUNTIME) {
    outFile.delete()
    ScratchProjectFile.open(inFile).use { project ->
        val result = ScratchCompiler.compile(inFile.nameWithoutExtension, project.project)
        FileSystems.newFileSystem(URI("jar:" + outFile.toURI()), mapOf("create" to "true")).use { outJar ->
            for (entry in project.scratchZip.entries()) {
                if (entry.name == "project.json") continue
                project.scratchZip.getInputStream(entry).use {
                    Files.copy(it, outJar.getPath(entry.name), StandardCopyOption.REPLACE_EXISTING)
                }
            }
            for ((name, clazz) in result.classes) {
                val writer = ClassWriter(ClassWriter.COMPUTE_FRAMES)
                clazz.accept(writer)
                val destPath = outJar.getPath("$name.class")
                destPath.parent.createDirectories()
                destPath.outputStream().use {
                    it.write(writer.toByteArray())
                }
            }
            for ((name, data) in result.resources) {
                val destPath = outJar.getPath(name)
                destPath.writer(Charsets.UTF_8).use {
                    it.write(data)
                }
            }
            outJar.getPath("META-INF").createDirectory()
            outJar.getPath("META-INF/MANIFEST.MF").writer(Charsets.UTF_8).use {
                it.write("Manifest-Version: 1.0\n")
                it.write("Main-Class: ${result.mainClass.replace('/', '.')}\n")
            }
            if (fatness >= FatnessLevel.RUNTIME) {
                copyPackageToJar("io.github.gaming32.scratch2jvm.runtime", outJar)
                if (fatness >= FatnessLevel.LWJGL) {
                    copyPackageToJar("org.lwjgl", outJar)
                    if (fatness >= FatnessLevel.NATIVES) {
                        Reflections(Scanners.Resources).get(
                            Scanners.Resources.with(".*lwjgl.*")
                        ).forEach { resource ->
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
        }
    }
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

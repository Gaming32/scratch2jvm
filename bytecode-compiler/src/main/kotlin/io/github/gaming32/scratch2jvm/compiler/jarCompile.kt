package io.github.gaming32.scratch2jvm.compiler

import io.github.gaming32.scratch2jvm.parser.ScratchProjectFile
import io.github.gaming32.scratch2jvm.runtime.ScratchABI
import org.objectweb.asm.ClassWriter
import java.io.File
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.createDirectories
import kotlin.io.path.createDirectory
import kotlin.io.path.outputStream
import kotlin.io.path.writer

public fun compileToJar(inFile: File, outFile: File) {
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
                val destPath = outJar.getPath("$name.class")
                destPath.writer(Charsets.UTF_8).use {
                    it.write(data)
                }
            }
            outJar.getPath("META-INF").createDirectory()
            outJar.getPath("META-INF/MANIFEST.MF").writer(Charsets.UTF_8).use {
                it.write("Manifest-Version: 1.0\n")
                it.write("Main-Class: ${result.mainClass.replace('/', '.')}\n")
            }
            for (className in ScratchCompiler.USED_RUNTIME_CLASSES) {
                ScratchABI::class.java.getResourceAsStream("/$className.class")?.use {
                    val destPath = outJar.getPath("$className.class")
                    destPath.parent.createDirectories()
                    Files.copy(it, destPath, StandardCopyOption.REPLACE_EXISTING)
                } ?: throw IllegalArgumentException("Missing runtime class $className")
            }
        }
    }
}

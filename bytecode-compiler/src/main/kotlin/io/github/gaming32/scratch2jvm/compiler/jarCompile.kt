package io.github.gaming32.scratch2jvm.compiler

import io.github.gaming32.scratch2jvm.parser.ScratchProjectFile
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
        val (classes, mainClassName) = ScratchCompiler.compile(inFile.nameWithoutExtension, project.project)
        FileSystems.newFileSystem(URI("jar:" + outFile.toURI()), mapOf("create" to "true")).use { outJar ->
            for (entry in project.scratchZip.entries()) {
                if (entry.name == "project.json") continue
                project.scratchZip.getInputStream(entry).use {
                    Files.copy(it, outJar.getPath(entry.name), StandardCopyOption.REPLACE_EXISTING)
                }
            }
            for (clazz in classes) {
                val writer = ClassWriter(ClassWriter.COMPUTE_FRAMES)
                clazz.accept(writer)
                val destPath = outJar.getPath(clazz.name + ".class")
                destPath.parent.createDirectories()
                destPath.outputStream().use {
                    it.write(writer.toByteArray())
                }
            }
            outJar.getPath("META-INF").createDirectory()
            outJar.getPath("META-INF/MANIFEST.MF").writer(Charsets.UTF_8).use {
                it.write("Manifest-Version: 1.0\n")
                it.write("Main-Class: ${mainClassName.replace('/', '.')}\n")
            }
        }
    }
}

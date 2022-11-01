package io.github.gaming32.scratch2jvm.parser

import com.google.gson.JsonParser
import io.github.gaming32.scratch2jvm.parser.data.ScratchProject
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.zip.ZipFile

public class ScratchProjectFile(private val scratchZip: ZipFile) {
    public companion object {
        @Throws(IOException::class)
        @JvmStatic
        public fun open(name: String): ScratchProjectFile {
            return ScratchProjectFile(ZipFile(name))
        }

        @Throws(IOException::class)
        @JvmStatic
        public fun open(file: File): ScratchProjectFile {
            return ScratchProjectFile(ZipFile(file))
        }
    }

    public val project: ScratchProject = ScratchProject(
        JsonParser.parseReader(
            InputStreamReader(
                scratchZip.getInputStream(
                    scratchZip.getEntry("project.json")
                )
            )
        ).asJsonObject
    )
}

package io.github.gaming32.scratch2jvm.compiler

import java.io.File
import kotlin.system.exitProcess

public fun main(vararg args: String) {
    if (args.isEmpty()) {
        println("Usage: java -jar bytecode-compiler.jar <sb3-in> [jar-out]")
        exitProcess(1)
    }
    val sb3In = File(args[0])
    val jarOut = if (args.size > 1) {
        File(args[1])
    } else {
        File(sb3In.path.substringBeforeLast('.') + ".jar")
    }
    jarOut.parentFile?.mkdirs()
    compileToJar(sb3In, jarOut, FatnessLevel.NATIVES)
}

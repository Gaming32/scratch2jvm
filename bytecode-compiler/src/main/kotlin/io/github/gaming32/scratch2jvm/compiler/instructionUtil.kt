package io.github.gaming32.scratch2jvm.compiler

import codes.som.koffee.insns.InstructionAssembly
import codes.som.koffee.insns.jvm.ldc
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.InsnNode

@Suppress("FunctionName")
public fun InstructionAssembly.push_double(value: Double) {
    val bits = value.toBits()
    if (bits == 0L || bits == 0x3FF0000000000000L) { // +0.0d and 1.0d
        instructions.add(InsnNode(Opcodes.DCONST_0 + value.toInt()))
    } else {
        ldc(value)
    }
}

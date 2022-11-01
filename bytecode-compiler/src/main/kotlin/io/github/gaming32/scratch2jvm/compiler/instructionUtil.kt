package io.github.gaming32.scratch2jvm.compiler

import codes.som.koffee.insns.InstructionAssembly
import codes.som.koffee.insns.jvm.bipush
import codes.som.koffee.insns.jvm.ldc
import codes.som.koffee.insns.jvm.sipush
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.InsnNode

public fun InstructionAssembly.iconst(value: Int): Unit = when (value) {
    in -1..5 -> instructions.add(InsnNode(Opcodes.ICONST_0 + value))
    in Byte.MIN_VALUE..Byte.MAX_VALUE -> bipush(value)
    in Short.MIN_VALUE..Short.MAX_VALUE -> sipush(value)
    else -> ldc(value)
}

public fun InstructionAssembly.dconst(value: Double) {
    val bits = value.toBits()
    if (bits == 0L || bits == 0x3FF0000000000000L) { // +0.0d and 1.0d
        instructions.add(InsnNode(Opcodes.DCONST_0 + value.toInt()))
    } else {
        ldc(value)
    }
}

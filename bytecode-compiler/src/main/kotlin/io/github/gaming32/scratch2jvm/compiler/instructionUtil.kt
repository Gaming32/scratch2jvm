package io.github.gaming32.scratch2jvm.compiler

import codes.som.koffee.insns.InstructionAssembly
import codes.som.koffee.insns.jvm.*
import codes.som.koffee.labels.KoffeeLabel
import codes.som.koffee.labels.LabelLike
import codes.som.koffee.types.boolean
import codes.som.koffee.types.int
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.LabelNode

@Suppress("FunctionName")
public fun InstructionAssembly.push_double(value: Double) {
    val bits = value.toBits()
    if (bits == 0L || bits == 0x3FF0000000000000L) { // +0.0d and 1.0d
        instructions.add(InsnNode(Opcodes.DCONST_0 + value.toInt()))
    } else {
        ldc(value)
    }
}

public fun InstructionAssembly.switchOnString(default: LabelLike, cases: Map<String, LabelLike>) {
    val hashLabels = cases.keys
        .asSequence()
        .map(String::hashCode)
        .sorted()
        .associateWith { label() }
    dup
    invokevirtual(String::class, "hashCode", int)
    val defaultPop = label()
    lookupswitch(defaultPop, *hashLabels.entries.map { Pair(it.key, it.value) }.toTypedArray())
    +defaultPop
    pop
    goto(default)
    for ((hash, label) in hashLabels) {
        +label
        val matchingCases = cases.filterKeys { it.hashCode() == hash }
        matchingCases.entries.forEachIndexed { index, (value, target) ->
            val last = index == matchingCases.size - 1
            if (!last) {
                dup
            }
            ldc(value)
            invokevirtual(String::class, "equals", boolean, Any::class)
            if (last) {
                ifne(target)
                goto(default)
            } else {
                val next = label()
                ifeq(next)
                pop
                goto(target)
                +next
            }
        }
    }
}

public fun InstructionAssembly.label(): KoffeeLabel = KoffeeLabel(this, LabelNode())

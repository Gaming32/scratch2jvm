package io.github.gaming32.scratch2jvm.compiler

import org.objectweb.asm.tree.ClassNode

public data class CompilationResult(
    public val classes: Map<String, ClassNode>,
    public val mainClass: String,
    public val resources: Map<String, String> = mapOf()
)

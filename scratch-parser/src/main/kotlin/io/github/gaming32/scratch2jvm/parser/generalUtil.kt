package io.github.gaming32.scratch2jvm.parser

public inline fun falseAndTrue(body: (Boolean) -> Unit) {
    repeat(2) { body(it == 1) }
}

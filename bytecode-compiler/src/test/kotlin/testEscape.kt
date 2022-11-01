import io.github.gaming32.scratch2jvm.compiler.escapeUnqualifiedName

fun main() {
    println(escapeUnqualifiedName("hello;world/i/", false))
}

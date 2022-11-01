package io.github.gaming32.scratch2jvm.parser

import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

public interface PrettyPrintable {
    public val prettyPrintWhitelistedProperties: Set<KProperty1<*, *>> get() = setOf()
    public val prettyPrintBlacklistedProperties: Set<KProperty1<*, *>> get() = setOf()

    public fun print(out: StringBuilder, currentIndent: String, indent: String): Unit = with(out) {
        append(this@PrettyPrintable.javaClass.simpleName)
        append('(')
        val props = this@PrettyPrintable::class.declaredMemberProperties.filter {
            if (it.name == "prettyPrintWhitelistedProperties") return@filter false
            if (it.name == "prettyPrintBlacklistedProperties") return@filter false
            it in prettyPrintWhitelistedProperties ||
                (it.javaField != null && it !in prettyPrintBlacklistedProperties)
        }
        if (props.isNotEmpty()) {
            val newIndent = currentIndent + indent
            props.forEachIndexed { i, prop ->
                if (i > 0) {
                    append(',')
                }
                append('\n')
                append(newIndent)
                append(prop.name)
                append(" = ")
                @Suppress("UNCHECKED_CAST")
                val value = (prop as KProperty1<in PrettyPrintable, *>).get(this@PrettyPrintable)
                value.prettyPrint(indent, newIndent, out)

            }
            append('\n')
            append(currentIndent)
        }
        append(')')
    }
}

@JvmOverloads
public fun Any?.prettyPrint(
    indent: String = "   ",
    currentIndent: String = "",
    out: StringBuilder = StringBuilder()
): StringBuilder {
    when (this@prettyPrint) {
        null -> out.append("null")
        is PrettyPrintable -> print(out, currentIndent, indent)
        is CharSequence -> {
            out.append('"')
            out.append(this)
            out.append('"')
        }
        is Collection<*> -> {
            out.append('[')
            if (isNotEmpty()) {
                val newIndent = currentIndent + indent
                forEachIndexed { i, value ->
                    if (i > 0) {
                        out.append(',')
                    }
                    out.append('\n')
                    out.append(newIndent)
                    value.prettyPrint(indent, newIndent, out)
                }
                out.append('\n')
                out.append(currentIndent)
            }
            out.append(']')
        }
        is Array<*> -> {
            out.append('[')
            if (isNotEmpty()) {
                val newIndent = currentIndent + indent
                forEachIndexed { i, value ->
                    if (i > 0) {
                        out.append(',')
                    }
                    out.append('\n')
                    out.append(newIndent)
                    value.prettyPrint(indent, newIndent, out)
                }
                out.append('\n')
                out.append(currentIndent)
            }
            out.append(']')
        }
        is Map<*, *> -> {
            out.append('{')
            if (isNotEmpty()) {
                val newIndent = currentIndent + indent
                entries.forEachIndexed { i, (key, value) ->
                    if (i > 0) {
                        out.append(',')
                    }
                    out.append('\n')
                    out.append(newIndent)
                    key.prettyPrint(indent, newIndent, out)
                    out.append(" = ")
                    value.prettyPrint(indent, newIndent, out)
                }
                out.append('\n')
                out.append(currentIndent)
            }
            out.append('}')
        }
        else -> out.append(this)
    }
    return out
}

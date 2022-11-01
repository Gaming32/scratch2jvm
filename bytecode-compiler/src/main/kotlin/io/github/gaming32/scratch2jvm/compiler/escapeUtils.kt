package io.github.gaming32.scratch2jvm.compiler

private val DISALLOWED_CHARS = BooleanArray(92).also {
    it['.'.code] = true
    it[';'.code] = true
    it['['.code] = true
    it['/'.code] = true
}
private val DISALLOWED_METHOD_CHARS = DISALLOWED_CHARS.copyOf().also {
    it['<'.code] = true
    it['>'.code] = true
}

@JvmOverloads
public fun escapeUnqualifiedName(name: String, isMethod: Boolean = false): String {
    val disallowedChars = if (isMethod) DISALLOWED_METHOD_CHARS else DISALLOWED_CHARS
    var startIndex = -1
    for (i in name.indices) {
        val c = name[i]
        if (c.code < disallowedChars.size && disallowedChars[c.code]) {
            startIndex = i
            break
        }
    }
    if (startIndex < 0) return name
    return buildString(name.length) {
        append(name, 0, startIndex)
        append('_')
        var validPos = startIndex + 1
        while (++startIndex < name.length) {
            val c = name[startIndex]
            if (c.code < disallowedChars.size && disallowedChars[c.code]) {
                append(name, validPos, startIndex)
                validPos = startIndex + 1
                append('_')
            }
        }
        if (validPos < name.length) {
            append(name, validPos, startIndex)
        }
    }
}

public fun escapeMethodName(name: String): String = escapeUnqualifiedName(name, true)

public fun escapePackageName(vararg names: String): String =
    names.joinToString("/") { escapeUnqualifiedName(it, false) }

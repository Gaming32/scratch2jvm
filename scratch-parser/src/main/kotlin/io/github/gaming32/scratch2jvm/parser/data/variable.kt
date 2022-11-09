package io.github.gaming32.scratch2jvm.parser.data

import com.google.gson.JsonArray
import io.github.gaming32.scratch2jvm.parser.PrettyPrintable

public sealed interface VariableLike : PrettyPrintable {
    private class Impl(override val id: String, override val name: String) : VariableLike

    public companion object {
        @JvmStatic
        public fun of(id: String, name: String): VariableLike = Impl(id, name)
    }

    public val id: String
    public val name: String
}

public data class ScratchVariable(
    override val id: String,
    override val name: String,
    public val value: String = ""
) : VariableLike {
    public companion object {
        @JvmStatic
        public fun fromJson(id: String, data: JsonArray): ScratchVariable = ScratchVariable(
            id,
            name = data[0].asString,
            value = data[1].asString
        )
    }
}

public data class ScratchList(
    override val id: String,
    override val name: String,
    public val value: List<String> = listOf()
) : VariableLike {
    public companion object {
        @JvmStatic
        public fun fromJson(id: String, data: JsonArray): ScratchList = ScratchList(
            id,
            name = data[0].asString,
            value = data[1].asJsonArray.map { it.asString }
        )
    }
}

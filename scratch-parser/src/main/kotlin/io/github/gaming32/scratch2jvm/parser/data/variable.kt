package io.github.gaming32.scratch2jvm.parser.data

import com.google.gson.JsonArray

public data class ScratchVariable(
    public val id: String,
    public val name: String,
    public val value: String
) {
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
    public val id: String,
    public val name: String,
    public val value: List<String>
) {
    public companion object {
        @JvmStatic
        public fun fromJson(id: String, data: JsonArray): ScratchList = ScratchList(
            id,
            name = data[0].asString,
            value = data[1].asJsonArray.map { it.asString }
        )
    }
}

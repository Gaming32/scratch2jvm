package io.github.gaming32.scratch2jvm.parser.ast

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.github.gaming32.scratch2jvm.parser.PrettyPrintable

public data class ScratchProcedureInfo(
    public val name: String,
    public val argumentIds: List<String>,
    public val warp: Boolean
) : PrettyPrintable {
    public companion object {
        public fun fromJsonOrNull(data: JsonObject?): ScratchProcedureInfo? {
            if (data == null) return null
            return ScratchProcedureInfo(
                name = data["proccode"]?.asString ?: return null,
                argumentIds = data["argumentids"]?.asString?.let { idsData ->
                    JsonParser.parseString(idsData).asJsonArray.map { it.asString }
                } ?: return null,
                warp = data["warp"]?.asString?.toBoolean() ?: return null
            )
        }
    }
}

package io.github.gaming32.scratch2jvm.parser.data

import com.google.gson.JsonObject
import io.github.gaming32.scratch2jvm.parser.PrettyPrintable

public data class ScratchCostume(
    public val name: String,
    public val path: String,
    public val format: Format,
    public val centerX: Double,
    public val centerY: Double
) : PrettyPrintable {
    public enum class Format {
        SVG, PNG
    }

    public companion object {
        public fun fromJson(data: JsonObject): ScratchCostume = ScratchCostume(
            name = data["name"].asString,
            path = "/" + data["md5ext"].asString,
            format = Format.valueOf(data["dataFormat"].asString.uppercase()),
            centerX = data["rotationCenterX"].asDouble,
            centerY = data["rotationCenterY"].asDouble
        )
    }
}

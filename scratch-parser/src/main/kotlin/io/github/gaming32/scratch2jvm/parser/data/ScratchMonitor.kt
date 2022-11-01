package io.github.gaming32.scratch2jvm.parser.data

import com.google.gson.JsonObject
import io.github.gaming32.scratch2jvm.parser.PrettyPrintable

public data class ScratchMonitor(
    public val id: String,
    public val mode: MonitorMode = MonitorMode.DEFAULT,
    public val width: Int = 0, public val height: Int = 0,
    public val x: Int = 0, public val y: Int,
    public val visible: Boolean = true,
    public val sliderMin: Int = 0, public val sliderMax: Int = 100
) : PrettyPrintable {
    public enum class MonitorMode {
        DEFAULT, LARGE, SLIDER, LIST
    }

    public companion object {
        @JvmStatic
        public fun fromJson(data: JsonObject): ScratchMonitor = ScratchMonitor(
            id = data["id"].asString,
            mode = MonitorMode.valueOf(data["mode"].asString.uppercase()),
            width = data["width"].asInt, height = data["height"].asInt,
            x = data["x"].asInt, y = data["y"].asInt,
            visible = data["visible"].asBoolean,
            sliderMin = data["sliderMin"]?.asInt ?: 0,
            sliderMax = data["sliderMax"]?.asInt ?: 0
        )
    }
}

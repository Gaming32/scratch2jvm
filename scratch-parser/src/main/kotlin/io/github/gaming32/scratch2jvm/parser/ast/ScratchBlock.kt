package io.github.gaming32.scratch2jvm.parser.ast

import com.google.gson.JsonObject

public class ScratchBlock(
    public val id: String,
    public val opcode: ScratchOpcodes,
    public val topLevel: Boolean = false,
    public val x: Int = 0, public val y: Int = 0
) {
    internal companion object {
        @JvmStatic
        fun fromJson(id: String, data: JsonObject): ScratchBlock = ScratchBlock(
            id,
            opcode = ScratchOpcodes.fromId(data["opcode"].asString),
            topLevel = data["topLevel"].asBoolean,
            x = data["x"]?.asInt ?: 0,
            y = data["y"]?.asInt ?: 0
        )
    }

    public var next: ScratchBlock? = null
        internal set
    public var parent: ScratchBlock? = null
        internal set

    internal val inputsMutable = mutableMapOf<String, ScratchInput<*>>()
    public val inputs: Map<String, ScratchInput<*>> get() = inputsMutable

    init {
        if (!topLevel) {
            require(x == 0 && y == 0) { "Only top-level blocks may have an X and Y" }
        }
    }

    override fun toString(): String = buildString {
        append("ScratchBlock(id='$id', opcode=$opcode, topLevel=$topLevel")
        if (topLevel) {
            append(", x=$x, y=$y")
        }
        append(", inputs=$inputs")
        if (next != null) {
            append(", next=$next")
        }
        append(')')
    }
}

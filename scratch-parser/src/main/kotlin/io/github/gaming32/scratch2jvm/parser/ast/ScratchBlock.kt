package io.github.gaming32.scratch2jvm.parser.ast

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.github.gaming32.scratch2jvm.parser.PrettyPrintable
import io.github.gaming32.scratch2jvm.parser.data.ScratchVariable
import kotlin.reflect.KProperty1

public class ScratchBlock(
    public val id: String,
    public val opcode: ScratchOpcodes,
    public val topLevel: Boolean = false,
    public val x: Int = 0, public val y: Int = 0,
    public val fields: Map<String, ScratchVariable> = mapOf()
) : PrettyPrintable {
    internal companion object {
        @JvmStatic
        fun fromJson(id: String, data: JsonObject): ScratchBlock = ScratchBlock(
            id,
            opcode = ScratchOpcodes.fromId(data["opcode"].asString),
            topLevel = data["topLevel"].asBoolean,
            x = data["x"]?.asInt ?: 0,
            y = data["y"]?.asInt ?: 0,
            fields = data["fields"]
                .asJsonObject
                .asMap()
                .entries
                .associate { (key, value) ->
                    value as JsonArray
                    key to ScratchVariable(value[1].asString, value[0].asString)
                }
        )
    }

    public override val prettyPrintWhitelistedProperties: Set<KProperty1<*, *>> = setOf(
        ScratchBlock::inputs
    )
    public override val prettyPrintBlacklistedProperties: Set<KProperty1<*, *>> = setOf(
        ScratchBlock::parent,
        ScratchBlock::inputsMutable,
    )

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

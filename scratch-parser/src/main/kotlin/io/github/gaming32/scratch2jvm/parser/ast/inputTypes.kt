package io.github.gaming32.scratch2jvm.parser.ast

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import io.github.gaming32.scratch2jvm.parser.PrettyPrintable
import io.github.gaming32.scratch2jvm.parser.data.ScratchList
import io.github.gaming32.scratch2jvm.parser.data.ScratchVariable
import kotlin.reflect.KProperty1

public enum class ScratchInputTypes(public vararg val ids: Int) {
    SUBVALUED(1),
    BLOCK_STACK(2),
    FALLBACK(3),
    VALUE(4, 5, 6, 7, 8, 10),
    VARIABLE(12),
    LIST(13),
    ;

    public companion object {
        private val TYPES = arrayOfNulls<ScratchInputTypes>(
            ScratchInputTypes.values().flatMap { it.ids.toList() }.max() + 1
        )

        init {
            ScratchInputTypes.values().forEach {
                it.ids.forEach { id -> TYPES[id] = it }
            }
        }

        @JvmStatic
        public fun fromId(id: Int): ScratchInputTypes =
            TYPES.getOrNull(id) ?: throw IllegalArgumentException("Unknown input type $id")
    }
}

public sealed interface ScratchInput<T> : PrettyPrintable {
    public val type: ScratchInputTypes
    public val value: T

    public companion object {
        public fun parse(
            data: JsonElement,
            blocks: Map<String, ScratchBlock>,
            variables: Map<String, ScratchVariable>,
            lists: Map<String, ScratchList>
        ): ScratchInput<*> = if (data.isJsonArray) {
            data as JsonArray
            when (ScratchInputTypes.fromId(data[0].asInt)) {
                ScratchInputTypes.SUBVALUED -> parse(data[1], blocks, variables, lists)
                ScratchInputTypes.BLOCK_STACK -> BlockStackInput(blocks.getValue(data[1].asString))
                ScratchInputTypes.FALLBACK -> FallbackInput(
                    parse(data[1], blocks, variables, lists),
                    parse(data[2], blocks, variables, lists)
                )
                ScratchInputTypes.VALUE -> ValueInput(data[1].asString)
                ScratchInputTypes.VARIABLE -> VariableInput(variables.getValue(data[2].asString))
                ScratchInputTypes.LIST -> ListInput(lists.getValue(data[2].asString))
            }
        } else if (data.isJsonNull) {
            ReferenceInput(null)
        } else {
            ReferenceInput(blocks.getValue(data.asString))
        }
    }
}

public data class ReferenceInput(override val value: ScratchBlock?) : ScratchInput<ScratchBlock?> {
    override val type: ScratchInputTypes get() = ScratchInputTypes.SUBVALUED
}

public data class BlockStackInput(override val value: ScratchBlock) : ScratchInput<ScratchBlock> {
    override val type: ScratchInputTypes get() = ScratchInputTypes.BLOCK_STACK
}

public data class FallbackInput<A, B>(
    public val primary: ScratchInput<A>,
    public val secondary: ScratchInput<B>
) : ScratchInput<Pair<ScratchInput<A>, ScratchInput<B>>> {
    override val type: ScratchInputTypes get() = ScratchInputTypes.FALLBACK
    override val value: Pair<ScratchInput<A>, ScratchInput<B>> = Pair(primary, secondary)
    override val prettyPrintBlacklistedProperties: Set<KProperty1<*, *>> = setOf(FallbackInput<*, *>::value)
}

public data class ValueInput(override val value: String) : ScratchInput<String> {
    override val type: ScratchInputTypes get() = ScratchInputTypes.VALUE
}

public data class VariableInput(override val value: ScratchVariable) : ScratchInput<ScratchVariable> {
    override val type: ScratchInputTypes get() = ScratchInputTypes.VARIABLE
}

public data class ListInput(override val value: ScratchList) : ScratchInput<ScratchList> {
    override val type: ScratchInputTypes get() = ScratchInputTypes.LIST
}

package io.github.gaming32.scratch2jvm.parser.data

import com.google.gson.JsonObject
import io.github.gaming32.scratch2jvm.parser.PrettyPrintable
import io.github.gaming32.scratch2jvm.parser.ast.ScratchBlock
import io.github.gaming32.scratch2jvm.parser.ast.ScratchInput

public data class ScratchTarget(
    public val name: String,
    public val isStage: Boolean = false,
    public val variables: Map<String, ScratchVariable> = mapOf(),
    public val lists: Map<String, ScratchList> = mapOf(),
    public val rootBlocks: Map<String, ScratchBlock> = mapOf(),
    public val currentCostume: Int = 0,
    public val volume: Double = 100.0,
    public val layerOrder: Int = 0,
    public val tempo: Double = 60.0,
    public val x: Double = 0.0,
    public val y: Double = 0.0,
    public val size: Double = 100.0,
    public val direction: Double = 90.0,
    public val draggable: Boolean = false,
    public val rotationStyle: Byte = ROTATION_ALL_AROUND,
) : PrettyPrintable {
    public companion object {
        public const val ROTATION_LEFT_RIGHT: Byte = 0
        public const val ROTATION_DONT_ROTATE: Byte = 1
        public const val ROTATION_ALL_AROUND: Byte = 2
        public val ROTATION_NAMES: List<String> = listOf("left-right", "don't rotate", "all around")

        @JvmStatic
        public fun fromJson(
            data: JsonObject,
            stageVariables: Map<String, ScratchVariable>,
            stageLists: Map<String, ScratchList>
        ): ScratchTarget {
            val variables = data["variables"]
                .asJsonObject
                .entrySet()
                .associate { (id, variable) ->
                    id to ScratchVariable.fromJson(id, variable.asJsonArray)
                }
            val scopedVariables = stageVariables + variables
            val lists = data["lists"]
                .asJsonObject
                .entrySet()
                .associate { (id, variable) ->
                    id to ScratchList.fromJson(id, variable.asJsonArray)
                }
            val scopedLists = stageLists + lists
            val blocksData = data.getAsJsonObject("blocks")
            val rootBlocks = blocksData
                .entrySet()
                .associateTo(mutableMapOf()) { it.key to ScratchBlock.fromJson(it.key, it.value.asJsonObject) }
            rootBlocks.values.forEach { block ->
                val blockData = blocksData[block.id].asJsonObject
                blockData["next"]?.let { next ->
                    if (!next.isJsonNull) {
                        block.next = rootBlocks[next.asString]
                    }
                }
                blockData["parent"]?.let { parent ->
                    if (!parent.isJsonNull) {
                        block.parent = rootBlocks[parent.asString]
                    }
                }
                blockData["inputs"].asJsonObject.asMap().forEach { (name, value) ->
                    block.inputsMutable[name] = ScratchInput.parse(value, rootBlocks, scopedVariables, scopedLists)
                }
            }
            return ScratchTarget(
                name = data["name"].asString,
                isStage = data["isStage"].asBoolean,
                variables = variables,
                lists = lists,
                rootBlocks = rootBlocks.filter { it.value.topLevel },
                currentCostume = data["currentCostume"].asInt,
                volume = data["volume"].asDouble,
                layerOrder = data["layerOrder"].asInt,
                tempo = data["tempo"]?.asDouble ?: 0.0,
                x = data["x"]?.asDouble ?: 0.0,
                y = data["y"]?.asDouble ?: 0.0,
                size = data["size"]?.asDouble ?: 100.0,
                direction = data["direction"]?.asDouble ?: 90.0,
                draggable = data["draggable"]?.asBoolean ?: false,
                rotationStyle = ROTATION_NAMES.indexOf(data["rotationStyle"]?.asString).toByte(),
            )
        }
    }
}

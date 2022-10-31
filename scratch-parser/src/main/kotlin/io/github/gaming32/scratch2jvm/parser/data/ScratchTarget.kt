package io.github.gaming32.scratch2jvm.parser.data

import com.google.gson.JsonObject
import io.github.gaming32.scratch2jvm.parser.ast.ScratchBlock
import io.github.gaming32.scratch2jvm.parser.ast.ScratchInput

public data class ScratchTarget(
    public val name: String,
    public val isStage: Boolean = false,
    public val variables: Map<String, ScratchVariable> = mapOf(),
    public val lists: Map<String, ScratchList> = mapOf(),
    public val rootBlocks: Map<String, ScratchBlock>,
    public val currentCostume: Int = 0
) {
    public companion object {
        @JvmStatic
        public fun fromJson(data: JsonObject, stageVariables: Map<String, ScratchVariable>): ScratchTarget {
            val variables = data["variables"]
                .asJsonObject
                .asMap()
                .entries
                .associate { (id, variable) ->
                    id to ScratchVariable.fromJson(id, variable.asJsonArray)
                }
            val scopedVariables = stageVariables + variables
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
                    block.inputsMutable[name] = ScratchInput.parse(value, rootBlocks, scopedVariables)
                }
            }
            return ScratchTarget(
                name = data["name"].asString,
                isStage = data["isStage"].asBoolean,
                variables = data["variables"]
                    .asJsonObject
                    .asMap()
                    .entries
                    .associate { (id, variable) ->
                        id to ScratchVariable.fromJson(id, variable.asJsonArray)
                    },
                lists = data["lists"]
                    .asJsonObject
                    .asMap()
                    .entries
                    .associate { (id, variable) ->
                        id to ScratchList.fromJson(id, variable.asJsonArray)
                    },
                rootBlocks = rootBlocks.filter { it.value.topLevel },
                currentCostume = data["currentCostume"].asInt
            )
        }
    }
}

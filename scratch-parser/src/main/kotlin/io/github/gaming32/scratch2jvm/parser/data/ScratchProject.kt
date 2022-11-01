package io.github.gaming32.scratch2jvm.parser.data

import com.google.gson.JsonObject
import io.github.gaming32.scratch2jvm.parser.PrettyPrintable

public class ScratchProject(root: JsonObject) : PrettyPrintable {
    public val meta: Map<String, String> = root.getAsJsonObject("meta")
        .asMap()
        .entries
        .associate { it.key to it.value.asString }

    public val monitors: Map<String, ScratchMonitor> = root.getAsJsonArray("monitors")
        .asSequence()
        .map { ScratchMonitor.fromJson(it.asJsonObject) }
        .associateBy { it.id }

    public val targets: Map<String, ScratchTarget> = root.getAsJsonArray("targets").let { targetsData ->
        val result = mutableMapOf<String, ScratchTarget>()
        val stageData = targetsData.first { it.asJsonObject["isStage"].asBoolean }
        val stage = ScratchTarget.fromJson(stageData.asJsonObject, mapOf())
        result[stage.name] = stage
        targetsData.forEach { targetData ->
            if (targetData === stageData) return@forEach
            val target = ScratchTarget.fromJson(targetData.asJsonObject, stage.variables)
            result[target.name] = target
        }
        result
    }

    public val stage: ScratchTarget = targets.values.first { it.isStage }
}

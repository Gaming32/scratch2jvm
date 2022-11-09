package io.github.gaming32.scratch2jvm.parser.data

import com.google.gson.JsonObject
import io.github.gaming32.scratch2jvm.parser.PrettyPrintable
import kotlin.reflect.KProperty1

public class ScratchProject(root: JsonObject) : PrettyPrintable {
    public val meta: Map<String, String> = root.getAsJsonObject("meta")
        .entrySet()
        .associate { it.key to it.value.asString }

    public val extensions: Set<String> = root.getAsJsonArray("extensions")
        ?.mapTo(mutableSetOf()) { it.asString }
        ?: setOf()

    public val monitors: Map<String, ScratchMonitor> = root.getAsJsonArray("monitors")
        .asSequence()
        .map { ScratchMonitor.fromJson(it.asJsonObject) }
        .associateBy { it.id }

    public val targets: Map<String, ScratchTarget> = root.getAsJsonArray("targets").let { targetsData ->
        val result = mutableMapOf<String, ScratchTarget>()
        val stageData = targetsData.first { it.asJsonObject["isStage"].asBoolean }
        val stage = ScratchTarget.fromJson(stageData.asJsonObject, mapOf(), mapOf())
        result[stage.name] = stage
        targetsData.forEach { targetData ->
            if (targetData === stageData) return@forEach
            val target = ScratchTarget.fromJson(targetData.asJsonObject, stage.variables, stage.lists)
            result[target.name] = target
        }
        result
    }

    public val stage: ScratchTarget = targets.values.first { it.isStage }

    override val prettyPrintBlacklistedProperties: Set<KProperty1<*, *>> = setOf(
        ScratchProject::stage
    )
}

package io.github.gaming32.scratch2jvm.compiler

import codes.som.koffee.MethodAssembly
import codes.som.koffee.assembleClass
import codes.som.koffee.insns.jvm.*
import codes.som.koffee.insns.sugar.construct
import codes.som.koffee.insns.sugar.push_int
import codes.som.koffee.modifiers.final
import codes.som.koffee.modifiers.public
import codes.som.koffee.sugar.ClassAssemblyExtension.clinit
import codes.som.koffee.sugar.ClassAssemblyExtension.init
import codes.som.koffee.types.TypeLike
import io.github.gaming32.scratch2jvm.parser.ast.*
import io.github.gaming32.scratch2jvm.parser.data.ScratchCostume
import io.github.gaming32.scratch2jvm.parser.data.ScratchProject
import io.github.gaming32.scratch2jvm.parser.data.ScratchTarget
import io.github.gaming32.scratch2jvm.parser.prettyPrint
import org.objectweb.asm.Type
import java.lang.invoke.*

public class ScratchCompiler private constructor(
    private val projectName: String,
    private val project: ScratchProject
) {
    public companion object {
        private const val RUNTIME_PACKAGE: String = "io/github/gaming32/scratch2jvm/runtime"
        public const val SCRATCH_ABI: String = "$RUNTIME_PACKAGE/ScratchABI"
        public const val SCRATCH_APPLICATION: String = "$RUNTIME_PACKAGE/ScratchApplication"
        public const val SCRATCH_COSTUME: String = "$RUNTIME_PACKAGE/ScratchCostume"
        public const val SCRATCH_COSTUME_FORMAT: String = "$SCRATCH_COSTUME\$Format"
        private const val ASYNC_PACKAGE: String = "$RUNTIME_PACKAGE/async"
        public const val ASYNC_HANDLER: String = "$ASYNC_PACKAGE/AsyncHandler"
        public const val ASYNC_SCHEDULER: String = "$ASYNC_PACKAGE/AsyncScheduler"
        public const val SCHEDULED_JOB: String = "$ASYNC_PACKAGE/ScheduledJob"
        private const val RENDERER_PACKAGE: String = "$RUNTIME_PACKAGE/renderer"
        public const val SCRATCH_RENDERER: String = "$RENDERER_PACKAGE/ScratchRenderer"
        private const val TARGET_PACKAGE: String = "$RUNTIME_PACKAGE/target"
        public const val TARGET_BASE: String = "$TARGET_PACKAGE/Target"
        public const val STAGE_BASE: String = "$TARGET_PACKAGE/Stage"
        public const val SPRITE_BASE: String = "$TARGET_PACKAGE/Sprite"
        public const val ROTATION_STYLE: String = "$TARGET_PACKAGE/RotationStyle"
        private const val UTIL_PACKAGE: String = "$RUNTIME_PACKAGE/util"
        public const val NAMED_INDEXED_ARRAY: String = "$UTIL_PACKAGE/NamedIndexedArray"

        private val REMAPPED_MATH_OPS = mapOf(
            "ceiling" to "ceil",
            "ln" to "log",
            "log" to "log10",
            "e ^" to "exp"
        )
        private val DEG_TO_RAD_OPS = setOf("sin", "cos", "tan")
        private val RAD_TO_DEG_OPS = setOf("asin", "acos", "atan")

        private val LOGGER = getLogger()

        @JvmStatic
        public fun compile(projectName: String, project: ScratchProject): CompilationResult =
            ScratchCompiler(projectName, project).compile()
    }

    private lateinit var target: ScratchTarget
    private var warp = false
    private var stateIndex = 0
    private var maxStateIndex = 0
    private var labelIndex = 0

    private fun compile(): CompilationResult {
        val mainClassName = escapePackageName("scratch", projectName, "Main")
        val resources = mutableMapOf<String, String>()
        return CompilationResult(
            buildMap {
                for (target in project.targets.values) {
                    val className = escapePackageName("scratch", projectName, "target", target.name)
                    val superName = if (target.isStage) STAGE_BASE else SPRITE_BASE
                    put(className, assembleClass(public + final, className, superName = superName) {
                        field(public + static + final, "INSTANCE", className)

                        for (block in target.rootBlocks.values) {
                            field(private + static + final, escapeUnqualifiedName(block.id), ASYNC_HANDLER)
                        }

                        clinit {
                            construct(className)
                            putstatic(className, "INSTANCE", className)
                            for (block in target.rootBlocks.values) {
                                invokedynamic(
                                    "handle",
                                    ASYNC_HANDLER as TypeLike,
                                    handle = h_invokestatic(
                                        LambdaMetafactory::class,
                                        "metafactory",
                                        CallSite::class,
                                        MethodHandles.Lookup::class,
                                        String::class,
                                        MethodType::class,
                                        MethodType::class,
                                        MethodHandle::class,
                                        MethodType::class
                                    ),
                                    args = arrayOf(
                                        Type.getMethodType(
                                            coerceType(int),
                                            coerceType(TARGET_BASE),
                                            coerceType(SCHEDULED_JOB)
                                        ),
                                        h_invokevirtual(
                                            className,
                                            escapeMethodName(block.id),
                                            int,
                                            SCHEDULED_JOB
                                        ),
                                        Type.getMethodType(
                                            coerceType(int),
                                            coerceType(className),
                                            coerceType(SCHEDULED_JOB)
                                        )
                                    )
                                )
                                putstatic(className, escapeUnqualifiedName(block.id), ASYNC_HANDLER)
                            }
                            _return
                        }

                        for (variable in target.variables.values) {
                            field(public, escapeUnqualifiedName(variable.name), String::class)
                        }

                        for (list in target.lists.values) {
                            field(public, escapeUnqualifiedName(list.name), List::class, "Ljava/util/List<Ljava/lang/String;>;")
                        }

                        method(private, "<init>", void) {
                            aload_0
                            ldc(target.name)
                            construct(NAMED_INDEXED_ARRAY, void, Array<String>::class, Array<Any>::class) {
                                push_int(target.costumes.size)
                                anewarray(Any::class)
                                push_int(target.costumes.size)
                                anewarray(String::class)
                                target.costumes.forEachIndexed { i, costume ->
                                    dup_x1
                                    push_int(i)
                                    if ((i and 1) == 0) {
                                        ldc(costume.name)
                                        aastore
                                        dup
                                        push_int(i)
                                        compileCostume(costume)
                                    } else {
                                        compileCostume(costume)
                                        aastore
                                        dup
                                        push_int(i)
                                        ldc(costume.name)
                                    }
                                    aastore
                                }
                                if ((target.costumes.size and 1) == 0) {
                                    swap
                                }
                            }
                            invokespecial(superName, "<init>", void, String::class, NAMED_INDEXED_ARRAY)
                            for (variable in target.variables.values) {
                                aload_0
                                ldc(variable.value)
                                putfield(className, escapeUnqualifiedName(variable.name), String::class)
                            }
                            for (list in target.lists.values) {
                                aload_0
                                if (list.value.size > 50) {
                                    val resourceName = escapeUnqualifiedName(list.id) + ".txt"
                                    resources[resourceName] = buildString {
                                        for (element in list.value) {
                                            append(element)
                                            append('\n')
                                        }
                                    }
                                    ldc(Type.getObjectType(className))
                                    ldc(resourceName)
                                    push_int(list.value.size)
                                    invokestatic(
                                        SCRATCH_ABI, "loadListResource",
                                        List::class, Class::class, String::class, int
                                    )
                                } else {
                                    construct(ArrayList::class, void, int) {
                                        push_int(list.value.size)
                                    }
                                    for (element in list.value) {
                                        dup
                                        ldc(element)
                                        invokeinterface(List::class, "add", boolean, Any::class)
                                        pop
                                    }
                                }
                                putfield(className, escapeUnqualifiedName(list.name), List::class)
                            }
                            aload_0
                            push_int(target.currentCostume)
                            putfield(TARGET_BASE, "costume", int)
                            aload_0
                            push_double(target.volume)
                            putfield(TARGET_BASE, "volume", double)
                            aload_0
                            push_int(target.layerOrder)
                            putfield(TARGET_BASE, "layerOrder", int)
                            if (target.isStage) {
                                aload_0
                                push_double(target.tempo)
                                putfield(STAGE_BASE, "tempo", double)
                            } else {
                                aload_0
                                push_double(target.x)
                                putfield(SPRITE_BASE, "x", double)
                                aload_0
                                push_double(target.y)
                                putfield(SPRITE_BASE, "y", double)
                                aload_0
                                push_double(target.size)
                                putfield(SPRITE_BASE, "size", double)
                                aload_0
                                push_double(target.direction)
                                putfield(SPRITE_BASE, "direction", double)
                                aload_0
                                if (target.draggable) {
                                    iconst_1
                                } else {
                                    iconst_0
                                }
                                putfield(SPRITE_BASE, "draggable", boolean)
                                aload_0
                                getstatic(ROTATION_STYLE, target.rotationStyle.name, ROTATION_STYLE)
                                putfield(SPRITE_BASE, "rotationStyle", ROTATION_STYLE)
                            }
                            _return
                        }

                        if (!target.isStage) {
                            method(private, "<init>", void, className) {
                                aload_0
                                ldc(target.name)
                                aload_1
                                getfield(TARGET_BASE, "costumes", NAMED_INDEXED_ARRAY)
                                invokespecial(superName, "<init>", void, String::class, NAMED_INDEXED_ARRAY)
                                for (variable in target.variables.values) {
                                    val name = escapeUnqualifiedName(variable.name)
                                    aload_0
                                    aload_1
                                    getfield(className, name, String::class)
                                    putfield(className, name, String::class)
                                }
                                for (list in target.lists.values) {
                                    val name = escapeUnqualifiedName(list.name)
                                    aload_0
                                    construct(ArrayList::class, void, Collection::class) {
                                        aload_1
                                        getfield(className, name, List::class)
                                    }
                                    putfield(className, name, List::class)
                                }
                                aload_0
                                aload_1
                                getfield(TARGET_BASE, "costume", int)
                                putfield(TARGET_BASE, "costume", int)
                                aload_0
                                aload_1
                                getfield(TARGET_BASE, "volume", double)
                                putfield(TARGET_BASE, "volume", double)
                                aload_0
                                aload_1
                                getfield(TARGET_BASE, "layerOrder", int)
                                putfield(TARGET_BASE, "layerOrder", int)
                                aload_0
                                aload_1
                                getfield(SPRITE_BASE, "x", double)
                                putfield(SPRITE_BASE, "x", double)
                                aload_0
                                aload_1
                                getfield(SPRITE_BASE, "y", double)
                                putfield(SPRITE_BASE, "y", double)
                                aload_0
                                aload_1
                                getfield(SPRITE_BASE, "size", double)
                                putfield(SPRITE_BASE, "size", double)
                                aload_0
                                aload_1
                                getfield(SPRITE_BASE, "direction", double)
                                putfield(SPRITE_BASE, "direction", double)
                                aload_0
                                aload_1
                                getfield(SPRITE_BASE, "draggable", boolean)
                                putfield(SPRITE_BASE, "draggable", boolean)
                                aload_0
                                aload_1
                                getfield(SPRITE_BASE, "rotationStyle", byte)
                                putfield(SPRITE_BASE, "rotationStyle", byte)
                                _return
                            }
                        }

                        val events = mutableListOf<Pair<String, Int>>()

                        for (block in target.rootBlocks.values) {
                            if (LOGGER.isTraceEnabled) {
                                LOGGER.trace(block.prettyPrint().toString())
                            }
                            method(public, escapeMethodName(block.id), int, SCHEDULED_JOB) {
                                this@ScratchCompiler.target = target
                                stateIndex = 0
                                maxStateIndex = -1
                                labelIndex = 0
                                goto(L["main_end_check"])
                                addAsyncLabel()
                                compileBlock(block)
                                push_int(SUSPEND_NO_RESCHEDULE)
                                ireturn
                                +L["main_end_check"]
                                aload_1
                                getfield(SCHEDULED_JOB, "label", int)
                                tableswitch(
                                    0, labelIndex - 1, L["main_unknown_label"],
                                    if (maxStateIndex != -1) L["main_init_state"] else L[0],
                                    *Array(labelIndex - 1) { L[it + 1] }
                                )
                                if (maxStateIndex != -1) {
                                    +L["main_init_state"]
                                    aload_1
                                    push_int(maxStateIndex + 1)
                                    newarray(double)
                                    putfield(SCHEDULED_JOB, "state", DoubleArray::class)
                                    goto(L[0])
                                }
                                +L["main_unknown_label"]
                                construct(Error::class, void, String::class) {
                                    construct(StringBuilder::class, void, String::class) {
                                        ldc("Unknown label: ")
                                    }
                                    aload_1
                                    getfield(SCHEDULED_JOB, "label", int)
                                    invokevirtual(StringBuilder::class, "append", StringBuilder::class, int)
                                    invokevirtual(StringBuilder::class, "toString", String::class)
                                }
                                athrow
                            }
                            when (block.opcode) {
                                ScratchOpcodes.EVENT_WHENFLAGCLICKED -> events += Pair(block.id, EVENT_FLAG_CLICKED)
                                else -> {}
                            }
                        }

                        method(public, "registerEvents", void, ASYNC_SCHEDULER) {
                            for ((blockId, eventType) in events) {
                                aload_1
                                aload_0
                                push_int(eventType)
                                getstatic(className, escapeUnqualifiedName(blockId), ASYNC_HANDLER)
                                invokevirtual(
                                    ASYNC_SCHEDULER, "registerEventHandler",
                                    void, TARGET_BASE, int, ASYNC_HANDLER
                                )
                            }
                            _return
                        }
                    })
                }

                put(mainClassName, assembleClass(public + final, mainClassName) {
                    field(public + static + final, "APPLICATION", SCRATCH_APPLICATION)

                    clinit {
                        construct(SCRATCH_APPLICATION, void, String::class, int) {
                            ldc(projectName)
                            push_int(30)
                        }
                        putstatic(mainClassName, "APPLICATION", SCRATCH_APPLICATION)
                        _return
                    }

                    init(private) {
                        _return
                    }

                    method(public + static, "main", void, Array<String>::class) {
                        getstatic(SCRATCH_ABI, "RENDERER", SCRATCH_RENDERER)
                        getstatic(mainClassName, "APPLICATION", SCRATCH_APPLICATION)
                        invokeinterface(SCRATCH_RENDERER, "setApplication", void, SCRATCH_APPLICATION)
                        getstatic(SCRATCH_ABI, "SCHEDULER", ASYNC_SCHEDULER)
                        for (target in project.targets.values.sortedBy { it.layerOrder }) {
                            val className = escapePackageName("scratch", projectName, "target", target.name)
                            dup
                            getstatic(className, "INSTANCE", className)
                            invokevirtual(ASYNC_SCHEDULER, "addTarget", void, TARGET_BASE)
                        }
                        dup
                        push_int(EVENT_FLAG_CLICKED)
                        invokevirtual(ASYNC_SCHEDULER, "scheduleEvent", void, int)
                        invokevirtual(ASYNC_SCHEDULER, "runUntilComplete", void)
                        _return
                    }
                })
            },
            mainClassName, resources
        )
    }

    private enum class CompileDataType {
        DEFAULT, NUMBER, BOOLEAN
    }

    private tailrec fun MethodAssembly.compileInput(
        input: ScratchInput<*>,
        type: CompileDataType = CompileDataType.DEFAULT
    ) {
        when (input.type) {
            ScratchInputTypes.SUBVALUED -> return compileBlock((input as ReferenceInput).value, type)
            ScratchInputTypes.FALLBACK -> return compileInput((input as FallbackInput<*, *>).primary, type)
            ScratchInputTypes.VALUE -> {
                val value = (input as ValueInput).value
                if (type == CompileDataType.NUMBER) {
                    push_double(value.toDoubleOrNull() ?: 0.0)
                } else {
                    ldc(value)
                }
                return
            }
            ScratchInputTypes.VARIABLE -> {
                val variable = (input as VariableInput).value
                if (variable.id in target.variables) {
                    aload_0
                    getfield(
                        escapePackageName("scratch", projectName, "target", target.name),
                        escapeUnqualifiedName(variable.name),
                        String::class
                    )
                } else {
                    val stageName = escapePackageName("scratch", projectName, "target", project.stage.name)
                    getstatic(stageName, "INSTANCE", stageName)
                    getfield(stageName, escapeUnqualifiedName(variable.name), String::class)
                }
            }
            ScratchInputTypes.LIST -> {
                val list = (input as ListInput).value
                if (list.id in target.lists) {
                    aload_0
                    getfield(
                        escapePackageName("scratch", projectName, "target", target.name),
                        escapeUnqualifiedName(list.name),
                        List::class
                    )
                } else {
                    val stageName = escapePackageName("scratch", projectName, "target", project.stage.name)
                    getstatic(stageName, "INSTANCE", stageName)
                    getfield(stageName, escapeUnqualifiedName(list.name), List::class)
                }
                invokestatic(SCRATCH_ABI, "listToString", String::class, List::class)
            }
            ScratchInputTypes.BLOCK_STACK -> return compileBlock((input as BlockStackInput).value, type)
            else -> throw IllegalArgumentException("Don't know how to compile input ${input.type} yet")
        }
        if (type == CompileDataType.NUMBER) {
            invokestatic(SCRATCH_ABI, "getNumber", double, String::class)
        }
    }

    private fun MethodAssembly.getList(variable: ScratchBlock.BlockField) {
        if (variable.id in target.lists) {
            aload_0
            getfield(
                escapePackageName("scratch", projectName, "target", target.name),
                escapeUnqualifiedName(variable.name),
                List::class
            )
        } else {
            val stageName = escapePackageName("scratch", projectName, "target", project.stage.name)
            getstatic(stageName, "INSTANCE", stageName)
            getfield(stageName, escapeUnqualifiedName(variable.name), List::class)
        }
    }

    private tailrec fun MethodAssembly.compileBlock(
        block: ScratchBlock,
        type: CompileDataType = CompileDataType.DEFAULT
    ) {
        when (block.opcode) {
            ScratchOpcodes.MOTION_GOTO,
            ScratchOpcodes.MOTION_GOTOXY -> {
                if (!target.isStage) {
                    aload_0
                    if (block.opcode == ScratchOpcodes.MOTION_GOTO) {
                        val toBlock = (block.inputs.getValue("TO") as ReferenceInput).value
                        when (toBlock.fields.getValue("TO").name) {
                            "_mouse_" -> {
                                invokevirtual(SPRITE_BASE, "gotoMousePosition", void)
                            }
                            "_random_" -> {
                                push_double(-240.0)
                                push_double(240.0)
                                invokestatic(SCRATCH_ABI, "random", double, double, double)
                                push_double(-180.0)
                                push_double(180.0)
                                invokestatic(SCRATCH_ABI, "random", double, double, double)
                                invokevirtual(SPRITE_BASE, "setXY", void, double, double)
                            }
                        }
                    } else {
                        compileInput(block.inputs.getValue("X"), CompileDataType.NUMBER)
                        compileInput(block.inputs.getValue("Y"), CompileDataType.NUMBER)
                        invokevirtual(SPRITE_BASE, "setXY", void, double, double)
                    }
                }
            }
            ScratchOpcodes.MOTION_GLIDETO,
            ScratchOpcodes.MOTION_GLIDESECSTOXY -> {
                if (!target.isStage) {
                    await {
                        aload_0
                        compileInput(block.inputs.getValue("SECS"), CompileDataType.NUMBER)
                        if (block.opcode == ScratchOpcodes.MOTION_GLIDETO) {
                            val toBlock = (block.inputs.getValue("TO") as ReferenceInput).value
                            when (toBlock.fields.getValue("TO").name) {
                                "_mouse_" -> {
                                    invokevirtual(SPRITE_BASE, "glideToMousePosition", SCHEDULED_JOB, double)
                                }
                                "_random_" -> {
                                    push_double(-240.0)
                                    push_double(240.0)
                                    invokestatic(SCRATCH_ABI, "random", double, double, double)
                                    push_double(-180.0)
                                    push_double(180.0)
                                    invokestatic(SCRATCH_ABI, "random", double, double, double)
                                    invokevirtual(SPRITE_BASE, "glideTo", SCHEDULED_JOB, double, double, double)
                                }
                            }
                        } else {
                            compileInput(block.inputs.getValue("X"), CompileDataType.NUMBER)
                            compileInput(block.inputs.getValue("Y"), CompileDataType.NUMBER)
                            invokevirtual(SPRITE_BASE, "glideTo", SCHEDULED_JOB, double, double, double)
                        }
                    }
                }
            }
            ScratchOpcodes.MOTION_POINTINDIRECTION -> {
                if (!target.isStage) {
                    aload_0
                    compileInput(block.inputs.getValue("DIRECTION"), CompileDataType.NUMBER)
                    invokevirtual(SPRITE_BASE, "setDirection", void, double)
                }
            }
            ScratchOpcodes.MOTION_POINTTOWARDS -> {
                if (!target.isStage) {
                    aload_0
                    invokevirtual(SPRITE_BASE, "pointTowardsMouse", void)
                }
            }
            ScratchOpcodes.MOTION_SETX,
            ScratchOpcodes.MOTION_SETY -> {
                if (!target.isStage) {
                    val axis = if (block.opcode == ScratchOpcodes.MOTION_SETX) "X" else "Y"
                    aload_0
                    compileInput(block.inputs.getValue(axis), CompileDataType.NUMBER)
                    invokevirtual(SPRITE_BASE, "set$axis", void, double)
                }
            }
            ScratchOpcodes.MOTION_CHANGEXBY,
            ScratchOpcodes.MOTION_CHANGEYBY -> {
                if (!target.isStage) {
                    val axis = if (block.opcode == ScratchOpcodes.MOTION_CHANGEXBY) "X" else "Y"
                    aload_0
                    dup
                    getfield(SPRITE_BASE, axis.lowercase(), double)
                    compileInput(block.inputs.getValue("D$axis"), CompileDataType.NUMBER)
                    dadd
                    invokevirtual(SPRITE_BASE, "set$axis", void, double)
                }
            }
            ScratchOpcodes.MOTION_SETROTATIONSTYLE -> {
                if (!target.isStage) {
                    aload_0
                    getstatic(
                        ROTATION_STYLE,
                        ScratchTarget.RotationStyle.fromName(block.fields.getValue("STYLE").name).name,
                        ROTATION_STYLE
                    )
                    putfield(SPRITE_BASE, "rotationStyle", ROTATION_STYLE)
                }
            }
            ScratchOpcodes.MOTION_XPOSITION,
            ScratchOpcodes.MOTION_YPOSITION -> {
                if (target.isStage) {
                    if (type == CompileDataType.NUMBER) {
                        push_double(0.0)
                    } else {
                        ldc("0")
                    }
                } else {
                    aload_0
                    getfield(SPRITE_BASE, if (block.opcode == ScratchOpcodes.MOTION_XPOSITION) "x" else "y", double)
                    if (type != CompileDataType.NUMBER) {
                        invokestatic(SCRATCH_ABI, "doubleToString", String::class, double)
                    }
                }
            }
            ScratchOpcodes.LOOKS_SAY -> {
                aload_0
                compileInput(block.inputs.getValue("MESSAGE"))
                invokestatic(SCRATCH_ABI, "say", void, TARGET_BASE, String::class)
            }
            ScratchOpcodes.EVENT_WHENFLAGCLICKED -> {}
            ScratchOpcodes.CONTROL_FOREVER -> run {
                val label = addAsyncLabel()
                block.inputs["SUBSTACK"]?.let { compileInput(it) }
                if (warp) {
                    goto(L[label])
                } else {
                    push_int(label)
                    ireturn
                }
            }
            ScratchOpcodes.CONTROL_WAIT -> {
                await {
                    compileInput(block.inputs.getValue("DURATION"), CompileDataType.NUMBER)
                    invokestatic(SCRATCH_ABI, "wait", SCHEDULED_JOB, double)
                }
            }
            ScratchOpcodes.CONTROL_REPEAT -> {
                val countState = newState()
                val indexState = newState()
                setState(countState) {
                    compileInput(block.inputs.getValue("TIMES"), CompileDataType.NUMBER)
                }
                setState(indexState) {
                    dconst_0
                }
                L.scope(this).also { l ->
                    +l["repeat"]
                    getState(indexState)
                    getState(countState)
                    dcmpg
                    ifge(l["end"])
                    compileInput(block.inputs.getValue("SUBSTACK"))
                    setState(indexState) {
                        getState(indexState)
                        dconst_1
                        dadd
                    }
                    goto(l["repeat"])
                    +l["end"]
                }
            }
            ScratchOpcodes.CONTROL_IF -> {
                val condition = block.inputs.getValue("CONDITION")
                L.scope(this).also { l ->
                    if (condition is BlockStackInput && condition.value.opcode == ScratchOpcodes.OPERATOR_NOT) {
                        compileInput(condition.value.inputs.getValue("OPERAND"), CompileDataType.BOOLEAN)
                        ifne(l["condition_false"])
                    } else {
                        compileInput(condition, CompileDataType.BOOLEAN)
                        ifeq(l["condition_false"])
                    }
                    compileInput(block.inputs.getValue("SUBSTACK"))
                    +l["condition_false"]
                }
            }
            ScratchOpcodes.CONTROL_IF_ELSE -> {
                val condition = block.inputs.getValue("CONDITION")
                L.scope(this).also { l ->
                    if (condition is BlockStackInput && condition.value.opcode == ScratchOpcodes.OPERATOR_NOT) {
                        compileInput(condition.value.inputs.getValue("OPERAND"), CompileDataType.BOOLEAN)
                        ifne(l["condition_false"])
                    } else {
                        compileInput(condition, CompileDataType.BOOLEAN)
                        ifeq(l["condition_false"])
                    }
                    compileInput(block.inputs.getValue("SUBSTACK"))
                    goto(l["if_end"])
                    +l["condition_false"]
                    compileInput(block.inputs.getValue("SUBSTACK2"))
                    +l["if_end"]
                }
            }
            ScratchOpcodes.CONTROL_WAIT_UNTIL -> {
                val label = addAsyncLabel()
                val condition = block.inputs.getValue("CONDITION")
                L.scope(this).also { l ->
                    if (condition is BlockStackInput && condition.value.opcode == ScratchOpcodes.OPERATOR_NOT) {
                        compileInput(condition.value.inputs.getValue("OPERAND"), CompileDataType.BOOLEAN)
                        ifeq(l["wait_end"])
                    } else {
                        compileInput(condition, CompileDataType.BOOLEAN)
                        ifne(l["wait_end"])
                    }
                    push_int(label)
                    ireturn
                    +l["wait_end"]
                }
            }
            ScratchOpcodes.CONTROL_STOP -> when (val stopOption = block.fields.getValue("STOP_OPTION").name) {
                "all" -> {
                    push_int(SUSPEND_CANCEL_ALL)
                    ireturn
                }
                "this script" -> {
                    push_int(SUSPEND_NO_RESCHEDULE)
                    ireturn
                }
                "other scripts in sprite" -> {
                    getstatic(SCRATCH_ABI, "SCHEDULER", ASYNC_SCHEDULER)
                    aload_0
                    aload_1
                    invokevirtual(ASYNC_SCHEDULER, "cancelJobs", void, TARGET_BASE, SCHEDULED_JOB)
                }
                else -> throw IllegalArgumentException("Unknown control_stop STOP_OPTION $stopOption")
            }
            ScratchOpcodes.SENSING_KEYPRESSED -> {
                getstatic(SCRATCH_ABI, "RENDERER", SCRATCH_RENDERER)
                val keyOptionBlock = (block.inputs.getValue("KEY_OPTION") as ReferenceInput).value
                val keyOption = keyOptionBlock.fields.getValue("KEY_OPTION").name
                push_int(EXTRA_KEYS[keyOption] ?: keyOption[0].code)
                invokeinterface(SCRATCH_RENDERER, "keyPressed", boolean, int)
                if (type != CompileDataType.BOOLEAN) {
                    invokestatic(Boolean::class.javaObjectType, "toString", String::class, boolean)
                }
            }
            ScratchOpcodes.SENSING_MOUSEDOWN -> {
                getstatic(SCRATCH_ABI, "RENDERER", SCRATCH_RENDERER)
                invokeinterface(SCRATCH_RENDERER, "isMouseDown", boolean)
                if (type != CompileDataType.BOOLEAN) {
                    invokestatic(Boolean::class.javaObjectType, "toString", String::class, boolean)
                }
            }
            ScratchOpcodes.SENSING_TIMER -> {
                getstatic(SCRATCH_ABI, "RENDERER", SCRATCH_RENDERER)
                invokeinterface(SCRATCH_RENDERER, "getAbsoluteTimer", double)
                getstatic(SCRATCH_ABI, "timerStart", double)
                dsub
                if (type != CompileDataType.NUMBER) {
                    invokestatic(SCRATCH_ABI, "doubleToString", String::class, double)
                }
            }
            ScratchOpcodes.OPERATOR_ADD,
            ScratchOpcodes.OPERATOR_SUBTRACT,
            ScratchOpcodes.OPERATOR_MULTIPLY,
            ScratchOpcodes.OPERATOR_DIVIDE,
            ScratchOpcodes.OPERATOR_MOD -> {
                compileInput(block.inputs.getValue("NUM1"), CompileDataType.NUMBER)
                compileInput(block.inputs.getValue("NUM2"), CompileDataType.NUMBER)
                when (block.opcode) {
                    ScratchOpcodes.OPERATOR_ADD -> dadd
                    ScratchOpcodes.OPERATOR_SUBTRACT -> dsub
                    ScratchOpcodes.OPERATOR_MULTIPLY -> dmul
                    ScratchOpcodes.OPERATOR_DIVIDE -> ddiv
                    ScratchOpcodes.OPERATOR_MOD -> invokestatic(SCRATCH_ABI, "mod", double, double, double)
                    else -> throw AssertionError()
                }
                if (type != CompileDataType.NUMBER) {
                    invokestatic(SCRATCH_ABI, "doubleToString", String::class, double)
                }
            }
            ScratchOpcodes.OPERATOR_RANDOM -> {
                compileInput(block.inputs.getValue("FROM"), CompileDataType.NUMBER)
                compileInput(block.inputs.getValue("TO"), CompileDataType.NUMBER)
                invokestatic(SCRATCH_ABI, "flooredRandom", double, double, double)
                if (type != CompileDataType.NUMBER) {
                    invokestatic(SCRATCH_ABI, "doubleToString", String::class, double)
                }
            }
            ScratchOpcodes.OPERATOR_GT,
            ScratchOpcodes.OPERATOR_LT,
            ScratchOpcodes.OPERATOR_EQUALS -> {
                compileInput(block.inputs.getValue("OPERAND1"))
                compileInput(block.inputs.getValue("OPERAND2"))
                push_int(when (block.opcode) {
                    ScratchOpcodes.OPERATOR_GT -> 1
                    ScratchOpcodes.OPERATOR_LT -> -1
                    ScratchOpcodes.OPERATOR_EQUALS -> 0
                    else -> throw AssertionError()
                })
                invokestatic(SCRATCH_ABI, "compareValues", boolean, String::class, String::class, int)
                if (type != CompileDataType.BOOLEAN) {
                    invokestatic(Boolean::class.javaObjectType, "toString", String::class, boolean)
                }
            }
            ScratchOpcodes.OPERATOR_AND,
            ScratchOpcodes.OPERATOR_OR -> {
                compileInput(block.inputs.getValue("OPERAND1"), CompileDataType.BOOLEAN)
                compileInput(block.inputs.getValue("OPERAND2"), CompileDataType.BOOLEAN)
                if (block.opcode == ScratchOpcodes.OPERATOR_AND) {
                    iand
                } else {
                    ior
                }
                if (type != CompileDataType.BOOLEAN) {
                    invokestatic(Boolean::class.javaObjectType, "toString", String::class, boolean)
                }
            }
            ScratchOpcodes.OPERATOR_NOT -> {
                compileInput(block.inputs.getValue("OPERAND"), CompileDataType.BOOLEAN)
                iconst_1
                ixor
                if (type != CompileDataType.BOOLEAN) {
                    invokestatic(Boolean::class.javaObjectType, "toString", String::class, boolean)
                }
            }
            ScratchOpcodes.OPERATOR_JOIN -> {
                compileInput(block.inputs.getValue("STRING1"))
                compileInput(block.inputs.getValue("STRING2"))
                invokevirtual(String::class, "concat", String::class, String::class)
            }
            ScratchOpcodes.OPERATOR_LETTER_OF -> {
                compileInput(block.inputs.getValue("STRING"))
                compileInput(block.inputs.getValue("LETTER"), CompileDataType.NUMBER)
                invokestatic(SCRATCH_ABI, "letterOf", String::class, String::class, double)
            }
            ScratchOpcodes.OPERATOR_LENGTH -> {
                compileInput(block.inputs.getValue("STRING"))
                invokevirtual(String::class, "length", int)
                coerceInt(type)
            }
            ScratchOpcodes.OPERATOR_MATHOP -> {
                val operator = block.fields.getValue("OPERATOR").name
                if (operator == "10 ^") {
                    push_double(10.0)
                }
                compileInput(block.inputs.getValue("NUM"), CompileDataType.NUMBER)
                if (operator == "10 ^") {
                    invokestatic(Math::class, "pow", double, double, double)
                } else {
                    if (operator in DEG_TO_RAD_OPS) {
                        invokestatic(Math::class, "toRadians", double, double)
                    }
                    invokestatic(Math::class, REMAPPED_MATH_OPS[operator] ?: operator, double, double)
                    if (operator in RAD_TO_DEG_OPS) {
                        invokestatic(Math::class, "toDegrees", double, double)
                    }
                }
                if (type != CompileDataType.NUMBER) {
                    invokestatic(SCRATCH_ABI, "doubleToString", String::class, double)
                }
            }
            ScratchOpcodes.DATA_SETVARIABLETO -> {
                val variable = block.fields.getValue("VARIABLE")
                val isLocal = variable.id in target.variables
                var stageName = ""
                if (isLocal) {
                    aload_0
                } else {
                    stageName = escapePackageName("scratch", projectName, "target", project.stage.name)
                    getstatic(stageName, "INSTANCE", stageName)
                }
                compileInput(block.inputs.getValue("VALUE"))
                if (isLocal) {
                    putfield(
                        escapePackageName("scratch", projectName, "target", target.name),
                        escapeUnqualifiedName(variable.name),
                        String::class
                    )
                } else {
                    putfield(stageName, escapeUnqualifiedName(variable.name), String::class)
                }
            }
            ScratchOpcodes.DATA_CHANGEVARIABLEBY -> {
                val variable = block.fields.getValue("VARIABLE")
                val isLocal = variable.id in target.variables
                var stageName = ""
                if (isLocal) {
                    aload_0
                } else {
                    stageName = escapePackageName("scratch", projectName, "target", project.stage.name)
                    getstatic(stageName, "INSTANCE", stageName)
                }
                dup
                if (isLocal) {
                    getfield(
                        escapePackageName("scratch", projectName, "target", target.name),
                        escapeUnqualifiedName(variable.name),
                        String::class
                    )
                } else {
                    getfield(stageName, escapeUnqualifiedName(variable.name), String::class)
                }
                invokestatic(SCRATCH_ABI, "getNumber", double, String::class)
                compileInput(block.inputs.getValue("VALUE"), CompileDataType.NUMBER)
                dadd
                invokestatic(SCRATCH_ABI, "doubleToString", String::class, double)
                if (isLocal) {
                    putfield(
                        escapePackageName("scratch", projectName, "target", target.name),
                        escapeUnqualifiedName(variable.name),
                        String::class
                    )
                } else {
                    putfield(stageName, escapeUnqualifiedName(variable.name), String::class)
                }
            }
            ScratchOpcodes.DATA_ADDTOLIST -> {
                getList(block.fields.getValue("LIST"))
                compileInput(block.inputs.getValue("ITEM"))
                invokeinterface(List::class, "add", boolean, Any::class)
                pop
            }
            ScratchOpcodes.DATA_DELETEOFLIST -> {
                getList(block.fields.getValue("LIST"))
                compileInput(block.inputs.getValue("INDEX"), CompileDataType.NUMBER)
                invokestatic(SCRATCH_ABI, "deleteOfList", void, List::class, double)
            }
            ScratchOpcodes.DATA_DELETEALLOFLIST -> {
                getList(block.fields.getValue("LIST"))
                invokeinterface(List::class, "clear", void)
            }
            ScratchOpcodes.DATA_INSERTATLIST -> {
                getList(block.fields.getValue("LIST"))
                compileInput(block.inputs.getValue("INDEX"), CompileDataType.NUMBER)
                compileInput(block.inputs.getValue("ITEM"))
                invokestatic(SCRATCH_ABI, "insertAtList", void, List::class, double, String::class)
            }
            ScratchOpcodes.DATA_REPLACEITEMOFLIST -> {
                getList(block.fields.getValue("LIST"))
                compileInput(block.inputs.getValue("INDEX"), CompileDataType.NUMBER)
                compileInput(block.inputs.getValue("ITEM"))
                invokestatic(SCRATCH_ABI, "replaceItemOfList", void, List::class, double, String::class)
            }
            ScratchOpcodes.DATA_ITEMOFLIST -> {
                getList(block.fields.getValue("LIST"))
                compileInput(block.inputs.getValue("INDEX"), CompileDataType.NUMBER)
                invokestatic(SCRATCH_ABI, "itemOfList", String::class, List::class, double)
            }
            ScratchOpcodes.DATA_ITEMNUMOFLIST -> {
                getList(block.fields.getValue("LIST"))
                compileInput(block.inputs.getValue("ITEM"))
                invokeinterface(List::class, "indexOf", int, Any::class)
                iconst_1
                iadd
                coerceInt(type)
            }
            ScratchOpcodes.DATA_LENGTHOFLIST -> {
                getList(block.fields.getValue("LIST"))
                invokeinterface(List::class, "size", int)
                coerceInt(type)
            }
            else -> throw IllegalArgumentException("Don't know how to compile block ${block.opcode}")
        }
        block.next?.let { return compileBlock(it) }
    }

    private fun MethodAssembly.coerceInt(type: CompileDataType) = when (type) {
        CompileDataType.DEFAULT -> invokestatic(Integer::class, "toString", String::class, int)
        CompileDataType.NUMBER -> i2d
        CompileDataType.BOOLEAN -> {}
    }

    private fun MethodAssembly.compileCostume(costume: ScratchCostume) {
        construct(
            SCRATCH_COSTUME,
            void,
            String::class, String::class, SCRATCH_COSTUME_FORMAT, double, double, double
        ) {
            ldc(costume.name)
            ldc(costume.path)
            getstatic(SCRATCH_COSTUME_FORMAT, costume.format.name, SCRATCH_COSTUME_FORMAT)
            push_double(costume.centerX)
            push_double(costume.centerY)
            push_double(costume.coordinateScale)
        }
    }

    private fun newState(): Int {
        val index = stateIndex++
        if (index > maxStateIndex) {
            maxStateIndex = index
        }
        return index
    }

    private fun MethodAssembly.getState(index: Int) {
        aload_1
        getfield(SCHEDULED_JOB, "state", DoubleArray::class)
        push_int(index)
        daload
    }

    private inline fun MethodAssembly.setState(index: Int, state: () -> Unit) {
        aload_1
        getfield(SCHEDULED_JOB, "state", DoubleArray::class)
        push_int(index)
        state()
        dastore
    }

    private fun MethodAssembly.addAsyncLabel(): Int {
        val newIndex = labelIndex++
        +L[newIndex]
        return newIndex
    }

    private inline fun MethodAssembly.await(body: () -> Unit) {
        aload_1
        getstatic(SCRATCH_ABI, "SCHEDULER", ASYNC_SCHEDULER)
        aload_0
        body()
        invokevirtual(ASYNC_SCHEDULER, "scheduleJob", SCHEDULED_JOB, TARGET_BASE, SCHEDULED_JOB)
        putfield(SCHEDULED_JOB, "awaiting", SCHEDULED_JOB)
        push_int(labelIndex)
        ireturn
        addAsyncLabel()
    }
}

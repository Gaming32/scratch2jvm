package io.github.gaming32.scratch2jvm.compiler

import codes.som.koffee.MethodAssembly
import codes.som.koffee.assembleClass
import codes.som.koffee.insns.jvm.*
import codes.som.koffee.insns.sugar.construct
import codes.som.koffee.modifiers.final
import codes.som.koffee.modifiers.public
import codes.som.koffee.sugar.ClassAssemblyExtension.clinit
import codes.som.koffee.sugar.ClassAssemblyExtension.init
import codes.som.koffee.types.TypeLike
import io.github.gaming32.scratch2jvm.parser.ast.*
import io.github.gaming32.scratch2jvm.parser.data.ScratchProject
import io.github.gaming32.scratch2jvm.parser.data.ScratchTarget
import io.github.gaming32.scratch2jvm.parser.prettyPrint
import org.objectweb.asm.Handle
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

public class ScratchCompiler private constructor(
    private val projectName: String,
    private val project: ScratchProject
) {
    public companion object {
        public const val RUNTIME_PACKAGE: String = "io/github/gaming32/scratch2jvm/runtime"
        public const val SCRATCH_ABI: String = "$RUNTIME_PACKAGE/ScratchABI"
        public const val ASYNC_HANDLER: String = "$RUNTIME_PACKAGE/AsyncHandler"
        public const val ASYNC_SCHEDULER: String = "$RUNTIME_PACKAGE/AsyncScheduler"
        public const val TARGET_BASE: String = "$RUNTIME_PACKAGE/Target"
        public const val STAGE_BASE: String = "$RUNTIME_PACKAGE/Stage"
        public const val SPRITE_BASE: String = "$RUNTIME_PACKAGE/Sprite"
        public val USED_RUNTIME_CLASSES: List<String> = listOf(
            SCRATCH_ABI,
            ASYNC_HANDLER, ASYNC_SCHEDULER, "$ASYNC_SCHEDULER\$ScheduledJob",
            TARGET_BASE, STAGE_BASE, SPRITE_BASE
        )

        @JvmStatic
        public fun compile(projectName: String, project: ScratchProject): CompilationResult =
            ScratchCompiler(projectName, project).compile()
    }

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
                                    handle = Handle(
                                        Opcodes.H_INVOKESTATIC,
                                        "java/lang/invoke/LambdaMetafactory",
                                        "metafactory",
                                        "(Ljava/lang/invoke/MethodHandles\$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
                                        false
                                    ),
                                    args = arrayOf(
                                        Type.getMethodType(
                                            coerceType(int),
                                            coerceType(TARGET_BASE),
                                            coerceType(int)
                                        ),
                                        Handle(
                                            Opcodes.H_INVOKEVIRTUAL,
                                            className,
                                            escapeMethodName(block.id),
                                            "(I)I",
                                            false
                                        ),
                                        Type.getMethodType(
                                            coerceType(int),
                                            coerceType(className),
                                            coerceType(int)
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
                            invokespecial(superName, "<init>", void, String::class)
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
                                    iconst(list.value.size)
                                    invokestatic(
                                        SCRATCH_ABI, "loadListResource",
                                        List::class, Class::class, String::class, int
                                    )
                                } else {
                                    construct(ArrayList::class, void, int) {
                                        iconst(list.value.size)
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
                            iconst(target.currentCostume)
                            putfield(TARGET_BASE, "costume", int)
                            aload_0
                            dconst(target.volume)
                            putfield(TARGET_BASE, "volume", double)
                            aload_0
                            iconst(target.layerOrder)
                            putfield(TARGET_BASE, "layerOrder", int)
                            if (target.isStage) {
                                aload_0
                                dconst(target.tempo)
                                putfield(STAGE_BASE, "tempo", double)
                            } else {
                                aload_0
                                dconst(target.x)
                                putfield(SPRITE_BASE, "x", double)
                                aload_0
                                dconst(target.y)
                                putfield(SPRITE_BASE, "y", double)
                                aload_0
                                dconst(target.size)
                                putfield(SPRITE_BASE, "size", double)
                                aload_0
                                dconst(target.direction)
                                putfield(SPRITE_BASE, "direction", double)
                                aload_0
                                if (target.draggable) {
                                    iconst_1
                                } else {
                                    iconst_0
                                }
                                putfield(SPRITE_BASE, "draggable", boolean)
                                aload_0
                                iconst(target.rotationStyle.toInt())
                                putfield(SPRITE_BASE, "rotationStyle", byte)
                            }
                            _return
                        }

                        if (!target.isStage) {
                            method(private, "<init>", void, className) {
                                aload_0
                                ldc(target.name)
                                invokespecial(superName, "<init>", void, String::class)
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
                            println(block.prettyPrint())
                            method(public, escapeMethodName(block.id), int, int) {
                                compileBlock(project.stage, target, block)
                                iconst(SUSPEND_NO_RESCHEDULE)
                                ireturn
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
                                iconst(eventType)
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
                    init(private) {
                        _return
                    }

                    method(public + static, "main", void, Array<String>::class) {
                        getstatic(SCRATCH_ABI, "SCHEDULER", ASYNC_SCHEDULER)
                        for (target in project.targets.values.sortedBy { it.layerOrder }) {
                            val className = escapePackageName("scratch", projectName, "target", target.name)
                            dup
                            getstatic(className, "INSTANCE", className)
                            invokevirtual(ASYNC_SCHEDULER, "addTarget", void, TARGET_BASE)
                        }
                        dup
                        iconst(EVENT_FLAG_CLICKED)
                        invokevirtual(ASYNC_SCHEDULER, "scheduleEvent", void, int)
                        invokevirtual(ASYNC_SCHEDULER, "runUntilComplete", void)
                        _return
                    }
                })
            },
            mainClassName, resources
        )
    }

    private tailrec fun MethodAssembly.compileInput(
        stage: ScratchTarget,
        target: ScratchTarget,
        input: ScratchInput<*>
    ): Unit = when (input.type) {
        ScratchInputTypes.FALLBACK -> compileInput(stage, target, (input as FallbackInput<*, *>).primary)
        ScratchInputTypes.VALUE -> ldc((input as ValueInput).value)
        ScratchInputTypes.VARIABLE -> {
            val variable = (input as VariableInput).value
            if (variable.name in target.variables) {
                aload_0
                getfield(
                    escapePackageName("scratch", projectName, "target", target.name),
                    escapeUnqualifiedName(variable.name),
                    String::class
                )
            } else {
                val stageName = escapePackageName("scratch", projectName, "target", stage.name)
                getstatic(stageName, "INSTANCE", stageName)
                getfield(stageName, escapeUnqualifiedName(variable.name), String::class)
            }
        }
        else -> throw IllegalArgumentException("Don't know how to compile input ${input.type} yet")
    }

    private tailrec fun MethodAssembly.compileBlock(stage: ScratchTarget, target: ScratchTarget, block: ScratchBlock) {
        when (block.opcode) {
            ScratchOpcodes.EVENT_WHENFLAGCLICKED -> {}
            ScratchOpcodes.LOOKS_SAY -> {
                aload_0
                compileInput(stage, target, block.inputs.getValue("MESSAGE"))
                invokestatic(SCRATCH_ABI, "say", void, TARGET_BASE, String::class)
            }
            else -> throw IllegalArgumentException("Don't know how to compile block ${block.opcode} yet")
        }
        block.next?.let { return compileBlock(stage, target, it) }
    }
}

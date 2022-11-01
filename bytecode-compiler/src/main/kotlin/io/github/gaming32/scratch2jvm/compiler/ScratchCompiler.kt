package io.github.gaming32.scratch2jvm.compiler

import codes.som.koffee.assembleClass
import codes.som.koffee.insns.jvm.*
import codes.som.koffee.insns.sugar.construct
import codes.som.koffee.modifiers.final
import codes.som.koffee.modifiers.public
import codes.som.koffee.sugar.ClassAssemblyExtension.init
import io.github.gaming32.scratch2jvm.parser.data.ScratchProject
import org.objectweb.asm.tree.ClassNode

public class ScratchCompiler private constructor(
    private val projectName: String,
    private val project: ScratchProject
) {
    public companion object {
        @JvmStatic
        public fun compile(projectName: String, project: ScratchProject): Pair<List<ClassNode>, String> = Pair(
            ScratchCompiler(projectName, project).compile(),
            escapePackageName("scratch", projectName, "Main")
        )
    }

    private fun compile() = buildList {
        for (target in project.targets.values) {
            val className = escapePackageName("scratch", projectName, "target", target.name)
            add(assembleClass(public + final, className) {
                for (variable in target.variables.values) {
                    field(public, escapeUnqualifiedName(variable.id), String::class, value = variable.value)
                }

                for (list in target.lists.values) {
                    field(public, escapeUnqualifiedName(list.id), List::class)
                }

                init(private) {
                    for (list in target.lists.values) {
                        aload_0
                        construct(ArrayList::class, void, Int::class) {
                            iconst(list.value.size)
                        }
                        for (element in list.value) {
                            dup
                            ldc(element)
                            invokeinterface(List::class, "add", Boolean::class, Any::class)
                            pop
                        }
                        putfield(className, escapeUnqualifiedName(list.id), List::class)
                    }
                    _return
                }

                init(public, className) {
                    for (variable in target.variables.values) {
                        val id = escapeUnqualifiedName(variable.id)
                        aload_0
                        aload_1
                        getfield(className, id, String::class)
                        putfield(className, id, String::class)
                    }
                    for (list in target.lists.values) {
                        val id = escapeUnqualifiedName(list.id)
                        aload_0
                        construct(ArrayList::class, void, Collection::class) {
                            aload_1
                            getfield(className, id, List::class)
                        }
                        putfield(className, id, List::class)
                    }
                    _return
                }
            })
        }

        add(assembleClass(public + final, escapePackageName("scratch", projectName, "Main")) {
            init(private) {
                _return
            }

            method(public + static, "main", void, Array<String>::class) {
                _return
            }
        })
    }
}

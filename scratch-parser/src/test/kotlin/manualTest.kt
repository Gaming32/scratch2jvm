import io.github.gaming32.scratch2jvm.parser.ScratchProjectFile
import java.io.File

fun main() {
    val project = ScratchProjectFile.open(File(object {}::class.java.getResource("test.sb3")!!.toURI()))
//    println(project.project.meta)
//    println(project.project.monitors)
//    println(project.project.targets)
    println(project.project.targets.values.asSequence().filterIndexed { index, _ -> index == 1 }.first().rootBlocks)
}

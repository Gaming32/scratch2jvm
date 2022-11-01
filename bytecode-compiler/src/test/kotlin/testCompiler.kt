import io.github.gaming32.scratch2jvm.compiler.ScratchCompiler
import io.github.gaming32.scratch2jvm.parser.ScratchProjectFile
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.util.CheckClassAdapter
import java.io.File
import java.io.PrintWriter

fun main() {
    val project = ScratchProjectFile.open(File(object {}::class.java.getResource("test.sb3")!!.toURI()))
    val (classes, mainName) = ScratchCompiler.compile("test", project.project)
    for (clazz in classes) {
        val writer = ClassWriter(ClassWriter.COMPUTE_FRAMES)
        clazz.accept(writer)
        CheckClassAdapter.verify(ClassReader(writer.toByteArray()), true, PrintWriter(System.err))
    }
}

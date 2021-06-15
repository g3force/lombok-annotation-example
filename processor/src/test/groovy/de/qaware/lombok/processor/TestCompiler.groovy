package de.qaware.lombok.processor

import com.google.testing.compile.Compiler
import com.google.testing.compile.Compilation
import com.google.testing.compile.JavaFileObjects
import lombok.javac.apt.LombokProcessor
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.util.TraceClassVisitor

import javax.tools.JavaFileObject
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import static com.google.testing.compile.Compiler.javac
import static javax.tools.StandardLocation.CLASS_OUTPUT

/**
 * Helper class that compiles java code to byte code, with and without lombok processor enabled.
 * Largely taken from https://github.com/sympower/symbok/blob/master/src/test/java/net/sympower/symbok/SymbokTest.java
 */
class TestCompiler {
    private static final String TEST_DATA_OUTPUT_PATH = "test-out"

    static String compileWithLombok(String prefix, String className) throws IOException {
        return compile(prefix, className, javac().withProcessors(new LombokProcessor()))
    }

    static String compileExpected(String className) throws IOException {
        return compile("expected", className, javac())
    }

    static String compileGiven(String className) throws IOException {
        compileWithLombok("given", className)
    }

    private static String compile(String prefix, String className, Compiler compiler) throws IOException {
        // Use '-g:none' to omit line numbers because lombok generates new code without source line numbers
        // and it messes up diff checker
        Compilation compilation = compiler
            .withOptions("-g:none", "-Xdiags:verbose")
            .compile(JavaFileObjects.forResource(String.join("/", prefix, className + ".java")))
        Optional<JavaFileObject> javaFileObject = compilation
            .generatedFile(CLASS_OUTPUT, "test", className + ".class")

        if (javaFileObject.isPresent()) {
            byte[] bytes = IOUtils.toByteArray(javaFileObject.get().openInputStream())

            String buildDir = System.getProperty("gradleBuildDir", "build")

            Path outputPath = Paths.get(buildDir, TEST_DATA_OUTPUT_PATH, prefix)
            Files.createDirectories(outputPath)

            Path outputFile = Paths.get(buildDir, TEST_DATA_OUTPUT_PATH, prefix, className + ".class")
            IOUtils.write(bytes, new FileOutputStream(outputFile.toFile()))
            return disassemble(bytes)
        }
        else {
            throw new IllegalStateException("Cannot find output")
        }
    }

    private static String disassemble(byte[] classBytes) throws IOException {
        ClassReader cr = new ClassReader(new ByteArrayInputStream(classBytes))
        new StringWriter().withCloseable {out ->
            new PrintWriter(out).withCloseable {printWriter ->
                cr.accept(new TraceClassVisitor(printWriter), 0)
                printWriter.flush()
                return out.toString()
            }
        }
    }
}

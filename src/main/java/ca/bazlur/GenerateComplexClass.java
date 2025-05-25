package ca.bazlur;

import java.io.IOException;
import java.lang.classfile.ClassFile;
import java.lang.constant.MethodTypeDesc;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.classfile.ClassFile.*;
import static java.lang.constant.ClassDesc.*;
import static java.lang.constant.ConstantDescs.*;

/**
 * Demonstrates a more complex example of the Class-File API (JEP 484).
 * Generates a class with multiple methods that performs mathematical operations.
 */
public final class GenerateComplexClass {
    public static void run(String outputPath) {
        System.out.println("=== Class-File API Demo (Generate Complex Class) ===");

        try {
            // Determine the output path
            Path path;
            if (outputPath.endsWith(".class")) {
                path = Paths.get(outputPath);
            } else {
                // If the path doesn't end with .class, assume it's a directory and add MathUtil.class
                path = Paths.get(outputPath, "MathUtil.class");
            }

            // Ensure parent directories exist
            Files.createDirectories(path.getParent());

            // Build the class model via the fluent builder DSL
            ClassFile.of().buildTo(path, of("MathUtil"), classBuilder -> {
                // Add add method
                classBuilder.withMethodBody("add", MethodTypeDesc.of(CD_int, CD_int, CD_int),
                    ACC_PUBLIC | ACC_STATIC, codeBuilder -> codeBuilder
                        .iload(0)
                        .iload(1)
                        .iadd()
                        .ireturn());

                // Add subtract method
                classBuilder.withMethodBody("subtract", MethodTypeDesc.of(CD_int, CD_int, CD_int),
                    ACC_PUBLIC | ACC_STATIC, codeBuilder -> codeBuilder
                        .iload(0)
                        .iload(1)
                        .isub()
                        .ireturn());

                // Add multiply method
                classBuilder.withMethodBody("multiply", MethodTypeDesc.of(CD_int, CD_int, CD_int),
                    ACC_PUBLIC | ACC_STATIC, codeBuilder -> codeBuilder
                        .iload(0)
                        .iload(1)
                        .imul()
                        .ireturn());

                // Add square method
                classBuilder.withMethodBody("square", MethodTypeDesc.of(CD_int, CD_int),
                    ACC_PUBLIC | ACC_STATIC, codeBuilder -> codeBuilder
                        .iload(0)
                        .iload(0)
                        .imul()
                        .ireturn());

                // Add cube method
                classBuilder.withMethodBody("cube", MethodTypeDesc.of(CD_int, CD_int),
                    ACC_PUBLIC | ACC_STATIC, codeBuilder -> codeBuilder
                        .iload(0)
                        .iload(0)
                        .imul()
                        .iload(0)
                        .imul()
                        .ireturn());

                // Add main method to demonstrate usage
                classBuilder.withMethodBody("main", MethodTypeDesc.of(CD_void, CD_String.arrayType()),
                    ACC_PUBLIC | ACC_STATIC, codeBuilder -> codeBuilder
                        // Print header
                        .getstatic(of("java.lang.System"), "out", of("java.io.PrintStream"))
                        .ldc("MathUtil Demo")
                        .invokevirtual(of("java.io.PrintStream"), "println",
                            MethodTypeDesc.of(CD_void, CD_String))

                        // Demonstrate add
                        .getstatic(of("java.lang.System"), "out", of("java.io.PrintStream"))
                        .new_(of("java.lang.StringBuilder"))
                        .dup()
                        .invokespecial(of("java.lang.StringBuilder"), "<init>", MethodTypeDesc.of(CD_void))
                        .ldc("5 + 3 = ")
                        .invokevirtual(of("java.lang.StringBuilder"), "append", MethodTypeDesc.of(of("java.lang.StringBuilder"), CD_String))
                        .iconst_5()
                        .iconst_3()
                        .invokestatic(of("MathUtil"), "add", MethodTypeDesc.of(CD_int, CD_int, CD_int))
                        .invokevirtual(of("java.lang.StringBuilder"), "append", MethodTypeDesc.of(of("java.lang.StringBuilder"), CD_int))
                        .invokevirtual(of("java.lang.StringBuilder"), "toString", MethodTypeDesc.of(CD_String))
                        .invokevirtual(of("java.io.PrintStream"), "println",
                            MethodTypeDesc.of(CD_void, CD_String))

                        // Demonstrate subtract
                        .getstatic(of("java.lang.System"), "out", of("java.io.PrintStream"))
                        .new_(of("java.lang.StringBuilder"))
                        .dup()
                        .invokespecial(of("java.lang.StringBuilder"), "<init>", MethodTypeDesc.of(CD_void))
                        .ldc("10 - 4 = ")
                        .invokevirtual(of("java.lang.StringBuilder"), "append", MethodTypeDesc.of(of("java.lang.StringBuilder"), CD_String))
                        .bipush(10)
                        .iconst_4()
                        .invokestatic(of("MathUtil"), "subtract", MethodTypeDesc.of(CD_int, CD_int, CD_int))
                        .invokevirtual(of("java.lang.StringBuilder"), "append", MethodTypeDesc.of(of("java.lang.StringBuilder"), CD_int))
                        .invokevirtual(of("java.lang.StringBuilder"), "toString", MethodTypeDesc.of(CD_String))
                        .invokevirtual(of("java.io.PrintStream"), "println",
                            MethodTypeDesc.of(CD_void, CD_String))

                        // Demonstrate multiply
                        .getstatic(of("java.lang.System"), "out", of("java.io.PrintStream"))
                        .new_(of("java.lang.StringBuilder"))
                        .dup()
                        .invokespecial(of("java.lang.StringBuilder"), "<init>", MethodTypeDesc.of(CD_void))
                        .ldc("6 * 7 = ")
                        .invokevirtual(of("java.lang.StringBuilder"), "append", MethodTypeDesc.of(of("java.lang.StringBuilder"), CD_String))
                        .bipush(6)
                        .bipush(7)
                        .invokestatic(of("MathUtil"), "multiply", MethodTypeDesc.of(CD_int, CD_int, CD_int))
                        .invokevirtual(of("java.lang.StringBuilder"), "append", MethodTypeDesc.of(of("java.lang.StringBuilder"), CD_int))
                        .invokevirtual(of("java.lang.StringBuilder"), "toString", MethodTypeDesc.of(CD_String))
                        .invokevirtual(of("java.io.PrintStream"), "println",
                            MethodTypeDesc.of(CD_void, CD_String))

                        // Demonstrate square
                        .getstatic(of("java.lang.System"), "out", of("java.io.PrintStream"))
                        .new_(of("java.lang.StringBuilder"))
                        .dup()
                        .invokespecial(of("java.lang.StringBuilder"), "<init>", MethodTypeDesc.of(CD_void))
                        .ldc("square(4) = ")
                        .invokevirtual(of("java.lang.StringBuilder"), "append", MethodTypeDesc.of(of("java.lang.StringBuilder"), CD_String))
                        .iconst_4()
                        .invokestatic(of("MathUtil"), "square", MethodTypeDesc.of(CD_int, CD_int))
                        .invokevirtual(of("java.lang.StringBuilder"), "append", MethodTypeDesc.of(of("java.lang.StringBuilder"), CD_int))
                        .invokevirtual(of("java.lang.StringBuilder"), "toString", MethodTypeDesc.of(CD_String))
                        .invokevirtual(of("java.io.PrintStream"), "println",
                            MethodTypeDesc.of(CD_void, CD_String))

                        // Demonstrate cube
                        .getstatic(of("java.lang.System"), "out", of("java.io.PrintStream"))
                        .new_(of("java.lang.StringBuilder"))
                        .dup()
                        .invokespecial(of("java.lang.StringBuilder"), "<init>", MethodTypeDesc.of(CD_void))
                        .ldc("cube(3) = ")
                        .invokevirtual(of("java.lang.StringBuilder"), "append", MethodTypeDesc.of(of("java.lang.StringBuilder"), CD_String))
                        .iconst_3()
                        .invokestatic(of("MathUtil"), "cube", MethodTypeDesc.of(CD_int, CD_int))
                        .invokevirtual(of("java.lang.StringBuilder"), "append", MethodTypeDesc.of(of("java.lang.StringBuilder"), CD_int))
                        .invokevirtual(of("java.lang.StringBuilder"), "toString", MethodTypeDesc.of(CD_String))
                        .invokevirtual(of("java.io.PrintStream"), "println",
                            MethodTypeDesc.of(CD_void, CD_String))

                        .return_());
            });

            System.out.println("Class written to " + path.toAbsolutePath());
            System.out.println("\nGenerated a MathUtil class with multiple mathematical methods");
            System.out.println("You can run it with: java -cp " + path.getParent() + " MathUtil");

        } catch (IOException e) {
            System.err.println("Error writing class file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error generating class file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Throwable {
        if (args.length > 0) {
            run(args[0]);
        } else {
            run("MathUtil.class");
        }
    }
}

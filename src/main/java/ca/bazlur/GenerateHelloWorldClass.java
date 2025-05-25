package ca.bazlur;

import java.io.IOException;
import java.lang.classfile.ClassFile;
import java.lang.constant.MethodTypeDesc;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.constant.ConstantDescs.CD_void;
import static java.lang.constant.ClassDesc.*;
import static java.lang.classfile.ClassFile.*;

public final class GenerateHelloWorldClass {
	public static void run(String outputPath) {
		System.out.println("=== Class-File API Demo (Generate HelloWorld) ===");

		try {
			// Determine the output path
			Path path;
			if (outputPath.endsWith(".class")) {
				path = Paths.get(outputPath);
			} else {
				// If the path doesn't end with .class, assume it's a directory and add HelloWorld.class
				path = Paths.get(outputPath, "HelloWorld.class");
			}

			// Ensure parent directories exist
			Files.createDirectories(path.getParent());

			// Build the class model via the fluent builder DSL
			ClassFile.of().buildTo(path, of("HelloWorld"), classBuilder -> classBuilder
					.withMethodBody("main", MethodTypeDesc.of(CD_void, of("java.lang.String").arrayType()),
							ACC_PUBLIC | ACC_STATIC, codeBuilder -> codeBuilder
									.getstatic(of("java.lang.System"), "out", of("java.io.PrintStream"))
									.ldc("Hello World")
									.invokevirtual(of("java.io.PrintStream"), "println",
											MethodTypeDesc.of(CD_void, of("java.lang.Object")))
									.return_()));

			System.out.println("Class written to " + path.toAbsolutePath());
			System.out.println("\nGenerated a HelloWorld class with a main method that prints 'Hello World'");
			System.out.println("You can run it with: java -cp " + path.getParent() + " HelloWorld");

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
			run("HelloWorld.class");
		}
	}
}
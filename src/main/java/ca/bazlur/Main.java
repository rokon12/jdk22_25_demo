package ca.bazlur;

import ca.bazlur.concurrent.DataFetcher;
import ca.bazlur.concurrent.DeadlineDemo;
import ca.bazlur.concurrent.FirstWinDemo;
import ca.bazlur.concurrent.HeatMapBuilder;
import ca.bazlur.concurrent.NestedScopesDemo;
import ca.bazlur.gatherers.GathererDemo;

/**
 * CLI parser & orchestrator for JDK 22-25 feature demos.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        try {
            switch (args[0]) {
                case "--concurrent" -> runConcurrentDemo();
                case "--gather" -> GathererDemo.run();
                case "--scoped" -> ScopedValueDemo.run();
                case "--primitive" -> PrimitivePatternDemo.run();
                case "--native" -> {
                    if (args.length < 2) {
                        System.out.println("Error: --native requires a string argument");
                        printUsage();
                        return;
                    }
                    NativeLibraryDemo.run(args[1]);
                }
                case "--generate" -> {
                    if (args.length < 2) {
                        System.out.println("Error: --generate requires an output path");
                        printUsage();
                        return;
                    }
                    GenerateHelloWorldClass.run(args[1]);
                }
                case "--generate-complex" -> {
                    if (args.length < 2) {
                        System.out.println("Error: --generate-complex requires an output path");
                        printUsage();
                        return;
                    }
                    GenerateComplexClass.run(args[1]);
                }
                case "--help" -> printUsage();
                default -> {
                    System.out.println("Unknown command: " + args[0]);
                    printUsage();
                }
            }
        } catch (Exception e) {
            System.err.println("Error executing demo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void runConcurrentDemo() throws Exception {
        System.out.println("=== Running Structured Concurrency Demos ===");

        System.out.println("\n1. ShutdownOnFailure scope (DataFetcher):");
        DataFetcher.run();

        System.out.println("\n2. ContinueOnFailure scope (HeatMapBuilder):");
        HeatMapBuilder.run();

        System.out.println("\n3. Deadline-capped scope (DeadlineDemo):");
        DeadlineDemo.run();

        System.out.println("\n4. FirstWin scope (FirstWinDemo):");
        FirstWinDemo.run();

        System.out.println("\n5. Nested Scopes demo (NestedScopesDemo):");
        NestedScopesDemo.run();
    }

    private static void printUsage() {
        System.out.println("""
            JDK 22-25 Feature-Rich CLI Showcase

            Usage: java --enable-preview --enable-native-access=ALL-UNNAMED -jar jdk22-25-cli-demo.jar [COMMAND]

            Commands:
              --concurrent     Run structured concurrency demos
              --gather         Run stream gatherers demo
              --scoped         Run scoped values demo
              --primitive      Run primitive pattern switch demo
              --native TEXT    Run FFM API demo with TEXT as input
              --generate PATH  Generate a HelloWorld class file at the specified path
              --generate-complex PATH  Generate a MathUtil class with multiple methods
              --help           Show this help message
            """);
    }
}

package ca.bazlur;


import ca.bazlur.concurrency.A_ConcurrencyDemo;
import ca.bazlur.gatherers.GathererDemo;
import ca.bazlur.model.DataPoint;
import ca.bazlur.model.UserInputTask;
import ca.bazlur.scopevalue.ScopedValueDemo;
import ca.bazlur.stablevalue.StableValuesDemo;
import ca.bazlur.SequencedCollectionsDemo;
import ca.bazlur.StatementsBeforeSuperDemo;
import ca.bazlur.VectorApiDemo;

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
                case "--concurrent" -> A_ConcurrencyDemo.run();
                case "--gather" -> GathererDemo.run();
                case "--scoped" -> ScopedValueDemo.run();
                case "--primitive" -> PrimitivePatternDemo.run();
                case "--native" -> NativeLibraryDemo.run();
                case "--stable" -> StableValuesDemo.run();
                case "--sequenced" -> SequencedCollectionsDemo.run();
                case "--presuper" -> StatementsBeforeSuperDemo.run();
                case "--vector" -> VectorApiDemo.run();
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
                case "--model" -> runModelDemo();
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

    private static void runModelDemo() {
        System.out.println("=== Running Model Class Demos ===");

        // Demonstrate DataPoint record patterns and unnamed patterns
        System.out.println("\n1. DataPoint record patterns and unnamed patterns:");

        // Create a sample DataPoint
        DataPoint samplePoint = DataPoint.createSample();
        System.out.println("Sample DataPoint: " + samplePoint);

        // Demonstrate summarize method (uses record pattern matching with unnamed patterns)
        System.out.println("\nSummarized DataPoint:");
        System.out.println(DataPoint.summarize(samplePoint));

        // Demonstrate categorize method (uses record pattern matching in switch expressions)
        System.out.println("\nCategorized DataPoint:");
        System.out.println(DataPoint.categorize(samplePoint));

        // Create a sequential DataPoint (with a previous point)
        DataPoint sequentialPoint = DataPoint.createSequential();
        System.out.println("\nSequential DataPoint:");
        System.out.println(DataPoint.summarize(sequentialPoint));
        System.out.println(DataPoint.categorize(sequentialPoint));

        // Demonstrate UserInputTask flexible constructor bodies
        System.out.println("\n2. UserInputTask flexible constructor bodies:");

        // Create a sample UserInputTask
        UserInputTask sampleTask = UserInputTask.createSample();

        // Execute the task
        System.out.println("\nExecuting UserInputTask:");
        sampleTask.execute();
    }

    private static void printUsage() {
        System.out.println("""
            JDK 22-25 Feature-Rich CLI Showcase

            Usage: java --enable-preview --enable-native-access=ALL-UNNAMED -jar jdk22-25-cli-demo.jar [COMMAND]

            Commands:
              --concurrent          Run structured concurrency demos
              --gather              Run stream gatherers demo
              --scoped              Run scoped values demo
              --primitive           Run primitive pattern switch demo
              --native              Run FFM API demo with TEXT as input
              --stable              Run stable values demo
              --sequenced           Run sequenced collections demo
              --presuper            Run statements-before-super demo
              --vector             Run Vector API demo
              --generate PATH       Generate a HelloWorld class file at the specified path
              --generate-complex    PATH  Generate a MathUtil class with multiple methods
              --model               Run model class demos (record patterns, unnamed patterns, flexible constructor bodies)
              --help                Show this help message
            """);
    }
}

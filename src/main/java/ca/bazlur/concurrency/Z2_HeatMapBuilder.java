package ca.bazlur.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Demonstrates structured concurrency with ContinueOnFailure scope.
 * This scope continues executing all subtasks even if some of them fail.
 */
public class Z2_HeatMapBuilder {

    public static void run() throws Exception {
        List<String> regions = List.of("North", "South", "East", "West", "Central");

        System.out.println("Building heat map from " + regions.size() + " regions...");
        var results = processRegions(regions);

        System.out.println("Heat map built with " + results.size() + " successful regions:");
        results.forEach(System.out::println);

        if (results.size() < regions.size()) {
            System.out.println("Note: " + (regions.size() - results.size()) + 
                               " regions failed to process but we continued with the available data.");
        }
    }

    private static List<String> processRegions(List<String> regions) throws InterruptedException {
        List<String> results = new ArrayList<>();

        // Using structured concurrency with a custom scope that continues on failure
        try (var scope = StructuredTaskScope.open(StructuredTaskScope.Joiner.awaitAll())) {
            // Fork subtasks for each region
            List<Subtask<String>> futures = regions.stream()
                .map(region -> scope.fork(() -> processRegion(region)))
                .toList();

            // Wait for all subtasks to complete (success or failure)
            scope.join();

            // Collect results from successful subtasks only
            for (var future : futures) {
                try {
                    String result = future.get();
                    results.add(result);
                } catch (Exception e) {
                    System.out.println("Skipping failed region: " + e.getMessage());
                }
            }
        }

        return results;
    }

    private static String processRegion(String region) {
        // Simulate processing time
        try {
            int processingTime = ThreadLocalRandom.current().nextInt(100, 300);
            Thread.sleep(processingTime);

            // Simulate random failures (20% chance)
            if (ThreadLocalRandom.current().nextInt(5) == 0) {
                throw new RuntimeException("Failed to process region: " + region);
            }

            return "Heat data for " + region + " region (processed in " + processingTime + "ms)";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Processing interrupted", e);
        }
    }
}
package demo.concurrent;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.ShutdownOnFailure;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Demonstrates structured concurrency with ShutdownOnFailure scope.
 * This scope cancels all subtasks if any one of them fails.
 */
public class DataFetcher {

    public static void run() throws Exception {
        List<String> results = fetchDataFromMultipleSources();
        System.out.println("Successfully fetched data from all sources:");
        results.forEach(result -> System.out.println("  - " + result));
    }

    private static List<String> fetchDataFromMultipleSources() throws Exception {
        List<String> dataSources = List.of("database", "api", "cache", "file-system");
        List<String> results = new ArrayList<>();

        // Using structured concurrency with ShutdownOnFailure scope
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            // Fork subtasks for each data source
            var futures = dataSources.stream()
                .map(source -> scope.fork(() -> fetchData(source)))
                .toList();

            // Wait for all subtasks to complete or for one to fail
            scope.join();

            // If any subtask failed, propagate the exception
            scope.throwIfFailed(e -> new RuntimeException("Data fetching failed", e));

            // Collect results from successful subtasks
            for (var future : futures) {
                results.add(future.get());
            }
        }

        return results;
    }

    private static String fetchData(String source) {
        try {
            // Simulate network delay
            int delay = ThreadLocalRandom.current().nextInt(100, 500);
            Thread.sleep(delay);

            // Uncomment to simulate random failures
            // if (ThreadLocalRandom.current().nextInt(10) < 2) {
            //     throw new RuntimeException("Failed to fetch data from " + source);
            // }

            return "Data from " + source + " (took " + delay + "ms)";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Fetch operation interrupted", e);
        }
    }
}

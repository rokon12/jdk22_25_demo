package ca.bazlur.concurrency;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;

/**
 * Demonstrates structured concurrency with a deadline.
 * This example shows how to set a timeout for all subtasks.
 */
public class Z3_DeadlineDemo {

    private static final long DEADLINE_MS = 500;

    public static void run() throws Exception {
        System.out.println("Starting deadline-capped operations (timeout: " + DEADLINE_MS + "ms)");

        Instant start = Instant.now();
        List<String> results = runWithDeadline();
        Duration elapsed = Duration.between(start, Instant.now());

        System.out.println("Completed in " + elapsed.toMillis() + "ms");
        System.out.println("Results collected: " + results.size());
        results.forEach(result -> System.out.println("  - " + result));
    }

    private static List<String> runWithDeadline() throws Exception {
        List<String> operations = List.of(
                "query database", "call external API", "process images",
                "generate report", "validate data", "backup files"
        );

        List<String> results = new ArrayList<>();
        Duration deadline = Duration.ofMillis(DEADLINE_MS);

        try (var scope = StructuredTaskScope.open(StructuredTaskScope.Joiner.anySuccessfulResultOrThrow(),
                cfg -> cfg.withTimeout(deadline))) {
            // Fork all operations as subtasks
            var futures = operations.stream()
                    .map(op -> scope.fork(() -> performOperation(op)))
                    .toList();

            // Wait until all tasks complete or deadline is reached
            scope.join();

            // Collect results from completed operations
            for (var future : futures) {
                if (future.state() == StructuredTaskScope.Subtask.State.SUCCESS) {
                    results.add(future.get());
                } else if (future.state() == StructuredTaskScope.Subtask.State.FAILED) {
                    System.out.println("Operation failed: " + future.exception().getMessage());
                } else {
                    System.out.println("Operation cancelled due to deadline");
                }
            }
        }

        return results;
    }

    private static String performOperation(String operation) {
        try {
            // Simulate varying operation times (100-800ms)
            int operationTime = ThreadLocalRandom.current().nextInt(100, 800);
            System.out.println("Starting: " + operation + " (will take " + operationTime + "ms)");

            Thread.sleep(operationTime);

            // Simulate occasional failures
            if (ThreadLocalRandom.current().nextInt(10) == 0) {
                throw new RuntimeException("Failed during " + operation);
            }

            return "Completed: " + operation + " in " + operationTime + "ms";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Interrupted: " + operation);
            throw new RuntimeException("Operation interrupted: " + operation);
        }
    }
}
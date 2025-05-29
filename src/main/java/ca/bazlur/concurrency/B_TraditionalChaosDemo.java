package ca.bazlur.concurrency;

import io.micrometer.core.annotation.Timed;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Demonstrates the problems with traditional concurrent programming using ExecutorService.
 * <p>
 * This is the "before" example showing the chaos of unstructured concurrency.
 */
public class B_TraditionalChaosDemo {

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    @Timed("order-processing")
    public static void run() {
        System.out.println("Demonstrating traditional concurrent chaos with ExecutorService...");

        try {
            // Process an order using traditional concurrency
            Response response = processOrder();
            System.out.println("Order processed successfully: " + response);

            // Demonstrate thread leaks by simulating a failure
            System.out.println("\nSimulating a failure scenario (watch for thread leaks):");
            try {
                Response failedResponse = processOrderWithFailure();
                System.out.println("This should not be reached due to failure");
            } catch (Exception e) {
                System.out.println("Order processing failed: " + e.getMessage());
                System.out.println("Note: Threads may have been leaked in the process!");
            }

        } catch (Exception e) {
            System.err.println("Error in demo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // The old way - ExecutorService nightmare
    private static Response processOrder() throws ExecutionException, InterruptedException {
        Future<User> userFuture = executor.submit(() -> fetchUser());
        Future<Inventory> inventoryFuture = executor.submit(() -> checkInventory());
        Future<Payment> paymentFuture = executor.submit(() -> processPayment());

        User user = userFuture.get();        // What if this fails?
        Inventory inventory = inventoryFuture.get();  // Thread leaks!
        Payment payment = paymentFuture.get(); // Cancellation chaos!

        return new Response(user, inventory, payment);
    }

    // Version that simulates a failure to demonstrate thread leaks
    private static Response processOrderWithFailure() throws ExecutionException, InterruptedException {
        Future<User> userFuture = executor.submit(() -> fetchUser());
        Future<Inventory> inventoryFuture = executor.submit(() -> checkInventory());
        Future<Payment> paymentFuture = executor.submit(() -> {
            // This will throw an exception
            throw new RuntimeException("Payment processing failed");
        });

        User user = userFuture.get();
        // The following line will never be reached if payment fails, potentially leaking threads
        Inventory inventory = inventoryFuture.get();
        Payment payment = paymentFuture.get(); // This will throw ExecutionException

        return new Response(user, inventory, payment);
    }

    // Simulated domain methods
    private static User fetchUser() throws InterruptedException {
        Thread.sleep(ThreadLocalRandom.current().nextInt(100, 300));
        return new User("user123", "John Doe");
    }

    private static Inventory checkInventory() throws InterruptedException {
        Thread.sleep(ThreadLocalRandom.current().nextInt(200, 500));
        return new Inventory("product456", 10);
    }

    private static Payment processPayment() throws InterruptedException {
        Thread.sleep(ThreadLocalRandom.current().nextInt(150, 400));
        return new Payment("payment789", 99.99);
    }

    // Domain model classes
    record User(String id, String name) {
    }

    record Inventory(String productId, int quantity) {
    }

    record Payment(String transactionId, double amount) {
    }

    record Response(User user, Inventory inventory, Payment payment) {
    }
}
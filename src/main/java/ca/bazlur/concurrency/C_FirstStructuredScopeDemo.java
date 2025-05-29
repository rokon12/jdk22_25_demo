package ca.bazlur.concurrency;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Demonstrates the solution to the problems shown in the B_TraditionalChaosDemo
 * using structured concurrency.
 * 
 * This is the "after" example showing how structured concurrency provides
 * a cleaner, safer approach.
 */
public class C_FirstStructuredScopeDemo {

    public static void run() {
        System.out.println("Demonstrating structured concurrency with your first StructuredTaskScope...");
        
        try {
            // Process an order using structured concurrency
            Response response = processOrder();
            System.out.println("Order processed successfully: " + response);
            
            // Demonstrate automatic cleanup by simulating a failure
            System.out.println("\nSimulating a failure scenario (with automatic cleanup):");
            try {
                Response failedResponse = processOrderWithFailure();
                System.out.println("This should not be reached due to failure");
            } catch (Exception e) {
                System.out.println("Order processing failed: " + e.getMessage());
                System.out.println("Note: All threads were automatically cleaned up!");
            }
            
        } catch (Exception e) {
            System.err.println("Error in demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // The new way - clean and structured
    private static Response processOrder() throws InterruptedException {
        try (var scope = StructuredTaskScope.open()) {
            
            Subtask<User> user = scope.fork(() -> fetchUser());
            Subtask<Inventory> inventory = scope.fork(() -> checkInventory());
            Subtask<Payment> payment = scope.fork(() -> processPayment());
            
            scope.join();  // Wait for all - automatic cleanup!
            
            return new Response(user.get(), inventory.get(), payment.get());
        }
    }
    
    // Version that simulates a failure to demonstrate automatic cleanup
    private static Response processOrderWithFailure() throws InterruptedException {
        try (var scope = StructuredTaskScope.open()) {
            
            Subtask<User> user = scope.fork(() -> fetchUser());
            Subtask<Inventory> inventory = scope.fork(() -> checkInventory());
            Subtask<Payment> payment = scope.fork(() -> {
                // This will throw an exception
                throw new RuntimeException("Payment processing failed");
            });
            
            scope.join();  // Wait for all - automatic cleanup even on failure!
            
            // The following line will throw an exception if payment failed
            return new Response(user.get(), inventory.get(), payment.get());
        }
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
    record User(String id, String name) {}
    record Inventory(String productId, int quantity) {}
    record Payment(String transactionId, double amount) {}
    record Response(User user, Inventory inventory, Payment payment) {}
}
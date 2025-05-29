package ca.bazlur.concurrency;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Demonstrates the "All or Nothing" pattern using structured concurrency.
 * 
 * This pattern is used when you need all tasks to succeed or none at all.
 * It's perfect for distributed transactions where partial success is not acceptable.
 */
public class E_AllOrNothingDemo {

    public static void run() {
        System.out.println("Demonstrating the All or Nothing pattern for distributed transactions...");
        
        try {
            // Execute a successful distributed transaction
            TransactionData successData = new TransactionData(
                "user123", 99.99, "item456", "card789", "123 Main St"
            );
            
            System.out.println("Executing successful transaction...");
            List<String> successResults = executeDistributedTransaction(successData);
            System.out.println("Transaction completed successfully!");
            successResults.forEach(result -> System.out.println("  - " + result));
            
            // Execute a transaction with a failure
            TransactionData failureData = new TransactionData(
                "user123", 999.99, "rare-item", "card789", "123 Main St"
            );
            
            System.out.println("\nExecuting transaction that will fail...");
            try {
                List<String> failureResults = executeDistributedTransaction(failureData);
                System.out.println("This should not be reached due to failure");
            } catch (Exception e) {
                System.out.println("Transaction failed as expected: " + e.getMessage());
                System.out.println("All steps were automatically cancelled!");
            }
            
        } catch (Exception e) {
            System.err.println("Error in demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // All or Nothing pattern - all must succeed or all are cancelled
    private static List<String> executeDistributedTransaction(TransactionData data) 
        throws InterruptedException {
        
        Collection<Callable<String>> transactionSteps = List.of(
            () -> userService(data.userId(), data.amount()),
            () -> inventoryService(data.itemId()),
            () -> paymentService(data.cardId(), data.amount()),
            () -> shippingService(data.address())
        );
        
        try (var scope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.<String>allSuccessfulOrThrow())) {
            
            transactionSteps.forEach(scope::fork);
            return scope.join().map(StructuredTaskScope.Subtask::get).toList();
        }
        // If ANY step fails, ALL are cancelled automatically!
    }
    
    // Simulated transaction services with varying response times and potential failures
    private static String userService(String userId, double amount) throws InterruptedException {
        int delay = ThreadLocalRandom.current().nextInt(100, 300);
        System.out.println("User service starting for " + userId + " (will take " + delay + "ms)");
        Thread.sleep(delay);
        
        // Simulate failure for large amounts
        if (amount > 500) {
            System.out.println("User service failed: amount exceeds user's limit");
            throw new RuntimeException("Amount exceeds user's limit");
        }
        
        return "User account updated for " + userId + " with amount $" + amount;
    }
    
    private static String inventoryService(String itemId) throws InterruptedException {
        int delay = ThreadLocalRandom.current().nextInt(150, 350);
        System.out.println("Inventory service starting for " + itemId + " (will take " + delay + "ms)");
        Thread.sleep(delay);
        
        // Simulate failure for rare items
        if (itemId.contains("rare")) {
            System.out.println("Inventory service failed: item not in stock");
            throw new RuntimeException("Item not in stock");
        }
        
        return "Item " + itemId + " reserved in inventory";
    }
    
    private static String paymentService(String cardId, double amount) throws InterruptedException {
        int delay = ThreadLocalRandom.current().nextInt(200, 400);
        System.out.println("Payment service starting for card " + cardId + " (will take " + delay + "ms)");
        Thread.sleep(delay);
        
        // Simulate failure for very large amounts
        if (amount > 900) {
            System.out.println("Payment service failed: payment declined");
            throw new RuntimeException("Payment declined");
        }
        
        return "Payment processed on card " + cardId + " for $" + amount;
    }
    
    private static String shippingService(String address) throws InterruptedException {
        int delay = ThreadLocalRandom.current().nextInt(250, 450);
        System.out.println("Shipping service starting for " + address + " (will take " + delay + "ms)");
        Thread.sleep(delay);
        
        return "Delivery scheduled to " + address;
    }
    
    // Domain model class
    record TransactionData(String userId, double amount, String itemId, String cardId, String address) {}
}
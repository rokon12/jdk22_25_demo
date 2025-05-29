package ca.bazlur.concurrency;

import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Demonstrates the "Hierarchy: Nested Scope Architecture" pattern using structured concurrency.
 * 
 * This pattern is used for building complex systems with clean structure, where
 * the concurrent code structure matches the block structure.
 */
public class H_NestedScopeDemo {

    public static void run() {
        System.out.println("Demonstrating the Hierarchy pattern with a multi-tier application...");
        
        try {
            // Process an order with a multi-tier architecture
            Order order = new Order("ORD-12345", "CUST-789", "PROD-456", 2);
            
            System.out.println("Processing order: " + order);
            ProcessingResult result = processOrder(order);
            
            System.out.println("Order processing completed with result: " + result);
            
        } catch (Exception e) {
            System.err.println("Error in demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Multi-tier application processing with nested scopes
    private static ProcessingResult processOrder(Order order) throws InterruptedException {
        try (var mainScope = StructuredTaskScope.open()) {
            
            // Tier 1: Validation (all must succeed)
            Subtask<ValidationResult> validation = mainScope.fork(() -> 
                validateOrder(order));
            
            // Tier 2: Data gathering (race for best performance) 
            Subtask<EnrichmentData> enrichment = mainScope.fork(() -> 
                enrichOrderData(order));
            
            // Tier 3: Business processing (custom logic)
            Subtask<BusinessResult> business = mainScope.fork(() -> 
                processBusinessLogic(order));
            
            mainScope.join();
            
            return combineResults(
                validation.get(), 
                enrichment.get(), 
                business.get()
            );
        }
    }
    
    // Tier 1: Validation logic
    private static ValidationResult validateOrder(Order order) throws InterruptedException {
        System.out.println("Tier 1: Validating order " + order.id());
        
        try (var validationScope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.<Boolean>allSuccessfulOrThrow())) {
            
            validationScope.fork(() -> {
                Thread.sleep(ThreadLocalRandom.current().nextInt(50, 150));
                System.out.println("  - Validating customer: " + order.customerId());
                return true;
            });
            
            validationScope.fork(() -> {
                Thread.sleep(ThreadLocalRandom.current().nextInt(30, 100));
                System.out.println("  - Validating product: " + order.productId());
                return true;
            });
            
            validationScope.fork(() -> {
                Thread.sleep(ThreadLocalRandom.current().nextInt(20, 80));
                System.out.println("  - Validating quantity: " + order.quantity());
                if (order.quantity() <= 0) {
                    throw new IllegalArgumentException("Quantity must be positive");
                }
                return true;
            });
            
            validationScope.join();
            return new ValidationResult(true, "Order validated successfully");
        }
    }
    
    // Tier 2: Data enrichment with racing pattern
    private static EnrichmentData enrichOrderData(Order order) throws InterruptedException {
        System.out.println("Tier 2: Enriching order data for " + order.id());
        
        // Nested scope for data enrichment (racing pattern)
        try (var enrichScope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.<String>anySuccessfulResultOrThrow())) {
            
            enrichScope.fork(() -> {
                Thread.sleep(ThreadLocalRandom.current().nextInt(100, 300));
                System.out.println("  - Customer profile retrieved from primary DB");
                return "Customer: Premium Member since 2020";
            });
            
            enrichScope.fork(() -> {
                Thread.sleep(ThreadLocalRandom.current().nextInt(150, 350));
                System.out.println("  - Customer profile retrieved from backup DB");
                return "Customer: Premium Member since 2020";
            });
            
            String customerProfile = enrichScope.join();
            
            // Another nested scope for product details
            try (var productScope = StructuredTaskScope.open()) {
                Subtask<String> details = productScope.fork(() -> {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(80, 200));
                    System.out.println("  - Product details retrieved");
                    return "Product: High-performance laptop";
                });
                
                Subtask<Double> price = productScope.fork(() -> {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(50, 150));
                    System.out.println("  - Product price retrieved");
                    return 999.99;
                });
                
                productScope.join();
                return new EnrichmentData(customerProfile, details.get(), price.get());
            }
        }
    }
    
    // Tier 3: Business logic processing
    private static BusinessResult processBusinessLogic(Order order) throws InterruptedException {
        System.out.println("Tier 3: Processing business logic for " + order.id());
        
        try (var businessScope = StructuredTaskScope.open()) {
            Subtask<String> inventory = businessScope.fork(() -> {
                Thread.sleep(ThreadLocalRandom.current().nextInt(100, 250));
                System.out.println("  - Inventory updated");
                return "Inventory updated for " + order.productId();
            });
            
            Subtask<String> accounting = businessScope.fork(() -> {
                Thread.sleep(ThreadLocalRandom.current().nextInt(120, 280));
                System.out.println("  - Accounting entry created");
                return "Accounting entry created for order " + order.id();
            });
            
            Subtask<String> notification = businessScope.fork(() -> {
                Thread.sleep(ThreadLocalRandom.current().nextInt(50, 150));
                System.out.println("  - Customer notification prepared");
                return "Notification prepared for customer " + order.customerId();
            });
            
            businessScope.join();
            
            return new BusinessResult(
                inventory.get(),
                accounting.get(),
                notification.get()
            );
        }
    }
    
    // Combine results from all tiers
    private static ProcessingResult combineResults(
            ValidationResult validation, 
            EnrichmentData enrichment, 
            BusinessResult business) {
        
        System.out.println("Combining results from all tiers...");
        return new ProcessingResult(
            "Order processed successfully",
            validation.isValid(),
            enrichment.customerProfile(),
            business.inventoryStatus()
        );
    }
    
    // Domain model classes
    record Order(String id, String customerId, String productId, int quantity) {}
    
    record ValidationResult(boolean isValid, String message) {}
    
    record EnrichmentData(String customerProfile, String productDetails, double price) {}
    
    record BusinessResult(String inventoryStatus, String accountingStatus, String notificationStatus) {}
    
    record ProcessingResult(String status, boolean valid, String customerInfo, String inventoryStatus) {}
}
package demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Demonstrates Scoped Values, a JDK 21+ feature for sharing immutable data
 * across virtual threads in a structured way.
 */
public class ScopedValueDemo {
    
    // Define scoped values for request context and tenant information
    private static final ScopedValue<RequestContext> REQUEST_CONTEXT = ScopedValue.newInstance();
    private static final ScopedValue<TenantInfo> TENANT_INFO = ScopedValue.newInstance();
    
    public static void run() {
        System.out.println("=== Scoped Values Demo ===");
        System.out.println("Demonstrating nested scoped values shared across virtual threads\n");
        
        // Simulate processing multiple requests with different contexts
        processRequest("GET /api/products", "user123", "tenant-abc");
        processRequest("POST /api/orders", "admin456", "tenant-xyz");
    }
    
    private static void processRequest(String requestPath, String userId, String tenantId) {
        // Create request context
        RequestContext context = new RequestContext(requestPath, userId, System.currentTimeMillis());
        
        // Use ScopedValue.where to bind the request context
        ScopedValue.where(REQUEST_CONTEXT, context).run(() -> {
            System.out.println("Processing request: " + REQUEST_CONTEXT.get().path() + 
                              " (User: " + REQUEST_CONTEXT.get().userId() + ")");
            
            // Create tenant info (nested scoped value)
            TenantInfo tenant = new TenantInfo(tenantId, "Region-" + ThreadLocalRandom.current().nextInt(1, 5));
            
            // Bind tenant info in a nested scope
            ScopedValue.where(TENANT_INFO, tenant).run(() -> {
                System.out.println("  Tenant context: " + TENANT_INFO.get().id() + 
                                  " (" + TENANT_INFO.get().region() + ")");
                
                // Simulate parallel processing with virtual threads
                try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                    // These tasks will inherit both REQUEST_CONTEXT and TENANT_INFO
                    executor.submit(ScopedValueDemo::validateRequest);
                    executor.submit(ScopedValueDemo::processBusinessLogic);
                    executor.submit(ScopedValueDemo::logActivity);
                    
                    // Wait for all tasks to complete
                    Thread.sleep(500);
                } catch (Exception e) {
                    System.err.println("Error in parallel processing: " + e.getMessage());
                }
            });
            
            System.out.println("Request completed in " + 
                              (System.currentTimeMillis() - REQUEST_CONTEXT.get().timestamp()) + "ms\n");
        });
    }
    
    private static void validateRequest() {
        simulateWork(50, 150);
        System.out.println("  [Thread: " + Thread.currentThread().getName() + "] " +
                          "Validating request from user " + REQUEST_CONTEXT.get().userId() +
                          " for tenant " + TENANT_INFO.get().id());
    }
    
    private static void processBusinessLogic() {
        simulateWork(100, 300);
        System.out.println("  [Thread: " + Thread.currentThread().getName() + "] " +
                          "Processing business logic for " + REQUEST_CONTEXT.get().path() +
                          " in region " + TENANT_INFO.get().region());
    }
    
    private static void logActivity() {
        simulateWork(30, 80);
        System.out.println("  [Thread: " + Thread.currentThread().getName() + "] " +
                          "Logging activity for tenant " + TENANT_INFO.get().id() +
                          " (Request ID: " + REQUEST_CONTEXT.get().timestamp() + ")");
    }
    
    private static void simulateWork(int minMs, int maxMs) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(minMs, maxMs));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // Immutable record for request context
    private record RequestContext(String path, String userId, long timestamp) {}
    
    // Immutable record for tenant information
    private record TenantInfo(String id, String region) {}
}
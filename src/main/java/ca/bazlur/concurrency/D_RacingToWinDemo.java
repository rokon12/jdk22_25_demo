package ca.bazlur.concurrency;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Demonstrates the "Racing to Win" pattern using structured concurrency.
 * 
 * This pattern is used when you need the fastest response from multiple providers.
 * The first successful result wins, and other tasks are automatically cancelled.
 */
public class D_RacingToWinDemo {

    public static void run() {
        System.out.println("Demonstrating the Racing to Win pattern for price comparison...");
        
        try {
            // Compare prices for a product across multiple providers
            String productId = "laptop-x1";
            BigDecimal bestPrice = getBestPrice(productId);
            System.out.println("Best price found for " + productId + ": $" + bestPrice);
            
            // Try with a different product
            productId = "smartphone-pro";
            bestPrice = getBestPrice(productId);
            System.out.println("Best price found for " + productId + ": $" + bestPrice);
            
            // Try with a product where some providers might fail
            productId = "rare-item";
            bestPrice = getBestPrice(productId);
            System.out.println("Best price found for " + productId + ": $" + bestPrice);
            
        } catch (Exception e) {
            System.err.println("Error in demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Racing pattern - first successful result wins
    private static BigDecimal getBestPrice(String productId) throws InterruptedException {
        Collection<Callable<BigDecimal>> priceProviders = List.of(
            () -> amazonPriceService(productId),
            () -> ebayPriceService(productId), 
            () -> walmartPriceService(productId)
        );
        
        // Race them - first one wins!
        try (var scope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.<BigDecimal>anySuccessfulResultOrThrow())) {
            
            priceProviders.forEach(scope::fork);
            return scope.join(); // Returns as soon as ANY succeeds
        }
    }
    
    // Simulated price services with varying response times and potential failures
    private static BigDecimal amazonPriceService(String productId) throws InterruptedException {
        int delay = ThreadLocalRandom.current().nextInt(300, 800);
        System.out.println("Amazon price service starting for " + productId + " (will take " + delay + "ms)");
        Thread.sleep(delay);
        
        // Simulate failure for rare items
        if (productId.contains("rare") && ThreadLocalRandom.current().nextBoolean()) {
            System.out.println("Amazon price service failed for " + productId);
            throw new RuntimeException("Product not available at Amazon");
        }
        
        BigDecimal price = new BigDecimal(ThreadLocalRandom.current().nextInt(80, 120));
        System.out.println("Amazon price for " + productId + ": $" + price);
        return price;
    }
    
    private static BigDecimal ebayPriceService(String productId) throws InterruptedException {
        int delay = ThreadLocalRandom.current().nextInt(200, 600);
        System.out.println("eBay price service starting for " + productId + " (will take " + delay + "ms)");
        Thread.sleep(delay);
        
        // Simulate failure for rare items
        if (productId.contains("rare") && ThreadLocalRandom.current().nextBoolean()) {
            System.out.println("eBay price service failed for " + productId);
            throw new RuntimeException("Product not available at eBay");
        }
        
        BigDecimal price = new BigDecimal(ThreadLocalRandom.current().nextInt(75, 115));
        System.out.println("eBay price for " + productId + ": $" + price);
        return price;
    }
    
    private static BigDecimal walmartPriceService(String productId) throws InterruptedException {
        int delay = ThreadLocalRandom.current().nextInt(400, 900);
        System.out.println("Walmart price service starting for " + productId + " (will take " + delay + "ms)");
        Thread.sleep(delay);
        
        // Simulate failure for rare items
        if (productId.contains("rare") && ThreadLocalRandom.current().nextBoolean()) {
            System.out.println("Walmart price service failed for " + productId);
            throw new RuntimeException("Product not available at Walmart");
        }
        
        BigDecimal price = new BigDecimal(ThreadLocalRandom.current().nextInt(70, 110));
        System.out.println("Walmart price for " + productId + ": $" + price);
        return price;
    }
}
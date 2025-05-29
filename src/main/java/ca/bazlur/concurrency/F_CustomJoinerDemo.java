package ca.bazlur.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates the "Custom Intelligence" pattern using a custom joiner.
 * 
 * This pattern is used when the built-in joiners aren't enough, and you need
 * custom business logic for handling task results.
 */
public class F_CustomJoinerDemo {

    public static void run() {
        System.out.println("Demonstrating the Custom Intelligence pattern with a resilient news aggregator...");
        
        try {
            // Aggregate news from multiple sources, some of which might be unreliable
            List<NewsArticle> articles = aggregateNews();
            
            System.out.println("News aggregation completed!");
            System.out.println("Articles collected: " + articles.size());
            articles.forEach(article -> 
                System.out.println("  - " + article.headline() + " (from " + article.source() + ")"));
            
        } catch (Exception e) {
            System.err.println("Error in demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Custom Intelligence pattern - collect successful results, ignore failures
    private static List<NewsArticle> aggregateNews() throws InterruptedException {
        try (var scope = StructuredTaskScope.open(new ResilientJoiner<NewsArticle>())) {
            
            // Some sources might be down - that's OK!
            scope.fork(() -> cnnService());
            scope.fork(() -> bbcService()); 
            scope.fork(() -> reutersService());
            scope.fork(() -> unreliableSourceService());
            
            return scope.join(); // Returns partial results
        }
    }
    
    // Custom joiner that collects successful results and ignores failures
    static class ResilientJoiner<T> implements Joiner<T, List<T>> {
        private final Queue<T> successes = new ConcurrentLinkedQueue<>();
        private final AtomicInteger failures = new AtomicInteger();
        
        @Override
        public boolean onComplete(Subtask<? extends T> subtask) {
            switch (subtask.state()) {
                case SUCCESS -> successes.add(subtask.get());
                case FAILED -> failures.incrementAndGet();
            }
            return false; // Never cancel - collect everything
        }
        
        @Override
        public List<T> result() {
            System.out.println("Collected " + successes.size() + 
                              " successes, " + failures.get() + " failures");
            return new ArrayList<>(successes);
        }
    }
    
    // Simulated news services with varying response times and potential failures
    private static NewsArticle cnnService() throws InterruptedException {
        int delay = ThreadLocalRandom.current().nextInt(200, 500);
        System.out.println("CNN service starting (will take " + delay + "ms)");
        Thread.sleep(delay);
        
        return new NewsArticle("CNN", "Breaking News: Major Tech Announcement");
    }
    
    private static NewsArticle bbcService() throws InterruptedException {
        int delay = ThreadLocalRandom.current().nextInt(300, 600);
        System.out.println("BBC service starting (will take " + delay + "ms)");
        Thread.sleep(delay);
        
        // Simulate occasional failure
        if (ThreadLocalRandom.current().nextInt(10) < 2) {
            System.out.println("BBC service failed: connection timeout");
            throw new RuntimeException("Connection timeout");
        }
        
        return new NewsArticle("BBC", "Global Markets Report: Stocks Rise");
    }
    
    private static NewsArticle reutersService() throws InterruptedException {
        int delay = ThreadLocalRandom.current().nextInt(250, 550);
        System.out.println("Reuters service starting (will take " + delay + "ms)");
        Thread.sleep(delay);
        
        return new NewsArticle("Reuters", "Economic Forecast: Growth Expected");
    }
    
    private static NewsArticle unreliableSourceService() throws InterruptedException {
        int delay = ThreadLocalRandom.current().nextInt(100, 400);
        System.out.println("Unreliable source service starting (will take " + delay + "ms)");
        Thread.sleep(delay);
        
        // Simulate frequent failure
        if (ThreadLocalRandom.current().nextInt(10) < 7) {
            System.out.println("Unreliable source service failed: server error");
            throw new RuntimeException("Server error");
        }
        
        return new NewsArticle("UnreliableSource", "Exclusive: Insider Information");
    }
    
    // Domain model class
    record NewsArticle(String source, String headline) {}
}
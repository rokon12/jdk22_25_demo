package ca.bazlur.concurrency;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Demonstrates the "Time is Money" pattern using deadline-aware structured concurrency.
 * 
 * This pattern is used when time constraints matter, such as in real-time trading systems
 * with strict SLAs.
 */
public class G_DeadlineAwareDemo {

    public static void run() {
        System.out.println("Demonstrating the Time is Money pattern with a real-time trading system...");

        try {
            // Make a trading decision with a tight deadline
            String symbol = "AAPL";
            Duration deadline = Duration.ofMillis(300);

            System.out.println("Making trading decision for " + symbol + " with " + deadline.toMillis() + "ms deadline...");
            Instant start = Instant.now();

            TradingDecision decision = makeTradeDecision(symbol, deadline);

            Duration elapsed = Duration.between(start, Instant.now());
            System.out.println("Decision made in " + elapsed.toMillis() + "ms: " + decision);

            // Try with a different symbol and a very tight deadline that will likely timeout
            symbol = "GOOG";
            deadline = Duration.ofMillis(150);

            System.out.println("\nMaking trading decision for " + symbol + " with very tight " + deadline.toMillis() + "ms deadline...");
            start = Instant.now();

            try {
                decision = makeTradeDecision(symbol, deadline);
                elapsed = Duration.between(start, Instant.now());
                System.out.println("Decision made in " + elapsed.toMillis() + "ms: " + decision);
            } catch (Exception e) {
                elapsed = Duration.between(start, Instant.now());
                System.out.println("Decision timed out after " + elapsed.toMillis() + "ms: " + e.getMessage());
                System.out.println("Using safe default: " + TradingDecision.HOLD);
            }

        } catch (Exception e) {
            System.err.println("Error in demo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Time is Money pattern - deadline-aware processing
    private static TradingDecision makeTradeDecision(String symbol, Duration deadline) 
        throws InterruptedException {

        try (var scope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.<AnalysisResult>allSuccessfulOrThrow(),
                cfg -> cfg.withTimeout(deadline)
                          .withThreadFactory(Thread.ofVirtual()
                                                  .name("trading-", 0)
                                                  .factory()))) {

            // Launch parallel analysis
            var technical = scope.fork(() -> technicalAnalysis(symbol));
            var fundamental = scope.fork(() -> fundamentalAnalysis(symbol)); 
            var sentiment = scope.fork(() -> sentimentAnalysis(symbol));
            var risk = scope.fork(() -> riskAnalysis(symbol));

            // Wait for all tasks to complete or deadline to expire
            scope.join();

            // Check if we have enough successful results to make a decision
            List<AnalysisResult> results = new ArrayList<>();

            // Collect results from successful subtasks
            for (var subtask : List.of(technical, fundamental, sentiment, risk)) {
                if (subtask.state() == StructuredTaskScope.Subtask.State.SUCCESS) {
                    results.add(subtask.get());
                } else if (subtask.state() == StructuredTaskScope.Subtask.State.FAILED) {
                    System.out.println("Analysis failed: " + subtask.exception().getMessage());
                } else {
                    System.out.println("Analysis cancelled due to deadline");
                }
            }

            // If we have at least 2 successful analyses, make a decision
            if (results.size() >= 2) {
                return decideTrade(results);
            } else {
                System.out.println("Not enough data to make a decision, using safe default");
                return TradingDecision.HOLD; // Safe default on timeout
            }
        }
    }

    // Decision logic based on all analysis results
    private static TradingDecision decideTrade(List<AnalysisResult> results) {
        // Count positive vs negative signals
        long positiveSignals = results.stream()
                .filter(r -> r.signal() > 0)
                .count();

        long negativeSignals = results.stream()
                .filter(r -> r.signal() < 0)
                .count();

        // Simple decision logic
        if (positiveSignals >= 3) return TradingDecision.BUY;
        if (negativeSignals >= 3) return TradingDecision.SELL;
        return TradingDecision.HOLD;
    }

    // Simulated analysis services with varying response times
    private static AnalysisResult technicalAnalysis(String symbol) throws InterruptedException {
        int processingTime = ThreadLocalRandom.current().nextInt(100, 300);
        System.out.println("Technical analysis starting for " + symbol + " (will take " + processingTime + "ms)");
        Thread.sleep(processingTime);

        int signal = ThreadLocalRandom.current().nextInt(-1, 2); // -1, 0, or 1
        return new AnalysisResult("Technical", signal, "Based on chart patterns");
    }

    private static AnalysisResult fundamentalAnalysis(String symbol) throws InterruptedException {
        int processingTime = ThreadLocalRandom.current().nextInt(150, 350);
        System.out.println("Fundamental analysis starting for " + symbol + " (will take " + processingTime + "ms)");
        Thread.sleep(processingTime);

        int signal = ThreadLocalRandom.current().nextInt(-1, 2); // -1, 0, or 1
        return new AnalysisResult("Fundamental", signal, "Based on financial statements");
    }

    private static AnalysisResult sentimentAnalysis(String symbol) throws InterruptedException {
        int processingTime = ThreadLocalRandom.current().nextInt(80, 250);
        System.out.println("Sentiment analysis starting for " + symbol + " (will take " + processingTime + "ms)");
        Thread.sleep(processingTime);

        int signal = ThreadLocalRandom.current().nextInt(-1, 2); // -1, 0, or 1
        return new AnalysisResult("Sentiment", signal, "Based on social media");
    }

    private static AnalysisResult riskAnalysis(String symbol) throws InterruptedException {
        int processingTime = ThreadLocalRandom.current().nextInt(120, 280);
        System.out.println("Risk analysis starting for " + symbol + " (will take " + processingTime + "ms)");
        Thread.sleep(processingTime);

        int signal = ThreadLocalRandom.current().nextInt(-1, 2); // -1, 0, or 1
        return new AnalysisResult("Risk", signal, "Based on market volatility");
    }

    // Domain model classes
    record AnalysisResult(String type, int signal, String reason) {}

    enum TradingDecision {
        BUY("Buy the stock"),
        SELL("Sell the stock"),
        HOLD("Hold current position");

        private final String description;

        TradingDecision(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return name() + " - " + description;
        }
    }
}
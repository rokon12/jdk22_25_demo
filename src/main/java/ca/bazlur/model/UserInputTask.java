package ca.bazlur.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Demonstrates flexible constructor bodies (JDK 22+).
 * This class extends Task and uses flexible constructor bodies to process
 * constructor arguments before calling super().
 */
public class UserInputTask extends Task {
    
    private final List<String> validatedInputs;
    private final Duration timeout;
    private final int retryCount;
    
    /**
     * Constructs a new UserInputTask with the given inputs.
     * Demonstrates flexible constructor bodies by processing arguments
     * before calling super().
     * 
     * @param name Task name
     * @param userInputs Raw user inputs to process
     * @param timeoutSeconds Timeout in seconds
     * @param maxRetries Maximum number of retries
     */
    public UserInputTask(String name, List<String> userInputs, int timeoutSeconds, int maxRetries) {
        // With flexible constructor bodies, we can compute values before calling super()
        
        // Validate and sanitize inputs
        List<String> sanitizedInputs = new ArrayList<>();
        for (String input : userInputs) {
            if (input != null && !input.isBlank()) {
                // Trim and sanitize input
                String sanitized = input.trim().replaceAll("[^a-zA-Z0-9\\s.,;:-]", "");
                sanitizedInputs.add(sanitized);
            }
        }
        
        // Compute a more descriptive name based on input count
        String enhancedName = name + " (" + sanitizedInputs.size() + " inputs)";
        
        // Adjust timeout based on input size (minimum 5 seconds)
        int adjustedTimeout = Math.max(5, timeoutSeconds);
        
        // Validate retry count (between 1 and 5)
        int validRetryCount = Math.min(5, Math.max(1, maxRetries));
        
        // Now call super() with the computed name
        super(enhancedName);
        
        // Store the processed values
        this.validatedInputs = sanitizedInputs;
        this.timeout = Duration.ofSeconds(adjustedTimeout);
        this.retryCount = validRetryCount;
        
        // Log the task creation
        System.out.println("Created UserInputTask with " + sanitizedInputs.size() + 
                          " inputs, " + adjustedTimeout + "s timeout, " + 
                          validRetryCount + " max retries");
    }
    
    @Override
    protected boolean doExecute() throws Exception {
        System.out.println("Executing UserInputTask with " + validatedInputs.size() + " inputs");
        System.out.println("Timeout: " + timeout.toSeconds() + "s, Max retries: " + retryCount);
        
        // Process each input with retry logic
        for (String input : validatedInputs) {
            boolean processed = false;
            
            for (int attempt = 1; attempt <= retryCount && !processed; attempt++) {
                try {
                    System.out.println("Processing input: '" + input + "' (Attempt " + attempt + ")");
                    
                    // Simulate processing time
                    Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));
                    
                    // Simulate occasional failures (20% chance)
                    if (ThreadLocalRandom.current().nextInt(5) == 0) {
                        throw new RuntimeException("Processing error for input: " + input);
                    }
                    
                    processed = true;
                    System.out.println("Successfully processed: '" + input + "'");
                    
                } catch (Exception e) {
                    System.err.println("Attempt " + attempt + " failed: " + e.getMessage());
                    
                    if (attempt == retryCount) {
                        System.err.println("All retry attempts failed for input: '" + input + "'");
                        return false;
                    }
                    
                    // Wait before retrying
                    Thread.sleep(attempt * 100);
                }
            }
        }
        
        return true;
    }
    
    /**
     * Gets the validated inputs.
     */
    public List<String> getValidatedInputs() {
        return validatedInputs;
    }
    
    /**
     * Gets the timeout duration.
     */
    public Duration getTimeout() {
        return timeout;
    }
    
    /**
     * Gets the retry count.
     */
    public int getRetryCount() {
        return retryCount;
    }
    
    /**
     * Creates a sample UserInputTask for demonstration.
     */
    public static UserInputTask createSample() {
        List<String> sampleInputs = List.of(
            "Hello, world!",
            "  This needs trimming  ",
            "Special@characters#should$be%removed",
            "Numbers123 are fine",
            ""  // Empty input should be filtered out
        );
        
        return new UserInputTask("Sample Task", sampleInputs, 10, 3);
    }
}
package ca.bazlur;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Demonstrates primitive pattern matching in switch expressions,
 * a JDK 22+ feature for more expressive pattern matching with primitive types.
 */
public class PrimitivePatternDemo {

    public static void run() {
        System.out.println("=== Primitive Pattern Matching Demo ===");
        
        // Generate sample numeric data
        List<Number> numbers = generateMixedNumbers(15);
        
        System.out.println("\nAnalyzing numeric values with primitive patterns:");
        for (Number number : numbers) {
            String analysis = analyzeNumber(number);
            System.out.printf("  %s: %s%n", number, analysis);
        }
        
        // Demonstrate temperature classification
        System.out.println("\nClassifying temperatures:");
        List<Double> temperatures = List.of(-15.5, 0.0, 18.2, 25.0, 32.7, 40.1);
        for (Double temp : temperatures) {
            String classification = classifyTemperature(temp);
            System.out.printf("  %.1f°C: %s%n", temp, classification);
        }
        
        // Demonstrate HTTP status code handling
        System.out.println("\nHandling HTTP status codes:");
        List<Integer> statusCodes = List.of(200, 201, 301, 400, 404, 500, 503);
        for (Integer code : statusCodes) {
            String meaning = describeHttpStatus(code);
            System.out.printf("  HTTP %d: %s%n", code, meaning);
        }
    }
    
    /**
     * Analyzes a number using primitive pattern matching in a switch expression.
     */
    private static String analyzeNumber(Number number) {
        return switch (number) {
            // Integer patterns with guards
            case Integer i when i == 0 -> "Zero integer";
            case Integer i when i > 0 && i <= 10 -> "Small positive integer (1-10)";
            case Integer i when i > 10 && i < 100 -> "Medium positive integer (11-99)";
            case Integer i when i >= 100 -> "Large positive integer (100+)";
            case Integer i when i < 0 && i >= -10 -> "Small negative integer (-1 to -10)";
            case Integer i when i < -10 -> "Large negative integer (< -10)";
            
            // Double patterns with range checks
            case Double d when d == 0.0 -> "Zero double";
            case Double d when d > 0 && d < 1.0 -> "Fractional positive double (0-1)";
            case Double d when d >= 1.0 && d < 1000.0 -> "Regular positive double (1-999.9)";
            case Double d when d >= 1000.0 -> "Large positive double (1000+)";
            case Double d when d < 0 -> "Negative double";
            
            // Long patterns
            case Long l -> "Long value: " + (l < 0 ? "negative" : "positive");
            
            // Float patterns
            case Float f -> "Float value: " + (f < 0 ? "negative" : "positive");
            
            // Default case
            default -> "Unknown number type";
        };
    }
    
    /**
     * Classifies temperature using primitive pattern matching with ranges.
     */
    private static String classifyTemperature(double celsius) {
        return switch (celsius) {
            case double d when d < -10.0 -> "Extremely cold";
            case double d when d >= -10.0 && d < 0.0 -> "Freezing";
            case double d when d >= 0.0 && d < 15.0 -> "Cold";
            case double d when d >= 15.0 && d < 25.0 -> "Moderate";
            case double d when d >= 25.0 && d < 35.0 -> "Warm";
            case double d when d >= 35.0 -> "Hot";
            default -> "Invalid temperature"; // Unreachable but required for compilation
        };
    }
    
    /**
     * Describes HTTP status codes using primitive pattern matching with specific values and ranges.
     */
    private static String describeHttpStatus(int code) {
        return switch (code) {
            // Specific status codes
            case 200 -> "OK - Request succeeded";
            case 201 -> "Created - Resource created successfully";
            case 204 -> "No Content - Request succeeded with no response body";
            
            // Status code ranges
            case int s when s >= 100 && s < 200 -> "Informational response";
            case int s when s >= 200 && s < 300 -> "Success";
            case int s when s >= 300 && s < 400 -> "Redirection";
            case int s when s >= 400 && s < 500 -> "Client error";
            case int s when s >= 500 && s < 600 -> "Server error";
            
            // Default case for invalid codes
            default -> "Invalid HTTP status code";
        };
    }
    
    /**
     * Generates a mix of Integer, Double, Long, and Float values.
     */
    private static List<Number> generateMixedNumbers(int count) {
        List<Number> numbers = new ArrayList<>(count);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        
        for (int i = 0; i < count; i++) {
            // Randomly choose which type of number to generate
            int type = random.nextInt(4);
            switch (type) {
                case 0 -> numbers.add(random.nextInt(-100, 200)); // Integer
                case 1 -> numbers.add(random.nextDouble(-50.0, 1500.0)); // Double
                case 2 -> numbers.add(random.nextLong(-1000, 1000)); // Long
                case 3 -> numbers.add(random.nextFloat() * 100 - 50); // Float
            }
        }
        
        return numbers;
    }
}
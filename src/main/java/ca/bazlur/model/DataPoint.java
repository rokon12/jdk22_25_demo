package ca.bazlur.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Demonstrates record patterns and unnamed patterns (JDK 21+).
 * This record represents a data point with various attributes.
 */
public record DataPoint(
    String id,
    double value,
    LocalDateTime timestamp,
    Map<String, String> metadata,
    List<Tag> tags,
    Optional<DataPoint> previousPoint
) {
    /**
     * Nested record for tags
     */
    public record Tag(String name, String value) {}
    
    /**
     * Demonstrates record pattern matching with unnamed patterns.
     * Extracts and processes information from a DataPoint.
     */
    public static String summarize(DataPoint point) {
        // Using record pattern with named components
        if (point instanceof DataPoint(String id, double value, var timestamp, var metadata, var tags, var prev)) {
            StringBuilder summary = new StringBuilder();
            summary.append("DataPoint ").append(id).append(": ")
                  .append(value).append(" at ")
                  .append(timestamp.toLocalTime());
            
            // Process metadata if present
            if (!metadata.isEmpty()) {
                summary.append(" [");
                metadata.forEach((k, v) -> summary.append(k).append("=").append(v).append(", "));
                summary.delete(summary.length() - 2, summary.length());
                summary.append("]");
            }
            
            // Process tags using unnamed patterns (using _ for unused components)
            if (!tags.isEmpty()) {
                summary.append(" Tags: ");
                for (Tag tag : tags) {
                    // Using record pattern with unnamed pattern (_) for value we don't need
                    if (tag instanceof Tag(String name, _)) {
                        summary.append("#").append(name).append(" ");
                    }
                }
            }
            
            // Check previous point using nested pattern matching with unnamed patterns
            if (prev.isPresent() && prev.get() instanceof DataPoint(_, double prevValue, _, _, _, _)) {
                double change = value - prevValue;
                double percentChange = (change / prevValue) * 100;
                summary.append(String.format(" (%.2f%% from previous)", percentChange));
            }
            
            return summary.toString();
        }
        
        return "Invalid data point";
    }
    
    /**
     * Demonstrates record pattern matching in switch expressions.
     */
    public static String categorize(Object obj) {
        return switch (obj) {
            // Record pattern with unnamed components
            case DataPoint(String id, double value, _, _, _, _) when value > 100 ->
                "High-value point: " + id + " (" + value + ")";
                
            // Record pattern with unnamed components and nested pattern
            case DataPoint(String id, _, _, _, List<Tag> tags, _) when !tags.isEmpty() ->
                "Tagged point: " + id + " with " + tags.size() + " tags";
                
            // Record pattern with nested pattern matching
            case DataPoint(String id, _, _, _, _, Optional<DataPoint> prev) when prev.isPresent() ->
                "Sequential point: " + id + " with previous data";
                
            // Simple record pattern
            case DataPoint(String id, _, _, _, _, _) ->
                "Basic point: " + id;
                
            // Default case
            default -> "Not a DataPoint";
        };
    }
    
    /**
     * Creates a sample DataPoint for demonstration.
     */
    public static DataPoint createSample() {
        return new DataPoint(
            "DP-" + System.currentTimeMillis() % 1000,
            Math.random() * 200,
            LocalDateTime.now(),
            Map.of("source", "sensor", "unit", "celsius"),
            List.of(new Tag("temperature", "ambient"), new Tag("location", "room")),
            Optional.empty()
        );
    }
    
    /**
     * Creates a sample DataPoint with a previous point.
     */
    public static DataPoint createSequential() {
        DataPoint previous = createSample();
        return new DataPoint(
            "DP-" + System.currentTimeMillis() % 1000,
            previous.value() * (0.8 + Math.random() * 0.4), // 80-120% of previous value
            LocalDateTime.now().plusMinutes(5),
            previous.metadata(),
            previous.tags(),
            Optional.of(previous)
        );
    }
}
package ca.bazlur.gatherers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Demonstrates Stream operations that simulate the behavior of Gatherers,
 * a JDK 22+ feature for advanced stream processing.
 * Shows window operations, chunking, and sliding averages.
 */
public class GathererDemo {

    public static void run() {
        System.out.println("=== Stream Gatherers Demo ===");

        // Generate sample data (simulated temperature readings)
        List<Double> temperatures = generateTemperatureData(20);
        System.out.println("\nRaw temperature readings:");
        printData(temperatures);

        // Demo 1: Window operation (simulating Gatherers.window)
        demoWindowOperation(temperatures);

        // Demo 2: Chunk operation (simulating Gatherers.chunk)
        demoChunkOperation(temperatures);

        // Demo 3: Custom sliding average operation
        demoSlidingAverageOperation(temperatures);
    }

    private static void demoWindowOperation(List<Double> data) {
        System.out.println("\n1. Using window operation (size 3):");
        System.out.println("   Groups elements into overlapping windows of size 3");

        // Simulate window gatherer with sliding window implementation
        List<List<Double>> windows = IntStream.range(0, data.size() - 2)
            .mapToObj(i -> data.subList(i, i + 3))
            .toList();

        windows.forEach(window -> System.out.printf("   Window: %s, Avg: %.2f%n", 
                                                  window, window.stream().mapToDouble(d -> d).average().orElse(0)));
    }

    private static void demoChunkOperation(List<Double> data) {
        System.out.println("\n2. Using chunk operation (size 5):");
        System.out.println("   Groups elements into non-overlapping chunks of size 5");

        // Simulate chunk gatherer with partitioning
        List<List<Double>> chunks = IntStream.range(0, (data.size() + 4) / 5)
            .mapToObj(i -> data.subList(
                i * 5, 
                Math.min((i + 1) * 5, data.size())
            ))
            .toList();

        chunks.forEach(chunk -> System.out.printf("   Chunk: %s, Avg: %.2f%n", 
                                                chunk, chunk.stream().mapToDouble(d -> d).average().orElse(0)));
    }

    private static void demoSlidingAverageOperation(List<Double> data) {
        System.out.println("\n3. Using sliding average operation (window size 3):");
        System.out.println("   Calculates moving averages with window size 3");

        // Calculate sliding averages manually
        List<Double> movingAverages = new ArrayList<>();
        for (int i = 0; i <= data.size() - 3; i++) {
            double sum = data.get(i) + data.get(i + 1) + data.get(i + 2);
            movingAverages.add(sum / 3);
        }

        movingAverages.forEach(avg -> System.out.printf("   Moving Avg: %.2f%n", avg));
    }

    private static List<Double> generateTemperatureData(int count) {
        List<Double> data = new ArrayList<>(count);
        double baseTemp = 20.0; // Base temperature in Celsius

        for (int i = 0; i < count; i++) {
            // Generate temperatures with some random variation
            double temp = baseTemp + ThreadLocalRandom.current().nextDouble(-5, 5);
            data.add(Math.round(temp * 10.0) / 10.0); // Round to 1 decimal place
        }

        return data;
    }

    private static void printData(List<Double> data) {
        System.out.print("   ");
        for (int i = 0; i < data.size(); i++) {
            System.out.printf("%.1f", data.get(i));
            if (i < data.size() - 1) {
                System.out.print(", ");
            }
            // Line break every 10 items for readability
            if ((i + 1) % 10 == 0 && i < data.size() - 1) {
                System.out.print("\n   ");
            }
        }
        System.out.println();
    }
}

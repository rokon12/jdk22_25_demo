
package ca.bazlur.gatherers;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;
import java.util.stream.Gatherers;
import java.util.stream.IntStream;

/**
 * Demonstrates the new Java 22+ Gatherers API functionality. The examples progress from simple to complex showing:
 *
 * <h2>Level 1: Built-in Gatherers</h2>
 * <ul>
 *   <li>{@code Gatherers.fold()} - Simple accumulation</li>
 *   <li>{@code Gatherers.scan()} - Running calculations</li>
 *   <li>{@code Gatherers.mapConcurrent()} - Parallel processing</li>
 * </ul>
 *
 * <h2>Level 2: Window Operations</h2>
 * <ul>
 *   <li>{@code Gatherers.windowFixed()} - Non-overlapping windows</li>
 *   <li>{@code Gatherers.windowSliding()} - Overlapping windows</li>
 * </ul>
 *
 * <h2>Level 3: Stateful Processing</h2>
 * <ul>
 *   <li>Complex scan operations (running max, product)</li>
 *   <li>Stateful fold operations for statistics</li>
 * </ul>
 *
 * <h2>Level 4: Custom Gatherers</h2>
 * <ul>
 *   <li>{@code distinctByKey()} - Custom distinct logic</li>
 *   <li>{@code takeWhileIncreasing()} - Conditional processing</li>
 *   <li>{@code batchByCondition()} - Dynamic batching</li>
 * </ul>
 *
 * <h2>Level 5: Advanced Custom Gatherers</h2>
 * <ul>
 *   <li>{@code movingAverage()} - Statistical processing</li>
 *   <li>{@code trendDetection()} - Pattern recognition</li>
 *   <li>{@code peakValleyDetection()} - Signal analysis</li>
 * </ul>
 *
 * <h2>Level 6: Real-World Scenarios</h2>
 * <ul>
 *   <li>{@code anomalyDetection()} - Real-time monitoring</li>
 *   <li>{@code windowAnalysis()} - Multi-stage processing</li>
 *   <li>{@code performanceMonitoring()} - System monitoring</li>
 * </ul>
 * <p>
 * Each level builds on the previous one, demonstrating the power and flexibility of the Gatherers API
 * for sophisticated stream processing scenarios.
 *
 * @see <a href="https://dev.java/learn/api/streams/gatherers/">Gatherers API Guide</a>
 * @see <a href="https://openjdk.org/jeps/485">JEP 461</a>
 */
public class GathererDemo {

	public static void run() {
		System.out.println("=== Java 22+ Stream Gatherers API Demo ===");

		// Level 1: Built-in Gatherers - Simple Examples
		demoSimpleBuiltInGatherers();

		// Level 2: Window Operations
		demoWindowOperations();

		// Level 3: Stateful Processing
		demoStatefulProcessing();

		// Level 4: Custom Gatherers
		demoCustomGatherers();

		// Level 5: Advanced Custom Gatherers
		demoAdvancedCustomGatherers();

		// Level 6: Real-World Complex Scenarios
		demoRealWorldScenarios();
	}

	// ===== LEVEL 1: SIMPLE BUILT-IN GATHERERS =====
	private static void demoSimpleBuiltInGatherers() {
		System.out.println("\n=== LEVEL 1: Simple Built-in Gatherers ===");

		List<Integer> numbers = IntStream.rangeClosed(1, 10).boxed().toList();
		System.out.println("Input: " + numbers);

		// Example 1.1: Simple fold operation
		System.out.println("\n1.1 Fold - Sum with custom accumulator:");
		int sum = numbers.stream()
				.gather(Gatherers.fold(() -> 0, Integer::sum))
				.findFirst()
				.orElse(0);
		System.out.println("   Sum: " + sum);

		// Example 1.2: Scan - Running totals
		System.out.println("\n1.2 Scan - Running totals:");
		List<Integer> runningTotals = numbers.stream()
				.gather(Gatherers.scan(() -> 0, Integer::sum))
				.toList();
		System.out.println("   Running totals: " + runningTotals);

		// Example 1.3: Simple mapping with mapConcurrent
		System.out.println("\n1.3 MapConcurrent - Parallel square calculation:");
		List<Integer> squares = numbers.stream()
				.gather(Gatherers.mapConcurrent(4, x -> x * x))
				.toList();
		System.out.println("   Squares: " + squares);
	}

	// ===== LEVEL 2: WINDOW OPERATIONS =====
	private static void demoWindowOperations() {
		System.out.println("\n=== LEVEL 2: Window Operations ===");

		List<Double> temperatures = generateTemperatureData(15);
		System.out.println("Temperature data:");
		printTemperatures(temperatures);

		// Example 2.1: Fixed windows
		System.out.println("\n2.1 Fixed windows (size 3):");
		List<List<Double>> fixedWindows = temperatures.stream()
				.gather(Gatherers.windowFixed(3))
				.toList();

		fixedWindows.forEach(window -> {
			double avg = window.stream().mapToDouble(d -> d).average().orElse(0);
			System.out.printf("   Window: %s → Avg: %.2f°C%n", formatWindow(window), avg);
		});

		// Example 2.2: Sliding windows
		System.out.println("\n2.2 Sliding windows (size 4):");
		List<List<Double>> slidingWindows = temperatures.stream()
				.gather(Gatherers.windowSliding(4))
				.toList();

		slidingWindows.forEach(window -> {
			double min = window.stream().mapToDouble(d -> d).min().orElse(0);
			double max = window.stream().mapToDouble(d -> d).max().orElse(0);
			System.out.printf("   Window: %s → Min: %.1f°C, Max: %.1f°C%n",
					formatWindow(window), min, max);
		});
	}

	// ===== LEVEL 3: STATEFUL PROCESSING =====
	private static void demoStatefulProcessing() {
		System.out.println("\n=== LEVEL 3: Stateful Processing ===");

		List<Integer> values = List.of(1, 5, 3, 8, 2, 9, 4, 7, 6);
		System.out.println("Input values: " + values);

		// Example 3.1: Scan with different operations
		System.out.println("\n3.1 Scan operations:");

		// Running maximum
		List<Integer> runningMax = values.stream()
				.gather(Gatherers.scan(() -> Integer.MIN_VALUE, Integer::max))
				.toList();
		System.out.println("   Running maximum: " + runningMax);

		// Running product (with overflow protection)
		List<Long> runningProduct = values.stream()
				.gather(Gatherers.scan(() -> 1L, (acc, val) -> acc * val))
				.toList();
		System.out.println("   Running product: " + runningProduct);

		// Example 3.2: Complex fold operations
		System.out.println("\n3.2 Complex fold - Statistics:");
		Statistics stats = values.stream()
				.gather(Gatherers.fold(
						Statistics::new,
						Statistics::add
				))
				.findFirst()
				.orElse(new Statistics());

		System.out.printf("   Count: %d, Sum: %d, Avg: %.2f, Min: %d, Max: %d%n",
				stats.count, stats.sum, stats.getAverage(), stats.min, stats.max);
	}

	// ===== LEVEL 4: CUSTOM GATHERERS =====
	private static void demoCustomGatherers() {
		System.out.println("\n=== LEVEL 4: Custom Gatherers ===");

		List<String> words = List.of("hello", "world", "java", "gatherers", "stream", "api");
		System.out.println("Words: " + words);

		// Example 4.1: Custom distinct by length
		System.out.println("\n4.1 Custom distinct by length:");
		List<String> distinctByLength = words.stream()
				.gather(distinctByKey(String::length))
				.toList();
		System.out.println("   Distinct by length: " + distinctByLength);

		// Example 4.2: Custom take while condition changes
		System.out.println("\n4.2 Take while length increasing:");
		List<String> increasingLength = words.stream()
				.gather(takeWhileIncreasing(String::length))
				.toList();
		System.out.println("   Increasing length sequence: " + increasingLength);

		// Example 4.3: Custom grouping gatherer
		List<Integer> numbers = List.of(1, 4, 2, 8, 5, 7, 3, 9, 6);
		System.out.println("\nNumbers: " + numbers);
		System.out.println("\n4.3 Custom batch by condition (sum <= 10):");
		List<List<Integer>> batches = numbers.stream()
				.gather(batchByCondition(batch -> batch.stream().mapToInt(i -> i).sum() <= 10))
				.toList();

		batches.forEach(batch -> {
			int sum = batch.stream().mapToInt(i -> i).sum();
			System.out.printf("   Batch: %s → Sum: %d%n", batch, sum);
		});
	}

	// ===== LEVEL 5: ADVANCED CUSTOM GATHERERS =====
	private static void demoAdvancedCustomGatherers() {
		System.out.println("\n=== LEVEL 5: Advanced Custom Gatherers ===");

		List<Double> stockPrices = generateStockPrices(20);
		System.out.println("Stock prices (first 10): " +
		                   stockPrices.stream().limit(10).map(p -> String.format("$%.2f", p))
				                   .collect(Collectors.joining(", ")));

		// Example 5.1: Moving average gatherer
		System.out.println("\n5.1 Moving average (window 3):");
		List<Double> movingAvgs = stockPrices.stream()
				.gather(movingAverage(3))
				.toList();

		movingAvgs.forEach(avg -> System.out.printf("   $%.2f%n", avg));

		// Example 5.2: Trend detection gatherer
		System.out.println("\n5.2 Trend detection:");
		List<TrendSignal> trends = stockPrices.stream()
				.gather(trendDetection(3))
				.toList();

		trends.forEach(trend ->
				System.out.printf("   Price: $%.2f → %s%n", trend.price(), trend.trend()));

		// Example 5.3: Peak/Valley detection
		System.out.println("\n5.3 Peak/Valley detection:");
		List<PeakValley> peaks = stockPrices.stream()
				.gather(peakValleyDetection())
				.toList();

		peaks.forEach(pv ->
				System.out.printf("   %s at $%.2f (index %d)%n", pv.type(), pv.value(), pv.index()));
	}

	// ===== LEVEL 6: REAL-WORLD COMPLEX SCENARIOS =====
	private static void demoRealWorldScenarios() {
		System.out.println("\n=== LEVEL 6: Real-World Complex Scenarios ===");

		List<SensorReading> sensorData = generateSensorData(30);
		System.out.println("Generated " + sensorData.size() + " sensor readings");

		// Example 6.1: Real-time anomaly detection
		System.out.println("\n6.1 Real-time anomaly detection:");
		List<AnomalyAlert> anomalies = sensorData.stream()
				.gather(anomalyDetection(5, 2.0)) // window=5, threshold=2 std devs
				.toList();

		if (anomalies.isEmpty()) {
			System.out.println("   No anomalies detected");
		} else {
			anomalies.forEach(alert ->
					System.out.printf("   🚨 Anomaly: Sensor %d, Value %.2f, Expected %.2f±%.2f%n",
							alert.sensorId(), alert.value(), alert.expectedMean(), alert.stdDev()));
		}

		// Example 6.2: Complex data pipeline
		System.out.println("\n6.2 Multi-stage data processing pipeline:");
		List<ProcessedMetric> processed = sensorData.stream()
				.filter(reading -> reading.quality() != Quality.LOW)
				.gather(Gatherers.windowSliding(5))
				.gather(windowAnalysis())
				.toList();

		processed.forEach(metric ->
				System.out.printf("   Window %d: Avg=%.2f, Stability=%.3f, Quality=%s%n",
						metric.windowId(), metric.average(), metric.stabilityIndex(), metric.overallQuality()));

		// Example 6.3: Performance monitoring
		System.out.println("\n6.3 Performance monitoring with alerts:");
		List<PerformanceAlert> alerts = sensorData.stream()
				.gather(performanceMonitoring(0.1, 0.8)) // degradation threshold, alert threshold
				.toList();

		alerts.forEach(alert ->
				System.out.printf("   ⚡ Performance Alert: %s - Current: %.2f, Baseline: %.2f%n",
						alert.severity(), alert.currentMetric(), alert.baselineMetric()));
	}

	// ===== CUSTOM GATHERER IMPLEMENTATIONS =====

	// Simple distinct by key gatherer
	public static <T, K> Gatherer<T, ?, T> distinctByKey(java.util.function.Function<T, K> keyExtractor) {
		return Gatherer.ofSequential(
				HashSet::new,
				(seen, element, downstream) -> {
					K key = keyExtractor.apply(element);
					if (seen.add(key)) {
						return downstream.push(element);
					}
					return true;
				}
		);
	}

	// Take while increasing gatherer
	public static <T, R extends Comparable<R>> Gatherer<T, ?, T> takeWhileIncreasing(
			java.util.function.Function<T, R> mapper) {
		return Gatherer.ofSequential(
				() -> new Object() {
					R lastValue = null;
				},
				(state, element, downstream) -> {
					R currentValue = mapper.apply(element);
					if (state.lastValue == null || currentValue.compareTo(state.lastValue) > 0) {
						state.lastValue = currentValue;
						return downstream.push(element);
					}
					return false; // Stop processing
				}
		);
	}

	// Batch by condition gatherer
	// Batch by condition gatherer
	public static <T> Gatherer<T, ?, List<T>> batchByCondition(Predicate<List<T>> condition) {
		return Gatherer.<T, ArrayList<T>, List<T>>ofSequential(
				ArrayList::new,
				(batch, element, downstream) -> {
					batch.add(element);
					if (!condition.test(batch)) {
						// Remove last element and push current batch
						batch.remove(batch.size() - 1);
						if (!batch.isEmpty()) {
							boolean result = downstream.push(new ArrayList<>(batch));
							if (!result) return false;
						}
						batch.clear();
						batch.add(element);
					}
					return true;
				},
				(batch, downstream) -> {
					if (!batch.isEmpty()) {
						downstream.push(new ArrayList<>(batch));
					}
				}
		);
	}

	// Moving average gatherer
	public static Gatherer<Double, ?, Double> movingAverage(int windowSize) {
		return Gatherer.ofSequential(
				() -> new ArrayDeque<Double>(),
				(window, element, downstream) -> {
					window.add(element);
					if (window.size() > windowSize) {
						window.removeFirst();
					}
					if (window.size() == windowSize) {
						double avg = window.stream().mapToDouble(d -> d).average().orElse(0);
						return downstream.push(avg);
					}
					return true;
				}
		);
	}

	// Trend detection gatherer
	public static Gatherer<Double, ?, TrendSignal> trendDetection(int windowSize) {
		return Gatherer.ofSequential(
				() -> new ArrayDeque<Double>(),
				(window, element, downstream) -> {
					window.add(element);
					if (window.size() > windowSize) {
						window.removeFirst();
					}
					if (window.size() == windowSize) {
						Trend trend = calculateTrend(new ArrayList<>(window));
						return downstream.push(new TrendSignal(element, trend));
					}
					return true;
				}
		);
	}

	// Peak/Valley detection gatherer
	public static Gatherer<Double, ?, PeakValley> peakValleyDetection() {
		return Gatherer.ofSequential(
				() -> new Object() {
					List<Double> window = new ArrayList<>();
					int index = 0;
				},
				(state, element, downstream) -> {
					state.window.add(element);
					if (state.window.size() >= 3) {
						int mid = state.window.size() - 2;
						double prev = state.window.get(mid - 1);
						double curr = state.window.get(mid);
						double next = state.window.get(mid + 1);

						if (curr > prev && curr > next) {
							boolean result = downstream.push(new PeakValley("PEAK", curr, state.index - 1));
							if (!result) return false;
						} else if (curr < prev && curr < next) {
							boolean result = downstream.push(new PeakValley("VALLEY", curr, state.index - 1));
							if (!result) return false;
						}

						if (state.window.size() > 3) {
							state.window.remove(0);
						}
					}
					state.index++;
					return true;
				}
		);
	}

	// Anomaly detection gatherer
	public static Gatherer<SensorReading, ?, AnomalyAlert> anomalyDetection(int windowSize, double threshold) {
		return Gatherer.ofSequential(
				() -> new ArrayDeque<Double>(),
				(window, reading, downstream) -> {
					window.add(reading.value());
					if (window.size() > windowSize) {
						window.removeFirst();
					}

					if (window.size() == windowSize) {
						double mean = window.stream().mapToDouble(d -> d).average().orElse(0);
						double variance = window.stream()
								.mapToDouble(d -> Math.pow(d - mean, 2))
								.average().orElse(0);
						double stdDev = Math.sqrt(variance);

						if (Math.abs(reading.value() - mean) > threshold * stdDev) {
							return downstream.push(new AnomalyAlert(
									reading.id(), reading.value(), mean, stdDev));
						}
					}
					return true;
				}
		);
	}

	// Window analysis gatherer
	public static Gatherer<List<SensorReading>, ?, ProcessedMetric> windowAnalysis() {
		return Gatherer.ofSequential(
				() -> new Object() {
					int windowCounter = 0;
				},
				(state, window, downstream) -> {
					state.windowCounter++;

					double avg = window.stream().mapToDouble(SensorReading::value).average().orElse(0);
					double variance = window.stream()
							.mapToDouble(r -> Math.pow(r.value() - avg, 2))
							.average().orElse(0);
					double stabilityIndex = 1.0 / (1.0 + Math.sqrt(variance));

					long highQualityCount = window.stream()
							.filter(r -> r.quality() == Quality.HIGH)
							.count();
					Quality overallQuality = highQualityCount >= window.size() * 0.7 ?
							Quality.HIGH : Quality.MEDIUM;

					return downstream.push(new ProcessedMetric(
							state.windowCounter, avg, stabilityIndex, overallQuality));
				}
		);
	}

	// Performance monitoring gatherer
	public static Gatherer<SensorReading, ?, PerformanceAlert> performanceMonitoring(
			double degradationThreshold, double alertThreshold) {
		return Gatherer.ofSequential(
				() -> new Object() {
					double baseline = -1;
					List<Double> recentValues = new ArrayList<>();
				},
				(state, reading, downstream) -> {
					state.recentValues.add(reading.value());
					if (state.recentValues.size() > 10) {
						state.recentValues.remove(0);
					}

					if (state.baseline < 0 && state.recentValues.size() >= 5) {
						state.baseline = state.recentValues.stream()
								.mapToDouble(d -> d).average().orElse(0);
					}

					if (state.baseline > 0 && state.recentValues.size() >= 5) {
						double currentMetric = state.recentValues.stream()
								.mapToDouble(d -> d).average().orElse(0);
						double degradation = Math.abs(currentMetric - state.baseline) / state.baseline;

						if (degradation > alertThreshold) {
							String severity = degradation > 0.5 ? "CRITICAL" : "WARNING";
							return downstream.push(new PerformanceAlert(
									severity, currentMetric, state.baseline));
						}
					}
					return true;
				}
		);
	}

	// ===== UTILITY METHODS =====

	private static Trend calculateTrend(List<Double> values) {
		if (values.size() < 2) return Trend.STABLE;

		int up = 0, down = 0;
		for (int i = 1; i < values.size(); i++) {
			if (values.get(i) > values.get(i - 1)) up++;
			else if (values.get(i) < values.get(i - 1)) down++;
		}

		if (up > down) return Trend.INCREASING;
		else if (down > up) return Trend.DECREASING;
		else return Trend.STABLE;
	}

	private static String formatWindow(List<Double> window) {
		return window.stream()
				.map(d -> String.format("%.1f", d))
				.collect(Collectors.joining(", ", "[", "]"));
	}

	// ===== DATA GENERATION METHODS =====

	private static List<Double> generateTemperatureData(int count) {
		List<Double> data = new ArrayList<>(count);
		double baseTemp = 20.0;

		for (int i = 0; i < count; i++) {
			double temp = baseTemp + ThreadLocalRandom.current().nextDouble(-5, 5);
			data.add(Math.round(temp * 10.0) / 10.0);
		}
		return data;
	}

	private static List<Double> generateStockPrices(int count) {
		List<Double> prices = new ArrayList<>(count);
		double price = 100.0;

		for (int i = 0; i < count; i++) {
			price += ThreadLocalRandom.current().nextDouble(-2, 2);
			prices.add(Math.round(price * 100.0) / 100.0);
		}
		return prices;
	}

	private static List<SensorReading> generateSensorData(int count) {
		List<SensorReading> data = new ArrayList<>(count);
		Quality[] qualities = Quality.values();

		for (int i = 0; i < count; i++) {
			int id = i + 1;
			double value = 50 + ThreadLocalRandom.current().nextDouble(-15, 15);
			// Occasionally add anomalies
			if (ThreadLocalRandom.current().nextDouble() < 0.1) {
				value += ThreadLocalRandom.current().nextDouble(-30, 30);
			}
			Quality quality = qualities[ThreadLocalRandom.current().nextInt(qualities.length)];
			data.add(new SensorReading(id, value, quality));
		}
		return data;
	}

	private static void printTemperatures(List<Double> temps) {
		System.out.print("   ");
		for (int i = 0; i < temps.size(); i++) {
			System.out.printf("%.1f°C", temps.get(i));
			if (i < temps.size() - 1) System.out.print(", ");
			if ((i + 1) % 8 == 0 && i < temps.size() - 1) System.out.print("\n   ");
		}
		System.out.println();
	}

	// ===== RECORD CLASSES =====

	public record SensorReading(int id, double value, Quality quality) {
	}

	public record TrendSignal(double price, Trend trend) {
	}

	public record PeakValley(String type, double value, int index) {
	}

	public record AnomalyAlert(int sensorId, double value, double expectedMean, double stdDev) {
	}

	public record ProcessedMetric(int windowId, double average, double stabilityIndex, Quality overallQuality) {
	}

	public record PerformanceAlert(String severity, double currentMetric, double baselineMetric) {
	}

	// ===== ENUMS =====

	public enum Quality {LOW, MEDIUM, HIGH}

	public enum Trend {INCREASING, DECREASING, STABLE}

	// ===== HELPER CLASSES =====

	public static class Statistics {
		int count = 0;
		int sum = 0;
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;

		public Statistics add(int value) {
			count++;
			sum += value;
			min = Math.min(min, value);
			max = Math.max(max, value);
			return this;
		}

		public double getAverage() {
			return count > 0 ? (double) sum / count : 0;
		}
	}
}
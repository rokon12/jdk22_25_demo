package ca.bazlur.concurrent;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.random.RandomGenerator;

/**
 * Demonstrates parent & child structured‑concurrency scopes where
 * the child runs on virtual threads. The method {@code runNested}
 * returns the list of successful child results ("A" and/or "B").
 */
public final class NestedScopesDemo {

	/**
	 * Runs the nested scopes demo.
	 */
	public static void run() throws InterruptedException {
		System.out.println("Running nested scopes demo with parent and child scopes...");

		List<String> result = runNested();
		System.out.println("Nested result = " + result);

		System.out.println("Running second example with child scope inside a virtual thread...");
		try (var parent = StructuredTaskScope.open()) {
			parent.fork(() -> {
				// child scope inside a virtual thread
				try (var child = StructuredTaskScope.open(
						StructuredTaskScope.Joiner.awaitAll(), cfg -> cfg.withThreadFactory(Thread.ofVirtual().factory()))) {
					child.fork(NestedScopesDemo::taskA);
					child.fork(NestedScopesDemo::taskB);
					child.join();               // waits both virtual subtasks
				}
				return null;
			});
			parent.join();
			System.out.println("Second example completed successfully");
		}
	}

	// --- demo tasks ---------------------------------------------------------
	private static String taskA() throws InterruptedException {
		Thread.sleep(RandomGenerator.getDefault().nextInt(50, 150));
		return "A";
	}

	private static String taskB() throws InterruptedException {
		Thread.sleep(RandomGenerator.getDefault().nextInt(60, 120));
		return "B";
	}

	/**
	 * Executes a parent scope on the current platform thread. Inside it we fork
	 * a child scope that uses a virtual‑thread factory and a 200 ms timeout.
	 * The parent waits for the child result and returns it.
	 */
	public static List<String> runNested() throws InterruptedException {
		try (var parent = StructuredTaskScope.open()) {

			// Parent forks a *single* subtask that itself opens a child scope
			StructuredTaskScope.Subtask<List<String>> combined = parent.fork(() -> {
				try (var child = StructuredTaskScope.open(
						StructuredTaskScope.Joiner.<String>allSuccessfulOrThrow(),
						cfg -> cfg.withThreadFactory(Thread.ofVirtual().factory())
								.withTimeout(Duration.ofMillis(200)))) {

					StructuredTaskScope.Subtask<String> a = child.fork(NestedScopesDemo::taskA);
					StructuredTaskScope.Subtask<String> b = child.fork(NestedScopesDemo::taskB);

					// Wait for both virtual subtasks (or timeout)

					return child.join()
							.filter(st -> st.state() == StructuredTaskScope.Subtask.State.SUCCESS)
							.map(st -> st.get())
							.toList();
				}
			});

			// Parent waits for the child scope to finish
			parent.join();
			return combined.get();
		}
	}

	// Simple CLI hook for standalone testing
	public static void main(String[] args) throws InterruptedException {
		run();
	}
}

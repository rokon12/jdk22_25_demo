package ca.bazlur.model;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Base class for task implementations.
 * Provides common functionality for all task types.
 */
public abstract class Task {
	private final String id;
	private final String name;
	private final Instant createdAt;
	private Instant startedAt;
	private Instant completedAt;
	private TaskStatus status;

	/**
	 * Constructs a new Task with the given name.
	 */
	public Task(String name) {
		this.id = UUID.randomUUID().toString();
		this.name = name;
		this.createdAt = Instant.now();
		this.status = TaskStatus.CREATED;
	}

	/**
	 * Executes the task.
	 *
	 * @return true if the task completed successfully, false otherwise
	 */
	public final boolean execute() {
		if (status != TaskStatus.CREATED) {
			System.out.println("Task " + name + " cannot be executed (status: " + status + ")");
			return false;
		}

		try {
			status = TaskStatus.RUNNING;
			startedAt = Instant.now();
			System.out.println("Starting task: " + name);

			boolean result = doExecute();

			completedAt = Instant.now();
			status = result ? TaskStatus.COMPLETED : TaskStatus.FAILED;

			Duration duration = Duration.between(startedAt, completedAt);
			System.out.println("Task " + name + " " +
			                   (result ? "completed" : "failed") +
			                   " in " + duration.toMillis() + "ms");

			return result;
		} catch (Exception e) {
			completedAt = Instant.now();
			status = TaskStatus.FAILED;
			System.err.println("Task " + name + " failed with exception: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Implement this method to define the task's execution logic.
	 *
	 * @return true if the task completed successfully, false otherwise
	 * @throws Exception if an error occurs during execution
	 */
	protected abstract boolean doExecute() throws Exception;

	/**
	 * Gets the task's unique identifier.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the task's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the task's creation time.
	 */
	public Instant getCreatedAt() {
		return createdAt;
	}

	/**
	 * Gets the task's start time, or null if not started.
	 */
	public Instant getStartedAt() {
		return startedAt;
	}

	/**
	 * Gets the task's completion time, or null if not completed.
	 */
	public Instant getCompletedAt() {
		return completedAt;
	}

	/**
	 * Gets the task's current status.
	 */
	public TaskStatus getStatus() {
		return status;
	}

	/**
	 * Gets the task's execution duration, or null if not completed.
	 */
	public Duration getDuration() {
		if (startedAt == null || completedAt == null) {
			return null;
		}
		return Duration.between(startedAt, completedAt);
	}

	/**
	 * Enum representing the possible states of a task.
	 */
	public enum TaskStatus {
		CREATED,
		RUNNING,
		COMPLETED,
		FAILED
	}

	@Override
	public String toString() {
		return "Task{" +
		       "id='" + id + '\'' +
		       ", name='" + name + '\'' +
		       ", status=" + status +
		       '}';
	}
}
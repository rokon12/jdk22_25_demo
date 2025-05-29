package ca.bazlur.stablevalue;

/**
 * JEP 502: Stable Values (Preview) - Comprehensive Demo
 * <p>
 * This demo showcases the new Stable Values API introduced in Java 25.
 * Stable Values provide deferred immutability - they can be initialized
 * once at any time, but are immutable thereafter.
 * <p>
 * Key Benefits:
 * - Improved application startup (lazy initialization)
 * - Thread-safe initialization guarantees
 * - JVM constant-folding optimizations (same as final fields)
 * - More flexible than final fields, safer than mutable fields
 * <p>
 * To run this demo:
 * javac --release 25 --enable-preview StableValuesDemo.java
 * java --enable-preview StableValuesDemo
 */

import java.util.List;
import java.util.function.Supplier;

public class StableValuesDemo {

    void main() {
        run();
    }

   public static void run() {
        System.out.println("=== JEP 502: Stable Values Demo ===\n");

        // Demo 1: Basic StableValue usage
        basicStableValueDemo();

        // Demo 2: Stable Suppliers
        stableSupplierDemo();

        // Demo 3: Stable Lists
        stableListDemo();

        // Demo 4: Application startup optimization
        applicationStartupDemo();

        // Demo 5: Thread safety demonstration
        threadSafetyDemo();
    }

    /**
     * Demo 1: Basic StableValue Usage
     * Shows how StableValue replaces the pattern of mutable fields
     * with null-checking for lazy initialization.
     */
    static void basicStableValueDemo() {
        System.out.println("1. Basic StableValue Usage");
        System.out.println("==========================");

        class ExpensiveResource {
            private final String name;

            ExpensiveResource(String name) {
                this.name = name;
                System.out.println("  → Creating expensive resource: " + name);
                // Simulate expensive initialization
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }

            @Override
            public String toString() {
                return "Resource[" + name + "]";
            }
        }

        class ComponentOldWay {
            private ExpensiveResource resource = null; // Mutable field - not ideal

            ExpensiveResource getResource() {
                if (resource == null) {
                    resource = new ExpensiveResource("OldWay");
                }
                return resource;
            }
        }

        class ComponentNewWay {
            // StableValue provides deferred immutability
            private final StableValue<ExpensiveResource> resource = StableValue.of();

            ExpensiveResource getResource() {
                // orElseSet() initializes only once, even with concurrent access
                return resource.orElseSet(() -> new ExpensiveResource("NewWay"));
            }
        }

        System.out.println("Old way (mutable field):");
        ComponentOldWay oldComponent = new ComponentOldWay();
        System.out.println("  Component created - no resource initialized yet");
        System.out.println("  First access: " + oldComponent.getResource());
        System.out.println("  Second access: " + oldComponent.getResource());

        System.out.println("\nNew way (StableValue):");
        ComponentNewWay newComponent = new ComponentNewWay();
        System.out.println("  Component created - no resource initialized yet");
        System.out.println("  First access: " + newComponent.getResource());
        System.out.println("  Second access: " + newComponent.getResource());

        System.out.println();
    }

    /**
     * Demo 2: Stable Suppliers
     * Shows how to specify initialization logic at declaration time
     * while still deferring actual initialization until first use.
     */
    static void stableSupplierDemo() {
        System.out.println("2. Stable Suppliers");
        System.out.println("==================");

        class DatabaseConnection {
            private final String url;

            DatabaseConnection(String url) {
                this.url = url;
                System.out.println("  → Connecting to database: " + url);
                // Simulate connection setup
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
            }

            @Override
            public String toString() {
                return "DB[" + url + "]";
            }
        }

        class DataService {
            // Stable supplier - initialization logic defined at declaration
            private final Supplier<DatabaseConnection> dbConnection =
                    StableValue.supplier(() -> new DatabaseConnection("jdbc:postgresql://localhost/mydb"));

            void performQuery() {
                // Simple .get() call - no need for separate method
                DatabaseConnection db = dbConnection.get();
                System.out.println("  Executing query with " + db);
            }
        }

        System.out.println("Creating DataService...");
        DataService service = new DataService();
        System.out.println("DataService created - no DB connection yet");

        System.out.println("First query:");
        service.performQuery();

        System.out.println("Second query:");
        service.performQuery();

        System.out.println();
    }

    /**
     * Demo 3: Stable Lists
     * Shows how to create collections where elements are initialized
     * independently as they're accessed.
     */
    static void stableListDemo() {
        System.out.println("3. Stable Lists");
        System.out.println("===============");

        class WorkerThread {
            private final int id;

            WorkerThread(int id) {
                this.id = id;
                System.out.println("  → Creating worker thread " + id);
                // Simulate thread pool initialization
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                }
            }

            @Override
            public String toString() {
                return "Worker-" + id;
            }
        }

        class ThreadPool {
            private static final int POOL_SIZE = 5;

            // Stable list - elements initialized independently as needed
            private final List<WorkerThread> workers =
                    StableValue.list(POOL_SIZE, index -> new WorkerThread(index));

            WorkerThread getWorker(int index) {
                return workers.get(index % POOL_SIZE);
            }
        }

        System.out.println("Creating thread pool...");
        ThreadPool pool = new ThreadPool();
        System.out.println("Pool created - no workers initialized yet");

        System.out.println("\nAccessing workers:");
        System.out.println("Getting worker 0: " + pool.getWorker(0));
        System.out.println("Getting worker 2: " + pool.getWorker(2));
        System.out.println("Getting worker 0 again: " + pool.getWorker(0)); // Already initialized
        System.out.println("Getting worker 1: " + pool.getWorker(1));

        System.out.println();
    }

    /**
     * Demo 4: Application Startup Optimization
     * Shows how stable values can dramatically improve application startup
     * by deferring initialization of components until they're needed.
     */
    static void applicationStartupDemo() {
        System.out.println("4. Application Startup Optimization");
        System.out.println("===================================");

        class Application {
            // Application components using stable values for deferred initialization
            static final StableValue<OrderController> orders = StableValue.of();
            static final StableValue<UserService> users = StableValue.of();

            static OrderController getOrderController() {
                return orders.orElseSet(OrderController::new);
            }

            static UserService getUserService() {
                return users.orElseSet(UserService::new);
            }
        }

        long startTime = System.currentTimeMillis();
        System.out.println("Starting application...");

        // Application starts instantly - no components are initialized yet!
        System.out.println("Application started in " + (System.currentTimeMillis() - startTime) + "ms");

        System.out.println("\nNow using order service:");
        Application.getOrderController().processOrder();

        System.out.println("\nNow using user service:");
        Application.getUserService().authenticateUser();

        System.out.println();
    }

    static class Logger {
        private final String name;

        Logger(String name) {
            this.name = name;
            System.out.println("  → Initializing logger for " + name);
            // Simulate expensive logger setup (reading config, creating files, etc.)
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
        }

        void info(String message) {
            System.out.println("  [" + name + "] " + message);
        }
    }

    static class OrderController {
        // Stable supplier for logger - initialized only when first used
        private final Supplier<Logger> logger =
                StableValue.supplier(() -> new Logger("OrderController"));

        void processOrder() {
            logger.get().info("Processing order");
        }
    }

    static class UserService {
        private final Supplier<Logger> logger =
                StableValue.supplier(() -> new Logger("UserService"));

        void authenticateUser() {
            logger.get().info("Authenticating user");
        }
    }

    /**
     * Demo 5: Thread Safety
     * Shows that stable values guarantee initialization happens only once,
     * even with concurrent access from multiple threads.
     */
    static void threadSafetyDemo() {
        System.out.println("5. Thread Safety Demonstration");
        System.out.println("==============================");

        class SharedResource {
            private static int instanceCount = 0;
            private final int id;

            SharedResource() {
                this.id = ++instanceCount;
                System.out.println("  → Creating SharedResource #" + id +
                        " (Thread: " + Thread.currentThread().getName() + ")");
                // Simulate some initialization work
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }

            @Override
            public String toString() {
                return "SharedResource#" + id;
            }
        }

        class ThreadSafeComponent {
            // StableValue guarantees single initialization even with concurrent access
            private final StableValue<SharedResource> resource = StableValue.of();

            SharedResource getResource() {
                return resource.orElseSet(SharedResource::new);
            }
        }

        ThreadSafeComponent component = new ThreadSafeComponent();

        System.out.println("Launching 5 threads to access the same resource concurrently...");

        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            final int threadNum = i;
            threads[i] = new Thread(() -> {
                SharedResource resource = component.getResource();
                System.out.println("  Thread " + threadNum + " got: " + resource);
            }, "Thread-" + i);
        }

        // Start all threads simultaneously
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("All threads completed. Notice only ONE SharedResource was created!");
        System.out.println();
    }
}

/**
 * Additional Examples and Patterns
 *
 * Here are some additional patterns you might find useful with Stable Values:
 */
class AdditionalExamples {

    /**
     * Pattern: Configuration Loading
     * Load configuration files only when needed, ensuring thread safety.
     */
    static class ConfigService {
        private final StableValue<Properties> config = StableValue.of();

        Properties getConfig() {
            return config.orElseSet(() -> {
                System.out.println("Loading configuration...");
                Properties props = new Properties();
                // Load from file, database, etc.
                props.setProperty("database.url", "jdbc:postgresql://localhost/myapp");
                props.setProperty("cache.size", "1000");
                return props;
            });
        }
    }

    /**
     * Pattern: Expensive Computation Caching
     * Cache results of expensive computations using stable values.
     */
    static class MathService {
        private final StableValue<Double> expensiveResult = StableValue.of();

        double getExpensiveComputation() {
            return expensiveResult.orElseSet(() -> {
                System.out.println("Performing expensive computation...");
                // Simulate complex calculation
                double result = 0;
                for (int i = 0; i < 1000000; i++) {
                    result += Math.sin(i) * Math.cos(i);
                }
                return result;
            });
        }
    }

    /**
     * Pattern: Dependency Injection with Stable Values
     * Create a simple dependency injection system using stable values.
     */
    static class DIContainer {
        private final StableValue<DatabaseService> dbService = StableValue.of();
        private final StableValue<EmailService> emailService = StableValue.of();

        DatabaseService getDatabaseService() {
            return dbService.orElseSet(() -> new DatabaseService(/* config */));
        }

        EmailService getEmailService() {
            return emailService.orElseSet(() -> new EmailService(getDatabaseService()));
        }
    }

    // Dummy classes for the examples above
    static class Properties {
        private java.util.Properties props = new java.util.Properties();

        void setProperty(String key, String value) {
            props.setProperty(key, value);
        }

        String getProperty(String key) {
            return props.getProperty(key);
        }
    }

    static class DatabaseService {
        DatabaseService() {
        }
    }

    static class EmailService {
        EmailService(DatabaseService db) {
        }
    }
}

/**
 * Performance Comparison Notes:
 *
 * Traditional approaches vs Stable Values:
 *
 * 1. Final fields:
 *    ✓ Constant folding optimizations
 *    ✗ Must initialize eagerly (poor startup)
 *    ✗ Initialization order constraints
 *
 * 2. Mutable fields with lazy initialization:
 *    ✓ Flexible initialization timing
 *    ✗ No constant folding optimizations
 *    ✗ Thread safety issues
 *    ✗ Performance overhead of null checks
 *
 * 3. Double-checked locking:
 *    ✓ Thread safe
 *    ✓ Flexible initialization timing
 *    ✗ Complex, error-prone code
 *    ✗ No constant folding optimizations
 *    ✗ Requires volatile fields
 *
 * 4. Stable Values:
 *    ✓ Constant folding optimizations (like final)
 *    ✓ Flexible initialization timing
 *    ✓ Thread safe by design
 *    ✓ Simple, clean code
 *    ✓ JVM optimizations enabled
 *
 * Stable Values provide the best of all worlds!
 */
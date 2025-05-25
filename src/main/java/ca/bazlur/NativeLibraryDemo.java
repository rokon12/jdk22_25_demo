package ca.bazlur;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.foreign.FunctionDescriptor.of;
import static java.lang.foreign.ValueLayout.*;

/**
 * The NativeLibraryDemo class demonstrates usage of the Foreign Function & Memory API
 * introduced in recent versions of Java for interacting with native code, memory, and libraries.
 *
 * This class showcases various use-cases such as:
 * - Working with custom structs
 * - Callback mechanisms (upcalls)
 * - Asynchronous operations with native memory
 * - Handling complex memory layouts involving nested structures and arrays
 */
public class NativeLibraryDemo {

    // Define a struct layout for a Point3D
    private static final GroupLayout POINT3D_LAYOUT = MemoryLayout.structLayout(
            JAVA_DOUBLE.withName("x"),
            JAVA_DOUBLE.withName("y"),
            JAVA_DOUBLE.withName("z")
    ).withName("Point3D");

    // Define a struct layout for a Person
    private static final GroupLayout PERSON_LAYOUT = MemoryLayout.structLayout(
            ADDRESS.withName("name"),
            JAVA_INT.withName("age"),
            MemoryLayout.paddingLayout(4), // alignment padding
            JAVA_DOUBLE.withName("salary")
    ).withName("Person");

    private static final Linker linker = Linker.nativeLinker();
    private static final SymbolLookup lookup = linker.defaultLookup();

    public static void run() {
        System.out.println("=== Advanced Foreign Function & Memory API Demo ===");

        demoStructOperations();
        demoCallbackMechanism();
        demoAsyncNativeCall();
        demoComplexMemoryLayouts();
    }

    /**
     * Demonstrates working with custom structs and calling C functions
     * that operate on structured data.
     */
    private static void demoStructOperations() {
        System.out.println("\n=== Struct Operations Demo ===");

        try (Arena arena = Arena.ofConfined()) {
            // Create and populate Point3D structs
            MemorySegment point1 = arena.allocate(POINT3D_LAYOUT);
            MemorySegment point2 = arena.allocate(POINT3D_LAYOUT);

            // Set values using field handles
            var xHandle = POINT3D_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("x"));
            var yHandle = POINT3D_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("y"));
            var zHandle = POINT3D_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("z"));

            // Point 1: (1.0, 2.0, 3.0)
            xHandle.set(point1, 0L, 1.0);
            yHandle.set(point1, 0L, 2.0);
            zHandle.set(point1, 0L, 3.0);

            // Point 2: (4.0, 5.0, 6.0)
            xHandle.set(point2, 0L, 4.0);
            yHandle.set(point2, 0L, 5.0);
            zHandle.set(point2, 0L, 6.0);

            System.out.println("Point 1: (" + xHandle.get(point1, 0L) + ", " +
                               yHandle.get(point1, 0L) + ", " + zHandle.get(point1, 0L) + ")");
            System.out.println("Point 2: (" + xHandle.get(point2, 0L) + ", " +
                               yHandle.get(point2, 0L) + ", " + zHandle.get(point2, 0L) + ")");

            // Calculate distance manually (simulating a native function)
            double dx = (double) xHandle.get(point2, 0L) - (double) xHandle.get(point1, 0L);
            double dy = (double) yHandle.get(point2, 0L) - (double) yHandle.get(point1, 0L);
            double dz = (double) zHandle.get(point2, 0L) - (double) zHandle.get(point1, 0L);
            double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);

            System.out.println("Distance between points: " + distance);
        }
    }

    /**
     * Demonstrates upcalls/callbacks - calling Java code from native code.
     */
    private static void demoCallbackMechanism() {
        System.out.println("\n=== Callback Mechanism Demo ===");

        try (Arena arena = Arena.ofConfined()) {
            // Define callback function descriptor
            FunctionDescriptor callbackDesc = FunctionDescriptor.of(JAVA_INT, JAVA_INT, JAVA_INT);

            // Create a Java method to be called from native code
            MethodHandle javaCallback = MethodHandles.lookup().findStatic(
                    NativeLibraryDemo.class, "addCallback",
                    MethodType.methodType(int.class, int.class, int.class)
            );

            // Create an upcall stub
            MemorySegment callbackAddress = linker.upcallStub(javaCallback, callbackDesc, arena);

            System.out.println("Created callback at address: " + callbackAddress.address());

            // Simulate calling the callback (in real scenario, this would be done by native code)
            try {
                MethodHandle invoker = linker.downcallHandle(callbackAddress, callbackDesc);
                int result = (int) invoker.invokeExact(15, 25);
                System.out.println("Callback result: " + result);
            } catch (Throwable t) {
                System.err.println("Error invoking callback: " + t.getMessage());
            }
        } catch (NoSuchMethodException | IllegalAccessException e) {
	        throw new RuntimeException(e);
        }
    }

    // Callback method to be called from native code
    public static int addCallback(int a, int b) {
        System.out.println("Java callback invoked with: " + a + " + " + b);
        return a + b;
    }

    /**
     * Demonstrates asynchronous operations with native memory.
     */
    private static void demoAsyncNativeCall() {
        System.out.println("\n=== Async Native Operations Demo ===");

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

        try (Arena arena = Arena.ofConfined()) {
            // Create a shared buffer for async operations
            MemorySegment sharedBuffer = arena.allocate(1024);

            // Simulate async native data processing
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                // Simulate writing data to buffer
                for (int i = 0; i < 256; i++) {
                    sharedBuffer.setAtIndex(JAVA_INT, i, i * i);
                }
                System.out.println("Async: Populated buffer with squared values");
            }, executor);

            // Schedule a delayed read operation
            executor.schedule(() -> {
                future.join(); // Wait for write to complete

                // Read and process data
                long sum = 0;
                for (int i = 0; i < 256; i++) {
                    sum += sharedBuffer.getAtIndex(JAVA_INT, i);
                }
                System.out.println("Async: Sum of all values in buffer: " + sum);
            }, 100, TimeUnit.MILLISECONDS);

            Thread.sleep(200); // Give async operations time to complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
    }

    /**
     * Demonstrates complex memory layouts with nested structures and arrays.
     */
    private static void demoComplexMemoryLayouts() {
        System.out.println("\n=== Complex Memory Layouts Demo ===");

        try (Arena arena = Arena.ofConfined()) {
            // Create an array of Person structs - allocate each individually
            int personCount = 3;
            MemorySegment[] persons = new MemorySegment[personCount];
            MemorySegment[] nameSegments = new MemorySegment[personCount]; // Keep references

            // Create handles for accessing fields
            var nameHandle = PERSON_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("name"));
            var ageHandle = PERSON_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("age"));
            var salaryHandle = PERSON_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("salary"));

            // Populate the array
            String[] names = {"Alice", "Bob", "Charlie"};
            int[] ages = {25, 30, 35};
            double[] salaries = {50000.0, 60000.0, 70000.0};

            for (int i = 0; i < personCount; i++) {
                // Allocate individual Person struct
                persons[i] = arena.allocate(PERSON_LAYOUT);

                // Allocate string memory and keep reference
                nameSegments[i] = arena.allocateFrom(names[i]);
                
                // Debug: verify the string allocation worked
                System.out.println("Allocated string '" + names[i] + "' at address: " + 
                                 nameSegments[i].address() + ", size: " + nameSegments[i].byteSize());

                // Set fields using VarHandles
                nameHandle.set(persons[i], 0L, nameSegments[i]);
                ageHandle.set(persons[i], 0L, ages[i]);
                salaryHandle.set(persons[i], 0L, salaries[i]);
            }

            // Read and display the data
            System.out.println("Person database:");
            for (int i = 0; i < personCount; i++) {
                MemorySegment nameAddr = (MemorySegment) nameHandle.get(persons[i], 0L);
                
                // Debug: check what we retrieved
                System.out.println("Retrieved address: " + nameAddr.address() + 
                                 ", size: " + nameAddr.byteSize());
                
                // Use a safer approach to read the string
                String name;
                if (nameAddr.byteSize() > 0) {
                    name = nameAddr.getString(0);
                } else {
                    // Fallback: reinterpret from address if size is 0
                    name = MemorySegment.ofAddress(nameAddr.address())
                            .reinterpret(nameSegments[i].byteSize())
                            .getString(0);
                }
                
                int age = (int) ageHandle.get(persons[i], 0L);
                double salary = (double) salaryHandle.get(persons[i], 0L);

                System.out.printf("  [%d] %s, age %d, salary $%.2f%n", i, name, age, salary);
            }

            // Alternative approach: Create a proper array layout
            System.out.println("\n=== Array-based approach ===");
            demoArrayBasedStructs(arena);

            // Demonstrate memory layout analysis
            System.out.println("\nMemory layout analysis:");
            System.out.println("Person struct size: " + PERSON_LAYOUT.byteSize() + " bytes");
            System.out.println("Total memory used: " + (PERSON_LAYOUT.byteSize() * personCount) + " bytes");
            for (int i = 0; i < personCount; i++) {
                System.out.println("Person[" + i + "] address: " + persons[i].address());
            }
        }
    }

    /**
     * Demonstrates proper array-based struct allocation with sequence layout.
     */
    private static void demoArrayBasedStructs(Arena arena) {
        // Create a sequence layout for an array of Person structs
        int personCount = 2;
        SequenceLayout personArrayLayout = MemoryLayout.sequenceLayout(personCount, PERSON_LAYOUT);

        // Allocate the array
        MemorySegment personArray = arena.allocate(personArrayLayout);

        // Create handles for accessing array elements
        var nameHandle = personArrayLayout.varHandle(
                MemoryLayout.PathElement.sequenceElement(),
                MemoryLayout.PathElement.groupElement("name")
        );
        var ageHandle = personArrayLayout.varHandle(
                MemoryLayout.PathElement.sequenceElement(),
                MemoryLayout.PathElement.groupElement("age")
        );
        var salaryHandle = personArrayLayout.varHandle(
                MemoryLayout.PathElement.sequenceElement(),
                MemoryLayout.PathElement.groupElement("salary")
        );

        // Populate array elements
        String[] names = {"David", "Eve"};
        int[] ages = {28, 32};
        double[] salaries = {55000.0, 65000.0};
        
        // Keep references to name segments
        MemorySegment[] nameSegments = new MemorySegment[personCount];

        for (int i = 0; i < personCount; i++) {
            // Use Arena.allocateFrom() for proper string allocation
            nameSegments[i] = arena.allocateFrom(names[i]);

            // Set fields using array-aware VarHandles
            nameHandle.set(personArray, 0L, (long) i, nameSegments[i]);
            ageHandle.set(personArray, 0L, (long) i, ages[i]);
            salaryHandle.set(personArray, 0L, (long) i, salaries[i]);
        }

        // Read back the data
        System.out.println("Array-based person database:");
        for (int i = 0; i < personCount; i++) {
            MemorySegment nameAddr = (MemorySegment) nameHandle.get(personArray, 0L, (long) i);
            
            // Use safer string reading approach
            String name;
            if (nameAddr.byteSize() > 0) {
                name = nameAddr.getString(0);
            } else {
                name = MemorySegment.ofAddress(nameAddr.address())
                        .reinterpret(nameSegments[i].byteSize())
                        .getString(0);
            }
            
            int age = (int) ageHandle.get(personArray, 0L, (long) i);
            double salary = (double) salaryHandle.get(personArray, 0L, (long) i);

            System.out.printf("  [%d] %s, age %d, salary $%.2f%n", i, name, age, salary);
        }

        System.out.println("Array layout size: " + personArrayLayout.byteSize() + " bytes");
        System.out.println("Array address: " + personArray.address());
    }
}
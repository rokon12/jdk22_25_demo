package demo;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.charset.StandardCharsets;

/**
 * Demonstrates the Foreign Function & Memory API (JDK 22+)
 * for calling native C functions and working with off-heap memory.
 */
public class NativeLibraryDemo {

    public static void run(String input) {
        System.out.println("=== Foreign Function & Memory API Demo ===");

        // Demo 1: Call C's strlen function
        demoStrlen(input);

        // Demo 2: Work with off-heap memory
        demoOffHeapMemory();
    }

    /**
     * Demonstrates calling the C standard library's strlen function.
     */
    private static void demoStrlen(String input) {
        System.out.println("\n1. Calling C's strlen function:");
        System.out.println("   Input string: \"" + input + "\"");

        try {
            // Get a linker for the platform's C library
            Linker linker = Linker.nativeLinker();
            SymbolLookup stdlib = SymbolLookup.libraryLookup("c", Arena.global());

            // Look up the strlen function
            MemorySegment strlenSymbol = stdlib.find("strlen").orElseThrow();

            // Create a method handle for strlen
            MethodHandle strlen = linker.downcallHandle(
                strlenSymbol,
                FunctionDescriptor.of(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS)
            );

            // Allocate off-heap memory and copy the string bytes
            try (Arena arena = Arena.ofConfined()) {
                byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
                MemorySegment cString = arena.allocate(bytes.length + 1);
                MemorySegment bytesSegment = MemorySegment.ofArray(bytes);
                MemorySegment.copy(bytesSegment, 0, cString, 0, bytes.length);

                // Call strlen and get the result
                long length = (long) strlen.invoke(cString);
                System.out.println("   C strlen result: " + length);
                System.out.println("   Java length: " + input.length());

                if (length != input.length()) {
                    System.out.println("   Note: Lengths differ because C counts bytes, not characters.");
                    System.out.println("         Multi-byte UTF-8 characters affect the count.");
                }
            }
        } catch (Throwable e) {
            System.err.println("Error calling native function: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Demonstrates working with off-heap memory directly.
     */
    private static void demoOffHeapMemory() {
        System.out.println("\n2. Working with off-heap memory:");

        try (Arena arena = Arena.ofConfined()) {
            // Allocate a buffer for 10 integers
            int bufferSize = 10;
            MemorySegment buffer = arena.allocate(bufferSize * ValueLayout.JAVA_INT.byteSize());

            System.out.println("   Allocated off-heap buffer: " + bufferSize + " integers (" + 
                              (bufferSize * ValueLayout.JAVA_INT.byteSize()) + " bytes)");

            // Write values to the buffer
            System.out.println("   Writing values to buffer...");
            for (int i = 0; i < bufferSize; i++) {
                int value = i * 10;
                buffer.setAtIndex(ValueLayout.JAVA_INT, i, value);
                System.out.println("     [" + i + "] = " + value);
            }

            // Read and sum values from the buffer
            System.out.println("   Reading and summing values...");
            int sum = 0;
            for (int i = 0; i < bufferSize; i++) {
                int value = buffer.getAtIndex(ValueLayout.JAVA_INT, i);
                sum += value;
            }

            System.out.println("   Sum of all values: " + sum);

            // Demonstrate memory slicing
            System.out.println("   Demonstrating memory slicing (middle portion):");
            MemorySegment slice = buffer.asSlice(2 * ValueLayout.JAVA_INT.byteSize(), 
                                               4 * ValueLayout.JAVA_INT.byteSize());

            System.out.println("   Slice contains 4 integers starting from index 2:");
            for (int i = 0; i < 4; i++) {
                int value = slice.getAtIndex(ValueLayout.JAVA_INT, i);
                System.out.println("     [" + i + "] = " + value + " (original index " + (i + 2) + ")");
            }
        }
    }
}

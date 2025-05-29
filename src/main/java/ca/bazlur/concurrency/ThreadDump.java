package ca.bazlur.concurrency;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for generating thread dumps in JSON format.
 * This class provides methods to capture the state of all threads
 * in the JVM and save the information to a file using jcmd.
 */
public class ThreadDump {

    /**
     * Generates a thread dump and saves it to the specified file in JSON format
     * using the jcmd command-line tool.
     *
     * @param filename The name of the file to save the thread dump to
     * @return The path to the generated thread dump file
     * @throws IOException If an I/O error occurs while executing jcmd
     */
    public static Path generate(String filename) throws IOException {
        // Get the current process ID
        String pid = getPid();

        // Create the path for the output file
        Path filePath = Paths.get(filename);

        // Build the jcmd command
        ProcessBuilder processBuilder = new ProcessBuilder(
            "jcmd", 
            pid, 
            "Thread.dump_to_file",
            "-format=json",
            filePath.toAbsolutePath().toString()
        );

        // Redirect error stream to output stream
        processBuilder.redirectErrorStream(true);

        // Execute the command
        Process process = processBuilder.start();

        try {
            // Wait for the process to complete
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new IOException("jcmd command failed with exit code: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Thread dump generation was interrupted", e);
        }

        return filePath;
    }

    /**
     * Gets the process ID of the current JVM.
     *
     * @return The process ID as a string
     */
    private static String getPid() {
        // Get the process ID from the JVM's runtime name
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        return jvmName.split("@")[0];
    }
}
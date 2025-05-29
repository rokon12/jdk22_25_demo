package ca.bazlur.concurrency;

import java.util.concurrent.StructuredTaskScope;

/**
 * Demonstrates structured concurrency with FirstWin pattern using anySuccessfulResultOrThrow.
 * This pattern returns the result of the first successful subtask and cancels the others.
 */
public class Z4_FirstWinDemo {
    public static void run() throws Exception {
        System.out.println("Testing mirror selection with different URL pairs:");

        // Test case 1: Both URLs should succeed, but with different latencies
        String result1 = fastestMirror("mirror1.example.com", "mirror2.example.com");
        System.out.println("  Test 1 (both valid): " + result1);

        // Test case 2: First URL will fail, second should succeed
        String result2 = fastestMirror("mirror-fail.example.com", "mirror3.example.com");
        System.out.println("  Test 2 (first fails): " + result2);

        // Test case 3: Second URL will fail, first should succeed
        String result3 = fastestMirror("mirror4.example.com", "mirror-fail.example.com");
        System.out.println("  Test 3 (second fails): " + result3);

        // Test case 4: URLs with very different latencies
        String result4 = fastestMirror("slow-mirror.example.com", "fast-mirror.example.com");
        System.out.println("  Test 4 (different speeds): " + result4);
    }

    public static String fastestMirror(String urlA, String urlB) throws Exception {
        try (var scope = StructuredTaskScope.open(StructuredTaskScope.Joiner.<String>anySuccessfulResultOrThrow())) {
            scope.fork(() -> fetch(urlA));
            scope.fork(() -> fetch(urlB));
            return scope.join();    // String of the first successful fetch
        }
    }

    private static String fetch(String url) throws Exception {
        Thread.sleep(url.hashCode() & 0x1FF); // mock latency per URL
        if (url.contains("fail")) throw new RuntimeException("Bad mirror");
        return "OK:" + url;
    }
}
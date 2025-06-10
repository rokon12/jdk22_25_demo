package ca.bazlur;

import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.SequencedCollection;
import java.util.SequencedMap;
import java.util.SequencedSet;

/**
 * Demonstrates the Sequenced Collection APIs introduced in Java 21+.
 */
public class SequencedCollectionsDemo {
    public static void run() {
        System.out.println("=== Sequenced Collections Demo ===");

        // SequencedCollection using ArrayDeque
        SequencedCollection<String> deque = new ArrayDeque<>();
        deque.addFirst("first");
        deque.addLast("second");
        deque.addLast("third");
        System.out.println("Deque forward: " + deque);
        System.out.println("Deque reversed: " + deque.reversed());

        // SequencedSet using LinkedHashSet
        SequencedSet<Integer> set = new LinkedHashSet<>();
        set.addFirst(1);
        set.addLast(2);
        set.addLast(3);
        System.out.println("Set first: " + set.getFirst());
        System.out.println("Set last: " + set.getLast());
        System.out.println("Set reversed: " + set.reversed());

        // SequencedMap using LinkedHashMap
        SequencedMap<String, Integer> map = new LinkedHashMap<>();
        map.putFirst("a", 1);
        map.putLast("b", 2);
        map.putLast("c", 3);
        System.out.println("Map entries: " + map.entrySet());
        System.out.println("Map reversed keys: " + map.reversed().keySet());
    }
}

package ca.bazlur;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;
import java.util.Arrays;

/**
 * Demonstrates the Vector API (incubator) introduced in JDK 22+.
 * Performs a simple vector addition using IntVector.
 */
public class VectorApiDemo {
    private static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;

    public static void run() {
        System.out.println("=== Vector API Demo ===");

        int[] a = {1, 2, 3, 4, 5, 6, 7, 8};
        int[] b = {8, 7, 6, 5, 4, 3, 2, 1};
        int[] c = new int[a.length];

        int i = 0;
        int upperBound = SPECIES.loopBound(a.length);
        for (; i < upperBound; i += SPECIES.length()) {
            IntVector va = IntVector.fromArray(SPECIES, a, i);
            IntVector vb = IntVector.fromArray(SPECIES, b, i);
            IntVector vc = va.add(vb);
            vc.intoArray(c, i);
        }

        for (; i < a.length; i++) {
            c[i] = a[i] + b[i];
        }

        System.out.println("Vector sum: " + Arrays.toString(c));
    }
}

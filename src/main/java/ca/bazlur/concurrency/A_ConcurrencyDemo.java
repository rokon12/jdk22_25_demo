package ca.bazlur.concurrency;

/**
 * Structured Concurrency Conference Demo: Progressive Use Cases
 * 
 * This class demonstrates the progressive use cases of structured concurrency
 * as described in the conference demo outline.
 * 
 * The Story Arc: "From Chaos to Structure"
 */
public class A_ConcurrencyDemo {
    
    public static void run() throws Exception {
        System.out.println("=== Structured Concurrency Conference Demo: Progressive Use Cases ===");
        
        System.out.println("\n1. The Problem: Traditional Concurrent Chaos");
        B_TraditionalChaosDemo.run();
        
        System.out.println("\n2. The Solution: Your First Structured Scope");
        C_FirstStructuredScopeDemo.run();
        
        System.out.println("\n3. Racing to Win: The Speed Demon Pattern");
        D_RacingToWinDemo.run();
        
        System.out.println("\n4. All or Nothing: The Reliability Pattern");
        E_AllOrNothingDemo.run();
        
        System.out.println("\n5. The Custom Intelligence: Smart Joiners");
        F_CustomJoinerDemo.run();
        
        System.out.println("\n6. Time is Money: Deadline-Aware Processing");
        G_DeadlineAwareDemo.run();
        
        System.out.println("\n7. The Hierarchy: Nested Scope Architecture");
        H_NestedScopeDemo.run();
    }
    
    // Simple CLI hook for standalone testing
    public static void main(String[] args) throws Exception {
        run();
    }
}
package ca.bazlur;

/**
 * Demonstrates JEP 447: Statements before super() in constructors.
 * Allows executing statements prior to calling the superclass constructor.
 */
public class StatementsBeforeSuperDemo {
    static class Parent {
        Parent(String value) {
            System.out.println("Parent constructor: " + value);
        }
    }

    static class Child extends Parent {
        Child() {
            String msg = "hello"; // statement before super
            System.out.println("Preparing to call super...");
            super(msg);
            System.out.println("Child constructor done");
        }
    }

    public static void run() {
        System.out.println("=== Statements Before super() Demo ===");
        new Child();
    }
}

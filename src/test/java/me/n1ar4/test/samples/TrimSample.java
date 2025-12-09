package me.n1ar4.test.samples;

public class TrimSample {
    public static void testAbs() {
        System.out.println("abs(5): " + Math.abs(5));
        System.out.println("abs(-5): " + Math.abs(-5));
        System.out.println("abs(5.5f): " + Math.abs(5.5f));
        System.out.println("abs(-5.5f): " + Math.abs(-5.5f));
        System.out.println("abs(5.5d): " + Math.abs(5.5d));
        System.out.println("abs(-5.5d): " + Math.abs(-5.5d));
    }

    public static void testMax() {
        System.out.println("max(10, 20): " + Math.max(10, 20));
        System.out.println("max(5.5f, 2.2f): " + Math.max(5.5f, 2.2f));
    }

    public static void testMin() {
        System.out.println("min(10, 20): " + Math.min(10, 20));
        System.out.println("min(5.5f, 2.2f): " + Math.min(5.5f, 2.2f));
    }

    public static void main(String[] args) {
        testAbs();
        testMax();
        testMin();
    }
}

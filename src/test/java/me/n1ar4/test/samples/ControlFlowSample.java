package me.n1ar4.test.samples;

public class ControlFlowSample {
    public static void testMethod() {
        int x = 0;
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                x += i;
            } else {
                x -= i;
            }
        }
        System.out.println("Result: " + x);
    }

    public static void main(String[] args) {
        testMethod();
    }
}

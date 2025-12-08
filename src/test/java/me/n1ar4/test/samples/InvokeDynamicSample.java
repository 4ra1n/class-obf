package me.n1ar4.test.samples;

public class InvokeDynamicSample {
    public static String helper1() {
        return "HD1";
    }

    public static int helper2(int x) {
        return x * 2;
    }

    public static void main(String[] args) {
        String a = helper1();
        int b = helper2(5);
        System.out.println(a + ":" + b);
    }
}
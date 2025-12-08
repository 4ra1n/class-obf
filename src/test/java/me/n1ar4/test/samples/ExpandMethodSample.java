package me.n1ar4.test.samples;

public class ExpandMethodSample {
    public void test(int a) {
        System.out.println("expand:" + a);
    }

    public static void main(String[] args) {
        ExpandMethodSample e = new ExpandMethodSample();
        e.test(5);
    }
}
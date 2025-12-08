package me.n1ar4.test.samples;

public class NumberOpsSample {
    public static int calc() {
        int a = 10;
        int b = 20;
        int c = 7;
        int d = a + b - c;
        System.out.println("数值:" + d);
        return d;
    }

    public static void main(String[] args) {
        int r = calc();
        System.out.println("结果:" + r);
    }
}
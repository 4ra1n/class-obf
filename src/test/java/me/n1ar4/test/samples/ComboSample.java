package me.n1ar4.test.samples;

public class ComboSample {
    private String title = "组合测试";
    private int base = 3;

    public static String make(String s) {
        return s + "-END";
    }

    public void run() {
        String a = "AA";
        String b = "BB";
        String c = make(a + b);
        int x = 12;
        int y = 8;
        int z = x + y + base;
        System.out.println(title);
        System.out.println(c);
        System.out.println(z);
    }

    public static void main(String[] args) {
        new ComboSample().run();
    }
}
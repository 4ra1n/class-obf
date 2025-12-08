package me.n1ar4.test.samples;

public class NameObfSample {
    private int count = 1;
    private String msg = "名称混淆";

    public void foo() {
        System.out.println("foo:" + msg + ":" + count);
        count++;
    }

    public String bar(int x) {
        String r = msg + "-" + x;
        System.out.println("bar:" + r);
        return r;
    }

    public int baz(String p, int y) {
        int v = y + p.length();
        System.out.println("baz:" + v);
        return v;
    }

    public static void staticMethod() {
        System.out.println("静态方法调用");
    }

    public static void main(String[] args) {
        NameObfSample s = new NameObfSample();
        s.foo();
        String r = s.bar(7);
        int v = s.baz(r, 3);
        System.out.println("结果:" + v);
        staticMethod();
    }
}
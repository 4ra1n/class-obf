package me.n1ar4.test.samples;

public class StringOpsSample {
    public static String join(String a, String b) {
        return a + "-" + b;
    }

    public static void main(String[] args) {
        String s1 = "字符串测试";
        String s2 = "AES与数组";
        String s3 = "混淆校验";
        String s4 = join(s1, s2);
        System.out.println(s1);
        System.out.println(s2);
        System.out.println(s3);
        System.out.println(s4);
        System.out.println("拼接:" + s1 + s2 + s3);
    }
}
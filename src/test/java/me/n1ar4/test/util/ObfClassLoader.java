package me.n1ar4.test.util;

public class ObfClassLoader extends ClassLoader {
    public Class<?> define(String name, byte[] bytes) {
        return defineClass(name, bytes, 0, bytes.length);
    }
}
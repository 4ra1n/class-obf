package me.n1ar4.clazz.obfuscator.utils;

import me.n1ar4.clazz.obfuscator.core.ObfEnv;
import me.n1ar4.jrandom.core.JRandom;

import java.util.HashSet;

@SuppressWarnings("all")
public class NameUtil {
    public static String[] CHAR_POOL = null;
    private static final HashSet<String> generatedStrings = new HashSet<>();
    private static final HashSet<String> generatedMethods = new HashSet<>();
    private static final HashSet<String> generatedFields = new HashSet<>();

    public static void exclude(String s) {
        generatedStrings.add(s);
        generatedMethods.add(s);
        generatedFields.add(s);
    }

    public static String genNewName() {
        return genBase(1);
    }

    public static String genNewMethod() {
        return genBase(2);
    }

    public static String genNewFields() {
        return genBase(3);
    }

    public static String genWithSet(HashSet<String> exists) {
        if (ObfEnv.config.isUseEvilCharInstead()) {
            while (true) {
                String result = EvilObfUtil.randomString(16);
                if (!exists.contains(result)) {
                    exists.add(result);
                    return result;
                }
            }
        } else {
            JRandom random = JRandom.getInstance();
            while (true) {
                int length = 10 + random.getInt(0, 3);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < length; i++) {
                    sb.append(CHAR_POOL[random.getInt(0, CHAR_POOL.length)]);
                }
                if (sb.charAt(0) == '~' || sb.charAt(0) == '1') {
                    continue;
                }
                String result = sb.toString();
                if (!exists.contains(result)) {
                    exists.add(result);
                    return result;
                }
            }
        }
    }

    private static String genBase(int op) {
        if (ObfEnv.config.isUseEvilCharInstead()) {
            while (true) {
                String result = EvilObfUtil.randomString(16);
                if (op == 2) {
                    if (!generatedMethods.contains(result)) {
                        generatedMethods.add(result);
                        return result;
                    }
                } else if (op == 1) {
                    if (!generatedStrings.contains(result)) {
                        generatedStrings.add(result);
                        return result;
                    }
                } else if (op == 3) {
                    if (!generatedFields.contains(result)) {
                        generatedFields.add(result);
                        return result;
                    }
                } else {
                    return null;
                }
            }
        } else {
            JRandom random = JRandom.getInstance();
            while (true) {
                int length = 10 + random.getInt(0, 3);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < length; i++) {
                    sb.append(CHAR_POOL[random.getInt(0, CHAR_POOL.length)]);
                }
                if (sb.charAt(0) == '~' || sb.charAt(0) == '1') {
                    continue;
                }
                String result = sb.toString();
                if (op == 2) {
                    if (!generatedMethods.contains(result)) {
                        generatedMethods.add(result);
                        return result;
                    }
                } else if (op == 1) {
                    if (!generatedStrings.contains(result)) {
                        generatedStrings.add(result);
                        return result;
                    }
                } else if (op == 3) {
                    if (!generatedFields.contains(result)) {
                        generatedFields.add(result);
                        return result;
                    }
                } else {
                    return null;
                }
            }
        }
    }
}

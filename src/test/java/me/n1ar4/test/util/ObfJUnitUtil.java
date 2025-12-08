package me.n1ar4.test.util;

import me.n1ar4.clazz.obfuscator.api.ClassObf;
import me.n1ar4.clazz.obfuscator.api.Result;
import me.n1ar4.clazz.obfuscator.config.BaseConfig;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

public class ObfJUnitUtil {
    public static BaseConfig baseOff() {
        BaseConfig cfg = BaseConfig.Default();
        cfg.setEnableFieldName(false);
        cfg.setEnableMethodName(false);
        cfg.setEnableParamName(false);
        cfg.setEnableAdvanceString(false);
        cfg.setEnableHideMethod(false);
        cfg.setEnableHideField(false);
        cfg.setEnableXOR(false);
        cfg.setEnableAES(false);
        cfg.setEnableDeleteCompileInfo(false);
        cfg.setEnableJunk(false);
        cfg.setEnableExpandMethod(false);
        cfg.setUseEvilCharInstead(false);
        cfg.setEnableEvilString(false);
        cfg.setAntiAI(false);
        cfg.setEnableInvokeDynamic(false);
        cfg.setQuiet(true);
        return cfg;
    }

    public static byte[] getClassBytes(Class<?> clazz) {
        try {
            String resourceName = clazz.getName().replace('.', '/') + ".class";
            ClassLoader cl = clazz.getClassLoader();
            if (cl == null) cl = ClassLoader.getSystemClassLoader();
            try (InputStream in = cl.getResourceAsStream(resourceName)) {
                if (in != null) {
                    return readAll(in);
                }
            }
            try (InputStream in2 = clazz.getResourceAsStream(clazz.getSimpleName() + ".class")) {
                if (in2 != null) {
                    return readAll(in2);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        throw new RuntimeException("class bytes not found for: " + clazz.getName());
    }

    private static byte[] readAll(InputStream in) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = in.read(buf)) != -1) {
            baos.write(buf, 0, n);
        }
        return baos.toByteArray();
    }

    public static byte[] obfuscateBytes(Class<?> clazz, BaseConfig config) {
        ClassObf api = new ClassObf(config);
        Result result = api.run(getClassBytes(clazz));
        if (!Result.SUCCESS.equals(result.getMessage())) {
            throw new RuntimeException(new String(result.getData(), StandardCharsets.UTF_8));
        }
        return result.getData();
    }

    public static String runMainAndCapture(Class<?> clazz) {
        PrintStream origin = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
        try {
            Method m = clazz.getMethod("main", String[].class);
            m.invoke(null, (Object) new String[]{});
        } catch (Throwable t) {
            throw new RuntimeException(t);
        } finally {
            System.setOut(origin);
        }
        return baos.toString();
    }
}
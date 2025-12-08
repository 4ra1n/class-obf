package me.n1ar4.test.jupiter;

import me.n1ar4.clazz.obfuscator.config.BaseConfig;
import me.n1ar4.test.samples.InvokeDynamicSample;
import me.n1ar4.test.util.ObfClassLoader;
import me.n1ar4.test.util.ObfJUnitUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InvokeDynamicObfTest {
    @Test
    public void invokeDynamicObf_preservesOutput() {
        BaseConfig cfg = ObfJUnitUtil.baseOff();
        cfg.setEnableInvokeDynamic(true);

        String original = ObfJUnitUtil.runMainAndCapture(InvokeDynamicSample.class);
        byte[] obf = ObfJUnitUtil.obfuscateBytes(InvokeDynamicSample.class, cfg);
        Class<?> oc = new ObfClassLoader().define("me.n1ar4.test.samples.InvokeDynamicSample", obf);
        String obfOut = ObfJUnitUtil.runMainAndCapture(oc);

        Assertions.assertEquals(original, obfOut);
    }
}
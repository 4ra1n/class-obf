package me.n1ar4.test.jupiter;

import me.n1ar4.clazz.obfuscator.config.BaseConfig;
import me.n1ar4.test.samples.NumberOpsSample;
import me.n1ar4.test.util.ObfClassLoader;
import me.n1ar4.test.util.ObfJUnitUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class XORObfTest {
    @Test
    public void xorObf_preservesOutput() {
        BaseConfig cfg = ObfJUnitUtil.baseOff();
        cfg.setEnableXOR(true);

        String original = ObfJUnitUtil.runMainAndCapture(NumberOpsSample.class);
        byte[] obf = ObfJUnitUtil.obfuscateBytes(NumberOpsSample.class, cfg);
        Class<?> oc = new ObfClassLoader().define("me.n1ar4.test.samples.NumberOpsSample", obf);
        String obfOut = ObfJUnitUtil.runMainAndCapture(oc);

        Assertions.assertEquals(original, obfOut);
    }
}
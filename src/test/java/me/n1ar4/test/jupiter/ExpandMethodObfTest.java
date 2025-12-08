package me.n1ar4.test.jupiter;

import me.n1ar4.clazz.obfuscator.config.BaseConfig;
import me.n1ar4.test.samples.ExpandMethodSample;
import me.n1ar4.test.util.ObfClassLoader;
import me.n1ar4.test.util.ObfJUnitUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExpandMethodObfTest {
    @Test
    public void expandMethodObf_preservesOutput() {
        BaseConfig cfg = ObfJUnitUtil.baseOff();
        cfg.setEnableExpandMethod(true);
        cfg.setExpandParamNum(3);
        cfg.setExpandMethodList(new String[]{"test"});

        String original = ObfJUnitUtil.runMainAndCapture(ExpandMethodSample.class);
        byte[] obf = ObfJUnitUtil.obfuscateBytes(ExpandMethodSample.class, cfg);
        Class<?> oc = new ObfClassLoader().define("me.n1ar4.test.samples.ExpandMethodSample", obf);
        String obfOut = ObfJUnitUtil.runMainAndCapture(oc);

        Assertions.assertEquals(original, obfOut);
    }
}
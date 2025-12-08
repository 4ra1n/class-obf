package me.n1ar4.test.jupiter;

import me.n1ar4.clazz.obfuscator.config.BaseConfig;
import me.n1ar4.test.samples.ComboSample;
import me.n1ar4.test.util.ObfClassLoader;
import me.n1ar4.test.util.ObfJUnitUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DeleteInfoObfTest {
    @Test
    public void deleteInfoObf_preservesOutput() {
        BaseConfig cfg = ObfJUnitUtil.baseOff();
        cfg.setEnableDeleteCompileInfo(true);

        String original = ObfJUnitUtil.runMainAndCapture(ComboSample.class);
        byte[] obf = ObfJUnitUtil.obfuscateBytes(ComboSample.class, cfg);
        Class<?> oc = new ObfClassLoader().define("me.n1ar4.test.samples.ComboSample", obf);
        String obfOut = ObfJUnitUtil.runMainAndCapture(oc);

        Assertions.assertEquals(original, obfOut);
    }
}
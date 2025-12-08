package me.n1ar4.test.jupiter;

import me.n1ar4.clazz.obfuscator.config.BaseConfig;
import me.n1ar4.test.samples.NameObfSample;
import me.n1ar4.test.util.ObfClassLoader;
import me.n1ar4.test.util.ObfJUnitUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ComboRenameTest {
    @Test
    public void comboRename_preservesOutput() {
        BaseConfig cfg = ObfJUnitUtil.baseOff();
        cfg.setEnableMethodName(true);
        cfg.setEnableFieldName(true);
        cfg.setEnableParamName(true);

        String original = ObfJUnitUtil.runMainAndCapture(NameObfSample.class);
        byte[] obf = ObfJUnitUtil.obfuscateBytes(NameObfSample.class, cfg);
        Class<?> oc = new ObfClassLoader().define("me.n1ar4.test.samples.NameObfSample", obf);
        String obfOut = ObfJUnitUtil.runMainAndCapture(oc);

        Assertions.assertEquals(original, obfOut);
    }
}
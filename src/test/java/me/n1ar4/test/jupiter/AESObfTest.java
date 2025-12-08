package me.n1ar4.test.jupiter;

import me.n1ar4.clazz.obfuscator.config.BaseConfig;
import me.n1ar4.test.samples.StringOpsSample;
import me.n1ar4.test.util.ObfClassLoader;
import me.n1ar4.test.util.ObfJUnitUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AESObfTest {
    @Test
    public void aesObf_preservesOutput() {
        BaseConfig cfg = ObfJUnitUtil.baseOff();
        cfg.setEnableAES(true);
        cfg.setAesKey("OBF_DEFAULT_KEYS");
        cfg.setAesDecName("iiLLiLi");
        cfg.setAesKeyField("iiiLLLi1i");

        String original = ObfJUnitUtil.runMainAndCapture(StringOpsSample.class);
        byte[] obf = ObfJUnitUtil.obfuscateBytes(StringOpsSample.class, cfg);
        Class<?> oc = new ObfClassLoader().define("me.n1ar4.test.samples.StringOpsSample", obf);
        String obfOut = ObfJUnitUtil.runMainAndCapture(oc);

        Assertions.assertEquals(original, obfOut);
    }
}
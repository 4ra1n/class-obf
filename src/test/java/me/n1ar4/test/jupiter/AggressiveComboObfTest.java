package me.n1ar4.test.jupiter;

import me.n1ar4.clazz.obfuscator.config.BaseConfig;
import me.n1ar4.test.samples.NameObfSample;
import me.n1ar4.test.util.ObfClassLoader;
import me.n1ar4.test.util.ObfJUnitUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AggressiveComboObfTest {
    @Test
    public void aggressiveCombo_preservesOutput() {
        BaseConfig cfg = ObfJUnitUtil.baseOff();
        // 重命名相关
        cfg.setEnableMethodName(true);
        cfg.setEnableFieldName(true);
        cfg.setEnableParamName(true);
        cfg.setIgnorePublic(false);
        cfg.setAutoDisableImpl(true);
        // 字符串与数值相关
        cfg.setEnableAdvanceString(true);
        cfg.setEnableAES(true);
        cfg.setAesKey("OBF_DEFAULT_KEYS");
        cfg.setAesDecName("iiLLiLi");
        cfg.setAesKeyField("iiiLLLi1i");
        cfg.setEnableXOR(true);
        // 花指令、隐藏与恶意字符
        cfg.setEnableJunk(true);
        cfg.setJunkLevel(4);
        cfg.setMaxJunkOneClass(800);
        cfg.setEnableHideMethod(true);
        cfg.setEnableHideField(true);
        cfg.setUseEvilCharInstead(true);
        cfg.setEnableEvilString(true);
        // 其它增强
        cfg.setEnableDeleteCompileInfo(true);
        cfg.setEnableBadAnno(true);
        cfg.setBadAnnoTextFile("bad-anno.txt");
        cfg.setAntiAI(true);
        cfg.setQuiet(true);

        String original = ObfJUnitUtil.runMainAndCapture(NameObfSample.class);
        byte[] obf = ObfJUnitUtil.obfuscateBytes(NameObfSample.class, cfg);
        Class<?> oc = new ObfClassLoader().define("me.n1ar4.test.samples.NameObfSample", obf);
        String obfOut = ObfJUnitUtil.runMainAndCapture(oc);

        Assertions.assertEquals(original, obfOut);
    }
}


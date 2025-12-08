package me.n1ar4.test.jupiter;

import me.n1ar4.clazz.obfuscator.config.BaseConfig;
import me.n1ar4.test.samples.ComboSample;
import me.n1ar4.test.util.ObfClassLoader;
import me.n1ar4.test.util.ObfJUnitUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AllEnabledObfTest {
    @Test
    public void allEnabled_preservesOutput() {
        BaseConfig cfg = ObfJUnitUtil.baseOff();
        // 名称相关
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
        // 花指令与隐藏
        cfg.setEnableJunk(true);
        cfg.setJunkLevel(3);
        cfg.setMaxJunkOneClass(500);
        cfg.setEnableHideMethod(true);
        cfg.setEnableHideField(true);
        // 其它增强
        cfg.setEnableDeleteCompileInfo(true);
        cfg.setEnableBadAnno(true);
        cfg.setBadAnnoTextFile("bad-anno.txt");
        cfg.setAntiAI(true);
        // invokedynamic 与参数拓展
        cfg.setEnableInvokeDynamic(true);
        cfg.setEnableExpandMethod(true);
        cfg.setExpandParamNum(3);
        cfg.setExpandMethodList(new String[]{"run"});
        // 保持静默
        cfg.setQuiet(true);

        String original = ObfJUnitUtil.runMainAndCapture(ComboSample.class);
        byte[] obf = ObfJUnitUtil.obfuscateBytes(ComboSample.class, cfg);
        Class<?> oc = new ObfClassLoader().define("me.n1ar4.test.samples.ComboSample", obf);
        String obfOut = ObfJUnitUtil.runMainAndCapture(oc);

        Assertions.assertEquals(original, obfOut);
    }
}


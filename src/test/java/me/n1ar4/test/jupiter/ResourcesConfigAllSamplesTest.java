package me.n1ar4.test.jupiter;

import me.n1ar4.clazz.obfuscator.config.BaseConfig;
import me.n1ar4.clazz.obfuscator.config.Parser;
import me.n1ar4.test.samples.ComboSample;
import me.n1ar4.test.samples.ExpandMethodSample;
import me.n1ar4.test.samples.InvokeDynamicSample;
import me.n1ar4.test.samples.NameObfSample;
import me.n1ar4.test.samples.NumberOpsSample;
import me.n1ar4.test.samples.StringOpsSample;
import me.n1ar4.test.util.ObfClassLoader;
import me.n1ar4.test.util.ObfJUnitUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourcesConfigAllSamplesTest {
    private BaseConfig loadFromTestResources() {
        try {
            URL url = ResourcesConfigAllSamplesTest.class.getClassLoader().getResource("config.yaml");
            if (url == null) {
                throw new RuntimeException("test resources config.yaml not found");
            }
            Path p = Paths.get(url.toURI());
            return new Parser().parse(p);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void assertPreservesOutput(Class<?> sample, BaseConfig cfg) {
        String original = ObfJUnitUtil.runMainAndCapture(sample);
        byte[] obf = ObfJUnitUtil.obfuscateBytes(sample, cfg);
        Class<?> oc = new ObfClassLoader().define(sample.getName(), obf);
        String obfOut = ObfJUnitUtil.runMainAndCapture(oc);
        Assertions.assertEquals(original, obfOut, "output mismatch for " + sample.getSimpleName());
    }

    @Test
    public void allSamples_pass_with_test_resources_config() {
        BaseConfig cfg = loadFromTestResources();
        Assertions.assertNotNull(cfg);

        assertPreservesOutput(NameObfSample.class, cfg);
        assertPreservesOutput(StringOpsSample.class, cfg);
        assertPreservesOutput(NumberOpsSample.class, cfg);
        assertPreservesOutput(ExpandMethodSample.class, cfg);
        assertPreservesOutput(ComboSample.class, cfg);
        assertPreservesOutput(InvokeDynamicSample.class, cfg);
    }
}


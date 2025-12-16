package me.n1ar4.test.jupiter;

import me.n1ar4.clazz.obfuscator.config.BaseConfig;
import me.n1ar4.clazz.obfuscator.core.JarClassRunner;
import me.n1ar4.test.samples.ComboSample;
import me.n1ar4.test.util.ObfClassLoader;
import me.n1ar4.test.util.ObfJUnitUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class JarClassModeTest {
    @Test
    public void obfuscate_class_in_jar_and_write_back() throws Exception {
        byte[] original = ObfJUnitUtil.getClassBytes(ComboSample.class);
        Path tempJar = Files.createTempFile("class-obf-test", ".jar");

        try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(tempJar))) {
            JarEntry e = new JarEntry("me/n1ar4/test/samples/ComboSample.class");
            jos.putNextEntry(e);
            jos.write(original);
            jos.closeEntry();
        }

        boolean ok = JarClassRunner.run(tempJar, "me.n1ar4.test.samples.ComboSample", BaseConfig.Default());
        Assertions.assertTrue(ok);

        byte[] obfBytes;
        try (JarFile jf = new JarFile(tempJar.toFile())) {
            JarEntry e = jf.getJarEntry("me/n1ar4/test/samples/ComboSample.class");
            try (InputStream in = jf.getInputStream(e)) {
                obfBytes = me.n1ar4.clazz.obfuscator.utils.IOUtils.readAllBytes(in);
            }
        }

        Assertions.assertNotEquals(original.length, 0);
        Assertions.assertNotEquals(obfBytes.length, 0);
        Assertions.assertNotEquals(new String(original), new String(obfBytes));

        Class<?> oc = new ObfClassLoader().define("me.n1ar4.test.samples.ComboSample", obfBytes);
        String originalOut = ObfJUnitUtil.runMainAndCapture(ComboSample.class);
        String obfOut = ObfJUnitUtil.runMainAndCapture(oc);
        Assertions.assertEquals(originalOut, obfOut);
    }
}


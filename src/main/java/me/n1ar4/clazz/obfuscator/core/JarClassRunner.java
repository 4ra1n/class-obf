package me.n1ar4.clazz.obfuscator.core;

import me.n1ar4.clazz.obfuscator.api.ClassObf;
import me.n1ar4.clazz.obfuscator.api.Result;
import me.n1ar4.clazz.obfuscator.config.BaseConfig;
import me.n1ar4.clazz.obfuscator.utils.ColorUtil;
import me.n1ar4.log.Logger;
import me.n1ar4.log.LogManager;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class JarClassRunner {
    private static final Logger logger = LogManager.getLogger();

    public static boolean run(Path jarPath, String className, BaseConfig config) {
        String entryName = className.replace('.', '/') + ".class";
        try (JarFile jarFile = new JarFile(jarPath.toFile())) {
            JarEntry target = jarFile.getJarEntry(entryName);
            if (target == null) {
                System.out.println(ColorUtil.red("[-] target class not found in jar: " + entryName));
                return false;
            }

            byte[] original;
            try (InputStream in = jarFile.getInputStream(target)) {
                original = me.n1ar4.clazz.obfuscator.utils.IOUtils.readAllBytes(in);
            }

            ClassObf api = new ClassObf(config);
            Result r = api.run(original);
            if (!Result.SUCCESS.equals(r.getMessage())) {
                logger.error("obfuscate error: {}", new String(r.getData()));
                return false;
            }
            byte[] obf = r.getData();

            Path tempJar = Files.createTempFile("class-obf", ".jar");
            try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(tempJar))) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry e = entries.nextElement();
                    JarEntry newEntry = new JarEntry(e.getName());
                    newEntry.setTime(e.getTime());
                    jos.putNextEntry(newEntry);
                    try (InputStream in = jarFile.getInputStream(e)) {
                        if (e.getName().equals(entryName)) {
                            jos.write(obf);
                        } else if (in != null) {
                            byte[] buf = new byte[8192];
                            int n;
                            while ((n = in.read(buf)) != -1) {
                                jos.write(buf, 0, n);
                            }
                        }
                    }
                    jos.closeEntry();
                }
            }

            Path backup = jarPath.resolveSibling(jarPath.getFileName().toString() + ".bak");
            Files.move(jarPath, backup, StandardCopyOption.REPLACE_EXISTING);
            Files.move(tempJar, jarPath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("backup created: {}", backup);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return false;
        }
    }
}

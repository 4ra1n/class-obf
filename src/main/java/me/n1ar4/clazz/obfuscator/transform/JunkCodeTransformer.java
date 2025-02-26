package me.n1ar4.clazz.obfuscator.transform;

import me.n1ar4.clazz.obfuscator.Const;
import me.n1ar4.clazz.obfuscator.asm.JunkCodeClassVisitor;
import me.n1ar4.clazz.obfuscator.config.BaseConfig;
import me.n1ar4.clazz.obfuscator.core.ObfEnv;
import me.n1ar4.clazz.obfuscator.loader.CustomClassLoader;
import me.n1ar4.clazz.obfuscator.loader.CustomClassWriter;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodTooLargeException;

import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("all")
public class JunkCodeTransformer {
    private static final Logger logger = LogManager.getLogger();

    public static void transform(BaseConfig config, CustomClassLoader loader) {
        Path newClassPath = Const.TEMP_PATH;
        if (!Files.exists(newClassPath)) {
            logger.error("class not exist: {}", newClassPath.toString());
            return;
        }
        try {
            ClassReader classReader = new ClassReader(Files.readAllBytes(newClassPath));
            ClassWriter classWriter = new CustomClassWriter(classReader,
                    ObfEnv.config.isAsmAutoCompute() ? Const.WriterASMOptions : 0, loader);
            JunkCodeClassVisitor changer = new JunkCodeClassVisitor(classWriter, config);
            try {
                classReader.accept(changer, Const.ReaderASMOptions);
            } catch (Exception ignored) {
                // CustomClassLoader 可能会找不到类
                return;
            }
            Files.delete(newClassPath);
            Files.write(newClassPath, classWriter.toByteArray());
        } catch (MethodTooLargeException ex) {
            logger.error("method too large");
            logger.error("please check max junk config");
            return;
        } catch (Exception ex) {
            logger.error("transform error: {}", ex.toString());
        }
    }
}

package me.n1ar4.clazz.obfuscator.transform;

import me.n1ar4.clazz.obfuscator.Const;
import me.n1ar4.clazz.obfuscator.asm.StringEncryptClassVisitor;
import me.n1ar4.clazz.obfuscator.core.ObfEnv;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("all")
public class StringEncryptTransformer {
    private static final Logger logger = LogManager.getLogger();

    public static void transform(String aesKey, String aesDecName) {
        Path newClassPath = Const.TEMP_PATH;
        if (!Files.exists(newClassPath)) {
            logger.error("class not exist: {}", newClassPath.toString());
            return;
        }
        try {
            ClassReader classReader = new ClassReader(Files.readAllBytes(newClassPath));
            ClassWriter classWriter = new ClassWriter(classReader,
                    ObfEnv.config.isAsmAutoCompute() ? Const.WriterASMOptions : 0);
            StringEncryptClassVisitor changer = new StringEncryptClassVisitor(classWriter, aesKey, aesDecName);
            classReader.accept(changer, Const.ReaderASMOptions);
            Files.delete(newClassPath);
            Files.write(newClassPath, classWriter.toByteArray());
        } catch (Exception ex) {
            logger.error("transform error: {}", ex.toString());
        }
    }
}

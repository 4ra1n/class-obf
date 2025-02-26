package me.n1ar4.clazz.obfuscator.transform;

import me.n1ar4.clazz.obfuscator.Const;
import me.n1ar4.clazz.obfuscator.asm.StringArrayClassVisitor;
import me.n1ar4.clazz.obfuscator.core.ObfEnv;
import me.n1ar4.clazz.obfuscator.loader.CustomClassLoader;
import me.n1ar4.clazz.obfuscator.loader.CustomClassWriter;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.nio.file.Files;
import java.nio.file.Path;

@SuppressWarnings("all")
public class StringArrayTransformer {
    private static final Logger logger = LogManager.getLogger();
    public static int INDEX = 0;

    public static void transform(CustomClassLoader loader) {
        Path newClassPath = Const.TEMP_PATH;
        if (!Files.exists(newClassPath)) {
            logger.error("class not exist: {}", newClassPath.toString());
            return;
        }
        try {
            INDEX = 0;
            ClassReader classReader = new ClassReader(Files.readAllBytes(newClassPath));
            ClassWriter classWriter = new CustomClassWriter(classReader,
                    ObfEnv.config.isAsmAutoCompute() ? Const.WriterASMOptions : 0, loader);
            StringArrayClassVisitor changer = new StringArrayClassVisitor(classWriter);
            classReader.accept(changer, Const.ReaderASMOptions);
            Files.delete(newClassPath);
            Files.write(newClassPath, classWriter.toByteArray());
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("transform error: {}", ex.toString());
        }
    }
}

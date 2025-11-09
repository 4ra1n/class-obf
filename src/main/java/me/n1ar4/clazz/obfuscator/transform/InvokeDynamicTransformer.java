package me.n1ar4.clazz.obfuscator.transform;

import me.n1ar4.clazz.obfuscator.Const;
import me.n1ar4.clazz.obfuscator.asm.InvokeDynamicClassVisitor;
import me.n1ar4.clazz.obfuscator.core.ObfEnv;
import me.n1ar4.clazz.obfuscator.loader.CustomClassLoader;
import me.n1ar4.clazz.obfuscator.loader.CustomClassWriter;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * InvokeDynamic混淆转换器
 * 将普通的invoke指令转换为invokedynamic指令，增加静态分析难度
 */
public class InvokeDynamicTransformer {
    private static final Logger logger = LogManager.getLogger();

    public static void transform(CustomClassLoader loader) {
        Path classPath = Const.TEMP_PATH;
        if (!Files.exists(classPath)) {
            logger.error("class not exist: {}", classPath.toString());
            return;
        }

        try {
            logger.info("applying invoke dynamic obfuscation");

            ClassReader classReader = new ClassReader(Files.readAllBytes(classPath));
            ClassWriter classWriter = new CustomClassWriter(classReader,
                    ObfEnv.config.isAsmAutoCompute() ? Const.WriterASMOptions : 0, loader);
            InvokeDynamicClassVisitor changer = new InvokeDynamicClassVisitor(classWriter);
            classReader.accept(changer, Const.ReaderASMOptions);

            Files.delete(classPath);
            Files.write(classPath, classWriter.toByteArray());

            logger.info("invoke dynamic obfuscation completed");
        } catch (Exception ex) {
            logger.error("invoke dynamic transform error: {}", ex.toString());
        }
    }
}
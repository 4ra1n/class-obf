package me.n1ar4.clazz.obfuscator.transform;

import me.n1ar4.clazz.obfuscator.Const;
import me.n1ar4.clazz.obfuscator.asm.CompileInfoClassVisitor;
import me.n1ar4.clazz.obfuscator.core.ObfEnv;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.nio.file.Files;
import java.nio.file.Path;

public class DeleteInfoTransformer {
    private static final Logger logger = LogManager.getLogger();

    public static void transform() {
        Path classPath = Const.TEMP_PATH;
        if (!Files.exists(classPath)) {
            logger.error("class not exist: {}", classPath.toString());
            return;
        }
        try {
            ClassReader classReader = new ClassReader(Files.readAllBytes(classPath));
            ClassWriter classWriter = new ClassWriter(classReader,
                    ObfEnv.config.isAsmAutoCompute() ? Const.WriterASMOptions : 0);
            CompileInfoClassVisitor changer = new CompileInfoClassVisitor(classWriter);
            classReader.accept(changer, Const.ReaderASMOptions);
            Files.delete(classPath);
            Files.write(classPath, classWriter.toByteArray());
        } catch (Exception ex) {
            logger.error("transform error: {}", ex.toString());
        }
    }
}

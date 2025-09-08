package me.n1ar4.clazz.obfuscator.transform;

import me.n1ar4.clazz.obfuscator.Const;
import me.n1ar4.clazz.obfuscator.asm.ParamObfCallClassVisitor;
import me.n1ar4.clazz.obfuscator.asm.ParamObfDefineClassVisitor;
import me.n1ar4.clazz.obfuscator.core.ObfEnv;
import me.n1ar4.clazz.obfuscator.loader.CustomClassLoader;
import me.n1ar4.clazz.obfuscator.loader.CustomClassWriter;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ParamObfTransformer {
    private static final Logger logger = LogManager.getLogger();

    public static void transform(CustomClassLoader loader) {
        Path classPath = Const.TEMP_PATH;
        if (!Files.exists(classPath)) {
            logger.error("class not exist: {}", classPath.toString());
            return;
        }
        Map<String, String> methodDescMap = new HashMap<>();
        try {
            ClassReader classReader = new ClassReader(Files.readAllBytes(classPath));
            ClassWriter classWriter = new CustomClassWriter(classReader,
                    ObfEnv.config.isAsmAutoCompute() ? Const.WriterASMOptions : 0, loader);
            ParamObfDefineClassVisitor defChanger = new ParamObfDefineClassVisitor(classWriter, methodDescMap);
            classReader.accept(defChanger, Const.ReaderASMOptions);
            Files.delete(classPath);
            Files.write(classPath, classWriter.toByteArray());

            for (Map.Entry<String, String> entry : methodDescMap.entrySet()) {
                logger.info("expand method {} desc {}", entry.getKey(), entry.getValue());
            }

            classReader = new ClassReader(Files.readAllBytes(classPath));
            classWriter = new CustomClassWriter(classReader,
                    ObfEnv.config.isAsmAutoCompute() ? Const.WriterASMOptions : 0, loader);
            ParamObfCallClassVisitor callChanger = new ParamObfCallClassVisitor(classWriter, methodDescMap);
            classReader.accept(callChanger, Const.ReaderASMOptions);
            Files.delete(classPath);
            Files.write(classPath, classWriter.toByteArray());
        } catch (Exception ex) {
            logger.error("transform error: {}", ex.toString());
        }
    }
}

package me.n1ar4.clazz.obfuscator.runtime;

import me.n1ar4.clazz.obfuscator.Const;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;
import org.objectweb.asm.ClassReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class RuntimeAnalyzer {
    private static final Logger logger = LogManager.getLogger();

    private final Path path;
    private final ArrayList<String> blackList;

    public RuntimeAnalyzer(Path path, ArrayList<String> blackList) {
        this.path = path;
        this.blackList = blackList;
    }

    public void analyze() {
        Path classPath = Const.TEMP_PATH;
        if (!Files.exists(classPath)) {
            logger.error("class not exist: {}", classPath.toString());
            return;
        }
        try {
            RuntimeClassVisitor rcv = new RuntimeClassVisitor(blackList);
            ClassReader cr = new ClassReader(Files.readAllBytes(path));
            cr.accept(rcv, Const.ReaderASMOptions);
        } catch (Exception ex) {
            logger.error("runtime analyze error: {}", ex.toString());
        }
    }
}

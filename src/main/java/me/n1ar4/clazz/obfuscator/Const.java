package me.n1ar4.clazz.obfuscator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface Const {
    String VERSION = "1.6.0";
    String PROJECT_URL = "https://github.com/jar-analyzer/class-obf";
    Path configPath = Paths.get("config.yaml");
    Path TEMP_PATH = Paths.get("class-obf-temp.class");
    int ASMVersion = Opcodes.ASM9;
    int ReaderASMOptions = ClassReader.SKIP_FRAMES;
    int WriterASMOptions = ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS;
}
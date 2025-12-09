package me.n1ar4.clazz.obfuscator.transform;

import me.n1ar4.clazz.obfuscator.Const;
import me.n1ar4.clazz.obfuscator.core.ObfEnv;
import me.n1ar4.clazz.obfuscator.loader.CustomClassLoader;
import me.n1ar4.clazz.obfuscator.loader.CustomClassWriter;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ControlFlowTransformer {
    private static final Logger logger = LogManager.getLogger();
    private static final Random random = new Random();
    private static int counter = 0;

    private static String jumpIntCondField;
    private static String jumpBoolCondField;
    private static int jumpIntCond;
    private static boolean jumpBoolCond;

    public static void transform(CustomClassLoader loader) {
        Path classPath = Const.TEMP_PATH;
        if (!Files.exists(classPath)) {
            logger.error("class not exist: {}", classPath.toString());
            return;
        }
        try {
            ClassReader classReader = new ClassReader(Files.readAllBytes(classPath));
            ClassNode classNode = new ClassNode();
            classReader.accept(classNode, Const.ReaderASMOptions);

            // Reset state for each class
            counter = 0;
            jumpIntCondField = null;
            jumpBoolCondField = null;

            for (MethodNode method : classNode.methods) {
                if (method.instructions != null && method.instructions.size() > 0) {
                    addFakeJumps(classNode, method.instructions);
                }
            }

            // Add fields if they were used
            if (jumpIntCondField != null) {
                classNode.fields.add(new FieldNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC,
                        jumpIntCondField, "I", null, jumpIntCond));
            }

            if (jumpBoolCondField != null) {
                classNode.fields.add(new FieldNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC,
                        jumpBoolCondField, "Z", null, jumpBoolCond));
            }

            logger.info("Added {} fake jumps", counter);

            ClassWriter classWriter = new CustomClassWriter(classReader,
                    ObfEnv.config.isAsmAutoCompute() ? Const.WriterASMOptions : 0, loader);
            classNode.accept(classWriter);
            Files.delete(classPath);
            Files.write(classPath, classWriter.toByteArray());
        } catch (Exception ex) {
            logger.error("transform error: {}", ex.toString());
        }
    }

    private static void addFakeJumps(ClassNode classNode, InsnList instructions) {
        AbstractInsnNode insn = instructions.getFirst();

        while (insn != null) {
            int opcode = insn.getOpcode();

            if (opcode == Opcodes.GOTO) {
                // we want to switch between comparing ints and booleans
                boolean useInt = random.nextBoolean();

                if (useInt ? jumpIntCondField == null : jumpBoolCondField == null) {
                    if (useInt) {
                        jumpIntCondField = getRandomName();
                        jumpIntCond = ThreadLocalRandom.current().nextInt();
                    } else {
                        jumpBoolCondField = getRandomName();
                        jumpBoolCond = random.nextBoolean();
                    }
                }

                InsnList jump = new InsnList();

                jump.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name,
                        useInt ? jumpIntCondField : jumpBoolCondField,
                        useInt ? "I" : "Z"));

                int jumpOpcode = useInt ?
                        (jumpIntCond < 0 ? Opcodes.IFLT : Opcodes.IFGE) :
                        (jumpBoolCond ? Opcodes.IFNE : Opcodes.IFEQ);

                jump.add(new JumpInsnNode(jumpOpcode, ((JumpInsnNode) insn).label));

                jump.add(new InsnNode(Opcodes.ACONST_NULL));
                jump.add(new InsnNode(Opcodes.ATHROW));

                AbstractInsnNode last = jump.getLast();

                instructions.insert(insn, jump);
                instructions.remove(insn);

                insn = last;

                ++counter;
            }
            insn = insn.getNext();
        }
    }

    private static String getRandomName() {
        StringBuilder sb = new StringBuilder();
        String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(CHARS.length());
            sb.append(CHARS.charAt(index));
        }
        return sb.toString();
    }
}

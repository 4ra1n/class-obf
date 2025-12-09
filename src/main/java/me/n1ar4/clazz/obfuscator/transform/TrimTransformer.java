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
import java.util.ListIterator;

public class TrimTransformer {
    private static final Logger logger = LogManager.getLogger();
    private static int counter = 0;

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

            counter = 0;

            for (MethodNode method : classNode.methods) {
                if (method.instructions != null && method.instructions.size() > 0) {
                    processMethod(method.instructions);
                }
            }

            logger.info("Trimmed {} math functions", counter);

            ClassWriter classWriter = new CustomClassWriter(classReader,
                    ObfEnv.config.isAsmAutoCompute() ? Const.WriterASMOptions : 0, loader);
            classNode.accept(classWriter);
            Files.delete(classPath);
            Files.write(classPath, classWriter.toByteArray());
        } catch (Exception ex) {
            logger.error("transform error: {}", ex.toString());
        }
    }

    private static void processMethod(InsnList instructions) {
        ListIterator<AbstractInsnNode> iterator = instructions.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode insn = iterator.next();
            if (insn instanceof MethodInsnNode) {
                MethodInsnNode methodInsn = (MethodInsnNode) insn;
                if ("java/lang/Math".equals(methodInsn.owner)) {
                    if ("abs".equals(methodInsn.name)) {
                        mutateAbs(instructions, methodInsn);
                    } else if ("max".equals(methodInsn.name)) {
                        mutateMax(instructions, methodInsn);
                    } else if ("min".equals(methodInsn.name)) {
                        mutateMin(instructions, methodInsn);
                    }
                }
            }
        }
    }

    private static void mutateMax(InsnList insns, MethodInsnNode insn) {
        mutate(insns, insn, insn.desc, Opcodes.IF_ICMPGE, Opcodes.IFGE);
    }

    private static void mutateMin(InsnList insns, MethodInsnNode insn) {
        mutate(insns, insn, insn.desc, Opcodes.IF_ICMPLE, Opcodes.IFLE);
    }

    private static void mutate(InsnList insns, MethodInsnNode insn, String desc, int cmp, int cmp2) {
        ++counter;
        char type = desc.charAt(desc.length() - 1);
        
        if (type == 'I') {
            LabelNode label = new LabelNode();
            InsnList toAdd = new InsnList();

            toAdd.add(new InsnNode(Opcodes.DUP2));
            toAdd.add(new JumpInsnNode(cmp, label));
            toAdd.add(new InsnNode(Opcodes.SWAP));
            toAdd.add(label);
            toAdd.add(new InsnNode(Opcodes.POP));

            insns.insert(insn, toAdd);
            insns.remove(insn);
        } else if (type == 'F') {
            LabelNode label = new LabelNode();
            InsnList toAdd = new InsnList();

            toAdd.add(new InsnNode(Opcodes.DUP2));
            toAdd.add(new InsnNode(Opcodes.FCMPL));
            toAdd.add(new JumpInsnNode(cmp2, label));
            toAdd.add(new InsnNode(Opcodes.SWAP));
            toAdd.add(label);
            toAdd.add(new InsnNode(Opcodes.POP));

            insns.insert(insn, toAdd);
            insns.remove(insn);
        }
    }

    private static void mutateAbs(InsnList insns, MethodInsnNode insn) {
        ++counter;
        String desc = insn.desc;
        char type = desc.charAt(desc.length() - 1);

        if (type == 'I') {
            LabelNode label = new LabelNode();
            InsnList toAdd = new InsnList();

            toAdd.add(new InsnNode(Opcodes.DUP));
            toAdd.add(new JumpInsnNode(Opcodes.IFGE, label));
            toAdd.add(new InsnNode(Opcodes.INEG));
            toAdd.add(label);

            insns.insert(insn, toAdd);
            insns.remove(insn);
        } else if (type == 'D') {
            mutateAbsNumber(insns, insn, Opcodes.DUP2, Opcodes.DCONST_0, Opcodes.DCMPG, Opcodes.DNEG);
        } else if (type == 'F') {
            mutateAbsNumber(insns, insn, Opcodes.DUP, Opcodes.FCONST_0, Opcodes.FCMPG, Opcodes.FNEG);
        }
    }

    private static void mutateAbsNumber(InsnList insns, MethodInsnNode insn, int dup, int const0, int cmpg, int neg) {
        LabelNode label = new LabelNode();
        InsnList toAdd = new InsnList();

        toAdd.add(new InsnNode(dup));
        toAdd.add(new InsnNode(const0));
        toAdd.add(new InsnNode(cmpg));
        toAdd.add(new JumpInsnNode(Opcodes.IFGE, label));
        toAdd.add(new InsnNode(neg));
        toAdd.add(label);

        insns.insert(insn, toAdd);
        insns.remove(insn);
    }
}

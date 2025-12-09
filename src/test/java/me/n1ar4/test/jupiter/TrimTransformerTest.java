package me.n1ar4.test.jupiter;

import me.n1ar4.clazz.obfuscator.Const;
import me.n1ar4.clazz.obfuscator.loader.CustomClassLoader;
import me.n1ar4.clazz.obfuscator.transform.TrimTransformer;
import me.n1ar4.test.samples.TrimSample;
import me.n1ar4.test.util.ObfClassLoader;
import me.n1ar4.test.util.ObfJUnitUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ListIterator;

public class TrimTransformerTest {

    @Test
    public void testTrimObfuscation() throws Exception {
        // 1. Get bytes from TrimSample
        byte[] original = ObfJUnitUtil.getClassBytes(TrimSample.class);
        String originalOutput = ObfJUnitUtil.runMainAndCapture(TrimSample.class);

        // 2. Write to Const.TEMP_PATH
        Path tempPath = Const.TEMP_PATH;
        Files.write(tempPath, original);

        try {
            // 3. Initialize ObfEnv.config and Run TrimTransformer
            me.n1ar4.clazz.obfuscator.core.ObfEnv.config = ObfJUnitUtil.baseOff();
            TrimTransformer.transform(new CustomClassLoader());

            // 4. Verify results
            byte[] transformed = Files.readAllBytes(tempPath);
            ClassReader cr = new ClassReader(transformed);
            ClassNode classNode = new ClassNode();
            cr.accept(classNode, 0);

            // Check if Math.abs/max/min calls are removed/replaced
            boolean foundMathCalls = false;
            for (MethodNode mn : classNode.methods) {
                ListIterator<AbstractInsnNode> iterator = mn.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode insn = iterator.next();
                    if (insn instanceof MethodInsnNode) {
                        MethodInsnNode methodInsn = (MethodInsnNode) insn;
                        if ("java/lang/Math".equals(methodInsn.owner) &&
                                ("abs".equals(methodInsn.name) ||
                                        "max".equals(methodInsn.name) ||
                                        "min".equals(methodInsn.name))) {
                            // Check descriptors that we support: I, F, D
                            String desc = methodInsn.desc;
                            char type = desc.charAt(desc.length() - 1);
                            if (type == 'I' || type == 'F' || type == 'D') {
                                foundMathCalls = true;
                                break;
                            }
                        }
                    }
                }
            }
            Assertions.assertFalse(foundMathCalls, "Math.abs/max/min calls should be replaced by instructions");

            // 5. Verify behavior is preserved
            Class<?> oc = new ObfClassLoader().define(TrimSample.class.getName(), transformed);
            String obfOutput = ObfJUnitUtil.runMainAndCapture(oc);
            Assertions.assertEquals(originalOutput, obfOutput, "Output should be preserved");

        } finally {
            Files.deleteIfExists(tempPath);
        }
    }
}

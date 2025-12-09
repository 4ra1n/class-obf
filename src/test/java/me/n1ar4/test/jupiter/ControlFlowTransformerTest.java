package me.n1ar4.test.jupiter;

import me.n1ar4.clazz.obfuscator.Const;
import me.n1ar4.clazz.obfuscator.core.ObfEnv;
import me.n1ar4.clazz.obfuscator.loader.CustomClassLoader;
import me.n1ar4.clazz.obfuscator.transform.ControlFlowTransformer;
import me.n1ar4.test.samples.ControlFlowSample;
import me.n1ar4.test.util.ObfClassLoader;
import me.n1ar4.test.util.ObfJUnitUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.nio.file.Files;
import java.nio.file.Path;

public class ControlFlowTransformerTest {

    @Test
    public void testControlFlowObfuscation() throws Exception {
        byte[] original = ObfJUnitUtil.getClassBytes(ControlFlowSample.class);
        String originalOutput = ObfJUnitUtil.runMainAndCapture(ControlFlowSample.class);

        Path tempPath = Const.TEMP_PATH;
        Files.write(tempPath, original);

        try {
            ObfEnv.config = ObfJUnitUtil.baseOff();
            ControlFlowTransformer.transform(new CustomClassLoader());

            byte[] transformed = Files.readAllBytes(tempPath);
            ClassReader cr = new ClassReader(transformed);
            ClassNode classNode = new ClassNode();
            cr.accept(classNode, 0);

            boolean hasIntField = false;
            boolean hasBoolField = false;
            if (classNode.fields != null) {
                for (Object f : classNode.fields) {
                    FieldNode fn = (FieldNode) f;
                    if (fn.desc.equals("I")) hasIntField = true;
                    if (fn.desc.equals("Z")) hasBoolField = true;
                }
            }

            Assertions.assertTrue(hasIntField || hasBoolField, "Should have added at least one condition field");

            MethodNode testMethod = null;
            for (MethodNode mn : classNode.methods) {
                if (mn.name.equals("testMethod")) {
                    testMethod = mn;
                    break;
                }
            }
            Assertions.assertNotNull(testMethod);

            boolean foundPattern = false;
            for (int i = 0; i < testMethod.instructions.size(); i++) {
                if (testMethod.instructions.get(i).getOpcode() == Opcodes.ACONST_NULL &&
                        i + 1 < testMethod.instructions.size() &&
                        testMethod.instructions.get(i + 1).getOpcode() == Opcodes.ATHROW) {
                    foundPattern = true;
                    break;
                }
            }
            Assertions.assertTrue(foundPattern, "Should find the obfuscated jump pattern (ACONST_NULL, ATHROW)");

            // 5. Verify behavior is preserved
            Class<?> oc = new ObfClassLoader().define(ControlFlowSample.class.getName(), transformed);
            String obfOutput = ObfJUnitUtil.runMainAndCapture(oc);
            Assertions.assertEquals(originalOutput, obfOutput, "Output should be preserved");

        } finally {
            Files.deleteIfExists(tempPath);
        }
    }
}

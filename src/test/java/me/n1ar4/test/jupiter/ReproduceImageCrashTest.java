package me.n1ar4.test.jupiter;

import me.n1ar4.clazz.obfuscator.asm.ImageCrashClassVisitor;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ReproduceImageCrashTest {
    @Test
    public void testImageCrash() throws Exception {
        // 1. Create a simple class
        ClassWriter cw = new ClassWriter(0);
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "TestClass", null, "java/lang/Object", null);
        cw.visitEnd();
        byte[] original = cw.toByteArray();

        // 2. Transform it using ImageCrashClassVisitor
        ClassReader cr = new ClassReader(original);
        ClassWriter cw2 = new ClassWriter(0);
        ImageCrashClassVisitor visitor = new ImageCrashClassVisitor(cw2);
        cr.accept(visitor, 0);
        byte[] transformed = cw2.toByteArray();

        Files.write(Paths.get("test.class"), transformed);

        // 3. Inspect the result
        ClassReader cr2 = new ClassReader(transformed);
        final boolean[] found = {false};
        cr2.accept(new ClassVisitor(Opcodes.ASM9) {
            @Override
            public void visitInnerClass(String name, String outerName, String innerName, int access) {
                System.out.println("Found Inner Class: " + name);
                if (name.startsWith("<html>")) {
                    found[0] = true;
                }
            }
        }, 0);

        if (found[0]) {
            System.out.println("SUCCESS: Image Crash Inner Class found.");
        } else {
            System.out.println("FAILURE: Image Crash Inner Class NOT found.");
            throw new RuntimeException("Image Crash Inner Class NOT found");
        }
    }
}

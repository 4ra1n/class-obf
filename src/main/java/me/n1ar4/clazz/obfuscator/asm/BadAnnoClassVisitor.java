package me.n1ar4.clazz.obfuscator.asm;

import me.n1ar4.clazz.obfuscator.Const;
import me.n1ar4.clazz.obfuscator.core.ObfEnv;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BadAnnoClassVisitor extends ClassVisitor {
    private static final String STRING = generate();
    private static final String DEFAULT = "\n\n\n\n\n\n\n\n\n\n\nCLASS-OBF PROTECTED\n\n\n\n\n\n\n\n\n\n\n";

    private int counter = 0;

    public BadAnnoClassVisitor(ClassVisitor classVisitor) {
        super(Const.ASMVersion, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        addInvisibleAnnotation();
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        FieldVisitor fv = super.visitField(access, name, descriptor, signature, value);
        if (fv != null) {
            fv.visitAnnotation(getAnnotationDescriptor(), false);
            counter++;
        }
        return fv;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (mv != null) {
            mv.visitAnnotation(getAnnotationDescriptor(), false);
            counter++;
        }
        return mv;
    }

    private void addInvisibleAnnotation() {
        super.visitAnnotation(getAnnotationDescriptor(), false);
        counter++;
    }

    private String getAnnotationDescriptor() {
        return STRING;
    }

    private static String generate() {
        String data;
        Path annoPath = Paths.get(ObfEnv.config.getBadAnnoTextFile());
        if (Files.notExists(annoPath)) {
            data = DEFAULT;
        } else {
            try {
                byte[] b = Files.readAllBytes(annoPath);
                data = new String(b);
            } catch (Exception ignored) {
                data = DEFAULT;
            }
        }
        return data;
    }

    public int getCounter() {
        return counter;
    }
}

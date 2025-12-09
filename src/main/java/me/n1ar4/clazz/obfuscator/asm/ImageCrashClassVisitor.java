package me.n1ar4.clazz.obfuscator.asm;

import me.n1ar4.clazz.obfuscator.Const;
import me.n1ar4.jrandom.core.JRandom;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class ImageCrashClassVisitor extends ClassVisitor {
    public ImageCrashClassVisitor(ClassVisitor classVisitor) {
        super(Const.ASMVersion, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        String randomName = JRandom.getInstance().randomString(5);
        String crashName = String.format("<html><img src=\"https://%s", randomName);
        super.visitInnerClass(crashName, name, randomName, Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC);
    }
}

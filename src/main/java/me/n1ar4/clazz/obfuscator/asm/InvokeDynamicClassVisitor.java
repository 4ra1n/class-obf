package me.n1ar4.clazz.obfuscator.asm;

import org.objectweb.asm.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class InvokeDynamicClassVisitor extends ClassVisitor {
    private static final String BOOTSTRAP_METHOD_NAME = "bootstrap";
    private static final String BOOTSTRAP_METHOD_DESC = "(Ljava/lang/invoke/MethodHandles$Lookup;" +
            "Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/invoke/CallSite;";
    private final Random random = new Random();
    private String className;
    private boolean hasBootstrapMethod = false;
    public InvokeDynamicClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM9, classVisitor);
    }
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }
    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new InvokeDynamicMethodAdapter(mv);
    }
    @Override
    public void visitEnd() {
        if (hasBootstrapMethod) {
            addBootstrapMethod();
        }
        super.visitEnd();
    }

    private void addBootstrapMethod() {
        MethodVisitor mv = super.visitMethod(
                Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
                BOOTSTRAP_METHOD_NAME,
                BOOTSTRAP_METHOD_DESC,
                null,
                new String[]{"java/lang/Exception"}
        );
        mv.visitCode();
        mv.visitTypeInsn(Opcodes.NEW, "java/lang/invoke/ConstantCallSite");
        mv.visitInsn(Opcodes.DUP);
        mv.visitVarInsn(Opcodes.ALOAD, 3);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
                "java/lang/Class", 
                "forName", 
                "(Ljava/lang/String;)Ljava/lang/Class;", 
                false);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.SWAP);
        mv.visitVarInsn(Opcodes.ALOAD, 4);
        mv.visitVarInsn(Opcodes.ALOAD, 2);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
                "java/lang/invoke/MethodHandles$Lookup", 
                "findStatic", 
                "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;", 
                false);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, 
                "java/lang/invoke/ConstantCallSite", 
                "<init>", 
                "(Ljava/lang/invoke/MethodHandle;)V", 
                false);
        mv.visitInsn(Opcodes.ARETURN);
        mv.visitMaxs(6, 5);
        mv.visitEnd();
    }

    private class InvokeDynamicMethodAdapter extends MethodVisitor {
        public InvokeDynamicMethodAdapter(MethodVisitor methodVisitor) {
            super(Opcodes.ASM9, methodVisitor);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            // 只处理静态方法调用
            if (opcode == Opcodes.INVOKESTATIC && shouldObfuscate(owner, name)) {
                hasBootstrapMethod = true;
                Handle bootstrapHandle = new Handle(
                        Opcodes.H_INVOKESTATIC,
                        className,
                        BOOTSTRAP_METHOD_NAME,
                        BOOTSTRAP_METHOD_DESC,
                        false
                );
                super.visitInvokeDynamicInsn(
                        name,
                        descriptor,
                        bootstrapHandle,
                        owner.replace('/', '.'),
                        name
                );
            } else {
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            }
        }

        private boolean shouldObfuscate(String owner, String name) {
            if (owner.startsWith("java/") || owner.startsWith("javax/") || 
                owner.startsWith("sun/") || name.equals("<init>") || name.equals("<clinit>")) {
                return false;
            }
            return !name.equals("main");
        }
    }
}
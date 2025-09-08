package me.n1ar4.clazz.obfuscator.asm;

import me.n1ar4.clazz.obfuscator.Const;
import me.n1ar4.clazz.obfuscator.base.ClassReference;
import me.n1ar4.clazz.obfuscator.base.MethodReference;
import me.n1ar4.clazz.obfuscator.core.ObfEnv;
import org.objectweb.asm.*;

import java.util.*;

public class ParamObfDefineClassVisitor extends ClassVisitor {
    private final List<String> obfWhiteLIst = new ArrayList<>();
    private final int obfParamNum;
    private String currentClassName;
    private static final Random random = new Random(0xcafe);
    private static final String[] STANDARD_TYPES = {"I", "J", "F", "D", "Z", "B", "C", "S"};

    private final Map<String, String> methodDescMap;
    private final List<String> randomParamTypes = new ArrayList<>();

    public ParamObfDefineClassVisitor(ClassVisitor classVisitor, Map<String, String> methodDescMap) {
        super(Const.ASMVersion, classVisitor);
        Collections.addAll(this.obfWhiteLIst, ObfEnv.config.getExpandMethodList());
        this.obfParamNum = ObfEnv.config.getExpandParamNum();
        this.methodDescMap = methodDescMap;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.currentClassName = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (obfWhiteLIst.contains(name)) {
            String newDesc = expandMethodDescriptor(descriptor);
            MethodVisitor mv = super.visitMethod(access, name, newDesc,
                    expandMethodSignature(signature), exceptions);
            MethodReference.Handle oldHandle = new MethodReference.Handle(
                    new ClassReference.Handle(currentClassName), name, descriptor);
            MethodReference.Handle handle = ObfEnv.methodNameObfMapping.get(oldHandle);
            if (handle != null) {
                handle.setDesc(newDesc);
                ObfEnv.methodNameObfMapping.put(new MethodReference.Handle(
                        new ClassReference.Handle(currentClassName), name, newDesc), handle);
                ObfEnv.methodNameObfMapping.remove(oldHandle);
            }
            methodDescMap.put(name, newDesc);
            return new ParamObfDefineMethodAdapter(mv, Type.getArgumentTypes(newDesc), access);
        }
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new ParamObfDefineMethodAdapter(mv, Type.getArgumentTypes(descriptor), access);
    }

    private String generateRandomTypeDescriptor() {
        return STANDARD_TYPES[random.nextInt(STANDARD_TYPES.length)];
    }

    private String expandMethodDescriptor(String descriptor) {
        if (descriptor == null) {
            return null;
        }
        Type[] argumentTypes = Type.getArgumentTypes(descriptor);
        Type returnType = Type.getReturnType(descriptor);

        StringBuilder newDesc = new StringBuilder("(");
        for (Type argType : argumentTypes) {
            newDesc.append(argType.getDescriptor());
        }
        randomParamTypes.clear();
        for (int i = 0; i < obfParamNum; i++) {
            String randomType = generateRandomTypeDescriptor();
            randomParamTypes.add(randomType);
            newDesc.append(randomType);
        }
        newDesc.append(")").append(returnType.getDescriptor());
        return newDesc.toString();
    }

    private String expandMethodSignature(String signature) {
        if (signature == null) {
            return null;
        }
        StringBuilder newSig = new StringBuilder(signature);
        int lastParenIndex = signature.lastIndexOf(')');
        if (lastParenIndex != -1) {
            StringBuilder insertStr = new StringBuilder();
            for (String randomType : randomParamTypes) {
                insertStr.append(randomType);
            }
            newSig.insert(lastParenIndex, insertStr);
        }
        return newSig.toString();
    }

    class ParamObfDefineMethodAdapter extends MethodVisitor {
        private final Type[] argumentTypes;
        private final Label startLabel = new Label();
        private final Label endLabel = new Label();
        private final int startIndex;
        private final int access;

        protected ParamObfDefineMethodAdapter(MethodVisitor mv, Type[] argumentTypes, int access) {
            super(Const.ASMVersion, mv);
            this.argumentTypes = argumentTypes;
            this.startIndex = argumentTypes.length - obfParamNum;
            this.access = access;
        }

        @Override
        public void visitCode() {
            mv.visitCode();
            mv.visitLabel(startLabel);
            int localVarIndex = calculateLocalVarStartIndex();
            performObfuscationOperations(localVarIndex);
        }

        private int calculateLocalVarStartIndex() {
            int index = 0;
            if ((access & Opcodes.ACC_STATIC) == 0) {
                index = 1;
            }
            for (Type type : argumentTypes) {
                index += type.getSize();
            }
            return index;
        }

        private void performObfuscationOperations(int localVarIndex) {
            int paramIndex = 0;
            if ((access & Opcodes.ACC_STATIC) == 0) {
                paramIndex = 1;
            }
            for (int i = 0; i < argumentTypes.length; i++) {
                Type argType = argumentTypes[i];
                if (i >= startIndex) {
                    performTypeSpecificObfuscation(argType, paramIndex, localVarIndex++);
                }
                paramIndex += argType.getSize();
            }
        }

        private void performTypeSpecificObfuscation(Type argType, int paramIndex, int localVarIndex) {
            switch (argType.getSort()) {
                case Type.INT:
                    mv.visitVarInsn(Opcodes.ILOAD, paramIndex);
                    obfuscateIntValue();
                    mv.visitVarInsn(Opcodes.ISTORE, localVarIndex);
                    break;
                case Type.LONG:
                    mv.visitVarInsn(Opcodes.LLOAD, paramIndex);
                    obfuscateLongValue();
                    mv.visitVarInsn(Opcodes.LSTORE, localVarIndex);
                    break;
                case Type.FLOAT:
                    mv.visitVarInsn(Opcodes.FLOAD, paramIndex);
                    obfuscateFloatValue();
                    mv.visitVarInsn(Opcodes.FSTORE, localVarIndex);
                    break;
                case Type.DOUBLE:
                    mv.visitVarInsn(Opcodes.DLOAD, paramIndex);
                    obfuscateDoubleValue();
                    mv.visitVarInsn(Opcodes.DSTORE, localVarIndex);
                    break;
                case Type.BOOLEAN:
                case Type.BYTE:
                case Type.CHAR:
                case Type.SHORT:
                    mv.visitVarInsn(Opcodes.ILOAD, paramIndex);
                    obfuscateIntValue();
                    mv.visitVarInsn(Opcodes.ISTORE, localVarIndex);
                    break;
            }
        }

        private void obfuscateIntValue() {
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitInsn(Opcodes.IADD);
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitInsn(Opcodes.ISUB);
        }

        private void obfuscateLongValue() {
            mv.visitInsn(Opcodes.LCONST_1);
            mv.visitInsn(Opcodes.LADD);
            mv.visitInsn(Opcodes.LCONST_1);
            mv.visitInsn(Opcodes.LSUB);
        }

        private void obfuscateFloatValue() {
            mv.visitInsn(Opcodes.FCONST_1);
            mv.visitInsn(Opcodes.FADD);
            mv.visitInsn(Opcodes.FCONST_1);
            mv.visitInsn(Opcodes.FSUB);
        }

        private void obfuscateDoubleValue() {
            mv.visitInsn(Opcodes.DCONST_1);
            mv.visitInsn(Opcodes.DADD);
            mv.visitInsn(Opcodes.DCONST_1);
            mv.visitInsn(Opcodes.DSUB);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            maxStack = Math.max(maxStack, 2);
            maxLocals = Math.max(maxLocals, calculateLocalVarStartIndex() + obfParamNum);
            mv.visitMaxs(maxStack, maxLocals);
        }

        @Override
        public void visitEnd() {
            mv.visitLabel(endLabel);
            mv.visitEnd();
        }

        @Override
        public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
            mv.visitFrame(type, numLocal, local, numStack, stack);
        }

        @Override
        public void visitInsn(int opcode) {
            mv.visitInsn(opcode);
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            mv.visitVarInsn(opcode, var);
        }

        @Override
        public void visitLabel(Label label) {
            mv.visitLabel(label);
        }
    }
}
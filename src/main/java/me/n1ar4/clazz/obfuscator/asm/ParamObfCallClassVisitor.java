package me.n1ar4.clazz.obfuscator.asm;

import me.n1ar4.clazz.obfuscator.Const;
import me.n1ar4.clazz.obfuscator.core.ObfEnv;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ParamObfCallClassVisitor extends ClassVisitor {
    private final List<String> obfWhiteLIst = new ArrayList<>();
    private String currentClassName;
    private final Map<String, String> methodDescMap;

    public ParamObfCallClassVisitor(ClassVisitor classVisitor, Map<String, String> methodDescMap) {
        super(Const.ASMVersion, classVisitor);
        Collections.addAll(this.obfWhiteLIst, ObfEnv.config.getExpandMethodList());
        this.methodDescMap = methodDescMap;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.currentClassName = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new ParamObfCallMethodAdapter(mv);
    }

    class ParamObfCallMethodAdapter extends MethodVisitor {
        protected ParamObfCallMethodAdapter(MethodVisitor mv) {
            super(Const.ASMVersion, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name,
                                    String descriptor, boolean isInterface) {

            if (obfWhiteLIst.contains(name)) {
                String expandedDesc;
                if (owner.equals(currentClassName)) {
                    expandedDesc = methodDescMap.get(name);
                    if (expandedDesc == null || expandedDesc.isEmpty()) {
                        throw new RuntimeException(name + " method desc is null");
                    }
                } else {
                    throw new RuntimeException("not support across class");
                }
                Type[] expandedArgTypes = Type.getArgumentTypes(expandedDesc);
                Type[] originalArgTypes = Type.getArgumentTypes(descriptor);
                int addedParamCount = expandedArgTypes.length - originalArgTypes.length;

                for (int i = 0; i < addedParamCount; i++) {
                    Type paramType = expandedArgTypes[originalArgTypes.length + i];
                    generateDefaultValueForType(paramType);
                }
                super.visitMethodInsn(opcode, owner, name, expandedDesc, isInterface);
            } else {
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            }
        }

        private void generateDefaultValueForType(Type paramType) {
            switch (paramType.getSort()) {
                case Type.INT:
                case Type.BOOLEAN:
                case Type.BYTE:
                case Type.CHAR:
                case Type.SHORT:
                    visitInsn(Opcodes.ICONST_0);
                    break;
                case Type.LONG:
                    visitInsn(Opcodes.LCONST_0);
                    break;
                case Type.FLOAT:
                    visitInsn(Opcodes.FCONST_0);
                    break;
                case Type.DOUBLE:
                    visitInsn(Opcodes.DCONST_0);
                    break;
                default:
                    // 对于其他类型（如对象引用），推送null
                    visitInsn(Opcodes.ACONST_NULL);
                    break;
            }
        }
    }
}
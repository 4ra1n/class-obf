package me.n1ar4.clazz.obfuscator.runtime;

import me.n1ar4.clazz.obfuscator.Const;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;
import org.objectweb.asm.ClassVisitor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class RuntimeClassVisitor extends ClassVisitor {
    private static final Logger logger = LogManager.getLogger();
    private final ArrayList<String> blackList;

    protected RuntimeClassVisitor(ArrayList<String> blackList) {
        super(Const.ASMVersion);
        this.blackList = blackList;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        // 自动添加实现
        String superRealClass = superName.replace("/", ".");
        try {
            Class<?> clazz = Class.forName(superRealClass);
            // 拿到 public 方法
            Method[] methods = clazz.getMethods();
            // SET 去重
            Set<String> set = new HashSet<>();
            for (Method method : methods) {
                set.add(method.getName());
            }
            for (String method : set) {
                blackList.add(method);
                logger.info("auto add (super class - {}) method black list: {}", superRealClass, method);
            }
        } catch (Exception ignored) {
        }
        // 自动添加接口
        for (String iface : interfaces) {
            try {
                String ifaceRealClass = iface.replace("/", ".");
                Class<?> clazz = Class.forName(ifaceRealClass);
                // 拿到 public 方法
                Method[] methods = clazz.getMethods();
                // SET 去重
                Set<String> set = new HashSet<>();
                for (Method method : methods) {
                    set.add(method.getName());
                }
                for (String method : set) {
                    blackList.add(method);
                    logger.info("auto add (iface - {}) method black list: {}", ifaceRealClass, method);
                }
            } catch (Exception ignored) {
            }
        }
    }
}

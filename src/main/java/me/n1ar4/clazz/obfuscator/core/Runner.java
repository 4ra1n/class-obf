package me.n1ar4.clazz.obfuscator.core;

import me.n1ar4.clazz.obfuscator.Const;
import me.n1ar4.clazz.obfuscator.base.ClassField;
import me.n1ar4.clazz.obfuscator.base.ClassFileEntity;
import me.n1ar4.clazz.obfuscator.base.ClassReference;
import me.n1ar4.clazz.obfuscator.base.MethodReference;
import me.n1ar4.clazz.obfuscator.config.BaseCmd;
import me.n1ar4.clazz.obfuscator.config.BaseConfig;
import me.n1ar4.clazz.obfuscator.loader.CustomClassLoader;
import me.n1ar4.clazz.obfuscator.transform.*;
import me.n1ar4.clazz.obfuscator.utils.ASMUtil;
import me.n1ar4.clazz.obfuscator.utils.ColorUtil;
import me.n1ar4.clazz.obfuscator.utils.FileUtil;
import me.n1ar4.clazz.obfuscator.utils.NameUtil;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;
import org.objectweb.asm.ClassReader;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Runner {
    private static final Logger logger = LogManager.getLogger();

    private static void addClass(Path path) {
        ClassFileEntity cf = new ClassFileEntity();
        cf.setPath(path);
        AnalyzeEnv.classFileList.add(cf);
    }

    public static void run(Path path, BaseConfig config, boolean isApi, BaseCmd cmd) {
        ObfEnv.config = config;
        if (!config.isQuiet()) {
            // 输出配置信息
            System.out.println(
                    ColorUtil.blue("######################## OBFUSCATE CONFIG ########################"));
            config.show();
            System.out.println(
                    ColorUtil.blue("##################################################################"));
        }

        String fileName = FileUtil.getFileNameWithoutExt(path);
        String newFile = fileName + "_obf.class";

        try {
            Files.write(Const.TEMP_PATH, Files.readAllBytes(path));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        Path dirPath = Paths.get(CustomClassLoader.LIB_DIR);
        try {
            Files.createDirectory(dirPath);
            logger.info("已成功创建 {} 目录", CustomClassLoader.LIB_DIR);
            Files.write(dirPath.resolve(Paths.get("README.md")), ("# README\n" +
                    "\n" +
                    "一些情况下混淆可能需要接触依赖库\n" +
                    "\n" +
                    "请将依赖放在 `class-obf-lib` 目录中").getBytes(StandardCharsets.UTF_8));
        } catch (Exception ignored) {
            logger.warn("无法创建 {} 目录", CustomClassLoader.LIB_DIR);
        }

        addClass(path);
        logger.info("add the input class to analyze");

        DiscoveryRunner.start(AnalyzeEnv.classFileList, AnalyzeEnv.discoveredClasses,
                AnalyzeEnv.discoveredMethods, AnalyzeEnv.classMap, AnalyzeEnv.methodMap,
                AnalyzeEnv.fieldsInClassMap);
        logger.info("discovered class and methods finish");

        for (MethodReference mr : AnalyzeEnv.discoveredMethods) {
            ClassReference.Handle ch = mr.getClassReference();
            if (AnalyzeEnv.methodsInClassMap.get(ch) == null) {
                List<MethodReference> ml = new ArrayList<>();
                ml.add(mr);
                AnalyzeEnv.methodsInClassMap.put(ch, ml);
            } else {
                List<MethodReference> ml = AnalyzeEnv.methodsInClassMap.get(ch);
                ml.add(mr);
                AnalyzeEnv.methodsInClassMap.put(ch, ml);
            }
        }
        logger.info("build methods in class map finish");

        MethodCallRunner.start(AnalyzeEnv.classFileList, AnalyzeEnv.methodCalls);
        logger.info("build method calls finish");

        if (AnalyzeEnv.discoveredClasses.size() > 1 || AnalyzeEnv.methodsInClassMap.size() > 1) {
            logger.error("inner class not support");
            return;
        }

        Map.Entry<ClassReference.Handle, List<MethodReference>> entry =
                AnalyzeEnv.methodsInClassMap.entrySet().iterator().next();
        ClassReference.Handle key = entry.getKey();
        List<MethodReference> value = entry.getValue();
        String newClassName = key.getName();
        for (MethodReference mr : value) {
            String desc = mr.getDesc();
            String oldMethodName = mr.getName();
            if (oldMethodName.startsWith("lambda$") ||
                    oldMethodName.startsWith("access$") ||
                    oldMethodName.equals("<init>") ||
                    oldMethodName.equals("<clinit>")) {
                logger.info("ignore method {}", oldMethodName);
                continue;
            }
            MethodReference.Handle oldHandle = new MethodReference.Handle(
                    new ClassReference.Handle(newClassName),
                    oldMethodName, desc);
            String newMethodName = NameUtil.genNewMethod();
            MethodReference.Handle newHandle = new MethodReference.Handle(
                    new ClassReference.Handle(newClassName),
                    newMethodName, desc);
            logger.info("replace method {} to {}", oldMethodName, newMethodName);
            ObfEnv.methodNameObfMapping.put(oldHandle, newHandle);
        }
        logger.info("build obfuscate method mapping finish");

        // 处理 method mapping 中的 black method 问题
        Map<MethodReference.Handle, MethodReference.Handle>
                methodNameObfMapping = new HashMap<>(ObfEnv.methodNameObfMapping);
        for (Map.Entry<MethodReference.Handle, MethodReference.Handle> en : ObfEnv.methodNameObfMapping.entrySet()) {
            String oldClassName = en.getKey().getName();
            for (String s : ObfEnv.config.getMethodBlackList()) {
                if (s.equals(oldClassName)) {
                    methodNameObfMapping.remove(en.getKey());
                    methodNameObfMapping.put(en.getKey(), en.getKey());
                    break;
                }
                Pattern pattern = Pattern.compile(s, Pattern.DOTALL);
                Matcher matcher = pattern.matcher(oldClassName);
                if (matcher.matches()) {
                    methodNameObfMapping.remove(en.getKey());
                    methodNameObfMapping.put(en.getKey(), en.getKey());
                    break;
                }
            }
        }
        ObfEnv.methodNameObfMapping.clear();
        ObfEnv.methodNameObfMapping.putAll(methodNameObfMapping);
        methodNameObfMapping.clear();
        logger.info("method blacklist filter finish");

        // 处理 field name
        ClassReference c = AnalyzeEnv.discoveredClasses.iterator().next();
        String theClassName = c.getName();
        for (String s : AnalyzeEnv.fieldsInClassMap.get(c.getName())) {
            ClassField oldMember = new ClassField();
            oldMember.setClassName(theClassName);
            oldMember.setFieldName(s);

            ClassField newMember = new ClassField();
            newMember.setClassName(newClassName);
            newMember.setFieldName(NameUtil.genNewFields());

            ObfEnv.fieldNameObfMapping.put(oldMember, newMember);
        }
        logger.info("build field mapping finish");

        Map.Entry<ClassReference.Handle, ArrayList<String>> e = ObfEnv.stringInClass.entrySet().iterator().next();
        ArrayList<String> t = e.getValue();
        if (t == null) {
            return;
        }
        ArrayList<String> newRes = new ArrayList<>(t);
        ObfEnv.newStringInClass.put(e.getKey().getName(), newRes);
        logger.info("build string mapping finish");
        if (!config.isQuiet()) {
            System.out.println(
                    ColorUtil.blue("######################### ANALYSIS DATA #########################"));
            System.out.println(ColorUtil.green("AnalyzeEnv.classFileList -> ") +
                    ColorUtil.red(String.valueOf(AnalyzeEnv.classFileList.size())));
            System.out.println(ColorUtil.green("AnalyzeEnv.discoveredClasses -> ") +
                    ColorUtil.red(String.valueOf(AnalyzeEnv.discoveredClasses.size())));
            System.out.println(ColorUtil.green("AnalyzeEnv.discoveredMethods -> ") +
                    ColorUtil.red(String.valueOf(AnalyzeEnv.discoveredMethods.size())));
            System.out.println(ColorUtil.green("AnalyzeEnv.methodsInClassMap -> ") +
                    ColorUtil.red(String.valueOf(AnalyzeEnv.methodsInClassMap.size())));
            System.out.println(ColorUtil.green("AnalyzeEnv.methodCalls -> ") +
                    ColorUtil.red(String.valueOf(AnalyzeEnv.methodCalls.size())));
            System.out.println(ColorUtil.green("AnalyzeEnv.classMap -> ") +
                    ColorUtil.red(String.valueOf(AnalyzeEnv.classMap.size())));
            System.out.println(ColorUtil.green("AnalyzeEnv.methodMap -> ") +
                    ColorUtil.red(String.valueOf(AnalyzeEnv.methodMap.size())));
            System.out.println(ColorUtil.green("AnalyzeEnv.fieldsInClassMap -> ") +
                    ColorUtil.red(String.valueOf(AnalyzeEnv.fieldsInClassMap.size())));
            System.out.println(
                    ColorUtil.blue("#################################################################"));
        }
        logger.info("build obfuscate data finish");
        if (!config.isQuiet()) {
            System.out.println(
                    ColorUtil.blue("######################### OBFUSCATE DATA #########################"));
            System.out.println(ColorUtil.green("ObfEnv.ADVANCE_STRING_NAME -> ") +
                    ColorUtil.red(String.valueOf(ObfEnv.ADVANCE_STRING_NAME)));
            System.out.println(ColorUtil.green("ObfEnv.methodNameObfMapping -> ") +
                    ColorUtil.red(String.valueOf(ObfEnv.methodNameObfMapping.size())));
            System.out.println(ColorUtil.green("ObfEnv.fieldNameObfMapping -> ") +
                    ColorUtil.red(String.valueOf(ObfEnv.fieldNameObfMapping.size())));
            System.out.println(ColorUtil.green("ObfEnv.stringInClass -> ") +
                    ColorUtil.red(String.valueOf(ObfEnv.stringInClass.size())));
            System.out.println(ColorUtil.green("ObfEnv.newStringInClass -> ") +
                    ColorUtil.red(String.valueOf(ObfEnv.newStringInClass.size())));
            System.out.println(
                    ColorUtil.blue("#################################################################"));
        }

        CustomClassLoader loader = new CustomClassLoader();

        if (config.isEnableExpandMethod()) {
            ParamObfTransformer.transform(loader);
            logger.info("run param obf transformer finish");
        }

        if (config.isEnableMethodName()) {
            MethodNameTransformer.transform(loader);
            logger.info("run method name transformer finish");
        }

        if (config.isEnableFieldName()) {
            FieldNameTransformer.transform(loader);
            logger.info("run field name transformer finish");
        }

        if (config.isEnableParamName()) {
            ParameterTransformer.transform(loader);
            logger.info("run parameter transformer finish");
        }

        if (config.isEnableXOR()) {
            XORTransformer.transform(loader);
            logger.info("run xor transformer finish");
        }

        if (config.isEnableAdvanceString()) {
            StringArrayTransformer.transform(loader);
            if (config.isEnableXOR()) {
                XORTransformer.transform(loader);
            }
            logger.info("run string array transformer finish");
        }

        if (config.isEnableAES()) {
            StringEncryptTransformer.transform(config.getAesKey(), config.getAesDecName(), loader);
            logger.info("run string aes transformer finish");
        }

        if (config.isEnableJunk()) {
            JunkCodeTransformer.transform(config, loader);
            logger.info("run junk transformer finish");
        }

        if (config.isEnableBadAnno()) {
            BadAnnoTransformer.transform(loader);
            logger.info("run bad anno transformer finish");
        }

        if (config.isAntiAI()) {
            AntiPromptTransformer.transform(loader);
            logger.info("run anti ai transformer finish");
        }

        if (config.isEnableInvokeDynamic()) {
            InvokeDynamicTransformer.transform(loader);
            logger.info("run invoke dynamic transformer finish");
        }

        if (config.isEnableDeleteCompileInfo()) {
            DeleteInfoTransformer.transform(loader);
            logger.info("run delete info transformer finish");
        }

        if (!config.isQuiet()) {
            System.out.println(
                    ColorUtil.blue("###################### ALL OBFUSCATE FINISH ######################"));
        }
        boolean simpleOutput = cmd == null || !cmd.isStdOutput();
        if (isApi || simpleOutput) {
            try {
                Files.write(Paths.get(newFile), Files.readAllBytes(Const.TEMP_PATH));
                logger.info("class obfuscate finish and write new class file");
            } catch (Exception ignored) {
            }
        } else {
            // 标准输出
            try {
                byte[] result = Files.readAllBytes(Const.TEMP_PATH);
                ClassReader classReader = new ClassReader(result);
                String packageName = ASMUtil.getPackageName(classReader);
                String className = ASMUtil.getClassName(classReader);

                Path baseDir = Files.createDirectory(Paths.get("class-obf-output"));

                if (!packageName.contains(".")) {
                    Path packDir = Files.createDirectory(baseDir.resolve(packageName));
                    Path classFile = packDir.resolve(String.format("%s.class", className));
                    Files.write(classFile, result);
                } else {
                    String[] dirs = packageName.split("\\.");
                    Path packDir = baseDir;
                    for (String dir : dirs) {
                        packDir = Files.createDirectory(packDir.resolve(dir));
                    }
                    Path classFile = packDir.resolve(String.format("%s.class", className));
                    Files.write(classFile, result);
                }
                logger.info("create dir {} and class {} finish", packageName, className);
            } catch (Exception ex) {
                logger.error("create class output error: {}", ex.getMessage());
                logger.info("please delete the class-obf-output dir");
                logger.info("可能是目标文件或目录已存在：请删除 class-obf-output 目录");
            }
        }
        try {
            Files.delete(Const.TEMP_PATH);
            logger.info("class obfuscate finish and delete temp file");
        } catch (Exception ignored) {
        }
    }
}

package me.n1ar4.clazz.obfuscator.api;

import me.n1ar4.clazz.obfuscator.config.BaseConfig;
import me.n1ar4.clazz.obfuscator.config.Manager;
import me.n1ar4.clazz.obfuscator.core.AnalyzeEnv;
import me.n1ar4.clazz.obfuscator.core.ObfEnv;
import me.n1ar4.clazz.obfuscator.core.Runner;
import me.n1ar4.clazz.obfuscator.utils.ColorUtil;
import me.n1ar4.clazz.obfuscator.utils.FileUtil;
import me.n1ar4.log.LogLevel;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class ClassObf {
    private static final Logger logger = LogManager.getLogger();
    private final BaseConfig config;

    public ClassObf(BaseConfig baseConfig) {
        LogManager.setLevel(LogLevel.INFO);
        this.config = baseConfig;
    }

    // 快速混淆
    // 使用默认配置 返回 base64 结果
    public static String quickRun(String path) {
        ClassObf instance = new ClassObf(BaseConfig.Default());
        Result result = instance.run(path);
        if (result.getMessage().equals(Result.SUCCESS)) {
            return Base64.getEncoder().encodeToString(result.getData());
        } else {
            return Result.ERROR;
        }
    }

    // 重载 - 支持输入路径字符串
    public Result run(String path) {
        return run(Paths.get(path));
    }

    // 重载 - 支持输入 byte[] 字节码
    public Result run(byte[] input) {
        try {
            Path inputPath = Files.createTempFile("class-obf", ".class");
            Files.write(inputPath, input);
            Result result = run(inputPath);
            if (result.getMessage().equals(Result.SUCCESS)) {
                Files.delete(inputPath);
                logger.info("delete temp file {} success", inputPath);
            }
            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return Result.Error(ex.getMessage());
        }
    }

    // 重载 - 支持输入 PATH
    public Result run(Path path) {
        // 是否要进行混淆
        // 如果 LOG LEVEL 和 OBF CHAR 都是空 显然没有成功拿到配置文件
        if (config.getObfuscateChars().length == 0 && config.getLogLevel() == null) {
            try {
                System.out.println(ColorUtil.red("[-] NO AVAILABLE CONFIG / 没有配置"));
                System.out.println(ColorUtil.red("[-] 不进行混淆 原样输出"));
                return Result.Success(Files.readAllBytes(path));
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                return Result.Error(ex.getMessage());
            }
        }
        try {
            clean();
            Manager.initConfig(this.config);
            Runner.run(path, this.config, true, null);
            String fileName = FileUtil.getFileNameWithoutExt(path);
            String newFile = fileName + "_obf.class";
            Path newFilePath = Paths.get(newFile);
            if (!Files.exists(newFilePath)) {
                return Result.Error("obfuscate data is null");
            }
            byte[] data = Files.readAllBytes(newFilePath);
            if (data.length > 0) {
                Files.delete(newFilePath);
                logger.info("delete temp file {} success", newFilePath);
            }
            return Result.Success(data);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return Result.Error(ex.getMessage());
        }
    }

    private void clean() {
        AnalyzeEnv.classFileList.clear();

        AnalyzeEnv.discoveredClasses.clear();
        AnalyzeEnv.discoveredMethods.clear();

        AnalyzeEnv.classMap.clear();
        AnalyzeEnv.methodMap.clear();

        AnalyzeEnv.methodCalls.clear();
        AnalyzeEnv.methodsInClassMap.clear();

        AnalyzeEnv.fieldsInClassMap.clear();

        ObfEnv.stringInClass.clear();
        ObfEnv.newStringInClass.clear();

        ObfEnv.methodNameObfMapping.clear();
        ObfEnv.fieldNameObfMapping.clear();

        ObfEnv.config = new BaseConfig();
        ObfEnv.ADVANCE_STRING_NAME = null;
    }
}

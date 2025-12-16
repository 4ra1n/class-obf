package me.n1ar4.clazz.obfuscator;

import com.beust.jcommander.JCommander;
import me.n1ar4.clazz.obfuscator.config.*;
import me.n1ar4.clazz.obfuscator.core.Runner;
import me.n1ar4.clazz.obfuscator.core.WorkflowRunner;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static final BaseCmd baseCmd = new BaseCmd();
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        Logo.printLogo();
        Parser parser = new Parser();
        JCommander commander = JCommander.newBuilder()
                .addObject(baseCmd)
                .build();
        try {
            commander.parse(args);
        } catch (Exception ignored) {
            commander.usage();
            return;
        }

        if (baseCmd.isVersion()) {
            return;
        }
        if (baseCmd.isGenerate()) {
            parser.generateConfig();
            logger.info("generate config.yaml file");
            return;
        }
        if (baseCmd.getConfig() == null || baseCmd.getConfig().isEmpty()) {
            baseCmd.setConfig("config.yaml");
        }
        boolean jarMode = baseCmd.getJarPath() != null && !baseCmd.getJarPath().isEmpty();
        if (!jarMode && (baseCmd.getPath() == null || baseCmd.getPath().isEmpty())) {
            logger.error("need -i/--input file or --jar");
            commander.usage();
            return;
        }
        BaseConfig config = parser.parse(Paths.get(baseCmd.getConfig()));
        if (config == null) {
            logger.warn("need config.yaml config");
            logger.info("generate config.yaml file");
            parser.generateConfig();
            return;
        }
        Path path = null;
        if (!jarMode) {
            String p = baseCmd.getPath();
            path = Paths.get(p);
            if (!Files.exists(path)) {
                logger.error("class file not exist");
                commander.usage();
                return;
            }
        }

        boolean success = Manager.initConfig(config);
        if (!success) {
            return;
        }

        if (!jarMode && baseCmd.getWorkflow() != null && !baseCmd.getWorkflow().isEmpty()) {
            WorkflowParser workflowParser = new WorkflowParser();
            WorkflowConfig workflowConfig = workflowParser.parse(Paths.get(baseCmd.getWorkflow()));
            if (workflowConfig == null) {
                logger.error("parse workflow config error");
                return;
            }
            // 允许根据 workflow 进行混淆
            logger.info("start workflow class obfuscate");
            WorkflowRunner.run(path, config, workflowConfig, false, baseCmd);
        } else if (!jarMode) {
            // 走普通流程
            logger.info("start class obfuscate");
            Runner.run(path, config, false, baseCmd);
        } else {
            if (baseCmd.getClassName() == null || baseCmd.getClassName().isEmpty()) {
                logger.error("need --class for jar mode");
                commander.usage();
                return;
            }
            Path jarPath = Paths.get(baseCmd.getJarPath());
            if (!Files.exists(jarPath)) {
                logger.error("jar file not exist");
                commander.usage();
                return;
            }
            logger.info("start jar class obfuscate");
            boolean ok = me.n1ar4.clazz.obfuscator.core.JarClassRunner.run(jarPath, baseCmd.getClassName(), config);
            if (!ok) {
                logger.error("jar class obfuscate failed");
            } else {
                logger.info("jar class obfuscate success");
            }
        }
    }
}

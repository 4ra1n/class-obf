package me.n1ar4.clazz.obfuscator.config;

import com.beust.jcommander.Parameter;

@SuppressWarnings("all")
public class BaseCmd {
    @Parameter(names = {"-i", "--input"}, description = "input class file path")
    private String path;
    @Parameter(names = {"-c", "--config"}, description = "config yaml file")
    private String config;
    @Parameter(names = {"-g", "--generate"}, description = "generate config file")
    private boolean generate;
    @Parameter(names = {"-v", "--version"}, description = "version")
    private boolean version;
    @Parameter(names = {"--std-output"}, description = "standard output (export file to package dir)")
    private boolean stdOutput;
    @Parameter(names = {"--workflow"}, description = "workflow yaml file")
    private String workflow;
    @Parameter(names = {"--jar"}, description = "input jar file path")
    private String jarPath;
    @Parameter(names = {"--class"}, description = "fully qualified class name in jar")
    private String className;

    public String getWorkflow() {
        return workflow;
    }

    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

    public boolean isVersion() {
        return version;
    }

    public void setVersion(boolean version) {
        this.version = version;
    }

    public boolean isGenerate() {
        return generate;
    }

    public void setGenerate(boolean generate) {
        this.generate = generate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public boolean isStdOutput() {
        return stdOutput;
    }

    public void setStdOutput(boolean stdOutput) {
        this.stdOutput = stdOutput;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}

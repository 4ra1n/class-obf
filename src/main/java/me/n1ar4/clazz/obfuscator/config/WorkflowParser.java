package me.n1ar4.clazz.obfuscator.config;

import me.n1ar4.clazz.obfuscator.Const;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.inspector.TagInspector;
import org.yaml.snakeyaml.representer.Representer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WorkflowParser {
    private static final Logger logger = LogManager.getLogger();
    private static final LoaderOptions lOptions = new LoaderOptions();
    private static final DumperOptions dOptions = new DumperOptions();
    private static final Yaml yaml;

    static {
        TagInspector taginspector =
                tag -> tag.getClassName().equals(WorkflowConfig.class.getName());
        lOptions.setTagInspector(taginspector);
        dOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dOptions.setPrettyFlow(true);
        yaml = new Yaml(
                new Constructor(WorkflowConfig.class, lOptions),
                new Representer(dOptions));
    }

    public WorkflowConfig parse(Path file) {
        if (!Files.exists(file)) {
            logger.error("workflow config file not exist");
            return null;
        }
        try {
            InputStream is = new ByteArrayInputStream(Files.readAllBytes(file));
            return yaml.load(is);
        } catch (Exception ex) {
            logger.error("parse workflow config error: {}", ex.toString());
            return null;
        }
    }

    public static void main(String[] args) {
        new WorkflowParser().generateConfigSTD();
    }

    public void generateConfigSTD() {
        WorkflowConfig config = new WorkflowConfig();
        List<String> list = new ArrayList<>();
        list.add("ParamObfTransformer");
        list.add("MethodNameTransformer");
        list.add("FieldNameTransformer");
        list.add("ParameterTransformer");
        list.add("XORTransformer");
        list.add("StringArrayTransformer");
        list.add("XORTransformer");
        list.add("StringEncryptTransformer");
        list.add("JunkCodeTransformer");
        list.add("BadAnnoTransformer");
        list.add("AntiPromptTransformer");
        list.add("ImageCrashTransformer");
        list.add("InvokeDynamicTransformer");
        list.add("ShuffleMemberTransformer");
        list.add("ControlFlowTransformer");
        list.add("TrimTransformer");
        list.add("DeleteInfoTransformer");
        config.setSteps(list);
        String data = yaml.dump(config);
        try {
            Files.write(Paths.get("workflow.yaml"), data.getBytes());
        } catch (Exception ex) {
            logger.error("write config file error: {}", ex.toString());
        }
    }
}

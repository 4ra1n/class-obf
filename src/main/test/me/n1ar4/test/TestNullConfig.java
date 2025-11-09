package me.n1ar4.test;

import me.n1ar4.clazz.obfuscator.api.ClassObf;
import me.n1ar4.clazz.obfuscator.api.Result;
import me.n1ar4.clazz.obfuscator.config.BaseConfig;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TestNullConfig {
    public static void main(String[] args) throws Exception {
        BaseConfig config = new BaseConfig();
        ClassObf classObf = new ClassObf(config);
        Result result = classObf.run("target/test-classes/me/n1ar4/test/TestAPI.class");
        if (result.getMessage().equals(Result.SUCCESS)) {
            Files.write(Paths.get("null.class"), result.getData());
        }
    }
}

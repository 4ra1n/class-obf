package me.n1ar4.clazz.obfuscator.config;

import me.n1ar4.clazz.obfuscator.utils.ColorUtil;

import java.util.Arrays;

public class BaseConfig {
    private String logLevel;

    private boolean quiet;

    private boolean asmAutoCompute;

    private boolean enableFieldName;
    private boolean enableMethodName;
    private boolean enableParamName;
    private boolean enableAdvanceString;
    private String advanceStringName;

    private boolean enableHideMethod;
    private boolean enableHideField;

    private boolean enableXOR;
    private boolean enableAES;
    private boolean enableDeleteCompileInfo;

    private boolean enableJunk;
    private int junkLevel;
    private boolean enableEvilString;
    private int maxJunkOneClass;

    private boolean ignorePublic;

    private String aesKey;
    private String aesDecName;
    private String aesKeyField;

    private String[] obfuscateChars;
    private String[] methodBlackList;

    private boolean enableExpandMethod;
    private int expandParamNum;
    private String[] expandMethodList;

    private boolean useEvilCharInstead;

    private boolean enableBadAnno;
    private String badAnnoTextFile;

    /**
     * 如果配置没问题可以启动就返回 true
     *
     * @return true/false
     */
    public boolean isValid() {
        if (logLevel == null || logLevel.trim().isEmpty()) {
            System.out.println(ColorUtil.red("[ERROR] log level is null"));
            return false;
        }
        if (advanceStringName == null || advanceStringName.trim().isEmpty()) {
            System.out.println(ColorUtil.red("[ERROR] advance string name is null"));
            return false;
        }
        if (aesKey == null || aesKey.trim().isEmpty()) {
            System.out.println(ColorUtil.red("[ERROR] aes key is null"));
            return false;
        }
        if (aesDecName == null || aesDecName.trim().isEmpty()) {
            System.out.println(ColorUtil.red("[ERROR] aes dec name is null"));
            return false;
        }
        if (aesKeyField == null || aesKeyField.trim().isEmpty()) {
            System.out.println(ColorUtil.red("[ERROR] aes key field is null"));
            return false;
        }
        if (obfuscateChars == null || obfuscateChars.length == 0) {
            System.out.println(ColorUtil.red("[ERROR] obfuscate chars is null"));
            return false;
        }
        // methodBlackList 允许是空
        if (junkLevel < 1 || junkLevel > 5) {
            System.out.println(ColorUtil.red("[ERROR] junk level must be between 1 and 5"));
            return false;
        }
        if (maxJunkOneClass < 1 || maxJunkOneClass > 10000) {
            System.out.println(ColorUtil.red("[ERROR] max junk must be between 1 and 10000"));
            return false;
        }
        return true;
    }

    public static BaseConfig Default() {
        BaseConfig config = new BaseConfig();
        config.setAsmAutoCompute(true);
        config.setLogLevel("info");
        // 默认不开隐藏
        config.setEnableHideField(false);
        config.setEnableHideMethod(false);
        // 默认开启所有混淆
        config.setEnableAdvanceString(true);
        config.setEnableFieldName(true);
        config.setEnableXOR(true);
        config.setEnableAES(true);
        config.setAesKey("OBF_DEFAULT_KEYS");
        config.setAesKeyField("iiiLLLi1i");
        config.setAesDecName("iiLLiLi");
        config.setEnableDeleteCompileInfo(true);
        config.setEnableParamName(true);
        config.setEnableMethodName(true);
        config.setIgnorePublic(false);
        // 默认花指令配置
        config.setEnableJunk(true);
        config.setJunkLevel(3);
        config.setMaxJunkOneClass(1000);
        // 默认额外配置
        config.setObfuscateChars(new String[]{"i", "l", "L", "1", "I"});
        config.setAdvanceStringName("iii");
        config.setMethodBlackList(new String[]{"main"});
        // 默认不开启多余功能
        config.setEnableExpandMethod(false);
        config.setUseEvilCharInstead(false);
        config.setEnableEvilString(false);
        return config;
    }

    @SuppressWarnings("all")
    public void show() {
        System.out.println(ColorUtil.purple("[GLOBAL] Log Level -> ") +
                ColorUtil.green(logLevel));
        System.out.println(ColorUtil.purple("[GLOBAL] ASM Auto Compute -> ") +
                ColorUtil.green(String.valueOf(asmAutoCompute)));
        System.out.println(ColorUtil.purple("[GLOBAL] Obfuscate Chars -> ") +
                ColorUtil.green(Arrays.toString(obfuscateChars)));
        System.out.println(ColorUtil.purple("[GLOBAL] Method Blacklist -> ") +
                ColorUtil.green(Arrays.toString(methodBlackList)));
        System.out.println(ColorUtil.yellow("Enable Delete Compile Info Obfuscate -> ") +
                ColorUtil.green(String.valueOf(enableDeleteCompileInfo)));
        System.out.println(ColorUtil.yellow("Enable Field Name Obfuscate -> ") +
                ColorUtil.green(String.valueOf(enableFieldName)));
        System.out.println(ColorUtil.yellow("Enable Method Name Obfuscate -> ") +
                ColorUtil.green(String.valueOf(enableMethodName)));
        System.out.println(ColorUtil.yellow("Ignore Public Method -> ") +
                ColorUtil.green(String.valueOf(ignorePublic)));
        System.out.println(ColorUtil.yellow("Enable Param Name Obfuscate -> ") +
                ColorUtil.green(String.valueOf(enableParamName)));
        System.out.println(ColorUtil.yellow("Enable Hide Method Obfuscate -> ") +
                ColorUtil.green(String.valueOf(enableHideMethod)));
        System.out.println(ColorUtil.yellow("Enable Hide Field Obfuscate -> ") +
                ColorUtil.green(String.valueOf(enableHideField)));
        System.out.println(ColorUtil.yellow("Enable XOR Obfuscate -> ") +
                ColorUtil.green(String.valueOf(enableXOR)));
        System.out.println(ColorUtil.yellow("Enable String AES Encrypt -> ") +
                ColorUtil.green(String.valueOf(enableAES)));
        System.out.println(ColorUtil.yellow("AES Decrypt KEY-> ") +
                ColorUtil.green(String.valueOf(aesKey)));
        System.out.println(ColorUtil.yellow("AES KEY Field-> ") +
                ColorUtil.green(String.valueOf(aesKeyField)));
        System.out.println(ColorUtil.yellow("AES Decrypt Method-> ") +
                ColorUtil.green(String.valueOf(aesDecName)));
        System.out.println(ColorUtil.yellow("Enable Advance String Obfuscate -> ") +
                ColorUtil.green(String.valueOf(enableAdvanceString)));
        System.out.println(ColorUtil.cyan("[Advance String] Global List Name -> ") +
                ColorUtil.green(String.valueOf(advanceStringName)));
        System.out.println(ColorUtil.yellow("Enable Junk Obfuscate -> ") +
                ColorUtil.green(String.valueOf(enableJunk)));
        System.out.println(ColorUtil.cyan("[Junk Obfuscate] Junk Obfuscate Level -> ") +
                ColorUtil.green(String.valueOf(junkLevel)));
        System.out.println(ColorUtil.cyan("[Junk Obfuscate] Max Number in One Class -> ") +
                ColorUtil.green(String.valueOf(maxJunkOneClass)));
        System.out.println(ColorUtil.cyan("[Junk Obfuscate] Enable Evil String -> ") +
                ColorUtil.green(String.valueOf(enableEvilString)));
        System.out.println(ColorUtil.yellow("Enable Expand Method -> ") +
                ColorUtil.green(String.valueOf(enableExpandMethod)));
        System.out.println(ColorUtil.cyan("[Expand Method] Expand Param Num -> ") +
                ColorUtil.green(String.valueOf(expandParamNum)));
        System.out.println(ColorUtil.cyan("[Expand Method] Expand Method List -> ") +
                ColorUtil.green(Arrays.toString(expandMethodList)));
        System.out.println(ColorUtil.yellow("Use Evil Char Instead -> ") +
                ColorUtil.green(String.valueOf(useEvilCharInstead)));
        System.out.println(ColorUtil.yellow("Enable Bad Annotation -> ") +
                ColorUtil.green(String.valueOf(enableBadAnno)));
        System.out.println(ColorUtil.cyan("[Bad Annotation] Bad Annotation Text File -> ") +
                ColorUtil.green(String.valueOf(badAnnoTextFile)));
    }

    public boolean isQuiet() {
        return quiet;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public boolean isIgnorePublic() {
        return ignorePublic;
    }

    public void setIgnorePublic(boolean ignorePublic) {
        this.ignorePublic = ignorePublic;
    }

    public String getAesKeyField() {
        return aesKeyField;
    }

    public void setAesKeyField(String aesKeyField) {
        this.aesKeyField = aesKeyField;
    }

    public boolean isAsmAutoCompute() {
        return asmAutoCompute;
    }

    public void setAsmAutoCompute(boolean asmAutoCompute) {
        this.asmAutoCompute = asmAutoCompute;
    }

    public String getAesDecName() {
        return aesDecName;
    }

    public void setAesDecName(String aesDecName) {
        this.aesDecName = aesDecName;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public boolean isEnableAES() {
        return enableAES;
    }

    public void setEnableAES(boolean enableAES) {
        this.enableAES = enableAES;
    }

    public boolean isEnableHideField() {
        return enableHideField;
    }

    public void setEnableHideField(boolean enableHideField) {
        this.enableHideField = enableHideField;
    }

    public boolean isEnableHideMethod() {
        return enableHideMethod;
    }

    public void setEnableHideMethod(boolean enableHideMethod) {
        this.enableHideMethod = enableHideMethod;
    }

    public String[] getMethodBlackList() {
        return methodBlackList != null ? methodBlackList : new String[0];
    }

    public void setMethodBlackList(String[] methodBlackList) {
        this.methodBlackList = methodBlackList;
    }

    public String getAdvanceStringName() {
        return advanceStringName;
    }

    public void setAdvanceStringName(String advanceStringName) {
        this.advanceStringName = advanceStringName;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public boolean isEnableFieldName() {
        return enableFieldName;
    }

    public void setEnableFieldName(boolean enableFieldName) {
        this.enableFieldName = enableFieldName;
    }

    public boolean isEnableMethodName() {
        return enableMethodName;
    }

    public void setEnableMethodName(boolean enableMethodName) {
        this.enableMethodName = enableMethodName;
    }

    public boolean isEnableParamName() {
        return enableParamName;
    }

    public void setEnableParamName(boolean enableParamName) {
        this.enableParamName = enableParamName;
    }

    public boolean isEnableAdvanceString() {
        return enableAdvanceString;
    }

    public void setEnableAdvanceString(boolean enableAdvanceString) {
        this.enableAdvanceString = enableAdvanceString;
    }

    public boolean isEnableXOR() {
        return enableXOR;
    }

    public void setEnableXOR(boolean enableXOR) {
        this.enableXOR = enableXOR;
    }

    public boolean isEnableJunk() {
        return enableJunk;
    }

    public void setEnableJunk(boolean enableJunk) {
        this.enableJunk = enableJunk;
    }

    public boolean isEnableDeleteCompileInfo() {
        return enableDeleteCompileInfo;
    }

    public void setEnableDeleteCompileInfo(boolean enableDeleteCompileInfo) {
        this.enableDeleteCompileInfo = enableDeleteCompileInfo;
    }

    public int getJunkLevel() {
        return junkLevel;
    }

    public void setJunkLevel(int junkLevel) {
        this.junkLevel = junkLevel;
    }

    public int getMaxJunkOneClass() {
        return maxJunkOneClass;
    }

    public void setMaxJunkOneClass(int maxJunkOneClass) {
        this.maxJunkOneClass = maxJunkOneClass;
    }

    public String[] getObfuscateChars() {
        return obfuscateChars != null ? obfuscateChars : new String[0];
    }

    public void setObfuscateChars(String[] obfuscateChars) {
        this.obfuscateChars = obfuscateChars;
    }


    public int getExpandParamNum() {
        return expandParamNum;
    }

    public void setExpandParamNum(int expandParamNum) {
        this.expandParamNum = expandParamNum;
    }

    public boolean isEnableExpandMethod() {
        return enableExpandMethod;
    }

    public void setEnableExpandMethod(boolean enableExpandMethod) {
        this.enableExpandMethod = enableExpandMethod;
    }

    public String[] getExpandMethodList() {
        return expandMethodList;
    }

    public void setExpandMethodList(String[] expandMethodList) {
        this.expandMethodList = expandMethodList;
    }

    public boolean isEnableEvilString() {
        return enableEvilString;
    }

    public void setEnableEvilString(boolean enableEvilString) {
        this.enableEvilString = enableEvilString;
    }

    public boolean isUseEvilCharInstead() {
        return useEvilCharInstead;
    }

    public void setUseEvilCharInstead(boolean useEvilCharInstead) {
        this.useEvilCharInstead = useEvilCharInstead;
    }

    public boolean isEnableBadAnno() {
        return enableBadAnno;
    }

    public void setEnableBadAnno(boolean enableBadAnno) {
        this.enableBadAnno = enableBadAnno;
    }

    public String getBadAnnoTextFile() {
        return badAnnoTextFile;
    }

    public void setBadAnnoTextFile(String badAnnoTextFile) {
        this.badAnnoTextFile = badAnnoTextFile;
    }
}

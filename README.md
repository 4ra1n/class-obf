# class-obf

[CHANGE LOG](CHANGELOG.MD)

<img alt="gitleaks badge" src="https://img.shields.io/badge/protected%20by-gitleaks-blue">

![](https://img.shields.io/github/downloads/4ra1n/class-obf/total)
![](https://img.shields.io/github/v/release/4ra1n/class-obf)

![](https://github.com/4ra1n/class-obf/workflows/maven%20check/badge.svg)
![](https://github.com/4ra1n/class-obf/workflows/leak%20check/badge.svg)
![](https://github.com/4ra1n/class-obf/workflows/truffle%20check/badge.svg)

`class-obf` 项目全称 `Class Obfuscator` 项目，专门用来混淆单个 `Class` 文件

示例图：混淆前 -> 混淆后

![](img/000.png)

可以自定义中文混淆字符，以及方法隐藏等技巧

![](img/004.png)

从 `1.2.0` 版本开始支持集中提取字符串后 `AES` 加密运行时解密

![](img/007.png)

本项目已深度集成到 `web-chains` 项目中 (https://github.com/Java-Chains/web-chains)

![](img/006.png)

## 背景

工具介绍

- 仅针对单个 `Class` 文件
- 命令行输出改善，详细展示混淆细节
- 你可以只混淆你项目的核心类替换即可（方便快速）
- 提供了多种方式的 `API` 调用

如果你有 `Jar` 混淆的需求：

- 可以把核心业务提取到一个 `class` 中通过该工具保护好这一个 `class`
- 尝试使用 [jar-obfuscator](https://github.com/jar-analyzer/jar-obfuscator) 工具（该工具 `v2` 版本正在开发中）

## 快速开始

生成配置文件：`java -jar class-obf.jar --generate`

使用指定配置文件混淆当前目录的 `Test.class`

```shell
java -jar class-obf.jar --config config.yaml --input Test.class
```

一个普通的类

```java
public class Test {
    private String a = "cal";
    private String b = "c.exe";
    private int c = 1;

    public static void eval() throws Exception {
        Test test = new Test();
        Runtime rt = Runtime.getRuntime();
        rt.exec(test.a + test.b);
        System.out.println(test.c);
    }

    public static void main(String[] args) throws Exception {
        eval();
    }
}
```

你可以随意搭配配置文件，得到以下几种混淆结果

效果一（默认配置）

![](img/001.png)

效果二（使用最高级别的花指令参数）

![](img/002.png)

自定义混淆字符（例如使用中文）

![](img/003.png)

开启隐藏方法和字段功能（反编译看不到方法）

![](img/004.png)

但是！可以成功执行

![](img/005.png)

使用 `--std-output` 参数将会导出标准的 `class` 文件以及对应的包名

例如 `me.n1ar4.Test` 将会导出 `class-obf-output/me/n1ar4/Test.class` 文件

## 问题

从 `1.5.0` 版本解决了缺少依赖的问题，如果你混淆时遇到报错找不到某些依赖类：

请将依赖的 `jar` 文件都放在当前目录下的 `class-obf-lib` 目录（会自动生成）

## API

你可以使用代码方式调用（参考 `test` 目录的 `TestQuick/TestAPI` 文件）

该项目在 `1.6.0` 版本上传了 `Maven` 中央仓库

```xml
<dependency>
    <groupId>io.github.4ra1n</groupId>
    <artifactId>class-obf</artifactId>
    <version>1.6.1</version>
</dependency>
```

最快速使用（使用默认配置输入文件返回 `base64` 字节码）

```java
String data = ClassObf.quickRun("Test.class");
System.out.println(data);
```

自行进行配置进阶写法

```java
BaseConfig config = new BaseConfig();
// 省略代码 自行设置 config 文件

ClassObf classObf = new ClassObf(config);
// 支持三种重载：输入文件字符串，输入文件 PATH 对象，输入 byte[] 数据
Result result = classObf.run("Test.class");
if(result.getMessage().equals(Result.SUCCESS)){
    // result.getData() 即可得到混淆后的 byte[] 字节码
    System.out.println(Base64.getEncoder().encodeToString(result.getData()));
}
```

如果你不想输出一大堆调试信息，可以这样设置

```java
config.setQuiet(true);
```

**注意：由于设计原因，混淆 `API` 不支持并发**

## 配置文件

可以根据你的需求修改配置文件

```yaml
!!me.n1ar4.clazz.obfuscator.config.BaseConfig
# 日志级别
logLevel: info
# 是否使用安静模式（不打印调试信息）
quiet: false
# 是否启动 JAVA ASM 的 COMPUTE FRAMES/MAX 自动计算
# 如果遇到 TYPE * NOT PRESENT 报错可以尝试设置该选项为 FALSE
asmAutoCompute: true

# 混淆字符组合
# 建议使用长度大于等于 5 否则可能有预期外的 BUG
obfuscateChars:
  - "i"
  - "l"
  - "L"
  - "1"
  - "I"

# 是否开启删除编译信息
enableDeleteCompileInfo: true

# 是否开启方法名混淆
# 这里会自动修改方法之间的引用
enableMethodName: true
# 一般 public 方法是被外部调用的
# 可以设置该选项为 true 来跳过 public 方法混淆
ignorePublic: false
# 全局方法黑名单
# 该方法不会进行混淆 引用也不会被修改
methodBlackList:
  - "main"

# 是否开启字段混淆
enableFieldName: true
# 是否开启方法参数名混淆
# 由于反编译器 可能显示的结果只是 var0 var1 等
enableParamName: true
# 是否对数字进行异或混淆
enableXOR: true

# 对所有字符串进行 AES 加密
enableAES: true
# 默认 AES KEY 注意长度必须是 16
aesKey: OBF_DEFAULT_KEYS
# AES 解密方法名
aesDecName: iiLLiLi
# AES KEY 字段名
aesKeyField: iiiLLLi1i

# 是否启用全局字符串提取混淆
enableAdvanceString: true
# 全局提取字符串的变量名可以自定义
advanceStringName: ME_N1AR4_CLAZZ_OBF_PROJECT

# 是否开启花指令混淆
enableJunk: true
# 花指令混淆级别 1-5
junkLevel: 3
# 一个类中花指令最多数量
maxJunkOneClass: 1000

# 是否开启字段隐藏
# 可以防止大部分 IDEA 版本反编译
enableHideField: false
# 是否开启方法隐藏
# 可以防止大部分 IDEA 版本反编译
enableHideMethod: false

# 是否将 JVM INVOKE 指令改成反射调用
# 注意：该功能会明显影响执行效率
# 优点：经过该混淆后会更加难以分析
# 缺点：该功能未经过完善测试不稳定
enableReflect: false
# INVOKEVIRTUAL 转换
enableReflectVirtual: false
# INVOKESTATIC 转换
enableReflectStatic: false
# INVOKESPECIAL 转换
enableReflectSpecial: false
# INVOKEINTERFACE 转换
enableReflectInterface: false

```

## test

如何测试你混淆后的单个 `class` 可用？

- 结合具体场景和项目测试，取决于实际情况
- 覆盖到 `jar` 文件中测试，比较麻烦
- 放到对应目录中使用 `java` 命令测试，更麻烦
- 使用自定义 `ClassLoader` 测试，方便快速

```java
public class Test extends ClassLoader {
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = getClassData(name);
        if (classData == null) {
            throw new ClassNotFoundException(name);
        }
        return defineClass(name, classData, 0, classData.length);
    }

    private byte[] getClassData(String className) {
        if ("test.ClassName".equals(className)) {
            try {
                // read bytes form obfuscated class
                return Files.readAllBytes(Paths.get("Test_obf.class"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        TestRunner loader = new TestRunner();
        Class<?> clazz = loader.loadClass("test.ClassName");
        Object instance = clazz.getDeclaredConstructor().newInstance();
        // usually main method
        Method method = clazz.getMethod("main", String[].class);
        method.invoke(instance, new Object[]{args});
    }
}
```

## class-obf 开发进度

| 功能                    | 开发进度 | 
|-----------------------|------|
| 方法名及引用混淆              | ✅    | 
| 字段名及引用混淆              | ✅    |
| 参数名及引用混淆              | ✅    |
| 字符串统一提取混淆             | ✅    | 
| 整型常量异或混淆              | ✅    | 
| 多级别可精细配置的垃圾指令混淆       | ✅    | 
| 已有方法名和字段名的隐藏混淆        | ✅    | 
| INVOKE 指令修改反射调用(beta) | ✅    | 
| 字符串混淆运行时 AES 解密       | ✅    | 
| 字符串混淆运行时自研算法解密        | ❌    | 
| 支持方法字段名加入换行           | ❌    | 
| 构造方法静态代码支持隐藏          | ❌    | 
| 字段名混淆黑名单的配置           | ❌    | 
| 字符串混淆加入不可见字符和特殊字符     | ❌    | 
| 方法支持拓展无意义参数并修改引用      | ❌    | 
| 方法支持精细配置拓展无意义参数个数     | ❌    | 
| 方法支持精细配置拓展无意义参数方法白名单  | ❌    | 
| 方法拓展后支持拓展参数无意义计算和调用   | ❌    | 
| 支持首次混淆生成的解密方法进一步混淆    | ❌    | 
| 字符串动态解密 KEY 支持隐藏      | ❌    | 
| 字符串动态解密方法支持隐藏         | ❌    |      
| 支持生成超大字符串分解代码         | ❌    |

## Star

<div align="center">

<img src="https://api.star-history.com/svg?repos=4ra1n/class-obf&type=Date" width="600" height="400" alt="Star History Chart" valign="middle">

</div>

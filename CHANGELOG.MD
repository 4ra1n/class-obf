# CHANGELOG

## 1.8.1

更新日志：

- todo

感谢以下用户的贡献:

- 4ra1n (https://github.com/4ra1n)

你可以从 `Maven` 中央仓库获得

```xml
<dependency>
    <groupId>io.github.4ra1n</groupId>
    <artifactId>class-obf</artifactId>
    <version>1.8.1</version>
</dependency>
```

可供下载的文件都由 `Github Actions` 构建，使用 `java -jar class-obf.jar` 启动

## 1.8.0

更新日志：

- [功能] 支持 `enableBadAnno` 错误注解混淆方式 @4ra1n
- [功能] 支持使用 `badAnnoTextFile` 指定错误注解文本内容 @4ra1n
- [功能] 方法字段参数名支持 `useEvilCharInstead` 特殊字符 @4ra1n
- [功能] 花指令添加字符串支持 `enableEvilString` 特殊字符 @4ra1n
- [BUG] 加密解密字符串编码问题导致中文乱码 @4ra1n
- [BUG] 某些情况当前目录会生成多余的临时文件 @4ra1n
- [优化] 优化丢失的命令行配置提示 @4ra1n

感谢以下用户的贡献:

- 4ra1n (https://github.com/4ra1n)

你可以从 `Maven` 中央仓库获得

```xml
<dependency>
    <groupId>io.github.4ra1n</groupId>
    <artifactId>class-obf</artifactId>
    <version>1.8.0</version>
</dependency>
```

可供下载的文件都由 `Github Actions` 构建，使用 `java -jar class-obf.jar` 启动

## 1.7.0

更新日志：

- [功能] 允许拓展方法无意义参数个数并修改引用 @4ra1n
- [优化] 删除编译信息应该位于最后一步 @4ra1n
- [优化] 删除不稳定不可用的反射混淆法 @4ra1n

感谢以下用户的贡献:

- 4ra1n (https://github.com/4ra1n)

你可以从 `Maven` 中央仓库获得

```xml
<dependency>
    <groupId>io.github.4ra1n</groupId>
    <artifactId>class-obf</artifactId>
    <version>1.7.0</version>
</dependency>
```

可供下载的文件都由 `Github Actions` 构建，使用 `java -jar class-obf.jar` 启动

## 1.6.1

```xml
<dependency>
    <groupId>io.github.4ra1n</groupId>
    <artifactId>class-obf</artifactId>
    <version>1.6.1</version>
</dependency>
```

## 1.6.0

上传 `Maven` 中央仓库，可以直接导入使用

https://central.sonatype.com/artifact/io.github.4ra1n/class-obf

```xml
<dependency>
    <groupId>io.github.4ra1n</groupId>
    <artifactId>class-obf</artifactId>
    <version>1.6.0</version>
</dependency>
```

更新日志：

- [重要] 上传 `Maven` 中央仓库

感谢以下用户的贡献:

- 4ra1n (https://github.com/4ra1n)

## 1.5.0

从该版本开始，运行混淆会自动创建一个 `class-obf-lib` 目录

用户如果遇到依赖报错，可以将混淆 `class` 需要的依赖 `jar` 放入该目录解决报错问题

更新日志：

- [重要] 解决某些情况下需要正确依赖才能运行的 `BUG`
- [优化] 生成 `AES` 字符串解密方法时避免抛出异常
- [优化] 提高解密方法内的 `Basse64` 解码兼容性
- [其他] 升级依赖

感谢以下用户的贡献:

- 4ra1n (https://github.com/4ra1n)
- lz520520 (https://github.com/lz520520)

可供下载的文件都由 `Github Actions` 构建，使用 `java -jar class-obf.jar` 启动

## 1.4.0

支持将 `INVOKE*` 指令转为反射调用，结合其他配置可完成进阶混淆

```yaml
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

更新日志：

- [重要] 支持方法调用 `INVOKE` 指令改反射调用混淆

感谢以下用户的贡献:

- 4ra1n (https://github.com/4ra1n)

可供下载的文件都由 `Github Actions` 构建，使用 `java -jar class-obf.jar` 启动

## 1.3.2

新增参数 `--std-output` 导出到标准路径方便后续处理

例如 `me.n1ar4.Test` 将会导出 `class-obf-output/me/n1ar4/Test.class` 文件

更新日志：

- [功能] 支持 `--std-output` 参数输出到包名对应的完整路径
- [其他] 代码优化并在 `actions` 中使用最新 `maven` 和插件

感谢以下用户的贡献:

- 4ra1n (https://github.com/4ra1n)

可供下载的文件都由 `Github Actions` 构建，使用 `java -jar class-obf.jar` 启动

## 1.3.1

更新日志：

- [功能] 支持新配置参数忽略 `public` 方法的混淆 @4ra1n
- [功能] 新增 `quiet` 配置参数不打印调试信息 @4ra1n
- [优化] 加密和解密使用反转的 `AES KEY` 和数据 @4ra1n 

感谢以下用户的贡献:

- 4ra1n (https://github.com/4ra1n)
- lz520520 (https://github.com/lz520520)

可供下载的文件都由 `Github Actions` 构建，使用 `java -jar class-obf.jar` 启动

## 1.3.0

更新日志：

- [BUG] 修复之前版本参数混淆功能实际没有生效
- [优化] 优化 `AES KEY` 提到全局变量并支持配置
- [优化] 允许混淆 `main` 方法但默认在方法黑名单中
- [优化] 配置文件支持关闭某些 `ASM` 选项提高兼容性
- [优化] 启动前检查各种配置参数是否合法有错误退出

感谢以下用户的贡献:

- 4ra1n (https://github.com/4ra1n)

可供下载的文件都由 `Github Actions` 构建，使用 `java -jar class-obf.jar` 启动

## 1.2.0

更新日志：

- [重要] 支持字符串 `AES` 加密运行时解密 @4ra1n
- [BUG] 避免 `advanceStringName` 和混淆标识符冲突 @4ra1n
- [优化] 优化 `config.yaml` 模板排序和注释内容 @4ra1n

感谢以下用户的贡献:

- 4ra1n (https://github.com/4ra1n)

可供下载的文件都由 `Github Actions` 构建，使用 `java -jar class-obf.jar` 启动

## 1.1.1

更新日志：

- [BUG] 修复 `API` 调用多次报错的问题

感谢以下用户的贡献:

- 4ra1n (https://github.com/4ra1n)

可供下载的文件都由 `Github Actions` 构建，使用 `java -jar class-obf.jar` 启动

## 1.1.0

更新日志：

- [功能] 上传到 `jitpack` 供其他项目引入 @4ra1n
- [功能] 提供 `API` 通过代码方式进行混淆 @4ra1n
- [优化] 更新依赖并缩小体积 @4ra1n
- [其他] 使用 `actions` 构建 @4ra1n

感谢以下用户的贡献:

- 4ra1n (https://github.com/4ra1n)

可供下载的文件都由 `Github Actions` 构建，使用 `java -jar class-obf.jar` 启动

## 1.0.0

初始版本

使用 `java -jar class-obf.jar` 启动

感谢以下用户的贡献:

- 4ra1n (https://github.com/4ra1n)
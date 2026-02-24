package com.autotestgen;

import com.autotestgen.core.TestFileGenerator;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 自动单元测试生成器入口 API。
 * 允许你通过纯代码方式调用生成流程，无需强制使用命令行。
 */
public class AutoTestGen {

    /**
     * 根据指定的 Java 源文件路径，自动为其生成对应的 JUnit 测试文件，并输出到目标目录。
     *
     * @param sourceFile 源文件地址 (例："src/main/java/com/example/OrderService.java")
     * @param outputDir  输出基础地址 (例："src/test/java")
     * @throws Exception 解析或网络请求出错时抛出异常
     */
    public static void generate(String sourceFile, String outputDir) throws Exception {
        if (!Files.exists(Paths.get(sourceFile))) {
            throw new IllegalArgumentException("错误: 源文件不存在: " + sourceFile);
        }

        System.out.println("====== AutoTestGen 启动 ======");
        TestFileGenerator generator = new TestFileGenerator();
        generator.generateTestFile(sourceFile, outputDir);
    }
}

package com.autotestgen;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * 演示如何通过代码编程式调用 AutoTestGen 引擎。
 */
public class AutoTestGenApiTest {

    @Test
    @Disabled("防止在 CI 或本地打包时自动执行去请求实际的大语言模型消耗 Token")
    public void testGenerateViaApi() throws Exception {
        // 设置源文件和输出目录
        String sourceFile = "src/main/java/com/autotestgen/sample/OrderService.java";
        String outDir = "src/test/java";

        // 编程式调用
        AutoTestGen.generate(sourceFile, outDir);

        System.out.println("生成完毕，请检查 src/test/java 目录下对应的测试类！");
    }
}

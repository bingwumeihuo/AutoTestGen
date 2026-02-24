package com.autotestgen;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("用法: java com.autotestgen.Main <source-file.java> <output-dir>");
            System.exit(1);
        }

        String sourceFile = args[0];
        String outputDir = args[1];

        if (!Files.exists(Paths.get(sourceFile))) {
            System.err.println("错误: 源文件不存在: " + sourceFile);
            System.exit(1);
        }

        try {
            AutoTestGen.generate(sourceFile, outputDir);
        } catch (Exception e) {
            System.err.println("生成测试文件失败: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

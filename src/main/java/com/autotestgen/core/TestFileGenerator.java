package com.autotestgen.core;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestFileGenerator {
    private final LLMClient llmClient;

    public TestFileGenerator() {
        this.llmClient = new LLMClient();
    }

    public void generateTestFile(String sourceFilePath, String baseOutDir) throws Exception {
        System.out.println("正在处理文件: " + sourceFilePath);
        File file = new File(sourceFilePath);
        CompilationUnit cu = StaticJavaParser.parse(file);

        // 确保存在主要的类型定义
        if (cu.getTypes().isEmpty()) {
            System.err.println("在目标文件中未找到任何类型定义: " + sourceFilePath);
            return;
        }

        ClassOrInterfaceDeclaration classDecl = cu.getTypes().get(0).asClassOrInterfaceDeclaration();
        String className = classDecl.getNameAsString();
        String packageName = cu.getPackageDeclaration().map(pd -> pd.getNameAsString()).orElse("");

        ClassInfoVisitor visitor = new ClassInfoVisitor();
        visitor.visit(classDecl, null);

        System.out.println("扫描到 " + visitor.getDependencies().size() + " 个可 Mock 依赖。");

        // 寻找需要测试的公开方法并收集源码
        java.util.List<String> methodSources = new java.util.ArrayList<>();

        for (MethodDeclaration md : classDecl.getMethods()) {
            if (md.isPublic() && !md.isConstructorDeclaration()) {
                methodSources.add(md.toString());
            }
        }

        if (methodSources.isEmpty()) {
            System.out.println("未找到任何可测试的 public 方法。");
            return;
        }

        System.out.println("正在为 " + methodSources.size() + " 个方法生成测试代码...");
        String prompt = PromptBuilder.buildPrompt(className, visitor.getDependencies(), methodSources);

        String generatedTest = llmClient.generateCode(prompt);
        saveTestFile(packageName, className, generatedTest, baseOutDir);
    }

    private void saveTestFile(String packageName, String className, String generatedContent, String baseOutDir)
            throws Exception {
        // 清理由于 GPT 提示词未完美遵循可能附带的 Markdown 代码块语法
        if (generatedContent.startsWith("```java")) {
            generatedContent = generatedContent.replaceFirst("```java\\s*", "");
        }
        if (generatedContent.endsWith("```")) {
            generatedContent = generatedContent.substring(0, generatedContent.lastIndexOf("```"));
        }

        String testClassName = className + "Test";
        Path outDir = Paths.get(baseOutDir, packageName.replace('.', '/'));
        Files.createDirectories(outDir);

        Path outFile = outDir.resolve(testClassName + ".java");

        // 基础包名处理：假设大模型生成了完整的内部类代码
        // 如果缺失则补充包声明
        if (!generatedContent.contains("package " + packageName + ";")) {
            generatedContent = "package " + packageName + ";\n\n" + generatedContent;
        }

        Files.writeString(outFile, generatedContent);
        System.out.println("✅ 生成完毕，测试文件已保存至: " + outFile.toAbsolutePath());
    }
}

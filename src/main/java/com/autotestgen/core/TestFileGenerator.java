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
        System.out.println("Processing file: " + sourceFilePath);
        File file = new File(sourceFilePath);
        CompilationUnit cu = StaticJavaParser.parse(file);

        // Ensure there is a primary type definition
        if (cu.getTypes().isEmpty()) {
            System.err.println("No types found in " + sourceFilePath);
            return;
        }

        ClassOrInterfaceDeclaration classDecl = cu.getTypes().get(0).asClassOrInterfaceDeclaration();
        String className = classDecl.getNameAsString();
        String packageName = cu.getPackageDeclaration().map(pd -> pd.getNameAsString()).orElse("");

        ClassInfoVisitor visitor = new ClassInfoVisitor();
        visitor.visit(classDecl, null);

        System.out.println("Discovered " + visitor.getDependencies().size() + " dependencies.");

        // Find public methods to test. Here we gather all their source codes and
        // concatenate
        // Alternatively we can do it per method, but let's test all public methods
        // together or individually
        StringBuilder testsCode = new StringBuilder();

        for (MethodDeclaration md : classDecl.getMethods()) {
            if (md.isPublic() && !md.isConstructorDeclaration()) {
                System.out.println("Generating test for method: " + md.getNameAsString());
                String prompt = PromptBuilder.buildPrompt(className, visitor.getDependencies(), md.toString());

                String generatedTest = llmClient.generateCode(prompt);
                testsCode.append(generatedTest).append("\n\n");
            }
        }

        if (testsCode.length() > 0) {
            saveTestFile(packageName, className, testsCode.toString(), baseOutDir);
        } else {
            System.out.println("No public methods found to test.");
        }
    }

    private void saveTestFile(String packageName, String className, String generatedContent, String baseOutDir)
            throws Exception {
        // Clean up markdown syntax if GPT didn't follow instructions perfectly
        if (generatedContent.contains("```java")) {
            generatedContent = generatedContent.replaceAll("```java\\s*", "");
            generatedContent = generatedContent.replaceAll("```\\s*", "");
        }
        // This generatedContent likely contains multiple class declarations if each
        // method returned a full class
        // Typically we would ask LLM to only return the methods if we are aggregating,
        // OR ask LLM for the entire class at once.
        // Let's modify the PromptBuilder or just write it directly.
        // Assuming we are writing it to a file directly. Note: this might create
        // multiple class definition strings if not careful.

        String testClassName = className + "Test";
        Path outDir = Paths.get(baseOutDir, packageName.replace('.', '/'));
        Files.createDirectories(outDir);

        Path outFile = outDir.resolve(testClassName + ".java");

        // Basic packaging: assuming GPT generated everything inside a class
        // Add package declaration if missing
        if (!generatedContent.contains("package " + packageName + ";")) {
            generatedContent = "package " + packageName + ";\n\n" + generatedContent;
        }

        Files.writeString(outFile, generatedContent);
        System.out.println("✅ Generated test file saved to: " + outFile.toAbsolutePath());
    }
}

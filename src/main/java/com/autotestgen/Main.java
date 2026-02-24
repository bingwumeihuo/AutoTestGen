package com.autotestgen;

import com.autotestgen.core.TestFileGenerator;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java com.autotestgen.Main <source-file.java> <output-dir>");
            System.exit(1);
        }

        String sourceFile = args[0];
        String outputDir = args[1];

        if (!Files.exists(Paths.get(sourceFile))) {
            System.err.println("Error: Source file does not exist: " + sourceFile);
            System.exit(1);
        }

        TestFileGenerator generator = new TestFileGenerator();
        try {
            generator.generateTestFile(sourceFile, outputDir);
        } catch (Exception e) {
            System.err.println("Failed to generate test file: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

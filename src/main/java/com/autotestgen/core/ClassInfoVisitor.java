package com.autotestgen.core;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

public class ClassInfoVisitor extends VoidVisitorAdapter<Void> {
    private List<String> dependencies = new ArrayList<>();
    private List<String> methods = new ArrayList<>();

    @Override
    public void visit(FieldDeclaration fd, Void arg) {
        // Extract all dependencies to mock
        fd.getVariables().forEach(var -> {
            dependencies.add(var.getTypeAsString() + " " + var.getNameAsString());
        });
        super.visit(fd, arg);
    }

    @Override
    public void visit(MethodDeclaration md, Void arg) {
        // Extract methods
        if (md.isPublic() && !md.isConstructorDeclaration()) {
            methods.add(md.getNameAsString());
        }
        super.visit(md, arg);
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public List<String> getMethods() {
        return methods;
    }
}

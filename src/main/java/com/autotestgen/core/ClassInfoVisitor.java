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
        // 提取所有需要 Mock 的依赖字段
        fd.getVariables().forEach(var -> {
            dependencies.add(var.getTypeAsString() + " " + var.getNameAsString());
        });
        super.visit(fd, arg);
    }

    @Override
    public void visit(MethodDeclaration md, Void arg) {
        // 提取公开的方法信息，排除构造函数
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

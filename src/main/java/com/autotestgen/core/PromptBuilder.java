package com.autotestgen.core;

import java.util.List;

public class PromptBuilder {

    public static String buildPrompt(String className, List<String> deps, List<String> targetMethodSources) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一个 Java 测试专家。请为 ").append(className).append(" 编写一组单元测试，所有的测试放在同一个 ")
                .append(className).append("Test 类中，不要生成多个类。\n");
        sb.append("【技术栈】：JUnit 5, Mockito\n");

        sb.append("【依赖组件（需要 Mock）】：\n");
        if (deps.isEmpty()) {
            sb.append("- 无依赖\n");
        } else {
            deps.forEach(d -> sb.append("- ").append(d).append("\n"));
        }

        sb.append("【待测方法源码】：\n");
        for (String mdSource : targetMethodSources) {
            sb.append(mdSource).append("\n\n");
        }

        sb.append("【要求】：\n");
        sb.append("1. 覆盖所有 if-else 分支。\n");
        sb.append("2. 使用 Assertions.assertEquals 断言。\n");
        sb.append("3. 使用 @ExtendWith(MockitoExtension.class)。\n");
        sb.append("4. 如果类名包含 Service 等，请使用 @InjectMocks 注入。\n");
        sb.append("5. 提供完整的可编译的 Java 测试类代码。\n");
        sb.append("6. 只返回 Java 代码，不要有任何 markdown 格式化标记 (例如 ```java)，不要有额外解释。");
        return sb.toString();
    }
}

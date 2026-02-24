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
        sb.append("5. 【防幻觉极其重要】：由于你没有外部请求参数（例如目标方法中的 DTO 或实体类，如 User, Item, OrderResult 等）的定义文件，请严格遵守以下防幻觉规则：\n");
        sb.append(
                "   - 不要随意瞎猜外部类的 Getter/Setter 方法名（例如不要凭直觉猜测是 getMessage() 还是 getMsg()）。务必【严格观察待测源码】中出现过的方法，源码中没出现的你尽量不要写。\n");
        sb.append(
                "   - 对于 Mockito 的参数匹配，在不确定某个 ID 或字段到底是什么基本类型（String, Long, Integer）的情况下，请优先使用 Mockito.any() 或其宽泛重载，避免因类型转换产生编译时报错 (例如 long无法转换为java.lang.String)。\n");
        sb.append("   - 你必须返回能够**直接成功编译**的 Java 代码！\n");
        sb.append(
                "6. 【增强可见性】：在每个测试方法执行前后，或是断言前后，请添加友好的 `System.out.println` 输出语句，打印当前正在执行的测试场景或关键参数/结果，以确保用户在控制台能直观看到测试执行的过程。\n");
        sb.append("7. 提供完整的可编译的 Java 测试类代码。\n");
        sb.append("8. 只返回 Java 代码，不要有任何 markdown 格式化标记 (例如 ```java)，不要有额外解释。");
        return sb.toString();
    }
}

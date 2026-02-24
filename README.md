# AutoTestGen (自动单测生成器)

AutoTestGen 是一个基于 **JavaParser**（AST 语法树分析）和 **LLM**（如 GPT-4, DeepSeek 等大语言模型）的开源 Java 智能单元测试生成工具。

编写单元测试往往伴随着大量的重复劳动：你需要手动 Mock 依赖、处理 `@InjectMocks`，并编写繁琐的边界条件测试（`if-else` 分支）。AutoTestGen 通过静态分析 Java 源码结构，提取依赖关系，并将精确的上下文提示（Prompt）发送给大语言模型，从而自动生成开箱即用、可直接编译运行的 JUnit 5 测试代码。

## 🚀 核心特性

*   **上下文感知**: 借助 JavaParser 准确提取类依赖和方法签名，避免将整个凌乱的源文件直接丢给大模型，节省 Token 并提高准确率。
*   **自动 Mock 依赖**: 自动识别类中的成员变量（如 `@Autowired` 或 `private final` 字段），并指示大模型使用 Mockito 自动生成 `@Mock` 注入。
*   **侧重分支覆盖**: 在 Prompt 层面强制要求大模型覆盖所有的 `if-else` 边界路径。
*   **兼容所有类 OpenAI 接口**: 原生支持 OpenAI API，但可以通过配置环境变量无缝切换至任何兼容 OpenAI 格式的大模型 API（例如本地部署的模型、DeepSeek、Cherry Studio 代理端等）。

## 🛠️ 工作原理

1.  **解析 (Parse)**: `ClassInfoVisitor` 读取目标 `.java` 文件，提取依赖项和公开方法。
2.  **提示 (Prompt)**: `PromptBuilder` 根据提取的结构化数据，组装包含严格技术栈要求（JUnit 5, Mockito）的提示词。
3.  **生成 (Generate)**: `LLMClient` 调用大模型 API 接口获取代码。
4.  **保存 (Save)**: `TestFileGenerator` 自动清理 Markdown 语法，并将生成的 `*Test.java` 测试类直接保存至项目对应的 `src/test/java/...` 目录中。

## 💻 快速开始

### 环境依赖

*   Java 17 或更高版本
*   Maven 3.8+

### 1. 配置 API Key

你可以通过**环境变量**或**配置文件**来设置参数（优先级：环境变量 > 配置文件 `application.properties/autotestgen.properties`）。

**方式一：环境变量**
```bash
# 必填项：你的 API Key
export OPENAI_API_KEY="sk-xxxxxxxxxxx"

# 可选项：如果你使用代理或其他的兼容模型配置
# export OPENAI_API_BASE="https://api.your-proxy.com/v1"
# export OPENAI_MODEL="gpt-4"
```

**方式二：配置文件**
在项目根目录或 `src/main/resources` 下创建 `autotestgen.properties`（或 `application.properties`）：
```properties
openai.api.key=sk-xxxxxxxxxxx
# openai.api.base=https://api.openai.com/v1
# openai.model=gpt-4
```

### 2. 编译项目

克隆本项目后，首先进行编译：
```bash
mvn clean compile
```

### 3. 生成单元测试（支持命令行与代码调用）

**方式一：CLI 命令行方式**
如果你想快速对某个源文件生成测试，可以通过如下方式：
```bash
mvn exec:java -Dexec.mainClass="com.autotestgen.Main" \
  -Dexec.args="src/main/java/com/autotestgen/sample/OrderService.java src/test/java"
```

**方式二：API 代码集成方式**
你还可以将生成器无缝嵌入到你自己的自动化流程中，只需要调用 `AutoTestGen` API 门面类即可：
```java
import com.autotestgen.AutoTestGen;

public class MyAutomation {
    public void generateMyTests() throws Exception {
        String sourceFilePath = "src/main/java/com/example/UserService.java";
        String testOutputDir = "src/test/java";
        AutoTestGen.generate(sourceFilePath, testOutputDir);
    }
}
```

执行成功后，工具会分析目标源文件，并在对应的测试环境包路径下自动生成带有分支测试用例的 `*Test.java`。

### 4. 运行验证测试

生成的测试代码是标准的 JUnit 代码，你可以直接使用 Maven 运行测试验证：
```bash
mvn test
```

## ⚠️ 局限性与避坑指南

1.  **超大类与上下文限制**: 目前工具会将当前类的所有公共方法打包在一次请求中发送。如果目标类拥有数千行代码，这可能会导致超出大模型的 Context Token 上限。建议用于标准的微服务类模块，或在未来版本中支持按方法级粒度拆分调用。
2.  **复杂数据结构**: 如果被测方法的入参是极为复杂、多层嵌套的对象（DTO），大模型可能会“偷懒”将其赋值为 `null`，导致 NPE 问题。对于复杂对象，建议结合 `EasyRandom` 等工具辅助生成数据。
3.  **私有方法测试**: 按照最佳实践，本工具默认只针对 `public` 方法生成测试。私有方法的逻辑应由测试公共方法来覆盖。

## 🤝 参与贡献

我们非常欢迎任何形式的贡献！如果你想增加新的语法解析规则（如识别特定的 Spring 注解）、改进提示词工程（Prompt Engineering），或者添加新的测试框架支持（如 TestNG），欢迎提交 Pull Request！

## 📄 开源许可证

本项目基于 [MIT License](LICENSE) 许可协议开源。

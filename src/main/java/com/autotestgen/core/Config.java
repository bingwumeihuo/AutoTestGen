package com.autotestgen.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();

    static {
        // 尝试从类路径 (src/main/resources) 加载 application.properties 或
        // autotestgen.properties
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException e) {
            // ignore
        }

        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("autotestgen.properties")) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException e) {
            // ignore
        }

        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("autotestgen-sample.properties")) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException e) {
            // ignore
        }

        // 尝试从根目录加载 application.properties
        try (FileInputStream fis = new FileInputStream("application.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            // ignore
        }

        try (FileInputStream fis = new FileInputStream("autotestgen.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            // ignore
        }

        try (FileInputStream fis = new FileInputStream("autotestgen-sample.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            // ignore
        }
    }

    /**
     * 获取配置，优先级：环境变量 > 配置文件 > 默认值
     */
    public static String get(String envKey, String propKey, String defaultValue) {
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.trim().isEmpty()) {
            return envVal;
        }
        return properties.getProperty(propKey, defaultValue);
    }

    public static String getApiKey() {
        return get("OPENAI_API_KEY", "openai.api.key", null);
    }

    public static String getApiBase() {
        return get("OPENAI_API_BASE", "openai.api.base", "https://api.openai.com/v1");
    }

    public static String getModel() {
        return get("OPENAI_MODEL", "openai.model", "gpt-4");
    }
}

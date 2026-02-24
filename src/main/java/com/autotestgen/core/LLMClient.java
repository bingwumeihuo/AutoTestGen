package com.autotestgen.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class LLMClient {
    private static final String API_KEY = Config.getApiKey();
    private static final String API_URL = Config.getApiBase().endsWith("/")
            ? Config.getApiBase() + "chat/completions"
            : Config.getApiBase() + "/chat/completions";

    // 默认大模型，可通过环境变量覆盖
    private static final String MODEL = Config.getModel();

    private final OkHttpClient client;
    private final Gson gson;

    public LLMClient() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    public String generateCode(String prompt) throws IOException {
        if (API_KEY == null || API_KEY.trim().isEmpty()) {
            throw new RuntimeException("未设置 OPENAI_API_KEY 环境变量或配置文件属性！请配置 openai.api.key 或环境变量 OPENAI_API_KEY。");
        }

        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt);

        JsonArray messages = new JsonArray();
        messages.add(message);

        JsonObject requestBodyJson = new JsonObject();
        requestBodyJson.addProperty("model", MODEL);
        requestBodyJson.add("messages", messages);
        requestBodyJson.addProperty("temperature", 0.0);

        RequestBody body = RequestBody.create(
                requestBodyJson.toString(),
                MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("请求失败，状态码: " + response);
            }

            String responseBody = response.body().string();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

            return jsonResponse.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
        }
    }
}

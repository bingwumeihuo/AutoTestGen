package com.autotestgen.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class LLMClient {
    private static final String API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String API_URL = System.getenv("OPENAI_API_BASE") != null 
            ? System.getenv("OPENAI_API_BASE") + "/chat/completions" 
            : "https://api.openai.com/v1/chat/completions";
    
    // Default model can be overridden by env variable
    private static final String MODEL = System.getenv("OPENAI_MODEL") != null 
            ? System.getenv("OPENAI_MODEL") 
            : "gpt-4";

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
            throw new RuntimeException("OPENAI_API_KEY environment variable is not set!");
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
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
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

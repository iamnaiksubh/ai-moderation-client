package com.subhashish.aimoderationclient.service;

import com.subhashish.aimoderationclient.model.ModerationResult;
import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ModerationValidatorImpl implements ModerationValidator {

    @Value("${gemini-model-url}")
    private String API_URL;

    @Value("${gemini-api-key}")
    private String apiKey;

    private static final String SYSTEM_PROMPT = """
        You are a content moderation classifier. Analyze the given text and respond ONLY with a JSON object.
        No explanation, no markdown, just raw JSON.
        
        Categories to check: hate, harassment, violence, sexual, self-harm, dangerous, spam
        
        Response format:
        {
          "flagged": true/false,
          "violations": ["category1", "category2"]
        }
        
        If content is safe, return: {"flagged": false, "violations": []}
        """;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();


    @Override
    public ModerationResult validate(String text) {
        try {
            String requestBody = mapper.writeValueAsString(Map.of(
                    "system_instruction", Map.of(
                            "parts", List.of(Map.of("text", SYSTEM_PROMPT))
                    ),
                    "contents", List.of(Map.of(
                            "parts", List.of(Map.of("text", text))
                    )),
                    "generationConfig", Map.of(
                            "temperature", 0,
                            "responseMimeType", "application/json"
                    )
            ));

            Request request = new Request.Builder()
                    .url(API_URL + "?key=" + apiKey)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                JsonNode root = mapper.readTree(response.body().string());

                String content = root
                        .path("candidates").get(0)
                        .path("content")
                        .path("parts").get(0)
                        .path("text").asText();

                JsonNode result = mapper.readTree(content);
                boolean flagged = result.get("flagged").asBoolean();

                List<String> violations = new ArrayList<>();
                if (flagged && result.has("violations")) {
                    result.get("violations").forEach(v -> violations.add(v.asText()));
                }

                return new ModerationResult(flagged, violations);
            }
        } catch (Exception e) {
            throw new RuntimeException("Gemini moderation call failed", e);
        }
    }
}
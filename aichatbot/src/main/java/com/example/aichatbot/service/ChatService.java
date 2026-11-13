
package com.example.aichatbot.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ChatService {

    private final RestClient restClient;
    private final String model;

    public ChatService(
            RestClient.Builder builder,
            @Value("${ollama.base-url}") String baseUrl,
            @Value("${ollama.model}") String model) {

        this.restClient = builder
                .baseUrl(baseUrl)
                .build();

        this.model = model;
    }

    public String askAI(String question) {

        Map<String, Object> request = Map.of(
            "model", model,
            "messages", List.of(
                Map.of(
                    "role", "user",
                    "content", question
                )
            ),
            "stream", false
        );

        Map response = restClient.post()
                .uri("/api/chat")
                .body(request)
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new RuntimeException(
                "Ollama returned an empty response");
        }

        Map message = (Map) response.get("message");

        if (message == null || message.get("content") == null) {
            throw new RuntimeException(
                "No AI response received from Ollama");
        }

        return message.get("content").toString();
    }
}

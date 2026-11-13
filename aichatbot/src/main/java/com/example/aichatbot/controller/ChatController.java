package com.example.aichatbot.controller;

import com.example.aichatbot.service.ChatService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<?> chat(
            @RequestBody ChatRequest request) {

        if (request.message() == null
                || request.message().isBlank()) {

            return ResponseEntity.badRequest().body(
                    Map.of("error", "Please enter a message."));
        }

        if (request.message().length() > 4000) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Message is too long."));
        }

        try {
            String answer =
                    chatService.askAI(request.message().trim());

            return ResponseEntity.ok(
                    new ChatResponse(answer));

        } catch (Exception e) {
            // Log the exception on the server; don't expose
            // API keys or internal error details to the user.
            System.err.println(
                    "Chat API request failed: " + e.getMessage());

            return ResponseEntity.status(
                    HttpStatus.BAD_GATEWAY).body(
                    Map.of("error",
                            "Unable to get an AI response. "
                            + "Check the server logs."));
        }
    }

    public record ChatRequest(String message) {}

    public record ChatResponse(String reply) {}
}
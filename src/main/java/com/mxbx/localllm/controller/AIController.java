package com.mxbx.localllm.controller;

import com.mxbx.localllm.dto.ChatRequestDTO;
import com.mxbx.localllm.dto.ChatResponseDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AIController {

    private final ChatClient chatClient;

    public AIController(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {
        this.chatClient = chatClientBuilder.defaultAdvisors(
                MessageChatMemoryAdvisor.builder(chatMemory).build()
        ).build();
    }

    @PostMapping("/chat")
    public ChatResponseDTO chat(@RequestBody ChatRequestDTO request) {

        String response = chatClient
                .prompt()
                .user(request.getMessage())
                .advisors(advisor -> advisor
                        .param(
                                ChatMemory.CONVERSATION_ID,
                                request.getConversationId()
                        )
                )
                .call()
                .content();

        return new ChatResponseDTO(request.getMessage(), response);
    }

    @GetMapping("/ask")
    public String ask(@RequestParam String question) {

        return chatClient
                .prompt()
                .user(question)
                .call()
                .content();
    }


}
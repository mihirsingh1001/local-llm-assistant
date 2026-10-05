package com.mxbx.localllm.controller;

import com.mxbx.localllm.dto.ChatRequestDTO;
import com.mxbx.localllm.dto.ChatResponseDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api")
public class AIController {

    private final ChatClient chatClient;

    public AIController(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {
        this.chatClient = chatClientBuilder.defaultAdvisors(
                MessageChatMemoryAdvisor.builder(chatMemory).build()
        ).build();
    }

    //Syncronized
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


    // Streaming chat
    @PostMapping(
            value = "/chat/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<ServerSentEvent<String>> chatStream(
            @RequestBody ChatRequestDTO request
    ) {

        return chatClient
                .prompt()
                .user(request.getMessage())
                .advisors(advisor -> advisor
                        .param(
                                ChatMemory.CONVERSATION_ID,
                                request.getConversationId()
                        )
                )
                .stream()
                .content()
                .doOnNext(chunk -> System.out.println("CHUNK = [" + chunk + "]"))
                .map(chunk ->
                        ServerSentEvent.<String>builder()
                                .event("message")
                                .data(chunk)
                                .build()
                )
                .concatWith(
                        Flux.just(
                                ServerSentEvent.<String>builder()
                                        .event("complete")
                                        .data("[DONE]")
                                        .build()
                        )
                );
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
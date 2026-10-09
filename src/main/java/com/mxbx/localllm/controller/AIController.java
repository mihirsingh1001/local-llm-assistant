package com.mxbx.localllm.controller;

import com.mxbx.localllm.dto.ChatRequestDTO;
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

    private static final String SYSTEM_PROMPT = """
            You are a helpful, accurate AI assistant.

            Follow these response rules:
            1. Answer the user's question directly.
            2. Use clear, grammatically correct English.
            3. Use Markdown for structured responses.
            4. Use headings for longer answers.
            5. Use numbered lists for ordered steps or features.
            6. Use bullet points for unordered lists.
            7. Bold important terms and headings.
            8. Keep paragraphs short and readable.
            9. Never intentionally repeat words, sentences, or information.
            10. Do not insert spaces inside words.
            11. For simple questions, answer in 1-3 sentences.
            12. For technical questions, include relevant examples when useful.
            13. Do not generate unnecessary headings or conclusions.
            14. Preserve correct spelling, punctuation, and sentence structure.
            """;

    private final ChatClient chatClient;

    public AIController(
            ChatClient.Builder chatClientBuilder,
            ChatMemory chatMemory
    ) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    @PostMapping(
            value = "/chat/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<ServerSentEvent<String>> chatStream(
            @RequestBody ChatRequestDTO request
    ) {
        return chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .user(request.getMessage())
                .advisors(advisor -> advisor
                        .param(
                                ChatMemory.CONVERSATION_ID,
                                request.getConversationId()
                        )
                )
                .stream()
                .content()
                .doOnNext(chunk ->
                        System.out.println("CHUNK = [" + chunk + "]")
                )
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
}
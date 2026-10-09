package com.mxbx.localllm.service;

import com.mxbx.localllm.entity.ConversationEntity;
import com.mxbx.localllm.entity.ConversationMessageEntity;
import com.mxbx.localllm.entity.MessageRole;
import com.mxbx.localllm.repository.ConversationMessageRepository;
import com.mxbx.localllm.repository.ConversationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class ConversationPersistenceService {

    private final ConversationRepository conversationRepository;
    private final ConversationMessageRepository messageRepository;

    public ConversationPersistenceService(
            ConversationRepository conversationRepository,
            ConversationMessageRepository messageRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional
    public void saveUserMessage(String conversationId, String message) {
        if (isBlank(message)) {
            return;
        }

        ConversationEntity conversation = getOrCreateConversation(conversationId);

        if (isBlank(conversation.getTitle())) {
            conversation.setTitle(createTitle(message));
        }

        saveMessage(conversation, MessageRole.USER, message);
    }

    @Transactional
    public void saveAssistantMessage(String conversationId, String response) {
        if (isBlank(response)) {
            return;
        }

        ConversationEntity conversation = getOrCreateConversation(conversationId);
        saveMessage(conversation, MessageRole.ASSISTANT, response);
    }

    private ConversationEntity getOrCreateConversation(String conversationId) {
        return conversationRepository
                .findById(conversationId)
                .orElseGet(() -> conversationRepository.save(new ConversationEntity(conversationId)));
    }

    private void saveMessage(
            ConversationEntity conversation,
            MessageRole role,
            String content
    ) {
        conversation.setUpdatedAt(Instant.now());
        messageRepository.save(new ConversationMessageEntity(conversation, role, content));
    }

    private String createTitle(String message) {
        String normalized = message.trim().replaceAll("\\s+", " ");

        if (normalized.length() <= 80) {
            return normalized;
        }

        return normalized.substring(0, 77) + "...";
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

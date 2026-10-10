
package com.mxbx.localllm.memory;

import com.mxbx.localllm.entity.ConversationMessageEntity;
import com.mxbx.localllm.entity.MessageRole;
import com.mxbx.localllm.service.ConversationPersistenceService;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostgresChatMemory implements ChatMemory {

    private static final int MAX_MEMORY_MESSAGES = 10;

    private final ConversationPersistenceService persistenceService;

    public PostgresChatMemory(
            ConversationPersistenceService persistenceService
    ) {
        this.persistenceService = persistenceService;
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        for (Message message : messages) {
            if (message.getText() == null || message.getText().isBlank()) {
                continue;
            }

            switch (message.getMessageType()) {
                case USER -> persistenceService.saveUserMessage(
                        conversationId,
                        message.getText()
                );

                case ASSISTANT -> persistenceService.saveAssistantMessage(
                        conversationId,
                        message.getText()
                );

                default -> throw new IllegalArgumentException(
                        "Unsupported message type: "
                                + message.getMessageType()
                );
            }
        }
    }

    @Override
    public List<Message> get(String conversationId) {
        List<ConversationMessageEntity> history =
                persistenceService.getMessagesForConversation(conversationId);

        int start = Math.max(0, history.size() - MAX_MEMORY_MESSAGES);

        return history.subList(start, history.size())
                .stream()
                .map(this::toSpringMessage)
                .toList();
    }

    @Override
    public void clear(String conversationId) {
        persistenceService.clearMessagesForConversation(conversationId);
    }

    private Message toSpringMessage(ConversationMessageEntity entity) {
        if (entity.getRole() == MessageRole.USER) {
            return new UserMessage(entity.getContent());
        }

        return new AssistantMessage(entity.getContent());
    }
}


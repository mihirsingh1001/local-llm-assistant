
package com.mxbx.localllm.repository;

import com.mxbx.localllm.entity.ConversationMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationMessageRepository
        extends JpaRepository<ConversationMessageEntity, Long> {

    List<ConversationMessageEntity>
    findByConversationIdOrderByCreatedAtAscIdAsc(String conversationId);

    void deleteByConversationId(String conversationId);
}

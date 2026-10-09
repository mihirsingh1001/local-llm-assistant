package com.mxbx.localllm.repository;

import com.mxbx.localllm.entity.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<ConversationEntity, String> {
}

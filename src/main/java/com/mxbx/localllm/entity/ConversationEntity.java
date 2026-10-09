package com.mxbx.localllm.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "conversations")
public class ConversationEntity {

    @Id
    @Column(nullable = false, updatable = false, length = 120)
    private String id;

    @Setter
    @Column(length = 160)
    private String title;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Setter
    @Column(nullable = false)
    private Instant updatedAt;

    @OneToMany(
            mappedBy = "conversation",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ConversationMessageEntity> messages = new ArrayList<>();

    public ConversationEntity(String id) {
        this.id = id;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }
}

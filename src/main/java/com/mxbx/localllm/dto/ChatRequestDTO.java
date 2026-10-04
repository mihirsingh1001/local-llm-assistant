package com.mxbx.localllm.dto;

import lombok.Data;

@Data
public class ChatRequestDTO {

    private String conversationId;
    private String message;
}

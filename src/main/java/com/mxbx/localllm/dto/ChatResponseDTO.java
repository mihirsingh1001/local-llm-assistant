package com.mxbx.localllm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class ChatResponseDTO {

    private String message;
    private String response;

}

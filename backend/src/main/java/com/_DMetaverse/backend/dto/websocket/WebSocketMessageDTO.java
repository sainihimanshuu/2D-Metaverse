package com._DMetaverse.backend.dto.websocket;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

@Data
public class WebSocketMessageDTO {
    private String type;
    private JsonNode content;
}

package com._DMetaverse.backend.dto.auth;

import lombok.Data;

@Data
public class AuthResponse {
    private Long userId;
    private String token;
}

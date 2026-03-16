package com._DMetaverse.backend.dto.auth;

import lombok.Data;

@Data
public class SignupRequest {
    private String email;
    private String password;
    private Long avatarId;
}

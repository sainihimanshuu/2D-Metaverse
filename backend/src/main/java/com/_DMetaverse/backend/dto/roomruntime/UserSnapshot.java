package com._DMetaverse.backend.dto.roomruntime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSnapshot {
    private long userId;
    private String username;
    private float x;
    private float y;
}

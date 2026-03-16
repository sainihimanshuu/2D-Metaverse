package com._DMetaverse.backend.dto.room;

import lombok.Data;

@Data
public class CreateRoomRequest {
    private Long mapId;
    private boolean isPublic;
}

package com._DMetaverse.backend.dto.room;

import lombok.Data;

@Data
public class RoomResponse {
    private Long id;
    private String roomCode;
    private boolean isPublic;
    private Long ownerId;
    private Long mapId;
}

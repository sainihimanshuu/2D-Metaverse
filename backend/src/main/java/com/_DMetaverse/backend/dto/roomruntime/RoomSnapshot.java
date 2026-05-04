package com._DMetaverse.backend.dto.roomruntime;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import com._DMetaverse.backend.models.Room;


public class RoomSnapshot {
    private long roomId;
    private String roomName;
    private String roomType; 
    private String roomStatus; 
    private Collection<UserSnapshot> roomMembersSnapshot;
    private Instant createdAt;
    private Instant updatedAt;

    public RoomSnapshot(Room room, Collection<UserSnapshot> roomMembersSnapshot) {
        this.roomId = room.getRoomId();
        this.roomName = room.getName();
        this.roomType = room.getType().name();
        this.roomStatus = room.getStatus().name();
        this.createdAt = room.getCreatedAt();
        this.updatedAt = room.getUpdatedAt();
        this.roomMembersSnapshot = roomMembersSnapshot;
    }
}

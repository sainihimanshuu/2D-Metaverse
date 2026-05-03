package com._DMetaverse.backend.dto.roomruntime;

import java.time.Instant;
import java.util.List;

public class RoomSnapshot {
    private long roomId;
    private String name;
    private String type; // or RoomType
    private String status; // or RoomStatus
    private List<UserSnapshot> members; // or List<Long> userIds
    private Instant createdAt;
    private Instant updatedAt;
}

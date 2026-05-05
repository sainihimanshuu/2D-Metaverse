package com._DMetaverse.backend.realtime;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com._DMetaverse.backend.dto.roomruntime.RoomSnapshot;
import com._DMetaverse.backend.service.RoomService;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class RoomRuntimeManager {
    private ConcurrentHashMap<Long, RoomRuntime> activeRooms;
    private RoomService roomService;

    public RoomRuntimeManager(RoomService roomService) {
        this.roomService = roomService;
        this.activeRooms = new ConcurrentHashMap<>();
    }

    private RoomRuntime getOrCreateRoomRuntime(Long roomId) {
        return activeRooms.computeIfAbsent(roomId, id -> {
            System.out.println("Creating new room runtime for room ID: " + id);
            RoomRuntime newRoomRuntime = new RoomRuntime(id, roomService);

            activeRooms.put(id, newRoomRuntime);

            return newRoomRuntime;
        });
    }

    public void removeRoomRuntime(Long roomId) {
        activeRooms.remove(roomId);

        return;
    }

    public RoomSnapshot getRoomSnapshot(Long roomId) {
        RoomRuntime roomRuntime = activeRooms.get(roomId);
        if(roomRuntime == null) {
            throw new IllegalArgumentException("Room runtime not found for room ID: " + roomId);
        }

        return roomRuntime.getRoomSnapshot();
    }

    public void joinRoomRuntime(JsonNode content) {
        Long roomId = content.get("roomId").asLong();
        Long userId = content.get("userId").asLong();
        String username = content.get("username").asText();

        RoomRuntime roomRuntime = getOrCreateRoomRuntime(roomId);
        roomRuntime.addUserToRoomRuntime(userId, username);
    }

    public void leaveRoomRuntime(JsonNode content) {
        Long roomId = content.get("roomId").asLong();
        Long userId = content.get("userId").asLong();

        RoomRuntime roomRuntime = activeRooms.get(roomId);
        if (roomRuntime == null) {
            throw new IllegalArgumentException("Room runtime not found for room ID: " + roomId);
        }

        roomRuntime.removeUserFromRoomRuntime(userId);
    }
}

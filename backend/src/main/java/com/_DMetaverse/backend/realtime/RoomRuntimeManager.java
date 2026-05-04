package com._DMetaverse.backend.realtime;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com._DMetaverse.backend.dto.roomruntime.RoomSnapshot;
import com._DMetaverse.backend.service.RoomService;

@Component
public class RoomRuntimeManager {
    private ConcurrentHashMap<Long, RoomRuntime> activeRooms;
    private RoomService roomService;

    public RoomRuntimeManager(RoomService roomService) {
        this.roomService = roomService;
        this.activeRooms = new ConcurrentHashMap<>();
    }

    public void createRoomRuntime(Long roomId) {
        if(activeRooms.containsKey(roomId)) {
            return;
        }
        
        RoomRuntime roomRuntime = new RoomRuntime(roomId, roomService);
        activeRooms.put(roomId, roomRuntime);
    }

    public Optional<RoomRuntime> getRoomRuntime(Long roomId) {
        return Optional.ofNullable(activeRooms.get(roomId));
    }

    public void removeRoomRuntime(Long roomId) {
        activeRooms.remove(roomId);

        return;
    }

    public RoomSnapshot getRoomSnapshot(Long roomId) {
        Optional<RoomRuntime> roomRuntimeOpt = getRoomRuntime(roomId);
        if(roomRuntimeOpt.isEmpty()) {
            throw new IllegalArgumentException("Room runtime not found for room ID: " + roomId);
        }

        RoomRuntime roomRuntime = roomRuntimeOpt.get();
        return roomRuntime.getRoomSnapshot();
    }
}

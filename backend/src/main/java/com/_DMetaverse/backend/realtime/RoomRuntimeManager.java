package com._DMetaverse.backend.realtime;

import java.util.List;
import java.util.Optional;

public class RoomRuntimeManager {
    List<RoomRuntime> activeRooms;

    public void createRoomRuntime(Long roomId) {
        if(activeRooms.stream().anyMatch(r -> r.roomId == roomId)) {
            return;
        }
        
        RoomRuntime roomRuntime = new RoomRuntime(roomId);
        activeRooms.add(roomRuntime);
    }

    public Optional<RoomRuntime> getRoomRuntime(Long roomId) {
        return activeRooms.stream()
            .filter(r -> r.roomId == roomId)
            .findFirst();
    }

    public void removeRoomRuntime(Long roomId) {
        activeRooms.removeIf(r -> r.roomId == roomId);

        return;
    }
}

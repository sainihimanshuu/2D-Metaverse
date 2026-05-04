package com._DMetaverse.backend.realtime;

import com._DMetaverse.backend.dto.roomruntime.UserSnapshot;
import com._DMetaverse.backend.models.Room;
import com._DMetaverse.backend.service.RoomService;
import com._DMetaverse.backend.dto.roomruntime.RoomSnapshot;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;

public class RoomRuntime {
    final Room room;
    private final ConcurrentHashMap<Long, UserSnapshot> activeUsers;
    private final RoomService roomService;

    public RoomRuntime(Long roomId, RoomService roomService) {
        this.roomService = roomService;
        try{
            Room room = this.roomService.getRoomById(roomId);

            this.room = room;
            this.activeUsers = new ConcurrentHashMap<>();
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid room ID");
        }
    }

    public void addUserToRoomRuntime(UserSnapshot userSnapshot) {
        activeUsers.put(userSnapshot.getUserId(), userSnapshot);
    }

    public void removeUserFromRoomRuntime(Long userId) {
        activeUsers.remove(userId);
    }

    public void updateUserPosition(Long userId, int x, int y) {
        activeUsers.computeIfPresent(userId, (id, snapshot) -> {
            snapshot.setX(x);
            snapshot.setY(y);

            return snapshot;
        });
    }

    public RoomSnapshot getRoomSnapshot() {
        Collection<UserSnapshot> users = activeUsers.values();
        return new RoomSnapshot(room, users);
    }

    public UserSnapshot getUserSnapshot(Long userId) {
        return activeUsers.get(userId);
    }
}
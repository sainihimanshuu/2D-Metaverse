package com._DMetaverse.backend.service;

import com._DMetaverse.backend.models.Room;
import com._DMetaverse.backend.models.RoomMembership;
import com._DMetaverse.backend.models.RoomRole;
import com._DMetaverse.backend.models.RoomStatus;
import com._DMetaverse.backend.models.RoomType;
import com._DMetaverse.backend.models.User;
import com._DMetaverse.backend.repository.RoomMembershipRepository;
import com._DMetaverse.backend.repository.RoomRepository;
import com._DMetaverse.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomMembershipRepository roomMembershipRepository;

    public RoomService(RoomRepository roomRepository,
                       UserRepository userRepository,
                       RoomMembershipRepository roomMembershipRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.roomMembershipRepository = roomMembershipRepository;
    }

    @Transactional
    public Room createRoom(Long ownerId, String name, String type, Integer maxOccupancy) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        if (name == null || name.isBlank()) throw new IllegalArgumentException("Room name required");
        if (maxOccupancy == null || maxOccupancy < 1) throw new IllegalArgumentException("Invalid max occupancy");

        Room room = new Room();
        room.setOwner(owner);
        room.setName(name);
        room.setType(RoomType.valueOf(type.toUpperCase()));
        room.setMaxOccupancy(maxOccupancy);
        room.setStatus(RoomStatus.ACTIVE);
        room.setCreatedAt(Instant.now());
        room.setUpdatedAt(Instant.now());

        Room savedRoom = roomRepository.save(room);

        RoomMembership membership = new RoomMembership();
        membership.setRoom(savedRoom);
        membership.setUser(owner);
        membership.setRole(RoomRole.OWNER);
        roomMembershipRepository.save(membership);

        return savedRoom;
    }

    @Transactional
    public void addMemberToRoom(Long userId, Long roomId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        if (roomMembershipRepository.findByUserAndRoom(user, room).isPresent()) {
            throw new IllegalStateException("User already in room");
        }

        int currentOccupancy = roomMembershipRepository.countByRoom(room);
        if (currentOccupancy >= room.getMaxOccupancy()) {
            throw new IllegalStateException("Room is full");
        }

        RoomMembership membership = new RoomMembership();
        membership.setRoom(room);
        membership.setUser(user);
        membership.setRole(RoomRole.MEMBER);
        roomMembershipRepository.save(membership);

    }

    @Transactional
    public void removeMemberFromRoom(Long userId, Long roomId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        RoomMembership membership = roomMembershipRepository.findByUserAndRoom(user, room)
                .orElseThrow(() -> new IllegalStateException("User is not a member of the room"));

        roomMembershipRepository.delete(membership);

        //TODO: If owner leaves, transfer ownership or handle room closure.
    }

    @Transactional
    public boolean isMemberOfRoom(Long userId, Long roomId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        return roomMembershipRepository.findByUserAndRoom(user, room).isPresent();
    }

    @Transactional
    public Room getRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
    }
}
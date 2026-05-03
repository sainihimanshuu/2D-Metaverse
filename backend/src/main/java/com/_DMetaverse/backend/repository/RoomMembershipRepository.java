package com._DMetaverse.backend.repository;

import com._DMetaverse.backend.models.RoomMembership;
import com._DMetaverse.backend.models.User;
import com._DMetaverse.backend.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomMembershipRepository extends JpaRepository<RoomMembership, Long> {
    List<RoomMembership> findByUser(User user);

    List<RoomMembership> findByRoom(Room room);

    Optional<RoomMembership> findByUserAndRoom(User user, Room room);

    int countByRoom(Room room);
}
package com._DMetaverse.backend.repository;

import com._DMetaverse.backend.models.Room;
import com._DMetaverse.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByOwner(User owner);

    List<Room> findByStatus(String status);
}
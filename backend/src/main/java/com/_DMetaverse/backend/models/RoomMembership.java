package com._DMetaverse.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "room_memberships", indexes = {
    @Index(columnList = "user_id,room_id"),
    @Index(columnList = "room_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long membershipId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Enumerated(EnumType.STRING)
    private RoomRole role;

    private String status;
    private Instant createdAt;
    private Instant updatedAt;
}
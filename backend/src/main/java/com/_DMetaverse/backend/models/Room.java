package com._DMetaverse.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "rooms", indexes = {
    @Index(columnList = "ownerUserId")
})

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long roomId;

    @ManyToOne
    @JoinColumn(name = "ownerUserId", nullable = false)
    private User owner;

    private String name;

    @Enumerated(EnumType.STRING)
    private RoomType type;

    private Integer maxOccupancy;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    private Instant createdAt;
    private Instant updatedAt;

    @OneToMany(mappedBy = "room")
    private Set<RoomMembership> memberships;
}
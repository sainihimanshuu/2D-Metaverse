package com._DMetaverse.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "users", indexes = {
    @Index(columnList = "username", unique = true),
    @Index(columnList = "accountStatus")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    private String displayName;

    @Column(nullable = false)
    private String passwordHash;

    private String avatarUrl;

    @Column(nullable = false)
    private String accountStatus;

    private Instant createdAt;
    private Instant updatedAt;

    @OneToMany(mappedBy = "user")
    private Set<RoomMembership> memberships;

    @OneToMany(mappedBy = "owner")
    private Set<Room> ownedRooms;
}
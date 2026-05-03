package com._DMetaverse.backend.repository;

import com._DMetaverse.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findByAccountStatus(String accountStatus);
}
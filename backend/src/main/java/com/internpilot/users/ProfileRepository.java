package com.internpilot.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRole(Role role);

    java.util.List<Profile> findByRole(Role role);
}

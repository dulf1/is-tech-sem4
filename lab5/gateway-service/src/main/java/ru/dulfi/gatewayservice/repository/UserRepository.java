package ru.dulfi.gatewayservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.dulfi.gatewayservice.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
} 
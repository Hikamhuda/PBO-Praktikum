package com.example.planydy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.example.planydy.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
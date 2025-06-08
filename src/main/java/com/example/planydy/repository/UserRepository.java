package com.example.planydy.repository;

import com.example.planydy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
    List<User> findAll();
    Optional<User> findByUsername(String username);
}
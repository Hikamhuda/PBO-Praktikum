package com.example.planydy.repository;

import com.example.planydy.model.RencanaStudi;
import com.example.planydy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RencanaStudiRepository extends JpaRepository<RencanaStudi, Long> {
    List<RencanaStudi> findByUser(User user);
}

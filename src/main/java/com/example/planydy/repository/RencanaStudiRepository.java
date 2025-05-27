package com.example.planydy.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.planydy.model.RencanaStudi;
import com.example.planydy.model.User;

public interface RencanaStudiRepository extends JpaRepository<RencanaStudi, Long> {
    List<RencanaStudi> findByUser(User user);
}

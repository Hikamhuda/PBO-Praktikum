package com.example.planydy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.planydy.model.RencanaStudi;

public interface RencanaStudiRepository extends JpaRepository<RencanaStudi, Long> {
    // HAPUS atau KOMENTARI baris berikut:
    // List<RencanaStudi> findByUser(User user);
}

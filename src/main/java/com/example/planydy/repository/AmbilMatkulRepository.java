package com.example.planydy.repository;

import com.example.planydy.model.AmbilMatkul;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmbilMatkulRepository extends JpaRepository<AmbilMatkul, Long> {
    // Tambahkan custom query jika diperlukan
}

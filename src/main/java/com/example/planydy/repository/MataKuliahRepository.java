package com.example.planydy.repository;

import com.example.planydy.model.MataKuliah;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MataKuliahRepository extends JpaRepository<MataKuliah, Long> {
    // CRUD sudah otomatis dari JpaRepository
}

package com.example.planydy.repository;

import com.example.planydy.model.MataKuliah;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MataKuliahRepository extends JpaRepository<MataKuliah, Long> {
    Optional<MataKuliah> findById(Long id);
    List<MataKuliah> findAll();
}

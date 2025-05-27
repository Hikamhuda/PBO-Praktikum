package com.example.planydy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.example.planydy.model.MataKuliah;

public interface MataKuliahRepository extends JpaRepository<MataKuliah, Long> {
    Optional<MataKuliah> findByKodeMk(String kodeMk);
}

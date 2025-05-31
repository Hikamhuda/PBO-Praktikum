package com.example.planydy.repository;

import com.example.planydy.model.AmbilMatkul;
import com.example.planydy.model.MataKuliah;
import com.example.planydy.model.RencanaStudi;
import com.example.planydy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AmbilMatkulRepository extends JpaRepository<AmbilMatkul, Long> {
    // Tambahkan custom query jika diperlukan
    List<AmbilMatkul> findByUserAndRencanaStudi(User user, RencanaStudi rencanaStudi);
    Optional<AmbilMatkul> findByUserAndRencanaStudiAndMataKuliah(User user, RencanaStudi rencanaStudi, MataKuliah mataKuliah);
    List<AmbilMatkul> findByUser(User user);
}
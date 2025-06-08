package com.example.planydy.repository;

import com.example.planydy.model.AmbilMatkul;
import com.example.planydy.model.MataKuliah;
import com.example.planydy.model.RencanaStudi;
import com.example.planydy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmbilMatkulRepository extends JpaRepository<AmbilMatkul, Long> {
    List<AmbilMatkul> findByUser(User user);
    List<AmbilMatkul> findByUserAndRencanaStudi(User user, RencanaStudi rencanaStudi);
    List<AmbilMatkul> findByUserAndRencanaStudiAndMataKuliah(User user, RencanaStudi rencanaStudi, MataKuliah mataKuliah);
}
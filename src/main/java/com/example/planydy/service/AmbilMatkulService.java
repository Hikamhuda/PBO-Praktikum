package com.example.planydy.service;

import com.example.planydy.model.AmbilMatkul;
import com.example.planydy.model.MataKuliah;
import com.example.planydy.model.RencanaStudi;
import com.example.planydy.model.User;
import com.example.planydy.repository.AmbilMatkulRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmbilMatkulService {
    @Autowired
    private AmbilMatkulRepository ambilMatkulRepo;

    public List<AmbilMatkul> findByUser(User user) {
        return ambilMatkulRepo.findByUser(user);
    }

    public List<AmbilMatkul> findByUserAndRencanaStudi(User user, RencanaStudi rs) {
        return ambilMatkulRepo.findByUserAndRencanaStudi(user, rs);
    }

    public List<AmbilMatkul> findByUserAndRencanaStudiAndMataKuliah(User user, RencanaStudi rs, MataKuliah mk) {
        return ambilMatkulRepo.findByUserAndRencanaStudiAndMataKuliah(user, rs, mk);
    }

    public void save(AmbilMatkul am) {
        ambilMatkulRepo.save(am);
    }

    public void deleteById(Long id) {
        ambilMatkulRepo.deleteById(id);
    }
}

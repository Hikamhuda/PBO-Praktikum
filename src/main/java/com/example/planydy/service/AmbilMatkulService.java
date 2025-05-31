package com.example.planydy.service;

import com.example.planydy.model.AmbilMatkul;
import com.example.planydy.model.User;
import com.example.planydy.model.RencanaStudi;
import com.example.planydy.repository.AmbilMatkulRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AmbilMatkulService {
    @Autowired
    private AmbilMatkulRepository ambilMatkulRepository;

    public List<AmbilMatkul> findByUserAndRencanaStudi(User user, RencanaStudi rencanaStudi) {
        return ambilMatkulRepository.findByUserAndRencanaStudi(user, rencanaStudi);
    }
}

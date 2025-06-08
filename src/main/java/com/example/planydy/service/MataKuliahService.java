package com.example.planydy.service;

import com.example.planydy.model.MataKuliah;
import com.example.planydy.repository.MataKuliahRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MataKuliahService {
    @Autowired
    private MataKuliahRepository mataKuliahRepo;

    public List<MataKuliah> findAll() {
        return mataKuliahRepo.findAll();
    }

    public MataKuliah findById(Long id) {
        return mataKuliahRepo.findById(id).orElse(null);
    }

    public void save(MataKuliah mk) {
        mataKuliahRepo.save(mk);
    }

    public void deleteById(Long id) {
        mataKuliahRepo.deleteById(id);
    }
}

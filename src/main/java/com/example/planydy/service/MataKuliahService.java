package com.example.planydy.service;

import com.example.planydy.model.MataKuliah;
import com.example.planydy.repository.MataKuliahRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MataKuliahService {

    @Autowired
    private MataKuliahRepository mataKuliahRepository;

    public List<MataKuliah> findAll() {
        return mataKuliahRepository.findAll();
    }

    public MataKuliah findById(Long id) {
        return mataKuliahRepository.findById(id).orElse(null);
    }

    public MataKuliah save(MataKuliah mk) {
        return mataKuliahRepository.save(mk);
    }

    public void deleteById(Long id) {
        mataKuliahRepository.deleteById(id);
    }
}

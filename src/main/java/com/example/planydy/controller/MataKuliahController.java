package com.example.planydy.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import com.example.planydy.model.MataKuliah;
import com.example.planydy.repository.MataKuliahRepository;

@RestController
@RequestMapping("/api/matakuliah")
public class MataKuliahController {

    private final MataKuliahRepository mataKuliahRepo;

    public MataKuliahController(MataKuliahRepository mataKuliahRepo) {
        this.mataKuliahRepo = mataKuliahRepo;
    }

    @GetMapping
    public List<MataKuliah> getAll() {
        return mataKuliahRepo.findAll();
    }

    @PostMapping
    public MataKuliah create(@RequestBody MataKuliah mk) {
        return mataKuliahRepo.save(mk);
    }
}

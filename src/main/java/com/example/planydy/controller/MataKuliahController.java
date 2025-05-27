package com.example.planydy.controller;

import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/{id}")
    public MataKuliah getById(@PathVariable Long id) {
        return mataKuliahRepo.findById(id).orElse(null);
    }

    @PostMapping
    public MataKuliah create(@RequestBody MataKuliah mk) {
        return mataKuliahRepo.save(mk);
    }

    @PutMapping("/{id}")
    public MataKuliah update(@PathVariable Long id, @RequestBody MataKuliah mk) {
        MataKuliah existing = mataKuliahRepo.findById(id).orElse(null);
        if (existing == null) return null;
        existing.setKodeMk(mk.getKodeMk());
        existing.setNamaMk(mk.getNamaMk());
        existing.setSks(mk.getSks());
        existing.setJenisMk(mk.getJenisMk());
        existing.setDefaultSemester(mk.getDefaultSemester());
        existing.setPrasyaratMkIds(mk.getPrasyaratMkIds());
        return mataKuliahRepo.save(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        mataKuliahRepo.deleteById(id);
    }
}

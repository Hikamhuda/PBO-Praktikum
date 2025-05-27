package com.example.planydy.controller;

import com.example.planydy.model.AmbilMatkul;
import com.example.planydy.repository.AmbilMatkulRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ambilmatkul")
public class AmbilMatkulController {

    private final AmbilMatkulRepository ambilMatkulRepo;

    public AmbilMatkulController(AmbilMatkulRepository ambilMatkulRepo) {
        this.ambilMatkulRepo = ambilMatkulRepo;
    }

    @GetMapping
    public List<AmbilMatkul> getAll() {
        return ambilMatkulRepo.findAll();
    }

    @PostMapping
    public AmbilMatkul create(@RequestBody AmbilMatkul ambilMatkul) {
        return ambilMatkulRepo.save(ambilMatkul);
    }

    @GetMapping("/{id}")
    public AmbilMatkul getById(@PathVariable Long id) {
        return ambilMatkulRepo.findById(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        ambilMatkulRepo.deleteById(id);
    }
}

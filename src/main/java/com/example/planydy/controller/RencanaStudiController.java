package com.example.planydy.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import com.example.planydy.model.RencanaStudi;
import com.example.planydy.repository.RencanaStudiRepository;

@RestController
@RequestMapping("/api/rencana-studi")
public class RencanaStudiController {

    private final RencanaStudiRepository rsRepo;

    public RencanaStudiController(RencanaStudiRepository rsRepo) {
        this.rsRepo = rsRepo;
    }

    @GetMapping
    public List<RencanaStudi> getAll() {
        return rsRepo.findAll();
    }

    @PostMapping
    public RencanaStudi create(@RequestBody RencanaStudi rs) {
        return rsRepo.save(rs);
    }
}

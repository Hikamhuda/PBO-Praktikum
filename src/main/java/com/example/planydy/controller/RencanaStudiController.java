package com.example.planydy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.planydy.repository.MataKuliahRepository;
import com.example.planydy.repository.RencanaStudiRepository;
import com.example.planydy.repository.UserRepository;

@Controller
@RequestMapping("/user")
public class RencanaStudiController {

    private final RencanaStudiRepository rencanaStudiRepo;
    private final UserRepository userRepo;
    private final MataKuliahRepository mataKuliahRepo;

    public RencanaStudiController(UserRepository userRepo, MataKuliahRepository mataKuliahRepo, RencanaStudiRepository rencanaStudiRepo) {
        this.userRepo = userRepo;
        this.mataKuliahRepo = mataKuliahRepo;
        this.rencanaStudiRepo = rencanaStudiRepo;
    }

    // HAPUS seluruh file ini jika sudah tidak dipakai
    // atau minimal hapus method @GetMapping("/semester/detail/{id}") di salah satu controller
}

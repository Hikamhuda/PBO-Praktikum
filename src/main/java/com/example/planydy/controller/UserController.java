package com.example.planydy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.example.planydy.model.User;
import com.example.planydy.repository.UserRepository;
import com.example.planydy.repository.MataKuliahRepository;
import com.example.planydy.model.MataKuliah;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepo;
    private final MataKuliahRepository mataKuliahRepo;

    public UserController(UserRepository userRepo, MataKuliahRepository mataKuliahRepo) {
        this.userRepo = userRepo;
        this.mataKuliahRepo = mataKuliahRepo;
    }

    @GetMapping("/matakuliah")
    public String mataKuliah(Model model) {
        model.addAttribute("matakuliahList", mataKuliahRepo.findAll());
        model.addAttribute("matakuliah", new MataKuliah()); // Tambahkan baris ini
        return "user/matakuliah";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("matakuliahList", mataKuliahRepo.findAll());
        return "user/home";
    }

    @GetMapping
    public String getAllUsers(Model model) {
        model.addAttribute("userList", userRepo.findAll());
        return "user/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "user/create";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute User user) {
        userRepo.save(user);
        return "redirect:/users";
    }

    @PostMapping("/matakuliah")
    public String createMatakuliah(@ModelAttribute MataKuliah matakuliah, Model model) {
        mataKuliahRepo.save(matakuliah);
        return "redirect:/user/matakuliah";
    }
}
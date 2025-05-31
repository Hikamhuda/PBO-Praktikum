package com.example.planydy.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.planydy.model.AmbilMatkul;
import com.example.planydy.model.MataKuliah;
import com.example.planydy.model.RencanaStudi;
import com.example.planydy.model.User;
import com.example.planydy.repository.AmbilMatkulRepository;
import com.example.planydy.repository.MataKuliahRepository;
import com.example.planydy.repository.RencanaStudiRepository;
import com.example.planydy.repository.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepo;
    private final MataKuliahRepository mataKuliahRepo;
    private final RencanaStudiRepository rencanaStudiRepo;
    private final AmbilMatkulRepository ambilMatkulRepo;

    public UserController(
            UserRepository userRepo,
            MataKuliahRepository mataKuliahRepo,
            RencanaStudiRepository rencanaStudiRepo,
            AmbilMatkulRepository ambilMatkulRepo) {
        this.userRepo = userRepo;
        this.mataKuliahRepo = mataKuliahRepo;
        this.rencanaStudiRepo = rencanaStudiRepo;
        this.ambilMatkulRepo = ambilMatkulRepo;
    }

    @GetMapping("/home")
    public String home(@RequestParam(value = "semesterId", required = false) Long semesterId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isEmpty()) return "redirect:/login";
        User user = userOpt.get();

        model.addAttribute("matakuliahList", mataKuliahRepo.findAll());
        model.addAttribute("semesterList", rencanaStudiRepo.findByUser(user));
        model.addAttribute("selectedSemesterId", semesterId);

        String selectedSemesterName = null;
        List<AmbilMatkul> ambilMatkulList = List.of();
        List<Long> ambilMatkulMkIds;

        if (semesterId != null) {
            Optional<RencanaStudi> selectedOpt = rencanaStudiRepo.findById(semesterId);
            if (selectedOpt.isPresent() && selectedOpt.get().getUser().getId().equals(user.getId())) {
                RencanaStudi selected = selectedOpt.get();
                selectedSemesterName = selected.getSemester();
                ambilMatkulList = ambilMatkulRepo.findByUserAndRencanaStudi(user, selected);
                ambilMatkulMkIds = ambilMatkulList.stream().map(am -> am.getMataKuliah().getId()).distinct().toList();
            } else {
                ambilMatkulMkIds = List.of();
            }
        } else {
            List<AmbilMatkul> allAmbilMatkulUser = ambilMatkulRepo.findByUser(user);
            ambilMatkulMkIds = allAmbilMatkulUser.stream()
                .map(am -> am.getMataKuliah().getId())
                .distinct()
                .toList();
        }
        model.addAttribute("selectedSemesterName", selectedSemesterName);
        model.addAttribute("ambilMatkulList", ambilMatkulList);
        model.addAttribute("ambilMatkulMkIds", ambilMatkulMkIds);
        return "user/home";
    }

    @PostMapping("/semester")
    public String tambahSemester(@RequestParam("semester") String semester) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isEmpty()) return "redirect:/login";
        User user = userOpt.get();

        RencanaStudi rs = new RencanaStudi();
        rs.setSemester(semester);
        rs.setUser(user);
        rencanaStudiRepo.save(rs);
        return "redirect:/user/home";
    }

    @GetMapping("/semester/detail/{id}")
    public String showSemesterDetail(@PathVariable Long id, Model model) {
        Optional<RencanaStudi> rsOpt = rencanaStudiRepo.findById(id);
        if (rsOpt.isPresent()) {
            model.addAttribute("rencanaStudi", rsOpt.get());
            return "user/semester_detail";
        }
        return "redirect:/user/home";
    }

    @GetMapping("/matakuliah")
    public String mataKuliah(Model model) {
        model.addAttribute("matakuliahList", mataKuliahRepo.findAll());
        model.addAttribute("matakuliah", new MataKuliah());
        return "user/matakuliah";
    }

    @GetMapping("/matakuliah/edit/{id}")
    public String editMatakuliah(@PathVariable Long id, Model model) {
        MataKuliah mk = mataKuliahRepo.findById(id).orElse(new MataKuliah());
        model.addAttribute("matakuliah", mk);
        model.addAttribute("matakuliahList", mataKuliahRepo.findAll());
        return "user/matakuliah";
    }

    @GetMapping("/matakuliah/delete/{id}")
    public String deleteMatakuliah(@PathVariable Long id) {
        mataKuliahRepo.deleteById(id);
        return "redirect:/user/matakuliah";
    }

    @PostMapping("/matakuliah")
    public String saveMatakuliah(@ModelAttribute("matakuliah") MataKuliah mk) {
        mataKuliahRepo.save(mk);
        return "redirect:/user/matakuliah";
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
        return "redirect:/user";
    }

    @PostMapping("/semester/delete/{id}")
    public String deleteSemester(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isEmpty()) return "redirect:/login";
        User user = userOpt.get();

        Optional<RencanaStudi> rsOpt = rencanaStudiRepo.findById(id);
        if (rsOpt.isPresent() && rsOpt.get().getUser().getId().equals(user.getId())) {
            rencanaStudiRepo.deleteById(id);
        }
        return "redirect:/user/home";
    }

    @PostMapping("/ambilmatkul/add")
    public String addAmbilMatkul(@RequestParam Long rencanaStudiId, @RequestParam Long mataKuliahId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isEmpty()) return "redirect:/login";
        User user = userOpt.get();

        Optional<RencanaStudi> rsOpt = rencanaStudiRepo.findById(rencanaStudiId);
        Optional<MataKuliah> mkOpt = mataKuliahRepo.findById(mataKuliahId);

        if (rsOpt.isPresent() && mkOpt.isPresent() && rsOpt.get().getUser().getId().equals(user.getId())) {
            RencanaStudi rs = rsOpt.get();
            MataKuliah mk = mkOpt.get();
            if (ambilMatkulRepo.findByUserAndRencanaStudiAndMataKuliah(user, rs, mk).isEmpty()) {
                AmbilMatkul am = new AmbilMatkul();
                am.setUser(user);
                am.setRencanaStudi(rs);
                am.setMataKuliah(mk);
                ambilMatkulRepo.save(am);
            }
        }
        return "redirect:/user/home?semesterId=" + rencanaStudiId;
    }

    @PostMapping("/ambilmatkul/delete/{id}")
    public String deleteAmbilMatkul(@PathVariable Long id, @RequestParam(value = "semesterId", required = false) Long semesterId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepo.findByUsername(username).orElse(null);

        AmbilMatkul am = ambilMatkulRepo.findById(id).orElse(null);
        if (am != null && am.getUser().getId().equals(user.getId())) {
            ambilMatkulRepo.deleteById(id);
        }
        // Agar tetap di semester yang sama setelah hapus
        return "redirect:/user/home" + (semesterId != null ? "?semesterId=" + semesterId : "");
    }
}
package com.example.planydy.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
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
    public String home(@RequestParam(value = "semesterId", required = false) Long semesterId,
                       @RequestParam(value = "deleteSemesterMsg", required = false) String deleteSemesterMsg,
                       Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isEmpty()) return "redirect:/login";
        User user = userOpt.get();
        model.addAttribute("user", user);

        model.addAttribute("matakuliahList", mataKuliahRepo.findAll());
        List<RencanaStudi> semesterList = rencanaStudiRepo.findByUser(user);
        model.addAttribute("semesterList", semesterList);
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
            // Ambil semua ambilMatkul milik user, bukan hanya semester terpilih
            List<AmbilMatkul> ambilMatkulListAll = ambilMatkulRepo.findByUser(user);
            ambilMatkulMkIds = ambilMatkulListAll.stream()
                .map(am -> am.getMataKuliah().getId())
                .distinct()
                .toList();
        }
        model.addAttribute("selectedSemesterName", selectedSemesterName);
        model.addAttribute("ambilMatkulList", ambilMatkulList);
        model.addAttribute("ambilMatkulMkIds", ambilMatkulMkIds);

        // Tambahkan perhitungan semesterSksMap
        Map<Long, Integer> semesterSksMap = semesterList.stream()
            .collect(Collectors.toMap(
                RencanaStudi::getId,
                rs -> ambilMatkulRepo.findByUserAndRencanaStudi(user, rs).stream()
                        .mapToInt(am -> am.getMataKuliah().getSks()).sum()
            ));
        model.addAttribute("semesterSksMap", semesterSksMap);

        // Tambahkan perhitungan total SKS semester aktif
        int totalSksSemester = ambilMatkulList.stream()
            .mapToInt(am -> am.getMataKuliah().getSks())
            .sum();
        model.addAttribute("totalSksSemester", totalSksSemester);

        // Setelah ambilMatkulList didapat:
        // Hitung IPK semester aktif
        totalSksSemester = ambilMatkulList.stream()
            .mapToInt(am -> am.getMataKuliah().getSks())
            .sum();
        int totalBobot = ambilMatkulList.stream()
            .mapToInt(am -> {
                if (am.getNilai() == null) return 0;
                switch (am.getNilai()) {
                    case "A": return 4 * am.getMataKuliah().getSks();
                    case "AB": return 3 * am.getMataKuliah().getSks() + 2 * am.getMataKuliah().getSks(); // 3.5 * sks
                    case "B": return 3 * am.getMataKuliah().getSks();
                    case "BC": return 2 * am.getMataKuliah().getSks() + 3 * am.getMataKuliah().getSks(); // 2.5 * sks
                    case "C": return 2 * am.getMataKuliah().getSks();
                    case "D": return 1 * am.getMataKuliah().getSks();
                    case "E": return 0;
                    default: return 0;
                }
            }).sum();
        int totalSksForIpk = ambilMatkulList.stream()
            .filter(am -> am.getNilai() != null && !am.getNilai().isEmpty())
            .mapToInt(am -> am.getMataKuliah().getSks())
            .sum();
        double ipkSemester = 0.0;
        if (totalSksForIpk > 0) {
            ipkSemester = ambilMatkulList.stream()
                .filter(am -> am.getNilai() != null && !am.getNilai().isEmpty())
                .mapToDouble(am -> {
                    switch (am.getNilai()) {
                        case "A": return 4.0 * am.getMataKuliah().getSks();
                        case "AB": return 3.5 * am.getMataKuliah().getSks();
                        case "B": return 3.0 * am.getMataKuliah().getSks();
                        case "BC": return 2.5 * am.getMataKuliah().getSks();
                        case "C": return 2.0 * am.getMataKuliah().getSks();
                        case "D": return 1.0 * am.getMataKuliah().getSks();
                        case "E": return 0.0;
                        default: return 0.0;
                    }
                }).sum() / totalSksForIpk;
        }
        model.addAttribute("ipkSemester", ipkSemester);

        // Perhitungan IPK total seluruh semester
        List<AmbilMatkul> allAmbilMatkul = ambilMatkulRepo.findByUser(user);
        int totalSksAll = allAmbilMatkul.stream()
            .filter(am -> am.getNilai() != null && !am.getNilai().isEmpty())
            .mapToInt(am -> am.getMataKuliah().getSks())
            .sum();
        double ipkTotal = 0.0;
        if (totalSksAll > 0) {
            double totalBobotAll = allAmbilMatkul.stream()
                .filter(am -> am.getNilai() != null && !am.getNilai().isEmpty())
                .mapToDouble(am -> {
                    switch (am.getNilai()) {
                        case "A": return 4.0 * am.getMataKuliah().getSks();
                        case "AB": return 3.5 * am.getMataKuliah().getSks();
                        case "B": return 3.0 * am.getMataKuliah().getSks();
                        case "BC": return 2.5 * am.getMataKuliah().getSks();
                        case "C": return 2.0 * am.getMataKuliah().getSks();
                        case "D": return 1.0 * am.getMataKuliah().getSks();
                        case "E": return 0.0;
                        default: return 0.0;
                    }
                }).sum();
            ipkTotal = totalBobotAll / totalSksAll;
        }
        model.addAttribute("ipkTotal", ipkTotal);

        if (deleteSemesterMsg != null) {
            model.addAttribute("deleteSemesterMsg", deleteSemesterMsg);
        }

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
    public String deleteSemester(@PathVariable Long id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isEmpty()) return "redirect:/login";
        User user = userOpt.get();

        Optional<RencanaStudi> rsOpt = rencanaStudiRepo.findById(id);
        if (rsOpt.isPresent() && rsOpt.get().getUser().getId().equals(user.getId())) {
            rencanaStudiRepo.deleteById(id);
            // Redirect dengan notifikasi
            return "redirect:/user/home?deleteSemesterMsg=Semester+berhasil+dihapus";
        }
        // Redirect dengan notifikasi gagal
        return "redirect:/user/home?deleteSemesterMsg=Gagal+hapus+semester";
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

    @PostMapping("/ambilmatkul/nilai/{id}")
    public String updateNilaiAmbilMatkul(@PathVariable Long id, @RequestParam("nilai") String nilai, @RequestParam("semesterId") Long semesterId) {
        AmbilMatkul am = ambilMatkulRepo.findById(id).orElse(null);
        if (am != null) {
            am.setNilai(nilai);
            ambilMatkulRepo.save(am);
        }
        return "redirect:/user/home?semesterId=" + semesterId;
    }
}
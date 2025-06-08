package com.example.planydy.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import com.example.planydy.service.AmbilMatkulService;
import com.example.planydy.service.MataKuliahService;
import com.example.planydy.service.RencanaStudiService;
import com.example.planydy.service.UserService;

@Controller
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private MataKuliahService mataKuliahService;
    @Autowired
    private UserService userService;
    @Autowired
    private RencanaStudiService rencanaStudiService;
    @Autowired
    private AmbilMatkulService ambilMatkulService;

    @GetMapping("/home")
    public String home(Model model,
                      @RequestParam(value = "userId", required = false) Long userId) {
        // Panel kiri: daftar user non-admin
        List<User> userList = userService.findAll().stream()
            .filter(u -> u.getRole() != User.Role.ADMIN)
            .collect(Collectors.toList());
        model.addAttribute("userList", userList);

        User selectedUser = null;
        List<RencanaStudi> rencanaStudiList = List.of();
        Map<Long, List<AmbilMatkul>> ambilMatkulMap = Map.of();
        Double ipkTotal = 0.0;
        Map<Long, Integer> totalSksMap = Map.of();
        Map<Long, Double> ipkSemesterMap = Map.of();

        if (userId != null) {
            User tempUser = userService.findById(userId);
            if (tempUser != null) {
                selectedUser = tempUser;
                final User finalSelectedUser = selectedUser;
                rencanaStudiList = rencanaStudiService.findByUser(selectedUser);
                ambilMatkulMap = rencanaStudiList.stream()
                    .collect(Collectors.toMap(
                        RencanaStudi::getId,
                        rs -> ambilMatkulService.findByUserAndRencanaStudi(finalSelectedUser, rs)
                    ));

                // Perhitungan total SKS dan IPK semester harus dilakukan di luar lambda
                Map<Long, List<AmbilMatkul>> finalAmbilMatkulMap = ambilMatkulMap;

                // Hitung total SKS per semester
                totalSksMap = rencanaStudiList.stream().collect(Collectors.toMap(
                    RencanaStudi::getId,
                    rs -> {
                        List<AmbilMatkul> amList = finalAmbilMatkulMap.getOrDefault(rs.getId(), List.of());
                        return amList.stream()
                            .filter(am -> am.getMataKuliah() != null)
                            .mapToInt(am -> am.getMataKuliah().getSks())
                            .sum();
                    }
                ));

                // Hitung IPK per semester
                ipkSemesterMap = rencanaStudiList.stream().collect(Collectors.toMap(
                    RencanaStudi::getId,
                    rs -> {
                        List<AmbilMatkul> amList = finalAmbilMatkulMap.getOrDefault(rs.getId(), List.of());
                        int totalSks = amList.stream()
                            .filter(am -> am.getNilai() != null && !am.getNilai().isEmpty())
                            .mapToInt(am -> am.getMataKuliah().getSks())
                            .sum();
                        double totalBobot = amList.stream()
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
                        return totalSks > 0 ? totalBobot / totalSks : 0.0;
                    }
                ));

                // Hitung IPK total seluruh semester user
                List<AmbilMatkul> allAmbilMatkul = ambilMatkulService.findByUser(selectedUser);
                int totalSksAll = allAmbilMatkul.stream()
                    .filter(am -> am.getNilai() != null && !am.getNilai().isEmpty())
                    .mapToInt(am -> am.getMataKuliah().getSks())
                    .sum();
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
                ipkTotal = totalSksAll > 0 ? totalBobotAll / totalSksAll : 0.0;
            }
        }

        model.addAttribute("admin", new Object() { public String getNamaLengkap() { return "Administrator"; } });
        model.addAttribute("selectedUser", selectedUser);
        model.addAttribute("rencanaStudiList", rencanaStudiList);
        model.addAttribute("ambilMatkulMap", ambilMatkulMap);
        model.addAttribute("matakuliahList", mataKuliahService.findAll());
        model.addAttribute("ipkTotal", ipkTotal);
        model.addAttribute("totalSksMap", totalSksMap);
        model.addAttribute("ipkSemesterMap", ipkSemesterMap);

        return "admin/home";
    }

    @PostMapping("/rencana-studi/add-matkul")
    public String addMatkulToRencanaStudi(
            @RequestParam Long rencanaStudiId,
            @RequestParam Long mataKuliahId
    ) {
        RencanaStudi rs = rencanaStudiService.findById(rencanaStudiId);
        MataKuliah mk = mataKuliahService.findById(mataKuliahId);
        if (rs != null && mk != null) {
            User user = rs.getUser();
            // Cek duplikasi
            List<AmbilMatkul> existing = ambilMatkulService.findByUserAndRencanaStudiAndMataKuliah(user, rs, mk);
            if (existing.isEmpty()) {
                AmbilMatkul am = new AmbilMatkul();
                am.setUser(user);
                am.setRencanaStudi(rs);
                am.setMataKuliah(mk);
                ambilMatkulService.save(am);
                // Berhasil tambah, redirect biasa
                return "redirect:/admin/home?userId=" + user.getId();
            } else {
                // Sudah ada, redirect dengan notifikasi error
                return "redirect:/admin/home?userId=" + user.getId() + "&addMatkulError=Mata+kuliah+sudah+terdaftar+di+rencana+studi";
            }
        }
        // Jika gagal, redirect ke home admin
        return "redirect:/admin/home";
    }

    @GetMapping("/matakuliah")
    public String matakuliah(Model model, @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("matakuliahList", mataKuliahService.findAll());
        model.addAttribute("matakuliah", new MataKuliah());
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "admin/matakuliah";
    }

    @GetMapping("/matakuliah/edit/{id}")
    public String editMatakuliah(@PathVariable Long id, Model model, @RequestParam(value = "error", required = false) String error) {
        MataKuliah mk = mataKuliahService.findById(id);
        model.addAttribute("matakuliah", mk != null ? mk : new MataKuliah());
        model.addAttribute("matakuliahList", mataKuliahService.findAll());
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "admin/matakuliah";
    }

    @GetMapping("/matakuliah/delete/{id}")
    public String deleteMatakuliah(@PathVariable Long id) {
        try {
            mataKuliahService.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // Redirect dengan pesan error jika gagal hapus karena constraint
            return "redirect:/admin/matakuliah?error=Tidak+bisa+hapus+karena+masih+digunakan+di+data+lain";
        }
        return "redirect:/admin/matakuliah";
    }

    @PostMapping("/matakuliah")
    public String saveMatakuliah(@ModelAttribute("matakuliah") MataKuliah mk) {
        mataKuliahService.save(mk);
        return "redirect:/admin/matakuliah";
    }

    @GetMapping("/rencana-studi")
    public String rencanaStudiPage(
            Model model,
            @RequestParam(value = "userId", required = false) Long userId) {
        List<User> userList = userService.findAll();
        model.addAttribute("userList", userList);

        final User selectedUser;
        final List<RencanaStudi> rencanaStudiList;
        final Map<Long, List<AmbilMatkul>> ambilMatkulMap;

        if (userId != null) {
            selectedUser = userService.findById(userId); // gunakan findById
            rencanaStudiList = rencanaStudiService.findByUser(selectedUser);
            ambilMatkulMap = rencanaStudiList.stream()
                .collect(Collectors.toMap(
                    RencanaStudi::getId,
                    rs -> ambilMatkulService.findByUserAndRencanaStudi(selectedUser, rs)
                ));
        } else {
            selectedUser = null;
            rencanaStudiList = List.of();
            ambilMatkulMap = Map.of();
        }

        model.addAttribute("selectedUser", selectedUser);
        model.addAttribute("rencanaStudiList", rencanaStudiList);
        model.addAttribute("ambilMatkulMap", ambilMatkulMap);

        return "admin/rencana_studi";
    }

    @PostMapping("/ambilmatkul/delete/{id}")
    public String deleteAmbilMatkul(@PathVariable Long id, @RequestParam("userId") Long userId) {
        ambilMatkulService.deleteById(id);
        return "redirect:/admin/home?userId=" + userId;
    }
}
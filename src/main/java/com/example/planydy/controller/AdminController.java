package com.example.planydy.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.planydy.model.MataKuliah;
import com.example.planydy.service.MataKuliahService;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import com.example.planydy.model.User;
import com.example.planydy.model.RencanaStudi;
import com.example.planydy.model.AmbilMatkul;
import com.example.planydy.service.UserService;
import com.example.planydy.service.RencanaStudiService;
import com.example.planydy.service.AmbilMatkulService;
import org.springframework.dao.DataIntegrityViolationException;

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
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        class Admin {
            public String getNamaLengkap() {
                return "Administrator";
            }
        }
        Admin admin = new Admin();
        model.addAttribute("admin", admin);
        return "admin/home";
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
}
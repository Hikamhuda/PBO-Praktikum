package com.example.planydy.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("admin")
public class AdminController {

    @GetMapping("/home")
    public String home() {
        return "admin/home";
    }

}
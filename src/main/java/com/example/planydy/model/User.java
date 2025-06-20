package com.example.planydy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;
    private String namaLengkap;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Double ipkSaatIni;
    private Integer sksMaksimalSemesterBerikutnya;

    public enum Role {
        ADMIN,
        USER
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public User.Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Double getIpkSaatIni() {
        return ipkSaatIni;
    }

    public void setIpkSaatIni(Double ipkSaatIni) {
        this.ipkSaatIni = ipkSaatIni;
    }

    public Integer getSksMaksimalSemesterBerikutnya() {
        return sksMaksimalSemesterBerikutnya;
    }

    public void setSksMaksimalSemesterBerikutnya(Integer sksMaksimalSemesterBerikutnya) {
        this.sksMaksimalSemesterBerikutnya = sksMaksimalSemesterBerikutnya;
    }

}

package com.example.planydy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Entity
public class MataKuliah {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String kodeMk;

    private String namaMk;
    private Integer sks;

    @Enumerated(EnumType.STRING)
    private JenisMk jenisMk;

    private Integer defaultSemester;

    // Representasi prasyarat (simple string, bisa diubah ke many-to-many)
    private String prasyaratMkIds;

    public enum JenisMk {
        WAJIB,
        PILIHAN
    }

    // Getters & Setters
}
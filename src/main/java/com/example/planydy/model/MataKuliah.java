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

    private String prasyaratMkIds;

    public enum JenisMk {
        WAJIB,
        PILIHAN
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKodeMk() {
        return kodeMk;
    }

    public void setKodeMk(String kodeMk) {
        this.kodeMk = kodeMk;
    }

    public String getNamaMk() {
        return namaMk;
    }

    public void setNamaMk(String namaMk) {
        this.namaMk = namaMk;
    }

    public Integer getSks() {
        return sks;
    }

    public void setSks(Integer sks) {
        this.sks = sks;
    }

    public JenisMk getJenisMk() {
        return jenisMk;
    }

    public void setJenisMk(JenisMk jenisMk) {
        this.jenisMk = jenisMk;
    }

    public Integer getDefaultSemester() {
        return defaultSemester;
    }

    public void setDefaultSemester(Integer defaultSemester) {
        this.defaultSemester = defaultSemester;
    }

    public String getPrasyaratMkIds() {
        return prasyaratMkIds;
    }

    public void setPrasyaratMkIds(String prasyaratMkIds) {
        this.prasyaratMkIds = prasyaratMkIds;
    }
}
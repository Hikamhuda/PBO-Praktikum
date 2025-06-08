package com.example.planydy.service;

import com.example.planydy.model.RencanaStudi;
import com.example.planydy.model.User;
import com.example.planydy.repository.RencanaStudiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RencanaStudiService {
    @Autowired
    private RencanaStudiRepository rencanaStudiRepo;

    public List<RencanaStudi> findByUser(User user) {
        return rencanaStudiRepo.findByUser(user);
    }

    public RencanaStudi findById(Long id) {
        return rencanaStudiRepo.findById(id).orElse(null);
    }
}

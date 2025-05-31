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
    private RencanaStudiRepository rencanaStudiRepository;

    public List<RencanaStudi> findByUser(User user) {
        return rencanaStudiRepository.findByUser(user);
    }
}

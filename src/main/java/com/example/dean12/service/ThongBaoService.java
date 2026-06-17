package com.example.dean12.service;

import com.example.dean12.model.ThongBao;
import com.example.dean12.repository.ThongBaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ThongBaoService {
    @Autowired
    private ThongBaoRepository repository;

    public List<ThongBao> getAll() {
        return repository.findAll();
    }

    public void save(ThongBao tb) {
        repository.save(tb);
    }
}

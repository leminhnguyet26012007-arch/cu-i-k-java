package com.example.dean12.service;

import com.example.dean12.model.GiangVien;
import com.example.dean12.repository.GiangVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GiangVienService {
    @Autowired
    private GiangVienRepository repository;

    public List<GiangVien> getAll() {
        return repository.findAll();
    }

    public GiangVien getById(String id) {
        return repository.findById(id).orElse(null);
    }

    public GiangVien getByUsername(String username) {
        return repository.findByUserUsername(username);
    }

    public void save(GiangVien gv) {
        repository.save(gv);
    }
}


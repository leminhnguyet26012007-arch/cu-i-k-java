package com.example.dean12.service;

import com.example.dean12.model.SinhVien;
import com.example.dean12.repository.SinhVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SinhVienService {
    @Autowired
    private SinhVienRepository repository;

    public List<SinhVien> getAllStudents() {
        return repository.findAll();
    }

    public SinhVien getByUsername(String username) {
        return repository.findByUserUsername(username);
    }

    public void save(SinhVien sv) {
        repository.save(sv);
    }
}


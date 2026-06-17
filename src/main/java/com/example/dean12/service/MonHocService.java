package com.example.dean12.service;

import com.example.dean12.model.MonHoc;
import com.example.dean12.repository.MonHocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MonHocService {
    @Autowired
    private MonHocRepository repository;

    public List<MonHoc> getAll() {
        return repository.findAll();
    }

    public MonHoc getById(String id) {
        return repository.findById(id).orElse(null);
    }

    public void save(MonHoc mh) {
        repository.save(mh);
    }
}

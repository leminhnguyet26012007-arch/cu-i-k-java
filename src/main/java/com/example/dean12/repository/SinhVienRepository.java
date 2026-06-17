package com.example.dean12.repository;

import com.example.dean12.model.SinhVien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SinhVienRepository extends JpaRepository<SinhVien, String> {
    SinhVien findByUserUsername(String username);
}

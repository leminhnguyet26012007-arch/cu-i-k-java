package com.example.dean12.repository;

import com.example.dean12.model.GiangVien;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GiangVienRepository extends JpaRepository<GiangVien, String> {
    GiangVien findByUserUsername(String username);
}

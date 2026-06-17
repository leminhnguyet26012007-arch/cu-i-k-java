package com.example.dean12.repository;

import com.example.dean12.model.HocPhi;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HocPhiRepository extends JpaRepository<HocPhi, Long> {
    List<HocPhi> findBySinhVienMaSV(String maSV);
    HocPhi findBySinhVienMaSVAndMaHk(String maSV, String maHk);
}

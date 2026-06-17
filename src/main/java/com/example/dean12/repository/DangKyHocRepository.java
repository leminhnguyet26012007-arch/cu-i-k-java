package com.example.dean12.repository;

import com.example.dean12.model.DangKyHoc;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DangKyHocRepository extends JpaRepository<DangKyHoc, Long> {
    List<DangKyHoc> findBySinhVienMaSV(String maSV);
    boolean existsBySinhVienMaSVAndLopHocPhanId(String maSV, Long lhpId);
    List<DangKyHoc> findByLopHocPhanId(Long lhpId);
}

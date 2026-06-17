package com.example.dean12.repository;

import com.example.dean12.model.Diem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiemRepository extends JpaRepository<Diem, Long> {
    List<Diem> findBySinhVienMaSV(String maSV);
    List<Diem> findByLopHocPhanId(Long lhpId);
}

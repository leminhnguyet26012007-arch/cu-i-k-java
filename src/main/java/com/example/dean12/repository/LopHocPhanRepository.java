package com.example.dean12.repository;

import com.example.dean12.model.LopHocPhan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LopHocPhanRepository extends JpaRepository<LopHocPhan, Long> {
    List<LopHocPhan> findByMaHk(String maHk);
    List<LopHocPhan> findByGiangVienMaGV(String maGV);
}

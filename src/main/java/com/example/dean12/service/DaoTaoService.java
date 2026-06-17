package com.example.dean12.service;

import com.example.dean12.model.*;
import com.example.dean12.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DaoTaoService {
    @Autowired private LopHocPhanRepository lhpRepo;
    @Autowired private DangKyHocRepository dkRepo;

    public List<LopHocPhan> getOpenClasses() {
        // Hardcoded for demo semester
        return lhpRepo.findByMaHk("20231");
    }

    public List<DangKyHoc> getStudentSchedule(String maSV) {
        return dkRepo.findBySinhVienMaSV(maSV);
    }

    @Transactional
    public String registerCourse(SinhVien sv, Long lhpId) {
        LopHocPhan lhp = lhpRepo.findById(lhpId).orElse(null);
        if (lhp == null) return "Class not found";

        if (dkRepo.existsBySinhVienMaSVAndLopHocPhanId(sv.getMaSV(), lhpId)) {
            return "Already registered for this class";
        }

        if (lhp.getSiSoHienTai() >= lhp.getSiSoToiDa()) {
            return "Class full";
        }

        // Register
        DangKyHoc dk = new DangKyHoc();
        dk.setSinhVien(sv);
        dk.setLopHocPhan(lhp);
        dkRepo.save(dk);

        // Update Capacity
        lhp.setSiSoHienTai(lhp.getSiSoHienTai() + 1);
        lhpRepo.save(lhp);

        return "Success";
    }

    public void saveLopHocPhan(LopHocPhan lhp) {
        lhpRepo.save(lhp);
    }

    public List<DangKyHoc> getStudentsInClass(Long lhpId) {
        return dkRepo.findByLopHocPhanId(lhpId);
    }
}

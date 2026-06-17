package com.example.dean12.service;

import com.example.dean12.model.*;
import com.example.dean12.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class FinanceService {
    @Autowired private HocPhiRepository hpRepo;
    @Autowired private DangKyHocRepository dkRepo;

    private static final BigDecimal COST_PER_CREDIT = new BigDecimal("500000");

    public HocPhi getTuition(String maSV, String maHk) {
        return hpRepo.findBySinhVienMaSVAndMaHk(maSV, maHk);
    }

    public HocPhi calculateAndSaveTuition(SinhVien sv, String maHk) {
        List<DangKyHoc> registrations = dkRepo.findBySinhVienMaSV(sv.getMaSV());
        // Simple logic: Sum credits of all registered courses (assuming they are in current semester for demo)
        int totalCredits = registrations.stream()
                .mapToInt(reg -> reg.getLopHocPhan().getMonHoc().getSoTinChi())
                .sum();

        BigDecimal totalAmount = COST_PER_CREDIT.multiply(new BigDecimal(totalCredits));

        HocPhi hp = hpRepo.findBySinhVienMaSVAndMaHk(sv.getMaSV(), maHk);
        if (hp == null) {
            hp = new HocPhi();
            hp.setSinhVien(sv);
            hp.setMaHk(maHk);
            hp.setDaDong(BigDecimal.ZERO);
            hp.setHanNop(LocalDate.now().plusMonths(1));
            hp.setTrangThai("CHUA_DONG");
        }
        hp.setTongTien(totalAmount);
        return hpRepo.save(hp);
    }

    public void payTuition(Long hpId) {
        HocPhi hp = hpRepo.findById(hpId).orElse(null);
        if (hp != null) {
            hp.setDaDong(hp.getTongTien());
            hp.setTrangThai("DA_DONG");
            hpRepo.save(hp);
        }
    }
}

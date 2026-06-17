package com.example.dean12.service;

import com.example.dean12.model.Diem;
import com.example.dean12.repository.DiemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DiemService {
    @Autowired
    private DiemRepository repository;

    public List<Diem> getAll() {
        return repository.findAll();
    }

    public List<Diem> getByStudent(String maSV) {
        return repository.findBySinhVienMaSV(maSV);
    }

    public void save(Diem diem) {
        repository.save(diem);
    }

    @Autowired private com.example.dean12.repository.DangKyHocRepository dkRepo;

    public List<Diem> getOrCreateGradesForClass(com.example.dean12.model.LopHocPhan lhp) {
        List<Diem> existing = repository.findByLopHocPhanId(lhp.getId());
        List<com.example.dean12.model.DangKyHoc> registrations = dkRepo.findByLopHocPhanId(lhp.getId());

        boolean changes = false;
        for (com.example.dean12.model.DangKyHoc dk : registrations) {
             boolean found = existing.stream().anyMatch(d -> d.getSinhVien().getMaSV().equals(dk.getSinhVien().getMaSV()));
             if (!found) {
                 Diem newDiem = new Diem();
                 newDiem.setSinhVien(dk.getSinhVien());
                 newDiem.setMonHoc(lhp.getMonHoc());
                 newDiem.setLopHocPhan(lhp);
                 newDiem.setGiangVien(lhp.getGiangVien()); // Assume assignment
                 repository.save(newDiem);
                 existing.add(newDiem);
                 changes = true;
             }
        }
        if (changes) {
            existing = repository.findByLopHocPhanId(lhp.getId());
        }
        return existing;
    }
}

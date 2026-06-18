package com.example.dean12.desktop.controller;

import com.example.dean12.desktop.data.DesktopDao;
import com.example.dean12.model.*;

import java.sql.Date;
import java.util.List;

public class TeacherController {
    private final DesktopDao dao;

    public TeacherController(DesktopDao dao) {
        this.dao = dao;
    }

    public GiangVien getTeacherByUsername(String username) { return dao.getTeacherByUsername(username); }
    public List<LopHocPhan> getClassesByTeacher(String maGV) { return dao.getClassesByTeacher(maGV); }
    public List<DangKyHoc> getStudentsInClass(Long lhpId) { return dao.getStudentsInClass(lhpId); }
    public void saveAttendance(Long lhpId, String maSV, Date date, boolean present) { dao.saveAttendance(lhpId, maSV, date, present); }
    public List<Diem> getGradesByClass(Long lhpId) { return dao.getGradesByClass(lhpId); }
    public void updateGrade(Diem d) { dao.updateGrade(d); }
    public void lockGrades(Long lhpId) { dao.lockGrades(lhpId); }
    public void uploadMaterial(Long classId, String title, String path) { dao.uploadMaterial(classId, title, path); }
    public void createNotification(String title, String content, String role, String targetId) { dao.createNotification(title, content, role, targetId); }
    public void updateTeacherProfile(String maGV, String email, String sdt) { dao.updateTeacherProfile(maGV, email, sdt); }
}

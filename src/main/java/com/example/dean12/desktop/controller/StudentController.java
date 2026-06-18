package com.example.dean12.desktop.controller;

import com.example.dean12.desktop.data.DesktopDao;
import com.example.dean12.model.*;

import java.util.List;

public class StudentController {
    private final DesktopDao dao;

    public StudentController(DesktopDao dao) {
        this.dao = dao;
    }

    public SinhVien getStudentByUsername(String username) { return dao.getStudentByUsername(username); }
    public List<DangKyHoc> getStudentSchedule(String maSV) { return dao.getStudentSchedule(maSV); }
    public List<Diem> getStudentGrades(String maSV) { return dao.getStudentGrades(maSV); }
    public double[] getStudentGpaSummary(String maSV) { return dao.getStudentGpaSummary(maSV); }
    public String[] getTuitionInfo(String maSV) { return dao.getTuitionInfo(maSV); }
    public void payTuition(String maSV) { dao.payTuition(maSV); }
    public List<String[]> getNotifications(String role, String classId) { return dao.getNotifications(role, classId); }
    public void updateStudentProfile(String maSV, String email, String sdt) { dao.updateStudentProfile(maSV, email, sdt); }
    public List<LopHocPhan> getAvailableClassesForRegistration(String search) { return dao.getAvailableClassesForRegistration(search); }
    public String registerClass(String maSV, Long lhpId) { return dao.registerClass(maSV, lhpId); }
    public void createFeedback(Feedback fb) { dao.createFeedback(fb); }
}

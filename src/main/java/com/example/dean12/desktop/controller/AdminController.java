package com.example.dean12.desktop.controller;

import com.example.dean12.desktop.data.DesktopDao;
import com.example.dean12.model.*;

import java.util.List;

public class AdminController {
    private final DesktopDao dao;

    public AdminController(DesktopDao dao) {
        this.dao = dao;
    }

    public String[] getSystemConfig() { return dao.getSystemConfig(); }
    public void updateSystemConfig(String year, String semester, double tuition) { dao.updateSystemConfig(year, semester, tuition); }
    public void createNotification(String title, String content, String role) { dao.createNotification(title, content, role); }
    public void createNotification(String title, String content, String role, String targetId) { dao.createNotification(title, content, role, targetId); }
    public List<ThongBao> getAllNotifications() { return dao.getAllNotifications(); }

    public List<SinhVien> getAllStudents() { return dao.getAllStudents(); }
    public void createStudent(SinhVien sv, String username, String password) { dao.createStudent(sv, username, password); }
    public void updateStudent(String maSV, String hoTen, String lop, String email, String sdt) { dao.updateStudent(maSV, hoTen, lop, email, sdt); }
    public void deleteStudent(String maSV) { dao.deleteStudent(maSV); }
    public String exportStudentsToXml() { return dao.exportStudentsToXml(); }
    public String importStudentsFromXml(String xmlData) { return dao.importStudentsFromXml(xmlData); }

    public List<MonHoc> getAllCourses() { return dao.getAllCourses(); }
    public void createCourse(MonHoc mh) { dao.createCourse(mh); }
    public void updateCourse(String maMH, String tenMH, int soTinChi) { dao.updateCourse(maMH, tenMH, soTinChi); }
    public void deleteCourse(String maMH) { dao.deleteCourse(maMH); }

    public List<LopHocPhan> getAllClasses() { return dao.getAllClasses(); }
    public void createClass(LopHocPhan lhp) { dao.createClass(lhp); }
    public void updateClass(Long id, String maLhp, String phongHoc, String lichHoc, String maGV) { dao.updateClass(id, maLhp, phongHoc, lichHoc, maGV); }
    public void deleteClass(Long id) { dao.deleteClass(id); }
    public List<GiangVien> getAllTeachers() { return dao.getAllTeachers(); }

    public List<User> getAllUsers() { return dao.getAllUsers(); }
    public boolean isUserLocked(String username) { return dao.isUserLocked(username); }
    public void createUserAccount(String username, String password, String email, String role) { dao.createUserAccount(username, password, email, role); }
    public void deleteUserAccount(String username) { dao.deleteUserAccount(username); }
    public void lockUserAccount(String username) { dao.lockUserAccount(username); }
    public void unlockUserAccount(String username) { dao.unlockUserAccount(username); }
}

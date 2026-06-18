package com.example.dean12.desktop.data;

import com.example.dean12.desktop.network.ServerDao;
import com.example.dean12.desktop.network.XmlUtil;
import com.example.dean12.model.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class DesktopDao {

    private final ServerDao dao = new ServerDao();

    public void initializeDatabaseSchema() {
        dao.initializeDatabaseSchema();
        dao.seedSampleDataIfEmpty();
    }

    public void updateSystemConfig(String year, String semester, double tuition) {
        dao.updateSystemConfig(year, semester, tuition);
    }

    public String[] getSystemConfig() {
        return dao.getSystemConfig();
    }

    public void createNotification(String title, String content, String role, String targetId) {
        dao.createNotification(title, content, role, targetId);
    }

    public void createNotification(String title, String content, String role) {
        dao.createNotification(title, content, role);
    }

    public List<ThongBao> getAllNotifications() {
        return dao.getAllNotifications();
    }

    public List<String[]> getNotifications(String role, String classId) {
        return dao.getNotifications(role, classId);
    }

    public void payTuition(String maSV) {
        dao.payTuition(maSV);
    }

    public User login(String username, String password) {
        return dao.login(username, password);
    }

    public List<SinhVien> getAllStudents() {
        return dao.getAllStudents();
    }

    public void createStudent(SinhVien sv, String username, String password) {
        dao.createStudent(sv, username, password);
    }

    public void updateStudent(String maSV, String hoTen, String lop, String email, String sdt) {
        dao.updateStudent(maSV, hoTen, lop, email, sdt);
    }

    public void deleteStudent(String maSV) {
        dao.deleteStudent(maSV);
    }

    public List<MonHoc> getAllCourses() {
        return dao.getAllCourses();
    }

    public void createCourse(MonHoc mh) {
        dao.createCourse(mh);
    }

    public void updateCourse(String maMH, String tenMH, int soTinChi) {
        dao.updateCourse(maMH, tenMH, soTinChi);
    }

    public void deleteCourse(String maMH) {
        dao.deleteCourse(maMH);
    }

    public List<LopHocPhan> getAllClasses() {
        return dao.getAllClasses();
    }

    public void createClass(LopHocPhan lhp) {
        dao.createClass(lhp);
    }

    public void updateClass(Long id, String maLhp, String phongHoc, String lichHoc, String maGV) {
        dao.updateClass(id, maLhp, phongHoc, lichHoc, maGV);
    }

    public void deleteClass(Long id) {
        dao.deleteClass(id);
    }

    public List<GiangVien> getAllTeachers() {
        return dao.getAllTeachers();
    }

    public List<LopHocPhan> getClassesByTeacher(String maGV) {
        return dao.getClassesByTeacher(maGV);
    }

    public List<DangKyHoc> getStudentsInClass(Long lhpId) {
        return dao.getStudentsInClass(lhpId);
    }

    public void saveAttendance(Long lhpId, String maSV, Date date, boolean present) {
        dao.saveAttendance(lhpId, maSV, date, present);
    }

    public List<Diem> getGradesByClass(Long lhpId) {
        return dao.getGradesByClass(lhpId);
    }

    public void updateGrade(Diem d) {
        dao.updateGrade(d);
    }

    public void lockGrades(Long lhpId) {
        dao.lockGrades(lhpId);
    }

    public SinhVien getStudentByUsername(String username) {
        return dao.getStudentByUsername(username);
    }

    public List<DangKyHoc> getStudentSchedule(String maSV) {
        return dao.getStudentSchedule(maSV);
    }

    public GiangVien getTeacherByUsername(String username) {
        return dao.getTeacherByUsername(username);
    }

    public void updateTeacherProfile(String maGV, String email, String sdt) {
        dao.updateTeacherProfile(maGV, email, sdt);
    }

    public void updateStudentProfile(String maSV, String email, String sdt) {
        dao.updateStudentProfile(maSV, email, sdt);
    }

    public List<LopHocPhan> getAvailableClassesForRegistration(String search) {
        return dao.getAvailableClassesForRegistration(search);
    }

    public String registerClass(String maSV, Long lhpId) {
        return dao.registerClass(maSV, lhpId);
    }

    public void createUserAccount(String username, String password, String email, String role) {
        dao.createUserAccount(username, password, email, role);
    }

    public void deleteUserAccount(String username) {
        dao.deleteUserAccount(username);
    }

    public void lockUserAccount(String username) {
        dao.lockUserAccount(username);
    }

    public void unlockUserAccount(String username) {
        dao.unlockUserAccount(username);
    }

    public List<User> getAllUsers() {
        return dao.getAllUsers();
    }

    public boolean isUserLocked(String username) {
        return dao.isUserLocked(username);
    }

    public List<Diem> getStudentGrades(String maSV) {
        return dao.getStudentGrades(maSV);
    }

    public void createFeedback(Feedback fb) {
        dao.createFeedback(fb);
    }

    public SinhVien getStudentByProperties(String maSV) {
        return dao.getStudentByProperties(maSV);
    }

    public String[] getTuitionInfo(String maSV) {
        return dao.getTuitionInfo(maSV);
    }

    public double[] getStudentGpaSummary(String maSV) {
        return dao.getStudentGpaSummary(maSV);
    }

    public String exportStudentsToXml() {
        try {
            return XmlUtil.exportStudentsToXml(dao.getAllStudents());
        } catch (Exception e) {
            return null;
        }
    }

    public String importStudentsFromXml(String xmlData) {
        try {
            List<SinhVien> students = XmlUtil.importStudentsFromXml(xmlData);
            for (SinhVien sv : students) {
                SinhVien existing = dao.getStudentByProperties(sv.getMaSV());
                if (existing == null) {
                    dao.createStudent(sv, sv.getMaSV().toLowerCase(), "123");
                } else {
                    dao.updateStudent(sv.getMaSV(), sv.getHoTen(), sv.getLop(), sv.getEmail(), sv.getSdt());
                }
            }
            return "Nhap XML thanh cong. Da xu ly " + students.size() + " sinh vien.";
        } catch (Exception e) {
            return "Nhap XML that bai: " + e.getMessage();
        }
    }

    public void uploadMaterial(Long classId, String title, String path) {
        dao.logActivity("TEACHER", "UPLOAD_MATERIAL", "SUCCESS",
                "Class: " + classId + ", Title: " + title + ", Path: " + path);
    }
}

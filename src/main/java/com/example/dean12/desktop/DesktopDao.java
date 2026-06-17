package com.example.dean12.desktop;

import com.example.dean12.model.*;
import com.example.dean12.desktop.network.Request;
import com.example.dean12.desktop.network.Response;
import com.example.dean12.desktop.network.ConfigUtil;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javafx.scene.control.Alert;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class DesktopDao {

    private Response sendRequest(Request req) {
        String host = ConfigUtil.getProperty("server.host", "localhost");
        int port = ConfigUtil.getIntProperty("server.port", 9000);
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            oos.flush();
            oos.writeObject(req);
            oos.flush();

            Object obj = ois.readObject();
            if (obj instanceof Response) {
                return (Response) obj;
            } else {
                return new Response(false, "Định dạng kết quả trả về không hợp lệ");
            }
        } catch (Exception e) {
            System.err.println("[Socket Client] Request error: " + e.getMessage());
            return new Response(false, "Không thể kết nối đến Máy chủ (Server Port " + port + "). Vui lòng chạy Server trước!");
        }
    }

    public void initializeDatabaseSchema() {
        // Do nothing on client. Server does this at boot.
    }

    // --- SYSTEM CONFIG ---
    public void updateSystemConfig(String year, String semester, double tuition) {
        sendRequest(new Request("UPDATE_SYSTEM_CONFIG", year, semester, tuition));
    }

    public String[] getSystemConfig() {
        Response res = sendRequest(new Request("GET_SYSTEM_CONFIG"));
        return res.isSuccess() ? (String[]) res.getData() : new String[]{"2025-2026", "1", "500000"};
    }

    // --- NOTIFICATIONS ---
    public void createNotification(String title, String content, String role, String targetId) {
        sendRequest(new Request("CREATE_NOTIFICATION", title, content, role, targetId));
    }

    public void createNotification(String title, String content, String role) {
        sendRequest(new Request("CREATE_NOTIFICATION", title, content, role));
    }

    public List<ThongBao> getAllNotifications() {
        Response res = sendRequest(new Request("GET_ALL_NOTIFICATIONS"));
        return res.isSuccess() ? (List<ThongBao>) res.getData() : new ArrayList<>();
    }

    public List<String[]> getNotifications(String role, String classId) {
        Response res = sendRequest(new Request("GET_NOTIFICATIONS", role, classId));
        return res.isSuccess() ? (List<String[]>) res.getData() : new ArrayList<>();
    }

    // --- TUITION ---
    public void payTuition(String maSV) {
         sendRequest(new Request("PAY_TUITION", maSV));
    }

    // --- USER / AUTH ---
    public User login(String username, String password) {
        Response res = sendRequest(new Request("LOGIN", username, password));
        if (!res.isSuccess()) {
            System.err.println("[Login Failed] " + res.getMessage());
            javafx.application.Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi Đăng Nhập");
                alert.setHeaderText("Không thể đăng nhập vào hệ thống");
                alert.setContentText("Phản hồi từ Server: " + res.getMessage());
                alert.showAndWait();
            });
        }
        return res.isSuccess() ? (User) res.getData() : null;
    }

    // --- ADMIN: MANAGE STUDENTS ---
    public List<SinhVien> getAllStudents() {
        Response res = sendRequest(new Request("GET_ALL_STUDENTS"));
        return res.isSuccess() ? (List<SinhVien>) res.getData() : new ArrayList<>();
    }

    public void createStudent(SinhVien sv, String username, String password) {
        sendRequest(new Request("CREATE_STUDENT", sv, username, password));
    }

    public void updateStudent(String maSV, String hoTen, String lop, String email, String sdt) {
        sendRequest(new Request("UPDATE_STUDENT", maSV, hoTen, lop, email, sdt));
    }

    public void deleteStudent(String maSV) {
        sendRequest(new Request("DELETE_STUDENT", maSV));
    }

    // --- ADMIN: MANAGE COURSES ---
    public List<MonHoc> getAllCourses() {
        Response res = sendRequest(new Request("GET_ALL_COURSES"));
        return res.isSuccess() ? (List<MonHoc>) res.getData() : new ArrayList<>();
    }

    public void createCourse(MonHoc mh) {
        sendRequest(new Request("CREATE_COURSE", mh));
    }

    public void updateCourse(String maMH, String tenMH, int soTinChi) {
        sendRequest(new Request("UPDATE_COURSE", maMH, tenMH, soTinChi));
    }

    public void deleteCourse(String maMH) {
        sendRequest(new Request("DELETE_COURSE", maMH));
    }

    // --- ADMIN: MANAGE CLASSES (LopHocPhan) ---
    public List<LopHocPhan> getAllClasses() {
        Response res = sendRequest(new Request("GET_ALL_CLASSES"));
        return res.isSuccess() ? (List<LopHocPhan>) res.getData() : new ArrayList<>();
    }

    public void createClass(LopHocPhan lhp) {
        sendRequest(new Request("CREATE_CLASS", lhp));
    }

    public void updateClass(Long id, String maLhp, String phongHoc, String lichHoc, String maGV) {
        sendRequest(new Request("UPDATE_CLASS", id, maLhp, phongHoc, lichHoc, maGV));
    }

    public void deleteClass(Long id) {
        sendRequest(new Request("DELETE_CLASS", id));
    }

    public List<GiangVien> getAllTeachers() {
        Response res = sendRequest(new Request("GET_ALL_TEACHERS"));
        return res.isSuccess() ? (List<GiangVien>) res.getData() : new ArrayList<>();
    }

    // --- TEACHER METHODS ---
    public List<LopHocPhan> getClassesByTeacher(String maGV) {
        Response res = sendRequest(new Request("GET_CLASSES_BY_TEACHER", maGV));
        return res.isSuccess() ? (List<LopHocPhan>) res.getData() : new ArrayList<>();
    }

    public List<DangKyHoc> getStudentsInClass(Long lhpId) {
        Response res = sendRequest(new Request("GET_STUDENTS_IN_CLASS", lhpId));
        return res.isSuccess() ? (List<DangKyHoc>) res.getData() : new ArrayList<>();
    }

    public void saveAttendance(Long lhpId, String maSV, Date date, boolean present) {
        sendRequest(new Request("SAVE_ATTENDANCE", lhpId, maSV, date, present));
    }

    public List<Diem> getGradesByClass(Long lhpId) {
        Response res = sendRequest(new Request("GET_GRADES_BY_CLASS", lhpId));
        return res.isSuccess() ? (List<Diem>) res.getData() : new ArrayList<>();
    }

    public void updateGrade(Diem d) {
        sendRequest(new Request("UPDATE_GRADE", d));
    }

    public void lockGrades(Long lhpId) {
        sendRequest(new Request("LOCK_GRADES", lhpId));
    }

    // --- STUDENT METHODS ---
    public SinhVien getStudentByUsername(String username) {
        Response res = sendRequest(new Request("GET_STUDENT_BY_USERNAME", username));
        return res.isSuccess() ? (SinhVien) res.getData() : null;
    }

    public List<DangKyHoc> getStudentSchedule(String maSV) {
        Response res = sendRequest(new Request("GET_STUDENT_SCHEDULE", maSV));
        return res.isSuccess() ? (List<DangKyHoc>) res.getData() : new ArrayList<>();
    }

    public GiangVien getTeacherByUsername(String username) {
        Response res = sendRequest(new Request("GET_TEACHER_BY_USERNAME", username));
        return res.isSuccess() ? (GiangVien) res.getData() : null;
    }

    public void updateTeacherProfile(String maGV, String email, String sdt) {
        sendRequest(new Request("UPDATE_TEACHER_PROFILE", maGV, email, sdt));
    }

    public void updateStudentProfile(String maSV, String email, String sdt) {
        sendRequest(new Request("UPDATE_STUDENT_PROFILE", maSV, email, sdt));
    }

    public List<LopHocPhan> getAvailableClassesForRegistration(String search) {
        Response res = sendRequest(new Request("GET_AVAILABLE_CLASSES_FOR_REGISTRATION", search));
        return res.isSuccess() ? (List<LopHocPhan>) res.getData() : new ArrayList<>();
    }

    public String registerClass(String maSV, Long lhpId) {
        Response res = sendRequest(new Request("REGISTER_CLASS", maSV, lhpId));
        return res.getMessage();
    }

    // --- ADMIN: USER ACCOUNT MANAGEMENT ---
    public void createUserAccount(String username, String password, String email, String role) {
        sendRequest(new Request("CREATE_USER_ACCOUNT", username, password, email, role));
    }

    public void deleteUserAccount(String username) {
        sendRequest(new Request("DELETE_USER_ACCOUNT", username));
    }

    public void lockUserAccount(String username) {
        sendRequest(new Request("LOCK_USER_ACCOUNT", username));
    }

    public void unlockUserAccount(String username) {
        sendRequest(new Request("UNLOCK_USER_ACCOUNT", username));
    }

    public List<User> getAllUsers() {
        Response res = sendRequest(new Request("GET_ALL_USERS"));
        return res.isSuccess() ? (List<User>) res.getData() : new ArrayList<>();
    }

    public boolean isUserLocked(String username) {
        Response res = sendRequest(new Request("IS_USER_LOCKED", username));
        return res.isSuccess() ? (Boolean) res.getData() : false;
    }

    public List<Diem> getStudentGrades(String maSV) {
        Response res = sendRequest(new Request("GET_STUDENT_GRADES", maSV));
        return res.isSuccess() ? (List<Diem>) res.getData() : new ArrayList<>();
    }

    public void createFeedback(Feedback fb) {
        sendRequest(new Request("CREATE_FEEDBACK", fb));
    }

    public SinhVien getStudentByProperties(String maSV) {
        Response res = sendRequest(new Request("GET_STUDENT_BY_PROPERTIES", maSV));
        return res.isSuccess() ? (SinhVien) res.getData() : null;
    }

    public String[] getTuitionInfo(String maSV) {
        Response res = sendRequest(new Request("GET_TUITION_INFO", maSV));
        return res.isSuccess() ? (String[]) res.getData() : new String[]{"0", "500000", "0", "false"};
    }

    public double[] getStudentGpaSummary(String maSV) {
        Response res = sendRequest(new Request("GET_GPA_SUMMARY", maSV));
        return res.isSuccess() ? (double[]) res.getData() : new double[]{0, 0, 0};
    }

    // --- XML UTILS ---
    public String exportStudentsToXml() {
        Response res = sendRequest(new Request("EXPORT_STUDENTS_XML"));
        return res.isSuccess() ? (String) res.getData() : null;
    }

    public String importStudentsFromXml(String xmlData) {
        Response res = sendRequest(new Request("IMPORT_STUDENTS_XML", xmlData));
        return res.getMessage();
    }

    // --- TEACHER MATERIAL MANAGEMENT ---
    public void uploadMaterial(Long classId, String title, String path) {
        sendRequest(new Request("UPLOAD_MATERIAL", classId, title, path));
    }
}

package com.example.dean12.desktop.network;

import com.example.dean12.model.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class TcpSocketServer {
    private static ServerSocket serverSocket;
    private static ExecutorService threadPool;
    private static boolean running = false;
    private static final ServerDao dao = new ServerDao();

    public static void start(int port) {
        if (running) return;
        running = true;

        threadPool = Executors.newFixedThreadPool(15);
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("[TCP Server] Server listening on port " + port + "...");

            Thread schemaThread = new Thread(() -> {
                try {
                    dao.initializeDatabaseSchema();
                } catch (Exception e) {
                    System.err.println("[TCP Server] Schema check failed: " + e.getMessage());
                }
            }, "TCP-Schema-Check");
            schemaThread.setDaemon(true);
            schemaThread.start();
            
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("[TCP Server] Client connected from: " + clientSocket.getRemoteSocketAddress());
                    threadPool.execute(new ClientHandler(clientSocket));
                } catch (IOException e) {
                    if (!running) break;
                    System.err.println("[TCP Server] Accept error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("[TCP Server] Server socket creation failed: " + e.getMessage());
        } finally {
            stop();
        }
    }

    public static void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (threadPool != null) {
                threadPool.shutdown();
            }
            System.out.println("[TCP Server] Server stopped.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
                input.mark(8);
                byte[] header = input.readNBytes(4);
                input.reset();

                if (isHttpProbe(header)) {
                    writeHttpStatusPage();
                    return;
                }

                try (ObjectInputStream ois = new ObjectInputStream(input);
                 ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {

                    while (running) {
                        Object obj = ois.readObject();
                        if (!(obj instanceof Request)) break;
                    
                        Request req = (Request) obj;
                        Response res = handleRequest(req);
                    
                        oos.writeObject(res);
                        oos.flush();
                    }
                }
            } catch (EOFException | java.net.SocketException e) {
                // Normal client disconnect
                System.out.println("[TCP Server] Client disconnected: " + socket.getRemoteSocketAddress());
            } catch (Exception e) {
                System.err.println("[TCP Server] Error handling client: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean isHttpProbe(byte[] header) {
            if (header.length < 3) return false;
            String prefix = new String(header, java.nio.charset.StandardCharsets.US_ASCII);
            return prefix.startsWith("GET") || prefix.startsWith("POS") || prefix.startsWith("HEA");
        }

        private void writeHttpStatusPage() throws IOException {
            String body = """
                    <!doctype html>
                    <html>
                    <head><meta charset="utf-8"><title>QLSV TCP Server</title></head>
                    <body style="font-family:Segoe UI,Arial;padding:32px">
                    <h2>QLSV TCP Socket Server is running</h2>
                    <p>Port 9000 is for the JavaFX desktop client, not for browser login.</p>
                    <p>Open the web app at <a href="http://localhost:8081/login">http://localhost:8081/login</a>.</p>
                    <p>Open the desktop app with RUN_DESKTOP.bat or DesktopMain.</p>
                    </body>
                    </html>
                    """;
            byte[] bytes = body.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            String response = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/html; charset=utf-8\r\n"
                    + "Content-Length: " + bytes.length + "\r\n"
                    + "Connection: close\r\n\r\n";
            OutputStream out = socket.getOutputStream();
            out.write(response.getBytes(java.nio.charset.StandardCharsets.US_ASCII));
            out.write(bytes);
            out.flush();
        }

        private Response handleRequest(Request req) {
            String action = req.getAction();
            Object[] params = req.getParams();
            try {
                switch (action) {
                    case "LOGIN":
                        User u = dao.login((String) params[0], (String) params[1]);
                        return new Response(u != null, u != null ? "Đăng nhập thành công" : "Sai tài khoản hoặc mật khẩu hoặc tài khoản đã khóa", u);
                    case "GET_ALL_STUDENTS":
                        return new Response(true, "Thành công", dao.getAllStudents());
                    case "CREATE_STUDENT":
                        dao.createStudent((SinhVien) params[0], (String) params[1], (String) params[2]);
                        return new Response(true, "Thêm sinh viên thành công");
                    case "UPDATE_STUDENT":
                        dao.updateStudent((String) params[0], (String) params[1], (String) params[2], (String) params[3], (String) params[4]);
                        return new Response(true, "Cập nhật sinh viên thành công");
                    case "DELETE_STUDENT":
                        dao.deleteStudent((String) params[0]);
                        return new Response(true, "Xóa sinh viên thành công");
                    case "GET_STUDENT_BY_USERNAME":
                        return new Response(true, "Thành công", dao.getStudentByUsername((String) params[0]));
                    case "GET_STUDENT_BY_PROPERTIES":
                        return new Response(true, "Thành công", dao.getStudentByProperties((String) params[0]));
                    case "GET_STUDENT_SCHEDULE":
                        return new Response(true, "Thành công", dao.getStudentSchedule((String) params[0]));
                    case "GET_TUITION_INFO":
                        return new Response(true, "Thành công", dao.getTuitionInfo((String) params[0]));
                    case "GET_GPA_SUMMARY":
                        return new Response(true, "Thành công", dao.getStudentGpaSummary((String) params[0]));
                    case "UPDATE_STUDENT_PROFILE":
                        dao.updateStudentProfile((String) params[0], (String) params[1], (String) params[2]);
                        return new Response(true, "Cập nhật thông tin cá nhân thành công");
                    case "GET_ALL_COURSES":
                        return new Response(true, "Thành công", dao.getAllCourses());
                    case "CREATE_COURSE":
                        dao.createCourse((MonHoc) params[0]);
                        return new Response(true, "Thêm môn học thành công");
                    case "UPDATE_COURSE":
                        dao.updateCourse((String) params[0], (String) params[1], (Integer) params[2]);
                        return new Response(true, "Cập nhật môn học thành công");
                    case "DELETE_COURSE":
                        dao.deleteCourse((String) params[0]);
                        return new Response(true, "Xóa môn học thành công");
                    case "GET_ALL_CLASSES":
                        return new Response(true, "Thành công", dao.getAllClasses());
                    case "CREATE_CLASS":
                        dao.createClass((LopHocPhan) params[0]);
                        return new Response(true, "Mở lớp học phần thành công");
                    case "UPDATE_CLASS":
                        dao.updateClass((Long) params[0], (String) params[1], (String) params[2], (String) params[3], (String) params[4]);
                        return new Response(true, "Cập nhật lớp học phần thành công");
                    case "DELETE_CLASS":
                        dao.deleteClass((Long) params[0]);
                        return new Response(true, "Xóa lớp học phần thành công");
                    case "GET_CLASSES_BY_TEACHER":
                        return new Response(true, "Thành công", dao.getClassesByTeacher((String) params[0]));
                    case "GET_STUDENTS_IN_CLASS":
                        return new Response(true, "Thành công", dao.getStudentsInClass((Long) params[0]));
                    case "SAVE_ATTENDANCE":
                        dao.saveAttendance((Long) params[0], (String) params[1], (java.sql.Date) params[2], (Boolean) params[3]);
                        return new Response(true, "Lưu điểm danh thành công");
                    case "GET_GRADES_BY_CLASS":
                        return new Response(true, "Thành công", dao.getGradesByClass((Long) params[0]));
                    case "UPDATE_GRADE":
                        dao.updateGrade((Diem) params[0]);
                        return new Response(true, "Cập nhật điểm thành công");
                    case "LOCK_GRADES":
                        dao.lockGrades((Long) params[0]);
                        return new Response(true, "Khóa điểm thành công");
                    case "GET_STUDENT_GRADES":
                        return new Response(true, "Thành công", dao.getStudentGrades((String) params[0]));
                    case "CREATE_FEEDBACK":
                        dao.createFeedback((Feedback) params[0]);
                        return new Response(true, "Gửi phản hồi thành công");
                    case "GET_SYSTEM_CONFIG":
                        return new Response(true, "Thành công", dao.getSystemConfig());
                    case "UPDATE_SYSTEM_CONFIG":
                        dao.updateSystemConfig((String) params[0], (String) params[1], (Double) params[2]);
                        return new Response(true, "Cập nhật cấu hình thành công");
                    case "CREATE_NOTIFICATION":
                        dao.createNotification((String) params[0], (String) params[1], (String) params[2]);
                        return new Response(true, "Tạo thông báo thành công");
                    case "GET_ALL_NOTIFICATIONS":
                        return new Response(true, "Thành công", dao.getAllNotifications());
                    case "GET_NOTIFICATIONS":
                        return new Response(true, "Thành công", dao.getNotifications((String) params[0], (String) params[1]));
                    case "PAY_TUITION":
                        dao.payTuition((String) params[0]);
                        return new Response(true, "Thanh toán học phí thành công");
                    case "GET_ALL_TEACHERS":
                        return new Response(true, "Thành công", dao.getAllTeachers());
                    case "GET_ALL_USERS":
                        return new Response(true, "Thành công", dao.getAllUsers());
                    case "IS_USER_LOCKED":
                        return new Response(true, "Thành công", dao.isUserLocked((String) params[0]));
                    case "LOCK_USER_ACCOUNT":
                        dao.lockUserAccount((String) params[0]);
                        return new Response(true, "Khóa tài khoản thành công");
                    case "UNLOCK_USER_ACCOUNT":
                        dao.unlockUserAccount((String) params[0]);
                        return new Response(true, "Mở khóa tài khoản thành công");
                    case "DELETE_USER_ACCOUNT":
                        dao.deleteUserAccount((String) params[0]);
                        return new Response(true, "Xóa tài khoản thành công");
                    case "CREATE_USER_ACCOUNT":
                        dao.createUserAccount((String) params[0], (String) params[1], (String) params[2], (String) params[3]);
                        return new Response(true, "Tạo tài khoản thành công");
                    case "GET_TEACHER_BY_USERNAME":
                        return new Response(true, "Thành công", dao.getTeacherByUsername((String) params[0]));
                    case "UPDATE_TEACHER_PROFILE":
                        dao.updateTeacherProfile((String) params[0], (String) params[1], (String) params[2]);
                        return new Response(true, "Cập nhật thông tin giảng viên thành công");
                    case "GET_AVAILABLE_CLASSES_FOR_REGISTRATION":
                        return new Response(true, "Thành công", dao.getAvailableClassesForRegistration((String) params[0]));
                    case "REGISTER_CLASS":
                        String regMsg = dao.registerClass((String) params[0], (Long) params[1]);
                        return new Response(regMsg.contains("thành công"), regMsg);
                    case "UPLOAD_MATERIAL":
                        dao.logActivity("TEACHER", "UPLOAD_MATERIAL", "SUCCESS", "Class: " + params[0] + ", Title: " + params[1] + ", Path: " + params[2]);
                        return new Response(true, "Upload tài liệu thành công");
                    case "EXPORT_STUDENTS_XML":
                        List<SinhVien> expList = dao.getAllStudents();
                        String xmlStr = XmlUtil.exportStudentsToXml(expList);
                        return new Response(true, "Xuất XML thành công", xmlStr);
                    case "IMPORT_STUDENTS_XML":
                        String importXmlData = (String) params[0];
                        List<SinhVien> impList = XmlUtil.importStudentsFromXml(importXmlData);
                        
                        // Transaction-like insert inside Server
                        try (Connection conn = ServerDao.getConnection()) {
                            conn.setAutoCommit(false);
                            try {
                                for (SinhVien sv : impList) {
                                    boolean exists = false;
                                    try (PreparedStatement check = conn.prepareStatement("SELECT ma_sv FROM sinh_vien WHERE ma_sv = ?")) {
                                        check.setString(1, sv.getMaSV());
                                        try (ResultSet rs = check.executeQuery()) {
                                            if (rs.next()) exists = true;
                                        }
                                    }
                                    
                                    if (exists) {
                                        try (PreparedStatement ps = conn.prepareStatement("UPDATE sinh_vien SET ho_ten = ?, lop = ?, email = ?, sdt = ? WHERE ma_sv = ?")) {
                                            ps.setString(1, sv.getHoTen());
                                            ps.setString(2, sv.getLop());
                                            ps.setString(3, AesUtil.encrypt(sv.getEmail()));
                                            ps.setString(4, AesUtil.encrypt(sv.getSdt()));
                                            ps.setString(5, sv.getMaSV());
                                            ps.executeUpdate();
                                        }
                                    } else {
                                        long userId = -1;
                                        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, 'STUDENT')", Statement.RETURN_GENERATED_KEYS)) {
                                            ps.setString(1, sv.getMaSV().toLowerCase());
                                            ps.setString(2, com.example.dean12.desktop.PasswordUtil.encode("123")); // Default password
                                            ps.setString(3, sv.getEmail());
                                            ps.executeUpdate();
                                            try (ResultSet rs = ps.getGeneratedKeys()) {
                                                if (rs.next()) userId = rs.getLong(1);
                                            }
                                        }
                                        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO sinh_vien (ma_sv, ho_ten, lop, email, sdt, user_id) VALUES (?, ?, ?, ?, ?, ?)")) {
                                            ps.setString(1, sv.getMaSV());
                                            ps.setString(2, sv.getHoTen());
                                            ps.setString(3, sv.getLop());
                                            ps.setString(4, AesUtil.encrypt(sv.getEmail()));
                                            ps.setString(5, AesUtil.encrypt(sv.getSdt()));
                                            ps.setLong(6, userId);
                                            ps.executeUpdate();
                                        }
                                    }
                                }
                                conn.commit();
                                dao.logActivity("ADMIN", "IMPORT_STUDENTS_XML", "SUCCESS", "Imported " + impList.size() + " students.");
                                return new Response(true, "Nhập XML thành công! Đã xử lý " + impList.size() + " sinh viên.");
                            } catch (Exception ex) {
                                conn.rollback();
                                throw ex;
                            }
                        }
                    default:
                        return new Response(false, "Hành động không hợp lệ: " + action);
                }
            } catch (Exception e) {
                dao.logActivity("SYSTEM", action, "ERROR", e.getMessage());
                e.printStackTrace();
                return new Response(false, "Lỗi Server: " + e.getMessage());
            }
        }
    }
}

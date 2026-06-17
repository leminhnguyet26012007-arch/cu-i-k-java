package com.example.dean12.config;

import com.example.dean12.desktop.network.AesUtil;
import com.example.dean12.desktop.network.ServerDao;

import java.sql.*;
import java.util.*;

public final class SampleDataGenerator {

    private static final String[] HO = {
            "Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Huỳnh", "Phan", "Vũ", "Võ", "Đặng",
            "Bùi", "Đỗ", "Hồ", "Ngô", "Dương", "Lý", "Đinh", "Cao", "Trịnh", "Lưu"
    };
    private static final String[] TEN_DEM = {
            "Văn", "Thị", "Hữu", "Minh", "Quốc", "Gia", "Thanh", "Thu", "Xuân", "Ngọc",
            "Đức", "Anh", "Bảo", "Kim", "Hồng"
    };
    private static final String[] TEN = {
            "An", "Bình", "Châu", "Dũng", "Em", "Giang", "Hà", "Khang", "Linh", "Mai",
            "Nam", "Oanh", "Phúc", "Quân", "Sơn", "Tâm", "Uyên", "Vinh", "Yến", "Long"
    };
    private static final String[] LOP = {"CNTT-K65", "CNTT-K66", "CNTT-K67", "KT-K65", "KHMT-K65"};

    private SampleDataGenerator() {
    }

    public static void seedIfEmpty(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM sinh_vien")) {
            if (rs.next() && false && rs.getInt(1) >= 50) {
                System.out.println("[SampleData] Đã có " + rs.getInt(1) + " sinh viên — bỏ qua.");
                return;
            }
        }

        seedUsers(conn);
        seedTeachers(conn);
        seedStudents(conn, 60);
        seedCourses(conn);
        Map<String, Long> classes = seedClasses(conn);
        seedRegistrationsAndGrades(conn, classes);

        insertNotification(conn, "Khai giảng HK1 2025-2026",
                "Chào mừng sinh viên đến với hệ thống QLSV. Vui lòng đóng học phí và đăng ký tín chỉ đúng hạn.", "ALL", null);
        insertNotification(conn, "Lịch thi giữa kỳ",
                "Xem bảng điểm trên portal sau khi giảng viên công bố và khóa điểm.", "STUDENT", null);

        System.out.println("[SampleData] Hoàn tất: 60 SV, 5 GV, 12 môn học, lớp HP, đăng ký & điểm.");
    }

    private static void seedTeachers(Connection conn) throws SQLException {
        insertTeacher(conn, "GV01", "Nguyễn Văn An", "gv01", "gv01@school.com", "0901000001");
        insertTeacher(conn, "GV02", "Trần Thị Bình", "gv02", "gv02@school.com", "0901000002");
        insertTeacher(conn, "GV03", "Lê Hữu Cường", "gv03", "gv03@school.com", "0901000003");
        insertTeacher(conn, "GV04", "Phạm Minh Đức", "gv04", "gv04@school.com", "0901000004");
        insertTeacher(conn, "GV05", "Hoàng Thị Lan", "gv05", "gv05@school.com", "0901000005");
    }

    private static void seedUsers(Connection conn) throws SQLException {
        insertUser(conn, "admin", "admin@school.com", "ADMIN");
        for (int i = 1; i <= 5; i++) {
            String username = String.format("gv%02d", i);
            insertUser(conn, username, username + "@school.com", "TEACHER");
        }
        for (int i = 1; i <= 60; i++) {
            String username = String.format("sv%02d", i);
            insertUser(conn, username, username + "@school.com", "STUDENT");
        }
    }

    private static void insertUser(Connection conn, String username, String email, String role) throws SQLException {
        if (exists(conn, "SELECT 1 FROM users WHERE username = ?", username)) return;
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO users (username, password, email, role, locked) VALUES (?, ?, ?, ?, FALSE)")) {
            ps.setString(1, username);
            ps.setString(2, com.example.dean12.desktop.PasswordUtil.encode("123"));
            ps.setString(3, email);
            ps.setString(4, role);
            ps.executeUpdate();
        }
    }

    private static void seedStudents(Connection conn, int count) throws SQLException {
        for (int i = 1; i <= count; i++) {
            String maSV = String.format("SV%02d", i);
            String username = String.format("sv%02d", i);
            String ho = HO[(i - 1) % HO.length];
            String tenDem = TEN_DEM[(i * 3) % TEN_DEM.length];
            String ten = TEN[(i * 7) % TEN.length];
            String hoTen = ho + " " + tenDem + " " + ten;
            String lop = LOP[(i - 1) % LOP.length];
            String email = username + "@school.com";
            String sdt = String.format("09%09d", 100000000L + i);
            boolean paid = (i % 3 == 0);
            insertStudent(conn, maSV, hoTen, lop, username, email, sdt, paid);
        }
    }

    private static void seedCourses(Connection conn) throws SQLException {
        String[][] courses = {
                {"IT101", "Lập trình hướng đối tượng (Java)", "3"},
                {"IT102", "Công nghệ Web và lập trình HTML/CSS", "3"},
                {"IT103", "Cơ sở dữ liệu và ngôn ngữ SQL", "3"},
                {"IT104", "Mạng máy tính và truyền thông dữ liệu", "3"},
                {"IT105", "An toàn thông tin và bảo mật mạng", "3"},
                {"IT106", "Trí tuệ nhân tạo cơ bản", "3"},
                {"IT107", "Lập trình di động (Android)", "3"},
                {"MA101", "Giải tích 1", "4"},
                {"MA102", "Xác suất thống kê ứng dụng", "3"},
                {"EN101", "Tiếng Anh chuyên ngành Công nghệ thông tin", "2"},
                {"PHY101", "Vật lý đại cương A1", "3"},
                {"ECO101", "Kinh tế đại cương", "2"}
        };
        for (String[] c : courses) {
            insertCourse(conn, c[0], c[1], Integer.parseInt(c[2]));
        }
    }

    private static Map<String, Long> seedClasses(Connection conn) throws SQLException {
        Map<String, Long> map = new LinkedHashMap<>();
        String[][] data = {
                {"LHP-IT101-A", "P.301", "Thứ 2, Tiết 1-3", "IT101", "GV01"},
                {"LHP-IT101-B", "P.302", "Thứ 4, Tiết 1-3", "IT101", "GV01"},
                {"LHP-IT102-A", "P.401", "Thứ 3, Tiết 4-6", "IT102", "GV01"},
                {"LHP-IT102-B", "P.402", "Thứ 5, Tiết 4-6", "IT102", "GV03"},
                {"LHP-IT103-A", "P.205", "Thứ 2, Tiết 7-9", "IT103", "GV02"},
                {"LHP-IT103-B", "P.206", "Thứ 6, Tiết 1-3", "IT103", "GV02"},
                {"LHP-IT104-A", "LAB-A1", "Thứ 3, Tiết 1-3", "IT104", "GV03"},
                {"LHP-IT105-A", "P.501", "Thứ 4, Tiết 4-6", "IT105", "GV04"},
                {"LHP-IT106-A", "P.502", "Thứ 5, Tiết 1-3", "IT106", "GV04"},
                {"LHP-IT107-A", "LAB-B2", "Thứ 6, Tiết 4-6", "IT107", "GV05"},
                {"LHP-MA101-A", "P.101", "Thứ 2, Tiết 4-6", "MA101", "GV02"},
                {"LHP-MA102-A", "P.102", "Thứ 3, Tiết 7-9", "MA102", "GV02"},
                {"LHP-EN101-A", "P.601", "Thứ 4, Tiết 1-3", "EN101", "GV05"},
                {"LHP-PHY101-A", "P.103", "Thứ 5, Tiết 7-9", "PHY101", "GV03"},
                {"LHP-ECO101-A", "P.104", "Thứ 7, Tiết 1-3", "ECO101", "GV04"}
        };
        for (String[] row : data) {
            map.put(row[0], insertClass(conn, row[0], row[1], row[2], row[3], row[4]));
        }
        return map;
    }

    private static void seedRegistrationsAndGrades(Connection conn, Map<String, Long> classes) throws SQLException {
        List<Long> lhpIds = new ArrayList<>(classes.values());
        String[] keys = classes.keySet().toArray(new String[0]);
        Random rnd = new Random(42);

        for (int i = 1; i <= 60; i++) {
            String maSV = String.format("SV%02d", i);
            int numClasses = 4 + (i % 3);
            Set<Integer> picked = new HashSet<>();
            while (picked.size() < numClasses) {
                picked.add(rnd.nextInt(keys.length));
            }
            int g = 0;
            for (int idx : picked) {
                long lhpId = lhpIds.get(idx);
                registerStudentClass(conn, maSV, lhpId);
                double qt = 5.5 + (i + g) % 45 / 10.0;
                double thi = 5.0 + (i * 2 + g) % 50 / 10.0;
                if (qt > 10) qt = 10;
                if (thi > 10) thi = 10;
                insertGrade(conn, maSV, lhpId, qt, thi);
                g++;
            }
        }
    }

    // --- JDBC helpers (mirror ServerDao) ---

    private static long findUserId(Connection conn, String username) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        return -1;
    }

    private static void insertTeacher(Connection conn, String maGV, String hoTen, String username,
                                      String email, String sdt) throws SQLException {
        if (exists(conn, "SELECT 1 FROM giang_vien WHERE ma_gv = ?", maGV)) return;
        long userId = findUserId(conn, username);
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO giang_vien (ma_gv, ho_ten, email, sdt, user_id) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, maGV);
            ps.setString(2, hoTen);
            ps.setString(3, AesUtil.encrypt(email));
            ps.setString(4, AesUtil.encrypt(sdt));
            if (userId > 0) ps.setLong(5, userId);
            else ps.setNull(5, Types.BIGINT);
            ps.executeUpdate();
        }
    }

    private static void insertStudent(Connection conn, String maSV, String hoTen, String lop, String username,
                                      String email, String sdt, boolean tuitionPaid) throws SQLException {
        if (exists(conn, "SELECT 1 FROM sinh_vien WHERE ma_sv = ?", maSV)) return;
        long userId = findUserId(conn, username);
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO sinh_vien (ma_sv, ho_ten, lop, email, sdt, user_id, tuition_paid) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, maSV);
            ps.setString(2, hoTen);
            ps.setString(3, lop);
            ps.setString(4, AesUtil.encrypt(email));
            ps.setString(5, AesUtil.encrypt(sdt));
            if (userId > 0) ps.setLong(6, userId);
            else ps.setNull(6, Types.BIGINT);
            ps.setBoolean(7, tuitionPaid);
            ps.executeUpdate();
        }
    }

    private static void insertCourse(Connection conn, String maMH, String tenMH, int soTC) throws SQLException {
        if (exists(conn, "SELECT 1 FROM mon_hoc WHERE ma_mh = ?", maMH)) return;
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO mon_hoc (ma_mh, ten_mh, so_tin_chi) VALUES (?, ?, ?)")) {
            ps.setString(1, maMH);
            ps.setString(2, tenMH);
            ps.setInt(3, soTC);
            ps.executeUpdate();
        }
    }

    private static long insertClass(Connection conn, String maLhp, String phong, String lich,
                                    String maMH, String maGV) throws SQLException {
        try (PreparedStatement check = conn.prepareStatement("SELECT id FROM lop_hoc_phan WHERE ma_lhp = ?")) {
            check.setString(1, maLhp);
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO lop_hoc_phan (ma_lhp, phong_hoc, lich_hoc, si_so_toi_da, si_so_hien_tai, mon_hoc_ma_mh, giang_vien_ma_gv) VALUES (?, ?, ?, 45, 0, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, maLhp);
            ps.setString(2, phong);
            ps.setString(3, lich);
            ps.setString(4, maMH);
            ps.setString(5, maGV);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        }
        return -1;
    }

    private static void registerStudentClass(Connection conn, String maSV, long lhpId) throws SQLException {
        if (lhpId <= 0 || isRegistered(conn, maSV, lhpId)) return;
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO dang_ky_hoc (sinh_vien_ma_sv, lop_hoc_phan_id) VALUES (?, ?)")) {
            ps.setString(1, maSV);
            ps.setLong(2, lhpId);
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE lop_hoc_phan SET si_so_hien_tai = si_so_hien_tai + 1 WHERE id = ?")) {
            ps.setLong(1, lhpId);
            ps.executeUpdate();
        }
        ensureGrades(conn, lhpId);
    }

    private static void ensureGrades(Connection conn, long lhpId) throws SQLException {
        String sqlMissing = "SELECT dk.sinh_vien_ma_sv FROM dang_ky_hoc dk " +
                "LEFT JOIN diem d ON dk.sinh_vien_ma_sv = d.sinh_vien_ma_sv AND d.lop_hoc_phan_id = dk.lop_hoc_phan_id " +
                "WHERE dk.lop_hoc_phan_id = ? AND d.id IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(sqlMissing)) {
            ps.setLong(1, lhpId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try (PreparedStatement pi = conn.prepareStatement(
                            "INSERT INTO diem (sinh_vien_ma_sv, lop_hoc_phan_id, diemqt, diem_thi, diem_tong_ket, locked) VALUES (?, ?, 0, 0, 0, FALSE)")) {
                        pi.setString(1, rs.getString(1));
                        pi.setLong(2, lhpId);
                        pi.executeUpdate();
                    }
                }
            }
        }
    }

    private static void insertGrade(Connection conn, String maSV, long lhpId, double qt, double thi) throws SQLException {
        ensureGrades(conn, lhpId);
        double tong = qt * 0.4 + thi * 0.6;
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE diem SET diemqt = ?, diem_thi = ?, diem_tong_ket = ? WHERE sinh_vien_ma_sv = ? AND lop_hoc_phan_id = ?")) {
            ps.setDouble(1, qt);
            ps.setDouble(2, thi);
            ps.setDouble(3, tong);
            ps.setString(4, maSV);
            ps.setLong(5, lhpId);
            ps.executeUpdate();
        }
    }

    private static void insertNotification(Connection conn, String title, String content, String role, String targetId)
            throws SQLException {
        if (exists(conn, "SELECT 1 FROM notifications WHERE title = ?", title)) return;
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO notifications (title, content, target_role, target_id, created_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)")) {
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setString(3, role);
            ps.setString(4, targetId);
            ps.executeUpdate();
        }
    }

    private static boolean exists(Connection conn, String sql, String param) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static boolean isRegistered(Connection conn, String maSV, long lhpId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM dang_ky_hoc WHERE sinh_vien_ma_sv = ? AND lop_hoc_phan_id = ?")) {
            ps.setString(1, maSV);
            ps.setLong(2, lhpId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static void run() {
        try (Connection conn = ServerDao.getConnection()) {
            boolean oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                seedIfEmpty(conn);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(oldAutoCommit);
            }
        } catch (SQLException e) {
            System.err.println("[SampleData] " + e.getMessage());
            e.printStackTrace();
        }
    }
}

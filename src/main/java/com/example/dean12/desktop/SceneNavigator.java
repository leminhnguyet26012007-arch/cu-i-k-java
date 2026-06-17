package com.example.dean12.desktop;

import com.example.dean12.model.LopHocPhan;
import com.example.dean12.model.SinhVien;
import com.example.dean12.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SceneNavigator {

    private final Stage primaryStage;
    private final DesktopDao dao;
    private User currentUser;
    private SinhVien currentStudent;
    private com.example.dean12.model.GiangVien currentTeacher;

    public SceneNavigator(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.dao = new DesktopDao();
    }

    public DesktopDao getDao() { return dao; }
    public User getCurrentUser() { return currentUser; }
    public Stage getPrimaryStage() { return primaryStage; }
    public SinhVien getCurrentStudent() { return currentStudent; }
    public com.example.dean12.model.GiangVien getCurrentTeacher() { return currentTeacher; }

    private Scene createStyledScene(javafx.scene.Parent root) {
        Scene scene = new Scene(root, 1024, 768);
        try {
            String cssPath = getClass().getResource("/com/example/dean12/desktop/style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.err.println("[UI] Failed to load stylesheet: " + e.getMessage());
        }
        return scene;
    }

    public void showLogin() {
        this.currentUser = null;
        this.currentStudent = null;
        this.currentTeacher = null;
        Scene scene = LoginSceneFactory.createLoginScene(this);
        primaryStage.setTitle("QLSV - Đăng nhập");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public void showDashboard(User user) {
        this.currentUser = user;
        if (user == null || user.getRole() == null) return;
        switch (user.getRole()) {
            case "ADMIN" -> showAdminDashboard();
            case "TEACHER" -> showTeacherDashboard();
            case "STUDENT" -> showStudentDashboard();
            default -> showAdminDashboard();
        }
    }

    private Button navButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.getStyleClass().add("nav-button");
        if (active) btn.getStyleClass().add("nav-button-active");
        return btn;
    }

    // --- ADMIN ---

    private BorderPane adminLayout(String activeMenu) {
        BorderPane root = new BorderPane();
        Label title = new Label("HỆ THỐNG QUẢN LÝ - ADMIN PORTAL");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Label lblUser = new Label("Tài khoản: " + currentUser.getUsername() + " | ADMIN");
        lblUser.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");
        HBox top = new HBox(20, title, lblUser);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(15, 20, 15, 20));
        top.setStyle("-fx-background-color: #ffffff; -fx-border-color: transparent transparent #e2e8f0 transparent; -fx-border-width: 1;");
        root.setTop(top);

        VBox sidebar = new VBox(6);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(240);
        Label lblMenu = new Label("DANH MỤC CHỨC NĂNG");
        lblMenu.setStyle("-fx-text-fill: #475569; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 0 0 10 10;");

        Button btnHome = navButton("📊 Tổng quan", "HOME".equals(activeMenu));
        Button btnStudents = navButton("👨‍🎓 Sinh viên", "STUDENTS".equals(activeMenu));
        Button btnCourses = navButton("📚 Môn học", "COURSES".equals(activeMenu));
        Button btnClasses = navButton("🏫 Lớp học phần", "CLASSES".equals(activeMenu));
        Button btnConfig = navButton("⚙️ Cấu hình", "CONFIG".equals(activeMenu));
        Button btnUsers = navButton("🔐 Tài khoản", "USERS".equals(activeMenu));
        Button btnNoti = navButton("🔔 Thông báo", "NOTI".equals(activeMenu));
        Button btnLogout = new Button("🚪 Đăng xuất");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.getStyleClass().add("nav-button");
        btnLogout.setStyle("-fx-text-fill: #f87171; -fx-font-weight: bold;");

        btnHome.setOnAction(e -> showAdminDashboard());
        btnStudents.setOnAction(e -> showAdminStudents());
        btnCourses.setOnAction(e -> showAdminCourses());
        btnClasses.setOnAction(e -> showAdminClasses());
        btnConfig.setOnAction(e -> showAdminSystemConfig());
        btnUsers.setOnAction(e -> showAdminUsers());
        btnNoti.setOnAction(e -> showAdminNotifications());
        btnLogout.setOnAction(e -> showLogin());

        sidebar.getChildren().addAll(lblMenu, btnHome, btnStudents, btnCourses, btnClasses,
                new Label(""), btnConfig, btnUsers, btnNoti, new Label(""), btnLogout);
        root.setLeft(sidebar);
        return root;
    }

    public void showAdminDashboard() {
        BorderPane root = adminLayout("HOME");
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        Label welcome = new Label("BẢNG ĐIỀU KHIỂN ADMIN");
        welcome.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        content.getChildren().addAll(welcome, AdminAdvancedScenes.createReportsView(this));
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        root.setCenter(scroll);
        primaryStage.setScene(createStyledScene(root));
        primaryStage.setTitle("QLSV - Admin");
    }

    public void showAdminStudents() {
        BorderPane root = adminLayout("STUDENTS");
        root.setCenter(AdminScenes.createStudentManagementView(this));
        primaryStage.setScene(createStyledScene(root));
    }

    public void showAdminCourses() {
        BorderPane root = adminLayout("COURSES");
        root.setCenter(AdminScenes.createCourseManagementView(this));
        primaryStage.setScene(createStyledScene(root));
    }

    public void showAdminClasses() {
        BorderPane root = adminLayout("CLASSES");
        root.setCenter(AdminScenes.createClassManagementView(this));
        primaryStage.setScene(createStyledScene(root));
    }

    public void showAdminSystemConfig() {
        BorderPane root = adminLayout("CONFIG");
        VBox container = new VBox(20, AdminAdvancedScenes.createSystemConfigView(this));
        container.setPadding(new Insets(10));
        ScrollPane scroll = new ScrollPane(container);
        scroll.setFitToWidth(true);
        root.setCenter(scroll);
        primaryStage.setScene(createStyledScene(root));
    }

    public void showAdminNotifications() {
        BorderPane root = adminLayout("NOTI");
        root.setCenter(AdminAdvancedScenes.createNotificationCenterView(this));
        primaryStage.setScene(createStyledScene(root));
    }

    public void showAdminUsers() {
        BorderPane root = adminLayout("USERS");
        root.setCenter(AdminAdvancedScenes.createUserManagementView(this));
        primaryStage.setScene(createStyledScene(root));
    }

    // --- TEACHER ---

    private BorderPane teacherLayout(String activeMenu) {
        if (currentTeacher == null && currentUser != null) {
            currentTeacher = dao.getTeacherByUsername(currentUser.getUsername());
        }
        BorderPane root = new BorderPane();
        String name = currentTeacher != null ? currentTeacher.getHoTen() : currentUser.getUsername();
        Label title = new Label("GIẢNG VIÊN PORTAL");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Label lblUser = new Label(name + " (" + currentUser.getUsername() + ")");
        lblUser.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");
        HBox top = new HBox(20, title, lblUser);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(15, 20, 15, 20));
        top.setStyle("-fx-background-color: #ffffff; -fx-border-color: transparent transparent #e2e8f0 transparent; -fx-border-width: 1;");
        root.setTop(top);

        VBox sidebar = new VBox(6);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(240);
        Label lblMenu = new Label("CHỨC NĂNG GIẢNG VIÊN");
        lblMenu.setStyle("-fx-text-fill: #475569; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 0 0 10 10;");

        Button btnHome = navButton("📊 Tổng quan", "HOME".equals(activeMenu));
        Button btnClasses = navButton("🏫 Lớp phụ trách", "CLASSES".equals(activeMenu));
        Button btnGrades = navButton("📝 Quản lý điểm", "GRADES".equals(activeMenu));
        Button btnProfile = navButton("👤 Hồ sơ", "PROFILE".equals(activeMenu));
        Button btnLogout = new Button("🚪 Đăng xuất");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.getStyleClass().add("nav-button");
        btnLogout.setStyle("-fx-text-fill: #f87171; -fx-font-weight: bold;");

        btnHome.setOnAction(e -> showTeacherDashboard());
        btnClasses.setOnAction(e -> showTeacherClasses());
        btnGrades.setOnAction(e -> showTeacherGrades());
        btnProfile.setOnAction(e -> showTeacherProfile());
        btnLogout.setOnAction(e -> showLogin());

        sidebar.getChildren().addAll(lblMenu, btnHome, btnClasses, btnGrades, btnProfile, new Label(""), btnLogout);
        root.setLeft(sidebar);
        return root;
    }

    public void showTeacherDashboard() {
        currentTeacher = dao.getTeacherByUsername(currentUser.getUsername());
        BorderPane root = teacherLayout("HOME");
        ScrollPane scroll = new ScrollPane(TeacherScenes.createHomeContent(this, currentUser.getUsername()));
        scroll.setFitToWidth(true);
        root.setCenter(scroll);
        primaryStage.setScene(createStyledScene(root));
        primaryStage.setTitle("QLSV - Giảng viên");
    }

    public void showTeacherClasses() {
        BorderPane root = teacherLayout("CLASSES");
        ScrollPane scroll = new ScrollPane(TeacherScenes.createClassesContent(this, currentUser.getUsername()));
        scroll.setFitToWidth(true);
        root.setCenter(scroll);
        primaryStage.setScene(createStyledScene(root));
    }

    public void showTeacherGrades() {
        BorderPane root = teacherLayout("GRADES");
        ScrollPane scroll = new ScrollPane(TeacherScenes.createGradesHubContent(this, currentUser.getUsername()));
        scroll.setFitToWidth(true);
        root.setCenter(scroll);
        primaryStage.setScene(createStyledScene(root));
    }

    public void showTeacherProfile() {
        if (currentTeacher == null) {
            currentTeacher = dao.getTeacherByUsername(currentUser.getUsername());
        }
        BorderPane root = teacherLayout("PROFILE");
        ScrollPane scroll = new ScrollPane(TeacherAdvancedScenes.createProfileView(currentTeacher));
        scroll.setFitToWidth(true);
        root.setCenter(scroll);
        primaryStage.setScene(createStyledScene(root));
    }

    public void showTeacherAttendance(LopHocPhan lhp) {
        BorderPane root = teacherLayout("CLASSES");
        root.setCenter(TeacherScenes.createAttendanceView(this, lhp));
        primaryStage.setScene(createStyledScene(root));
    }

    public void showTeacherGrading(LopHocPhan lhp) {
        BorderPane root = teacherLayout("CLASSES");
        root.setCenter(TeacherScenes.createGradingView(this, lhp));
        primaryStage.setScene(createStyledScene(root));
    }

    public void showTeacherClassManager(LopHocPhan lhp) {
        BorderPane root = teacherLayout("CLASSES");
        ScrollPane scroll = new ScrollPane(TeacherAdvancedScenes.createClassManagerView(this, lhp));
        scroll.setFitToWidth(true);
        root.setCenter(scroll);
        primaryStage.setScene(createStyledScene(root));
    }

    // --- STUDENT ---

    private BorderPane studentLayout(String activeMenu) {
        if (currentStudent == null && currentUser != null) {
            currentStudent = dao.getStudentByUsername(currentUser.getUsername());
        }
        BorderPane root = new BorderPane();
        String name = currentStudent != null ? currentStudent.getHoTen() : currentUser.getUsername();
        String ma = currentStudent != null ? currentStudent.getMaSV() : "";
        Label title = new Label("SINH VIÊN PORTAL");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Label lblUser = new Label(name + " (" + ma + ")");
        lblUser.setStyle("-fx-text-fill: #64748b; -fx-font-weight: bold;");
        HBox top = new HBox(20, title, lblUser);
        top.setAlignment(Pos.CENTER_LEFT);
        top.setPadding(new Insets(15, 20, 15, 20));
        top.setStyle("-fx-background-color: #ffffff; -fx-border-color: transparent transparent #e2e8f0 transparent; -fx-border-width: 1;");
        root.setTop(top);

        VBox sidebar = new VBox(6);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(240);
        Label lblMenu = new Label("CHỨC NĂNG SINH VIÊN");
        lblMenu.setStyle("-fx-text-fill: #475569; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 0 0 10 10;");

        Button btnHome = navButton("📊 Tổng quan", "HOME".equals(activeMenu));
        Button btnSchedule = navButton("📅 Thời khóa biểu", "SCHEDULE".equals(activeMenu));
        Button btnGrades = navButton("📝 Điểm số", "GRADES".equals(activeMenu));
        Button btnReg = navButton("➕ Đăng ký tín chỉ", "REG".equals(activeMenu));
        Button btnTuition = navButton("💳 Học phí", "TUITION".equals(activeMenu));
        Button btnFeedback = navButton("📨 Đơn từ / Phản hồi", "FEEDBACK".equals(activeMenu));
        Button btnNoti = navButton("🔔 Thông báo", "NOTI".equals(activeMenu));
        Button btnProfile = navButton("👤 Hồ sơ", "PROFILE".equals(activeMenu));
        Button btnLogout = new Button("🚪 Đăng xuất");
        btnLogout.setMaxWidth(Double.MAX_VALUE);
        btnLogout.getStyleClass().add("nav-button");
        btnLogout.setStyle("-fx-text-fill: #f87171; -fx-font-weight: bold;");

        btnHome.setOnAction(e -> showStudentDashboard());
        btnSchedule.setOnAction(e -> { if (currentStudent != null) showStudentSchedule(currentStudent); });
        btnGrades.setOnAction(e -> { if (currentStudent != null) showStudentGrades(currentStudent); });
        btnReg.setOnAction(e -> { if (currentStudent != null) showStudentRegistration(currentStudent); });
        btnTuition.setOnAction(e -> { if (currentStudent != null) showStudentTuition(currentStudent); });
        btnFeedback.setOnAction(e -> { if (currentStudent != null) showStudentFeedback(currentStudent); });
        btnNoti.setOnAction(e -> { if (currentStudent != null) showStudentNotifications(currentStudent); });
        btnProfile.setOnAction(e -> { if (currentStudent != null) showStudentProfile(currentStudent); });
        btnLogout.setOnAction(e -> showLogin());

        sidebar.getChildren().addAll(lblMenu, btnHome, btnSchedule, btnGrades, btnReg, btnTuition,
                btnFeedback, btnNoti, btnProfile, new Label(""), btnLogout);
        root.setLeft(sidebar);
        return root;
    }

    public void showStudentDashboard() {
        currentStudent = dao.getStudentByUsername(currentUser.getUsername());
        if (currentStudent == null) {
            BorderPane err = new BorderPane(new Label("Không tìm thấy hồ sơ sinh viên. Chạy lại ServerMain để nạp dữ liệu mẫu."));
            primaryStage.setScene(createStyledScene(err));
            return;
        }
        BorderPane root = studentLayout("HOME");
        ScrollPane scroll = new ScrollPane(StudentScenes.createHomeContent(this, currentStudent));
        scroll.setFitToWidth(true);
        root.setCenter(scroll);
        primaryStage.setScene(createStyledScene(root));
        primaryStage.setTitle("QLSV - Sinh viên");
    }

    public void showStudentSchedule(SinhVien sv) {
        currentStudent = sv;
        BorderPane root = studentLayout("SCHEDULE");
        root.setCenter(StudentScenes.createScheduleView(this, sv));
        primaryStage.setScene(createStyledScene(root));
    }

    public void showStudentGrades(SinhVien sv) {
        currentStudent = sv;
        BorderPane root = studentLayout("GRADES");
        root.setCenter(StudentScenes.createGradesView(this, sv));
        primaryStage.setScene(createStyledScene(root));
    }

    public void showStudentRegistration(SinhVien sv) {
        currentStudent = sv;
        BorderPane root = studentLayout("REG");
        ScrollPane scroll = new ScrollPane(StudentAdvancedScenes.createRegistrationView(this, sv));
        scroll.setFitToWidth(true);
        root.setCenter(scroll);
        primaryStage.setScene(createStyledScene(root));
    }

    public void showStudentTuition(SinhVien sv) {
        currentStudent = sv;
        BorderPane root = studentLayout("TUITION");
        ScrollPane scroll = new ScrollPane(StudentAdvancedScenes.createTuitionView(this, sv));
        scroll.setFitToWidth(true);
        root.setCenter(scroll);
        primaryStage.setScene(createStyledScene(root));
    }

    public void showStudentFeedback(SinhVien sv) {
        currentStudent = sv;
        BorderPane root = studentLayout("FEEDBACK");
        root.setCenter(StudentScenes.createFeedbackView(this, sv));
        primaryStage.setScene(createStyledScene(root));
    }

    public void showStudentNotifications(SinhVien sv) {
        currentStudent = sv;
        BorderPane root = studentLayout("NOTI");
        ScrollPane scroll = new ScrollPane(StudentAdvancedScenes.createNotificationView(sv));
        scroll.setFitToWidth(true);
        root.setCenter(scroll);
        primaryStage.setScene(createStyledScene(root));
    }

    public void showStudentProfile(SinhVien sv) {
        currentStudent = sv;
        BorderPane root = studentLayout("PROFILE");
        ScrollPane scroll = new ScrollPane(StudentAdvancedScenes.createProfileView(sv));
        scroll.setFitToWidth(true);
        root.setCenter(scroll);
        primaryStage.setScene(createStyledScene(root));
    }
}

package com.example.dean12.desktop;

import com.example.dean12.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class StudentScenes {

    public static VBox createHomeContent(SceneNavigator navigator, SinhVien sv) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));

        Label welcome = new Label("Xin chào, " + sv.getHoTen());
        welcome.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        List<DangKyHoc> schedule = navigator.getDao().getStudentSchedule(sv.getMaSV());
        List<Diem> grades = navigator.getDao().getStudentGrades(sv.getMaSV());
        String[] tuition = navigator.getDao().getTuitionInfo(sv.getMaSV());

        double[] gpaSummary = navigator.getDao().getStudentGpaSummary(sv.getMaSV());
        double gpa = gpaSummary[0];
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        HBox stats = new HBox(20);
        stats.setAlignment(Pos.CENTER_LEFT);
        stats.getChildren().addAll(
                statCard(String.valueOf(schedule.size()), "Môn đã đăng ký", "stat-card-blue"),
                statCard(String.format("%.2f", gpa), "Điểm TB", "stat-card-purple"),
                statCard("true".equals(tuition[3]) ? "Đã đóng" : "Chưa đóng",
                        "Học phí", "true".equals(tuition[3]) ? "stat-card-amber" : "stat-card-pink"),
                statCard(nf.format(Long.parseLong(tuition[2])) + " đ", "Tổng HP", "stat-card-amber")
        );

        Label hint = new Label("Dùng menu bên trái: thời khóa biểu, điểm, đăng ký tín chỉ, đóng học phí...");
        hint.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");

        content.getChildren().addAll(welcome, stats, hint);
        return content;
    }

    private static VBox statCard(String value, String label, String style) {
        VBox card = new VBox(6);
        card.getStyleClass().addAll("stat-card", style);
        Label v = new Label(value);
        v.getStyleClass().add("stat-value");
        v.setStyle("-fx-font-size: 24px;");
        Label n = new Label(label);
        n.getStyleClass().add("stat-title");
        card.getChildren().addAll(v, n);
        return card;
    }

    public static BorderPane createScheduleView(SceneNavigator navigator, SinhVien sv) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        Label title = new Label("Thời khóa biểu — " + sv.getHoTen());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        TableView<DangKyHoc> table = new TableView<>();
        table.getStyleClass().add("table-view");
        TableColumn<DangKyHoc, String> colMH = new TableColumn<>("Môn học");
        colMH.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLopHocPhan().getMonHoc().getTenMH()));
        TableColumn<DangKyHoc, String> colLHP = new TableColumn<>("Mã lớp");
        colLHP.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLopHocPhan().getMaLhp()));
        TableColumn<DangKyHoc, String> colGV = new TableColumn<>("Giảng viên");
        colGV.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getLopHocPhan().getGiangVien() != null ? c.getValue().getLopHocPhan().getGiangVien().getHoTen() : ""));
        TableColumn<DangKyHoc, String> colTime = new TableColumn<>("Lịch học");
        colTime.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLopHocPhan().getLichHoc()));
        TableColumn<DangKyHoc, String> colRoom = new TableColumn<>("Phòng");
        colRoom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLopHocPhan().getPhongHoc()));
        table.getColumns().addAll(colMH, colLHP, colGV, colTime, colRoom);
        table.setItems(FXCollections.observableArrayList(navigator.getDao().getStudentSchedule(sv.getMaSV())));

        VBox box = new VBox(15, title, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        root.setCenter(box);
        return root;
    }

    public static BorderPane createGradesView(SceneNavigator navigator, SinhVien sv) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("BẢNG ĐIỂM — " + sv.getHoTen() + " (" + sv.getMaSV() + ")");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        double[] summary = navigator.getDao().getStudentGpaSummary(sv.getMaSV());
        HBox stats = new HBox(20);
        stats.getChildren().addAll(
                statCard(String.format("%.2f", summary[0]), "Điểm TB", "stat-card-blue"),
                statCard(String.valueOf((int) summary[1]), "Số môn", "stat-card-purple"),
                statCard(String.valueOf((int) summary[2]), "Môn đạt (≥4.0)", "stat-card-amber")
        );

        TableView<Diem> table = new TableView<>();
        table.getStyleClass().add("table-view");

        TableColumn<Diem, String> colMH = new TableColumn<>("Tên môn học");
        colMH.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLopHocPhan().getMonHoc().getTenMH()));
        colMH.setPrefWidth(280);

        TableColumn<Diem, String> colLHP = new TableColumn<>("Lớp HP");
        colLHP.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLopHocPhan().getMaLhp()));

        TableColumn<Diem, String> colTC = new TableColumn<>("TC");
        colTC.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().getLopHocPhan().getMonHoc().getSoTinChi())));

        TableColumn<Diem, String> colQT = new TableColumn<>("QT (40%)");
        colQT.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.1f", c.getValue().getDiemQT())));

        TableColumn<Diem, String> colThi = new TableColumn<>("Thi (60%)");
        colThi.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.1f", c.getValue().getDiemThi())));

        TableColumn<Diem, String> colTK = new TableColumn<>("Tổng kết");
        colTK.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getDiemTongKet())));

        TableColumn<Diem, String> colChu = new TableColumn<>("Điểm chữ");
        colChu.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDiemChu() != null ? c.getValue().getDiemChu() : ""));

        TableColumn<Diem, String> colTT = new TableColumn<>("Công bố");
        colTT.setCellValueFactory(c -> new SimpleStringProperty(
                Boolean.TRUE.equals(c.getValue().getLocked()) ? "Đã khóa (chính thức)" : "Chưa khóa"));

        table.getColumns().addAll(colMH, colLHP, colTC, colQT, colThi, colTK, colChu, colTT);
        table.setItems(FXCollections.observableArrayList(navigator.getDao().getStudentGrades(sv.getMaSV())));

        Label note = new Label("Điểm đã khóa là điểm chính thức do giảng viên công bố. Điểm chưa khóa có thể thay đổi.");
        note.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");
        note.setWrapText(true);

        VBox box = new VBox(15, title, stats, table, note);
        VBox.setVgrow(table, Priority.ALWAYS);
        root.setCenter(box);
        return root;
    }

    public static BorderPane createFeedbackView(SceneNavigator navigator, SinhVien sv) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        Label title = new Label("Gửi phản hồi / đơn từ");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        VBox form = new VBox(15);
        form.setPadding(new Insets(10));
        form.setMaxWidth(600);

        ComboBox<String> cbLoai = new ComboBox<>();
        cbLoai.getItems().addAll("Đơn xin nghỉ học", "Đơn xin phúc khảo", "Góp ý chất lượng", "Khác");
        cbLoai.getStyleClass().add("combo-box");
        TextField txtTieuDe = new TextField();
        txtTieuDe.setPromptText("Tiêu đề");
        txtTieuDe.getStyleClass().add("text-field");
        TextArea txtNoiDung = new TextArea();
        txtNoiDung.setPromptText("Nội dung chi tiết...");
        txtNoiDung.setPrefRowCount(6);
        txtNoiDung.getStyleClass().add("text-field");

        Button btnGui = new Button("Gửi đơn");
        btnGui.getStyleClass().add("btn-primary");
        btnGui.setOnAction(e -> {
            if (cbLoai.getValue() == null || txtTieuDe.getText().isBlank()) {
                new Alert(Alert.AlertType.ERROR, "Vui lòng nhập đủ thông tin!").show();
                return;
            }
            Feedback fb = new Feedback();
            fb.setSinhVien(sv);
            fb.setLoaiDon(cbLoai.getValue());
            fb.setTieuDe(txtTieuDe.getText());
            fb.setNoiDung(txtNoiDung.getText());
            navigator.getDao().createFeedback(fb);
            new Alert(Alert.AlertType.INFORMATION, "Gửi thành công!").show();
            navigator.showStudentDashboard();
        });

        form.getChildren().addAll(new Label("Loại đơn:"), cbLoai, new Label("Tiêu đề:"), txtTieuDe,
                new Label("Nội dung:"), txtNoiDung, btnGui);
        root.setTop(title);
        root.setCenter(form);
        return root;
    }
}

package com.example.dean12.desktop;

import com.example.dean12.model.SinhVien;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class StudentAdvancedScenes {

    public static VBox createTuitionView(SceneNavigator navigator, SinhVien sv) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(25));

        Label title = new Label("Thanh toán học phí");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        String[] info = navigator.getDao().getTuitionInfo(sv.getMaSV());
        int credits = Integer.parseInt(info[0]);
        long rate = Long.parseLong(info[1]);
        long total = Long.parseLong(info[2]);
        boolean paid = "true".equals(info[3]);

        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(12);
        grid.addRow(0, label("Sinh viên:"), value(sv.getMaSV() + " — " + sv.getHoTen()));
        grid.addRow(1, label("Lớp:"), value(sv.getLop()));
        grid.addRow(2, label("Tín chỉ đã đăng ký:"), value(String.valueOf(credits)));
        grid.addRow(3, label("Đơn giá / tín chỉ:"), value(nf.format(rate) + " VNĐ"));
        grid.addRow(4, label("Tổng học phí:"), value(nf.format(total) + " VNĐ"));
        grid.addRow(5, label("Trạng thái:"), value(paid ? "✅ ĐÃ ĐÓNG" : "❌ CHƯA ĐÓNG"));

        Label note = new Label("Lưu ý: Cần đóng học phí trước khi đăng ký thêm lớp học phần mới.");
        note.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13px;");

        ComboBox<String> cbMethod = new ComboBox<>();
        cbMethod.getItems().addAll("Chuyển khoản ngân hàng", "Ví điện tử", "Tiền mặt tại phòng TC");
        cbMethod.setValue("Chuyển khoản ngân hàng");
        cbMethod.getStyleClass().add("combo-box");
        cbMethod.setMaxWidth(320);

        Label lblStatus = new Label(paid ? "Đã thanh toán đủ học phí kỳ này." : "Chưa thanh toán — vui lòng hoàn tất.");
        lblStatus.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + (paid ? "#16a34a" : "#dc2626") + ";");

        Button btnPay = new Button("Xác nhận thanh toán");
        btnPay.getStyleClass().add("btn-primary");
        btnPay.setDisable(paid || total <= 0);
        btnPay.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Xác nhận");
            confirm.setHeaderText("Thanh toán " + nf.format(total) + " VNĐ");
            confirm.setContentText("Phương thức: " + cbMethod.getValue() + "\nBạn xác nhận đã thanh toán?");
            confirm.showAndWait().ifPresent(btn -> {
                if (btn == ButtonType.OK) {
                    navigator.getDao().payTuition(sv.getMaSV());
                    sv.setTuitionPaid(true);
                    lblStatus.setText("Đã thanh toán thành công!");
                    lblStatus.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #16a34a;");
                    btnPay.setDisable(true);
                    showReceipt(sv, total, cbMethod.getValue());
                }
            });
        });

        Button btnRefresh = new Button("Làm mới");
        btnRefresh.getStyleClass().add("btn-success");
        btnRefresh.setOnAction(e -> navigator.showStudentTuition(sv));

        HBox actions = new HBox(12, btnPay, btnRefresh);
        actions.setAlignment(Pos.CENTER_LEFT);

        root.getChildren().addAll(title, grid, note, new Label("Phương thức thanh toán:"), cbMethod, lblStatus, actions);
        return root;
    }

    private static void showReceipt(SinhVien sv, long amount, String method) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Biên lai học phí");
        alert.setHeaderText("Thanh toán thành công");
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        Text content = new Text(
                "Mã SV: " + sv.getMaSV() + "\n" +
                "Họ tên: " + sv.getHoTen() + "\n" +
                "Số tiền: " + nf.format(amount) + " VNĐ\n" +
                "Phương thức: " + method + "\n" +
                "Thời gian: " + java.time.LocalDateTime.now().toString().replace("T", " ")
        );
        alert.getDialogPane().setContent(content);
        alert.show();
    }

    private static Label label(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569;");
        return l;
    }

    private static Label value(String t) {
        Label l = new Label(t);
        l.setStyle("-fx-text-fill: #0f172a;");
        return l;
    }

    public static VBox createNotificationView(SinhVien sv) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        Label title = new Label("Thông báo");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        ListView<String> listView = new ListView<>();
        listView.setPrefHeight(400);
        DesktopDao dao = new DesktopDao();
        for (String[] n : dao.getNotifications("STUDENT", "ALL")) {
            listView.getItems().add("[" + n[2] + "] " + n[0] + ": " + n[1]);
        }
        for (String[] n : dao.getNotifications("ALL", "")) {
            listView.getItems().add("[TOÀN TRƯỜNG] " + n[0] + ": " + n[1]);
        }
        if (listView.getItems().isEmpty()) {
            listView.getItems().add("Chưa có thông báo.");
        }
        root.getChildren().addAll(title, listView);
        return root;
    }

    public static VBox createProfileView(SinhVien sv) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        Label title = new Label("Hồ sơ cá nhân");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.addRow(0, label("Mã SV:"), value(sv.getMaSV()));
        grid.addRow(1, label("Họ tên:"), value(sv.getHoTen()));
        grid.addRow(2, label("Lớp:"), value(sv.getLop()));

        TextField txtEmail = new TextField(sv.getEmail());
        TextField txtSdt = new TextField(sv.getSdt() != null ? sv.getSdt() : "");
        txtEmail.getStyleClass().add("text-field");
        txtSdt.getStyleClass().add("text-field");
        grid.addRow(3, label("Email:"), txtEmail);
        grid.addRow(4, label("SĐT:"), txtSdt);

        Button btnUpdate = new Button("Lưu thay đổi");
        btnUpdate.getStyleClass().add("btn-primary");
        btnUpdate.setOnAction(e -> {
            new DesktopDao().updateStudentProfile(sv.getMaSV(), txtEmail.getText(), txtSdt.getText());
            sv.setEmail(txtEmail.getText());
            sv.setSdt(txtSdt.getText());
            new Alert(Alert.AlertType.INFORMATION, "Đã lưu hồ sơ!").show();
        });

        root.getChildren().addAll(title, grid, btnUpdate);
        return root;
    }

    public static VBox createRegistrationView(SceneNavigator navigator, SinhVien sv) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Label title = new Label("Đăng ký học phần");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        if (!sv.isTuitionPaid()) {
            Label warn = new Label("⚠ Bạn chưa đóng học phí. Vào mục Học phí để thanh toán trước khi đăng ký thêm lớp.");
            warn.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
            root.getChildren().addAll(title, warn);
            return root;
        }

        HBox searchBox = new HBox(10);
        TextField txtSearch = new TextField();
        txtSearch.setPromptText("Tìm theo tên môn hoặc mã môn...");
        txtSearch.setPrefWidth(300);
        txtSearch.getStyleClass().add("text-field");
        Button btnSearch = new Button("Tìm kiếm");
        btnSearch.getStyleClass().add("btn-primary");
        searchBox.getChildren().addAll(txtSearch, btnSearch);

        TableView<com.example.dean12.model.LopHocPhan> table = new TableView<>();
        table.getStyleClass().add("table-view");

        TableColumn<com.example.dean12.model.LopHocPhan, String> colMH = new TableColumn<>("Môn học");
        colMH.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMonHoc().getTenMH()));
        TableColumn<com.example.dean12.model.LopHocPhan, String> colLHP = new TableColumn<>("Mã lớp");
        colLHP.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getMaLhp()));
        TableColumn<com.example.dean12.model.LopHocPhan, String> colGV = new TableColumn<>("Giảng viên");
        colGV.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getGiangVien() != null ? c.getValue().getGiangVien().getHoTen() : ""));
        TableColumn<com.example.dean12.model.LopHocPhan, String> colLich = new TableColumn<>("Lịch học");
        colLich.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getLichHoc()));
        TableColumn<com.example.dean12.model.LopHocPhan, String> colSiSo = new TableColumn<>("Sĩ số");
        colSiSo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getSiSoHienTai() + "/" + c.getValue().getSiSoToiDa()));

        TableColumn<com.example.dean12.model.LopHocPhan, Void> colAction = new TableColumn<>("Thao tác");
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Đăng ký");
            {
                btn.getStyleClass().add("btn-success");
                btn.setOnAction(e -> {
                    com.example.dean12.model.LopHocPhan lhp = getTableView().getItems().get(getIndex());
                    String result = navigator.getDao().registerClass(sv.getMaSV(), lhp.getId());
                    Alert a = new Alert(result.contains("thành công") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                    a.setContentText(result);
                    a.show();
                    btnSearch.fire();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        table.getColumns().addAll(colMH, colLHP, colGV, colLich, colSiSo, colAction);
        VBox.setVgrow(table, Priority.ALWAYS);

        btnSearch.setOnAction(e -> {
            List<com.example.dean12.model.LopHocPhan> list = navigator.getDao().getAvailableClassesForRegistration(txtSearch.getText());
            table.setItems(javafx.collections.FXCollections.observableArrayList(list));
        });
        btnSearch.fire();

        root.getChildren().addAll(title, searchBox, table);
        return root;
    }
}

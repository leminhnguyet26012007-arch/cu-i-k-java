package com.example.dean12.desktop;

import com.example.dean12.model.ThongBao;
import com.example.dean12.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;
import java.util.Optional;

public class AdminAdvancedScenes {

    public static VBox createSystemConfigView(SceneNavigator navigator) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(25));

        Label title = new Label("Cấu hình Hệ thống");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        String[] current = navigator.getDao().getSystemConfig(); // [Year, Semester, Tuition]

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        TextField txtYear = new TextField(current[0]);
        txtYear.getStyleClass().add("text-field");
        txtYear.setPrefWidth(250);

        ComboBox<String> cbSem = new ComboBox<>();
        cbSem.getStyleClass().add("combo-box");
        cbSem.getItems().addAll("1", "2", "3");
        cbSem.setValue(current[1]);
        cbSem.setPrefWidth(250);

        TextField txtTuition = new TextField(current[2]);
        txtTuition.getStyleClass().add("text-field");
        txtTuition.setPrefWidth(250);

        grid.addRow(0, new Label("Năm học hiện tại:"), txtYear);
        grid.addRow(1, new Label("Học kỳ hiện tại:"), cbSem);
        grid.addRow(2, new Label("Học phí / Tín chỉ (VNĐ):"), txtTuition);

        Button btnSave = new Button("Lưu Cấu Hình");
        btnSave.getStyleClass().add("btn-primary");
        
        btnSave.setOnAction(e -> {
            try {
                double t = Double.parseDouble(txtTuition.getText());
                if (t < 0) throw new NumberFormatException();
                
                navigator.getDao().updateSystemConfig(txtYear.getText(), cbSem.getValue(), t);
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật cấu hình hệ thống!");
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Học phí phải là một số dương!");
            }
        });

        root.getChildren().addAll(title, grid, btnSave);
        return root;
    }

    public static VBox createNotificationCenterView(SceneNavigator navigator) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(25));

        Label title = new Label("Trung tâm Thông báo");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-background-color: transparent;");

        // Tab: Gửi thông báo
        Tab tabSend = new Tab("Gửi Thông báo");
        VBox vSend = new VBox(15);
        vSend.setPadding(new Insets(20));

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        TextField txtTitle = new TextField(); txtTitle.getStyleClass().add("text-field");
        txtTitle.setPrefWidth(500);
        
        TextArea txtContent = new TextArea(); txtContent.getStyleClass().add("text-field");
        txtContent.setPrefRowCount(6);
        txtContent.setPrefWidth(500);

        ComboBox<String> cbTarget = new ComboBox<>(); cbTarget.getStyleClass().add("combo-box");
        cbTarget.getItems().addAll("Toàn trường (ALL)", "Tất cả Giảng viên (TEACHER)", "Tất cả Sinh viên (STUDENT)");
        cbTarget.setValue("Toàn trường (ALL)");
        cbTarget.setPrefWidth(500);

        grid.addRow(0, new Label("Tiêu đề:"), txtTitle);
        grid.addRow(1, new Label("Nội dung:"), txtContent);
        grid.addRow(2, new Label("Gửi đến:"), cbTarget);

        Button btnSend = new Button("Gửi Thông Báo 🚀");
        btnSend.getStyleClass().add("btn-success");
        
        btnSend.setOnAction(e -> {
            String roleRaw = cbTarget.getValue();
            String roleCode = "ALL";
            if (roleRaw.contains("TEACHER")) roleCode = "TEACHER";
            if (roleRaw.contains("STUDENT")) roleCode = "STUDENT";

            if (txtTitle.getText().isEmpty() || txtContent.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ tiêu đề và nội dung!");
                return;
            }

            navigator.getDao().createNotification(txtTitle.getText(), txtContent.getText(), roleCode);
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã gửi thông báo đến các đối tượng!");
            txtTitle.clear();
            txtContent.clear();
        });
        vSend.getChildren().addAll(grid, btnSend);
        tabSend.setContent(vSend);

        // Tab: Lịch sử đã gửi
        Tab tabHistory = new Tab("Lịch sử Thông báo");
        VBox vHistory = new VBox(12);
        vHistory.setPadding(new Insets(20));

        TableView<ThongBao> table = new TableView<>();
        table.getStyleClass().add("table-view");

        TableColumn<ThongBao, String> colTime = new TableColumn<>("Thời gian");
        colTime.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNgayTao() != null ? c.getValue().getNgayTao().toString() : ""));
        colTime.setPrefWidth(160);

        TableColumn<ThongBao, String> colTieuDe = new TableColumn<>("Tiêu đề");
        colTieuDe.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTieuDe()));
        colTieuDe.setPrefWidth(200);

        TableColumn<ThongBao, String> colDoiTuong = new TableColumn<>("Đối tượng");
        colDoiTuong.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDoiTuong()));
        colDoiTuong.setPrefWidth(120);

        TableColumn<ThongBao, String> colNoiDung = new TableColumn<>("Nội dung");
        colNoiDung.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNoiDung()));
        colNoiDung.setPrefWidth(300);

        table.getColumns().addAll(colTime, colTieuDe, colDoiTuong, colNoiDung);
        VBox.setVgrow(table, Priority.ALWAYS);

        Button btnRefresh = new Button("🔄 Làm mới lịch sử");
        btnRefresh.getStyleClass().add("btn-primary");
        btnRefresh.setOnAction(e -> table.setItems(FXCollections.observableArrayList(navigator.getDao().getAllNotifications())));

        vHistory.getChildren().addAll(btnRefresh, table);
        tabHistory.setContent(vHistory);

        tabHistory.setOnSelectionChanged(e -> {
            if (tabHistory.isSelected()) {
                table.setItems(FXCollections.observableArrayList(navigator.getDao().getAllNotifications()));
            }
        });

        tabs.getTabs().addAll(tabSend, tabHistory);
        root.getChildren().addAll(title, tabs);
        return root;
    }

    public static VBox createReportsView(SceneNavigator navigator) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(10, 0, 10, 0));

        int studentCount = navigator.getDao().getAllStudents().size();
        int courseCount = navigator.getDao().getAllCourses().size();
        int classCount = navigator.getDao().getAllClasses().size();
        
        // Calculate tuition statistics
        int paidCount = 0;
        for (com.example.dean12.model.SinhVien sv : navigator.getDao().getAllStudents()) {
            if (sv.isTuitionPaid()) paidCount++;
        }

        TilePane tiles = new TilePane();
        tiles.setHgap(20);
        tiles.setVgap(20);
        tiles.setPrefColumns(4);

        tiles.getChildren().addAll(
            createStatCard("SINH VIÊN", String.valueOf(studentCount), "stat-card-blue"),
            createStatCard("MÔN HỌC", String.valueOf(courseCount), "stat-card-purple"),
            createStatCard("LỚP HỌC PHẦN", String.valueOf(classCount), "stat-card-pink"),
            createStatCard("ĐÃ ĐÓNG HỌC PHÍ", String.valueOf(paidCount) + "/" + studentCount, "stat-card-amber")
        );

        root.getChildren().addAll(tiles);
        return root;
    }

    private static VBox createStatCard(String label, String value, String colorClass) {
        VBox card = new VBox(6);
        card.getStyleClass().addAll("stat-card", colorClass);

        Label lblVal = new Label(value);
        lblVal.getStyleClass().add("stat-value");

        Label lblName = new Label(label);
        lblName.getStyleClass().add("stat-title");

        card.getChildren().addAll(lblVal, lblName);
        return card;
    }

    public static VBox createUserManagementView(SceneNavigator navigator) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(25));

        Label title = new Label("Quản lý Tài khoản Hệ thống");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        TableView<User> table = new TableView<>();
        table.getStyleClass().add("table-view");

        TableColumn<User, String> colUser = new TableColumn<>("Username");
        colUser.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsername()));
        colUser.setPrefWidth(120);

        TableColumn<User, String> colPass = new TableColumn<>("Password (Encoded)");
        colPass.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPassword()));
        colPass.setPrefWidth(150);

        TableColumn<User, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail() != null ? c.getValue().getEmail() : ""));
        colEmail.setPrefWidth(180);

        TableColumn<User, String> colRole = new TableColumn<>("Vai trò");
        colRole.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole()));
        colRole.setPrefWidth(120);

        TableColumn<User, String> colStatus = new TableColumn<>("Trạng thái");
        colStatus.setCellValueFactory(c -> {
            boolean locked = navigator.getDao().isUserLocked(c.getValue().getUsername());
            return new SimpleStringProperty(locked ? "🔒 Đã khóa" : "✅ Hoạt động");
        });
        colStatus.setPrefWidth(120);

        TableColumn<User, Void> colAction = new TableColumn<>("Thao tác");
        colAction.setPrefWidth(220);
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnLock = new Button("Khóa");
            private final Button btnUnlock = new Button("Mở");
            private final Button btnDel = new Button("Xóa");
            {
                btnLock.getStyleClass().add("btn-warning");
                btnLock.setStyle("-fx-padding: 4 10; -fx-font-size: 11px;");
                
                btnUnlock.getStyleClass().add("btn-success");
                btnUnlock.setStyle("-fx-padding: 4 10; -fx-font-size: 11px;");
                
                btnDel.getStyleClass().add("btn-danger");
                btnDel.setStyle("-fx-padding: 4 10; -fx-font-size: 11px;");

                btnLock.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    if ("ADMIN".equals(u.getRole())) {
                         showAlert(Alert.AlertType.WARNING, "Lỗi", "Không thể khóa tài khoản ADMIN!");
                         return;
                    }
                    navigator.getDao().lockUserAccount(u.getUsername());
                    refreshTable();
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã khóa tài khoản: " + u.getUsername());
                });

                btnUnlock.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    navigator.getDao().unlockUserAccount(u.getUsername());
                    refreshTable();
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã mở khóa tài khoản: " + u.getUsername());
                });

                btnDel.setOnAction(e -> {
                    User u = getTableView().getItems().get(getIndex());
                    if ("ADMIN".equals(u.getRole())) {
                        showAlert(Alert.AlertType.WARNING, "Lỗi", "Không thể xóa tài khoản ADMIN!");
                        return;
                    }
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa tài khoản này?", ButtonType.YES, ButtonType.NO);
                    alert.setTitle("Xác nhận xóa tài khoản");
                    alert.setHeaderText(null);
                    alert.getDialogPane().getStylesheets().add(AdminAdvancedScenes.class.getResource("/com/example/dean12/desktop/style.css").toExternalForm());
                    Optional<ButtonType> res = alert.showAndWait();
                    if (res.isPresent() && res.get() == ButtonType.YES) {
                        navigator.getDao().deleteUserAccount(u.getUsername());
                        refreshTable();
                        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa tài khoản!");
                    }
                });
            }

            private void refreshTable() {
                table.setItems(FXCollections.observableArrayList(navigator.getDao().getAllUsers()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(6, btnLock, btnUnlock, btnDel);
                    setGraphic(box);
                }
            }
        });

        table.getColumns().addAll(colUser, colPass, colEmail, colRole, colStatus, colAction);
        VBox.setVgrow(table, Priority.ALWAYS);
        table.setItems(FXCollections.observableArrayList(navigator.getDao().getAllUsers()));

        // Add form
        Label addTitle = new Label("Tạo tài khoản mới:");
        addTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b; -fx-font-size: 14px;");

        TextField txtUsername = new TextField(); txtUsername.setPromptText("Tên tài khoản"); txtUsername.getStyleClass().add("text-field");
        PasswordField txtPassword = new PasswordField(); txtPassword.setPromptText("Mật khẩu"); txtPassword.getStyleClass().add("text-field");
        TextField txtEmail = new TextField(); txtEmail.setPromptText("Email"); txtEmail.getStyleClass().add("text-field");
        
        ComboBox<String> cbRole = new ComboBox<>(); cbRole.getStyleClass().add("combo-box");
        cbRole.getItems().addAll("STUDENT", "TEACHER", "ADMIN");
        cbRole.setPromptText("Vai trò");

        Button btnCreate = new Button("➕ Tạo Tài Khoản");
        btnCreate.getStyleClass().add("btn-success");
        
        btnCreate.setOnAction(e -> {
            String uname = txtUsername.getText().trim();
            String pword = txtPassword.getText();
            String mail = txtEmail.getText().trim();
            String role = cbRole.getValue();

            if (uname.isEmpty() || pword.isEmpty() || mail.isEmpty() || role == null) {
                showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ thông tin!");
                return;
            }
            
            // Check duplicates
            for (User ex : navigator.getDao().getAllUsers()) {
                if (ex.getUsername().equalsIgnoreCase(uname)) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi Trùng Lặp", "Tên đăng nhập này đã tồn tại!");
                    return;
                }
            }

            navigator.getDao().createUserAccount(uname, pword, mail, role);
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã tạo tài khoản thành công!");
            txtUsername.clear(); txtPassword.clear(); txtEmail.clear(); cbRole.setValue(null);
            table.setItems(FXCollections.observableArrayList(navigator.getDao().getAllUsers()));
        });

        HBox form = new HBox(8, txtUsername, txtPassword, txtEmail, cbRole, btnCreate);
        form.setAlignment(Pos.CENTER_LEFT);

        Button btnRefresh = new Button("🔄 Làm mới danh sách");
        btnRefresh.getStyleClass().add("btn-primary");
        btnRefresh.setOnAction(e -> table.setItems(FXCollections.observableArrayList(navigator.getDao().getAllUsers())));

        root.getChildren().addAll(title, btnRefresh, table, addTitle, form);
        return root;
    }

    private static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.getDialogPane().getStylesheets().add(AdminAdvancedScenes.class.getResource("/com/example/dean12/desktop/style.css").toExternalForm());
        alert.showAndWait();
    }
}

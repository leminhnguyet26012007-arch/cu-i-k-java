package com.example.dean12.desktop.view;
import com.example.dean12.desktop.controller.SceneNavigator;
import com.example.dean12.model.SinhVien;
import com.example.dean12.model.MonHoc;
import com.example.dean12.model.LopHocPhan;
import com.example.dean12.model.GiangVien;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Pattern;

public class AdminScenes {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");

    public static VBox createStudentManagementView(SceneNavigator navigator) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Label title = new Label("Quản lý Sinh viên");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm kiếm theo Mã SV, Họ Tên, Lớp...");
        txtSearch.setPrefWidth(300);
        txtSearch.getStyleClass().add("text-field");

        Button btnExport = new Button("📥 Xuất XML");
        btnExport.getStyleClass().add("btn-warning");
        
        Button btnImport = new Button("📤 Nhập XML");
        btnImport.getStyleClass().add("btn-success");

        HBox toolbar = new HBox(12, txtSearch, new Region(), btnExport, btnImport);
        HBox.setHgrow(toolbar.getChildren().get(1), Priority.ALWAYS);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        TableView<SinhVien> table = new TableView<>();
        table.getStyleClass().add("table-view");
        
        TableColumn<SinhVien, String> colId = new TableColumn<>("Mã SV");
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMaSV()));
        colId.setPrefWidth(100);
        
        TableColumn<SinhVien, String> colName = new TableColumn<>("Họ Tên");
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHoTen()));
        colName.setPrefWidth(180);
        
        TableColumn<SinhVien, String> colClass = new TableColumn<>("Lớp");
        colClass.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLop()));
        colClass.setPrefWidth(120);
        
        TableColumn<SinhVien, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colEmail.setPrefWidth(180);
        
        TableColumn<SinhVien, String> colPhone = new TableColumn<>("SĐT");
        colPhone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSdt() != null ? c.getValue().getSdt() : ""));
        colPhone.setPrefWidth(120);

        TableColumn<SinhVien, Void> colAction = new TableColumn<>("Thao tác");
        colAction.setPrefWidth(180);
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnDelete = new Button("Xóa");
            {
                btnEdit.getStyleClass().add("btn-primary");
                btnEdit.setStyle("-fx-padding: 4 10; -fx-font-size: 11px;");
                btnDelete.getStyleClass().add("btn-danger");
                btnDelete.setStyle("-fx-padding: 4 10; -fx-font-size: 11px;");
                
                btnEdit.setOnAction(e -> {
                    SinhVien sv = getTableView().getItems().get(getIndex());
                    showEditStudentDialog(navigator, sv, table, txtSearch);
                });
                
                btnDelete.setOnAction(e -> {
                    SinhVien sv = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                        "Bạn có chắc chắn muốn xóa sinh viên " + sv.getHoTen() + "?\nMọi điểm số, lịch sử học tập liên quan sẽ bị xóa vĩnh viễn và không thể khôi phục!",
                        ButtonType.YES, ButtonType.NO);
                    confirm.setTitle("Xác nhận xóa sinh viên");
                    confirm.setHeaderText(null);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            navigator.getAdminController().deleteStudent(sv.getMaSV());
                            refreshTable(navigator, table, txtSearch);
                            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa sinh viên!");
                        }
                    });
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(6, btnEdit, btnDelete));
            }
        });

        table.getColumns().addAll(colId, colName, colClass, colEmail, colPhone, colAction);
        VBox.setVgrow(table, Priority.ALWAYS);

        // Add form
        Label addTitle = new Label("Thêm Sinh viên mới:");
        addTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b; -fx-font-size: 14px;");
        
        TextField txtId = new TextField(); txtId.setPromptText("Mã SV (vd: SV02)"); txtId.getStyleClass().add("text-field");
        TextField txtName = new TextField(); txtName.setPromptText("Họ tên"); txtName.getStyleClass().add("text-field");
        TextField txtClass = new TextField(); txtClass.setPromptText("Lớp"); txtClass.getStyleClass().add("text-field");
        TextField txtEmail = new TextField(); txtEmail.setPromptText("Email"); txtEmail.getStyleClass().add("text-field");
        TextField txtPhone = new TextField(); txtPhone.setPromptText("SĐT"); txtPhone.getStyleClass().add("text-field");
        Button btnAdd = new Button("➕ Thêm Mới");
        btnAdd.getStyleClass().add("btn-success");
        
        HBox form = new HBox(8, txtId, txtName, txtClass, txtEmail, txtPhone, btnAdd);
        form.setAlignment(Pos.CENTER_LEFT);
        
        btnAdd.setOnAction(e -> {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String lop = txtClass.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();

            // Advanced Data Validation
            if (id.isEmpty() || name.isEmpty() || lop.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Nhập Liệu", "Vui lòng nhập đầy đủ thông tin!");
                return;
            }
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Định Dạng", "Email không đúng định dạng (vd: sv@gmail.com)!");
                return;
            }
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Định Dạng", "Số điện thoại phải là số và có đúng 10 chữ số!");
                return;
            }

            // Check duplicate ID locally first
            for (SinhVien ex : navigator.getAdminController().getAllStudents()) {
                if (ex.getMaSV().equalsIgnoreCase(id)) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi Trùng Lặp", "Mã sinh viên này đã tồn tại trong hệ thống!");
                    return;
                }
            }

            SinhVien sv = new SinhVien();
            sv.setMaSV(id.toUpperCase());
            sv.setHoTen(name);
            sv.setLop(lop);
            sv.setEmail(email);
            sv.setSdt(phone);
            
            navigator.getAdminController().createStudent(sv, id.toLowerCase(), "123");
            txtId.clear(); txtName.clear(); txtClass.clear(); txtEmail.clear(); txtPhone.clear();
            refreshTable(navigator, table, txtSearch);
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm sinh viên mới! Mật khẩu mặc định là: 123");
        });

        // XML Export action
        btnExport.setOnAction(e -> {
            try {
                String xml = navigator.getAdminController().exportStudentsToXml();
                if (xml == null) {
                    showAlert(Alert.AlertType.ERROR, "Thất bại", "Không có dữ liệu sinh viên để xuất!");
                    return;
                }
                
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Lưu file XML");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files (*.xml)", "*.xml"));
                fileChooser.setInitialFileName("students.xml");
                
                File file = fileChooser.showSaveDialog(navigator.getPrimaryStage());
                if (file != null) {
                    try (PrintWriter pw = new PrintWriter(file)) {
                        pw.write(xml);
                    }
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xuất danh sách sinh viên ra file XML thành công!");
                }
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Lỗi khi xuất dữ liệu: " + ex.getMessage());
            }
        });

        btnImport.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chọn file XML để nhập");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files (*.xml)", "*.xml"));
            
            File file = fileChooser.showOpenDialog(navigator.getPrimaryStage());
            if (file != null) {
                try {
                    String xmlData = new String(Files.readAllBytes(file.toPath()), java.nio.charset.StandardCharsets.UTF_8);
                    String result = navigator.getAdminController().importStudentsFromXml(xmlData);
                    refreshTable(navigator, table, txtSearch);
                    if (result.contains("thành công")) {
                        showAlert(Alert.AlertType.INFORMATION, "Kết quả Nhập XML", result);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi Nhập XML", result);
                    }
                } catch (Exception ex) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi cấu trúc XML", "File XML không đúng cấu trúc hoặc lỗi định dạng: " + ex.getMessage());
                }
            }
        });

        // Initialize table and setup dynamic search filter
        refreshTable(navigator, table, txtSearch);

        root.getChildren().addAll(title, toolbar, table, addTitle, form);
        return root;
    }
    
    private static void refreshTable(SceneNavigator navigator, TableView<SinhVien> table, TextField txtSearch) {
        ObservableList<SinhVien> masterData = FXCollections.observableArrayList(navigator.getAdminController().getAllStudents());
        
        // 1. Wrap the ObservableList in a FilteredList
        FilteredList<SinhVien> filteredData = new FilteredList<>(masterData, p -> true);
        
        // 2. Set the filter Predicate whenever the filter changes.
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(student -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase().trim();
                
                if (student.getMaSV().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (student.getHoTen().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (student.getLop().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (student.getEmail() != null && student.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
        
        // 3. Wrap the FilteredList in a SortedList. 
        SortedList<SinhVien> sortedData = new SortedList<>(filteredData);
        
        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        
        // 5. Add sorted and filtered data to the table
        table.setItems(sortedData);
    }
    
    private static void showEditStudentDialog(SceneNavigator navigator, SinhVien sv, TableView<SinhVien> table, TextField txtSearch) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sửa thông tin Sinh viên");
        dialog.setHeaderText(null);
        
        TextField txtName = new TextField(sv.getHoTen()); txtName.getStyleClass().add("text-field");
        TextField txtClass = new TextField(sv.getLop()); txtClass.getStyleClass().add("text-field");
        TextField txtEmail = new TextField(sv.getEmail()); txtEmail.getStyleClass().add("text-field");
        TextField txtPhone = new TextField(sv.getSdt() != null ? sv.getSdt() : ""); txtPhone.getStyleClass().add("text-field");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        grid.addRow(0, new Label("Mã Sinh Viên:"), new Label(sv.getMaSV()));
        grid.addRow(1, new Label("Họ tên:"), txtName);
        grid.addRow(2, new Label("Lớp:"), txtClass);
        grid.addRow(3, new Label("Email:"), txtEmail);
        grid.addRow(4, new Label("SĐT:"), txtPhone);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().getStylesheets().add(AdminScenes.class.getResource("/com/example/dean12/desktop/style.css").toExternalForm());
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String name = txtName.getText().trim();
                String lop = txtClass.getText().trim();
                String email = txtEmail.getText().trim();
                String phone = txtPhone.getText().trim();

                // Advanced Data Validation
                if (name.isEmpty() || lop.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi Nhập Liệu", "Cập nhật thất bại do thiếu thông tin!");
                    return;
                }
                if (!EMAIL_PATTERN.matcher(email).matches()) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi Định Dạng", "Email không đúng định dạng!");
                    return;
                }
                if (!PHONE_PATTERN.matcher(phone).matches()) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi Định Dạng", "Số điện thoại phải là số và đúng 10 chữ số!");
                    return;
                }

                navigator.getAdminController().updateStudent(sv.getMaSV(), name, lop, email, phone);
                refreshTable(navigator, table, txtSearch);
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật thông tin sinh viên!");
            }
        });
    }

    // --- COURSE MANAGEMENT VIEW ---
    public static VBox createCourseManagementView(SceneNavigator navigator) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Label title = new Label("Quản lý Môn học");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm kiếm theo Mã môn, Tên môn học...");
        txtSearch.getStyleClass().add("text-field");
        txtSearch.setPrefWidth(300);

        HBox toolbar = new HBox(txtSearch);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        TableView<MonHoc> table = new TableView<>();
        table.getStyleClass().add("table-view");
        
        TableColumn<MonHoc, String> colId = new TableColumn<>("Mã MH");
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMaMH()));
        colId.setPrefWidth(120);
        
        TableColumn<MonHoc, String> colName = new TableColumn<>("Tên Môn học");
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTenMH()));
        colName.setPrefWidth(350);
        
        TableColumn<MonHoc, String> colCredits = new TableColumn<>("Số TC");
        colCredits.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getSoTinChi())));
        colCredits.setPrefWidth(100);

        TableColumn<MonHoc, Void> colAction = new TableColumn<>("Thao tác");
        colAction.setPrefWidth(180);
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnDelete = new Button("Xóa");
            {
                btnEdit.getStyleClass().add("btn-primary");
                btnEdit.setStyle("-fx-padding: 4 10; -fx-font-size: 11px;");
                btnDelete.getStyleClass().add("btn-danger");
                btnDelete.setStyle("-fx-padding: 4 10; -fx-font-size: 11px;");
                
                btnEdit.setOnAction(e -> {
                    MonHoc mh = getTableView().getItems().get(getIndex());
                    showEditCourseDialog(navigator, mh, table, txtSearch);
                });
                
                btnDelete.setOnAction(e -> {
                    MonHoc mh = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có muốn xóa môn học " + mh.getTenMH() + "?", ButtonType.YES, ButtonType.NO);
                    confirm.setTitle("Xác nhận xóa môn học");
                    confirm.setHeaderText(null);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            navigator.getAdminController().deleteCourse(mh.getMaMH());
                            refreshCourseTable(navigator, table, txtSearch);
                            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa môn học!");
                        }
                    });
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(6, btnEdit, btnDelete));
            }
        });

        table.getColumns().addAll(colId, colName, colCredits, colAction);
        VBox.setVgrow(table, Priority.ALWAYS);

        Label addTitle = new Label("Thêm Môn học mới:");
        addTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b; -fx-font-size: 14px;");
        
        TextField txtId = new TextField(); txtId.setPromptText("Mã MH (vd: IT103)"); txtId.getStyleClass().add("text-field");
        TextField txtName = new TextField(); txtName.setPromptText("Tên môn học"); txtName.getStyleClass().add("text-field");
        TextField txtCredits = new TextField(); txtCredits.setPromptText("Số TC (vd: 3)"); txtCredits.getStyleClass().add("text-field");
        Button btnAdd = new Button("➕ Thêm Mới");
        btnAdd.getStyleClass().add("btn-success");
        
        HBox form = new HBox(8, txtId, txtName, txtCredits, btnAdd);
        form.setAlignment(Pos.CENTER_LEFT);
        
        btnAdd.setOnAction(e -> {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String creditsRaw = txtCredits.getText().trim();

            if (id.isEmpty() || name.isEmpty() || creditsRaw.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Nhập Liệu", "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            int credits;
            try {
                credits = Integer.parseInt(creditsRaw);
                if (credits <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi Định Dạng", "Số tín chỉ phải là một số nguyên dương lớn hơn 0!");
                return;
            }

            // Check duplicate course id
            for (MonHoc ex : navigator.getAdminController().getAllCourses()) {
                if (ex.getMaMH().equalsIgnoreCase(id)) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi Trùng Lặp", "Mã môn học này đã tồn tại!");
                    return;
                }
            }

            MonHoc mh = new MonHoc();
            mh.setMaMH(id.toUpperCase());
            mh.setTenMH(name);
            mh.setSoTinChi(credits);
            
            navigator.getAdminController().createCourse(mh);
            txtId.clear(); txtName.clear(); txtCredits.clear();
            refreshCourseTable(navigator, table, txtSearch);
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm môn học thành công!");
        });

        refreshCourseTable(navigator, table, txtSearch);

        root.getChildren().addAll(title, toolbar, table, addTitle, form);
        return root;
    }

    private static void refreshCourseTable(SceneNavigator navigator, TableView<MonHoc> table, TextField txtSearch) {
        ObservableList<MonHoc> masterData = FXCollections.observableArrayList(navigator.getAdminController().getAllCourses());
        FilteredList<MonHoc> filteredData = new FilteredList<>(masterData, p -> true);
        
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(course -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase().trim();
                if (course.getMaMH().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (course.getTenMH().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
        
        SortedList<MonHoc> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);
    }

    private static void showEditCourseDialog(SceneNavigator navigator, MonHoc mh, TableView<MonHoc> table, TextField txtSearch) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sửa thông tin Môn học");
        dialog.setHeaderText(null);
        
        TextField txtName = new TextField(mh.getTenMH()); txtName.getStyleClass().add("text-field");
        TextField txtCredits = new TextField(String.valueOf(mh.getSoTinChi())); txtCredits.getStyleClass().add("text-field");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        grid.addRow(0, new Label("Mã môn học:"), new Label(mh.getMaMH()));
        grid.addRow(1, new Label("Tên môn học:"), txtName);
        grid.addRow(2, new Label("Số tín chỉ:"), txtCredits);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().getStylesheets().add(AdminScenes.class.getResource("/com/example/dean12/desktop/style.css").toExternalForm());
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String name = txtName.getText().trim();
                String creditsRaw = txtCredits.getText().trim();

                if (name.isEmpty() || creditsRaw.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi Nhập Liệu", "Thiếu thông tin chỉnh sửa!");
                    return;
                }

                int credits;
                try {
                    credits = Integer.parseInt(creditsRaw);
                    if (credits <= 0) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi Định Dạng", "Số tín chỉ phải là số nguyên lớn hơn 0!");
                    return;
                }

                navigator.getAdminController().updateCourse(mh.getMaMH(), name, credits);
                refreshCourseTable(navigator, table, txtSearch);
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật môn học!");
            }
        });
    }

    // --- CLASS MANAGEMENT VIEW (LopHocPhan) ---
    public static VBox createClassManagementView(SceneNavigator navigator) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));

        Label title = new Label("Danh sách Lớp Học Phần");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("🔍 Tìm kiếm theo Lớp, Môn học, Giảng viên...");
        txtSearch.getStyleClass().add("text-field");
        txtSearch.setPrefWidth(300);

        HBox toolbar = new HBox(txtSearch);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        TableView<LopHocPhan> table = new TableView<>();
        table.getStyleClass().add("table-view");
        
        TableColumn<LopHocPhan, String> colId = new TableColumn<>("Mã Lớp");
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMaLhp()));
        colId.setPrefWidth(120);
        
        TableColumn<LopHocPhan, String> colSub = new TableColumn<>("Môn Học");
        colSub.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMonHoc() != null ? c.getValue().getMonHoc().getTenMH() : ""));
        colSub.setPrefWidth(280);
        
        TableColumn<LopHocPhan, String> colTea = new TableColumn<>("Giảng Viên");
        colTea.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGiangVien() != null ? c.getValue().getGiangVien().getHoTen() : "Chưa phân"));
        colTea.setPrefWidth(180);
        
        TableColumn<LopHocPhan, String> colRoom = new TableColumn<>("Phòng");
        colRoom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhongHoc()));
        colRoom.setPrefWidth(100);
        
        TableColumn<LopHocPhan, String> colSch = new TableColumn<>("Lịch");
        colSch.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLichHoc()));
        colSch.setPrefWidth(150);

        TableColumn<LopHocPhan, Void> colAction = new TableColumn<>("Thao tác");
        colAction.setPrefWidth(150);
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("Sửa");
            private final Button btnDelete = new Button("Xóa");
            {
                btnEdit.getStyleClass().add("btn-primary");
                btnEdit.setStyle("-fx-padding: 4 10; -fx-font-size: 11px;");
                btnDelete.getStyleClass().add("btn-danger");
                btnDelete.setStyle("-fx-padding: 4 10; -fx-font-size: 11px;");
                
                btnEdit.setOnAction(e -> {
                    LopHocPhan lhp = getTableView().getItems().get(getIndex());
                    showEditClassDialog(navigator, lhp, table, txtSearch);
                });
                
                btnDelete.setOnAction(e -> {
                    LopHocPhan lhp = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                        "Bạn có chắc chắn muốn xóa lớp " + lhp.getMaLhp() + "?\nDữ liệu liên quan (điểm danh, điểm số) của học sinh trong lớp sẽ bị xóa!",
                        ButtonType.YES, ButtonType.NO);
                    confirm.setTitle("Xác nhận xóa lớp học");
                    confirm.setHeaderText(null);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            navigator.getAdminController().deleteClass(lhp.getId());
                            refreshClassTable(navigator, table, txtSearch);
                            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa lớp học!");
                        }
                    });
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(6, btnEdit, btnDelete));
            }
        });

        table.getColumns().addAll(colId, colSub, colTea, colRoom, colSch, colAction);
        VBox.setVgrow(table, Priority.ALWAYS);

        // Form
        Label lblNew = new Label("Mở lớp học phần mới:"); lblNew.setStyle("-fx-font-weight:bold; -fx-text-fill: #1e293b; -fx-font-size: 14px;");
        
        ComboBox<MonHoc> cbCourse = new ComboBox<>(); cbCourse.getStyleClass().add("combo-box");
        cbCourse.setPromptText("Chọn môn học");
        cbCourse.setItems(FXCollections.observableArrayList(navigator.getAdminController().getAllCourses()));
        cbCourse.setConverter(new javafx.util.StringConverter<>() {
            public String toString(MonHoc o) { return o == null ? "" : o.getMaMH() + " - " + o.getTenMH(); }
            public MonHoc fromString(String s) { return null; }
        });
        
        ComboBox<GiangVien> cbTeacher = new ComboBox<>(); cbTeacher.getStyleClass().add("combo-box");
        cbTeacher.setPromptText("Chọn giảng viên");
        cbTeacher.setItems(FXCollections.observableArrayList(navigator.getAdminController().getAllTeachers()));
        cbTeacher.setConverter(new javafx.util.StringConverter<>() {
            public String toString(GiangVien o) { return o == null ? "" : o.getHoTen(); }
            public GiangVien fromString(String s) { return null; }
        });

        TextField txtId = new TextField(); txtId.setPromptText("Mã LHP (vd: IT001.1)"); txtId.getStyleClass().add("text-field");
        TextField txtRoom = new TextField(); txtRoom.setPromptText("Phòng học"); txtRoom.getStyleClass().add("text-field");
        TextField txtSch = new TextField(); txtSch.setPromptText("Lịch học (thứ, tiết)"); txtSch.getStyleClass().add("text-field");
        
        Button btnAdd = new Button("🏫 Mở Lớp Học");
        btnAdd.getStyleClass().add("btn-success");
        
        HBox row1 = new HBox(8, txtId, cbCourse, cbTeacher);
        HBox row2 = new HBox(8, txtRoom, txtSch, btnAdd);
        row1.setAlignment(Pos.CENTER_LEFT);
        row2.setAlignment(Pos.CENTER_LEFT);
        
        btnAdd.setOnAction(e -> {
            try {
                String maLhp = txtId.getText().trim();
                MonHoc mh = cbCourse.getValue();
                String phong = txtRoom.getText().trim();
                String lich = txtSch.getText().trim();

                if (maLhp.isEmpty() || mh == null || phong.isEmpty() || lich.isEmpty()) {
                    throw new Exception("Vui lòng điền đầy đủ Mã LHP, Môn Học, Phòng Học và Lịch Học!");
                }
                
                LopHocPhan lhp = new LopHocPhan();
                lhp.setMaLhp(maLhp.toUpperCase());
                lhp.setMonHoc(mh);
                lhp.setGiangVien(cbTeacher.getValue()); 
                lhp.setPhongHoc(phong);
                lhp.setLichHoc(lich);
                
                navigator.getAdminController().createClass(lhp);
                
                txtId.clear(); txtRoom.clear(); txtSch.clear();
                cbCourse.setValue(null); cbTeacher.setValue(null);
                refreshClassTable(navigator, table, txtSearch);
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Mở lớp học phần mới thành công!");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", ex.getMessage());
            }
        });
        
        refreshClassTable(navigator, table, txtSearch);
        
        root.getChildren().addAll(title, toolbar, table, lblNew, row1, row2);
        return root;
    }
    
    private static void refreshClassTable(SceneNavigator navigator, TableView<LopHocPhan> table, TextField txtSearch) {
        ObservableList<LopHocPhan> masterData = FXCollections.observableArrayList(navigator.getAdminController().getAllClasses());
        FilteredList<LopHocPhan> filteredData = new FilteredList<>(masterData, p -> true);
        
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(lhp -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase().trim();
                if (lhp.getMaLhp().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (lhp.getMonHoc() != null && lhp.getMonHoc().getTenMH().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (lhp.getGiangVien() != null && lhp.getGiangVien().getHoTen().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (lhp.getPhongHoc().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
        
        SortedList<LopHocPhan> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);
    }
    
    private static void showEditClassDialog(SceneNavigator navigator, LopHocPhan lhp, TableView<LopHocPhan> table, TextField txtSearch) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sửa thông tin Lớp học phần");
        dialog.setHeaderText(null);
        
        TextField txtMaLhp = new TextField(lhp.getMaLhp()); txtMaLhp.getStyleClass().add("text-field");
        TextField txtRoom = new TextField(lhp.getPhongHoc()); txtRoom.getStyleClass().add("text-field");
        TextField txtSchedule = new TextField(lhp.getLichHoc()); txtSchedule.getStyleClass().add("text-field");
        
        ComboBox<GiangVien> cbTeacher = new ComboBox<>(); cbTeacher.getStyleClass().add("combo-box");
        cbTeacher.setItems(FXCollections.observableArrayList(navigator.getAdminController().getAllTeachers()));
        
        // Match current teacher
        if (lhp.getGiangVien() != null) {
            for (GiangVien gv : cbTeacher.getItems()) {
                if (gv.getMaGV().equals(lhp.getGiangVien().getMaGV())) {
                    cbTeacher.setValue(gv);
                    break;
                }
            }
        }
        
        cbTeacher.setConverter(new javafx.util.StringConverter<>() {
            public String toString(GiangVien o) { 
                return o == null ? "Chưa phân công" : o.getHoTen(); 
            }
            public GiangVien fromString(String s) { return null; }
        });
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        grid.addRow(0, new Label("Mã Lớp Học Phần:"), txtMaLhp);
        grid.addRow(1, new Label("Phòng học:"), txtRoom);
        grid.addRow(2, new Label("Lịch học:"), txtSchedule);
        grid.addRow(3, new Label("Giảng viên:"), cbTeacher);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().getStylesheets().add(AdminScenes.class.getResource("/com/example/dean12/desktop/style.css").toExternalForm());
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String maLhp = txtMaLhp.getText().trim();
                String room = txtRoom.getText().trim();
                String sched = txtSchedule.getText().trim();
                String maGV = cbTeacher.getValue() != null ? cbTeacher.getValue().getMaGV() : null;

                if (maLhp.isEmpty() || room.isEmpty() || sched.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không được bỏ trống thông tin chính!");
                    return;
                }

                navigator.getAdminController().updateClass(lhp.getId(), maLhp, room, sched, maGV);
                refreshClassTable(navigator, table, txtSearch);
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật lớp học phần!");
            }
        });
    }

    private static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.getDialogPane().getStylesheets().add(AdminScenes.class.getResource("/com/example/dean12/desktop/style.css").toExternalForm());
        alert.showAndWait();
    }
}

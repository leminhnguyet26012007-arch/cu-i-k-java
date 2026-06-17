package com.example.dean12.desktop;

import com.example.dean12.model.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.util.converter.DoubleStringConverter;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class TeacherScenes {

    private static String resolveMaGV(SceneNavigator navigator, String username) {
        GiangVien gv = navigator.getDao().getTeacherByUsername(username);
        if (gv != null) return gv.getMaGV();
        String u = username.toLowerCase();
        if (u.startsWith("gv")) return "GV" + u.substring(2).toUpperCase();
        return username.toUpperCase();
    }

    public static VBox createHomeContent(SceneNavigator navigator, String teacherUsername) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));

        Label welcome = new Label("TỔNG QUAN GIẢNG VIÊN");
        welcome.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        String maGV = resolveMaGV(navigator, teacherUsername);
        List<LopHocPhan> classes = navigator.getDao().getClassesByTeacher(maGV);

        HBox stats = new HBox(20);
        stats.setAlignment(Pos.CENTER_LEFT);
        stats.getChildren().addAll(
                statCard(String.valueOf(classes.size()), "Lớp phụ trách", "stat-card-blue"),
                statCard(String.valueOf(countStudents(navigator, classes)), "Sinh viên", "stat-card-purple"),
                statCard(maGV, "Mã GV", "stat-card-amber")
        );

        Label lbl = new Label("Lớp học phần gần đây");
        lbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #334155;");
        TableView<LopHocPhan> table = buildClassTable(navigator, classes);
        table.setPrefHeight(280);
        VBox.setVgrow(table, Priority.ALWAYS);

        content.getChildren().addAll(welcome, stats, lbl, table);
        return content;
    }

    public static VBox createClassesContent(SceneNavigator navigator, String teacherUsername) {
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        Label title = new Label("QUẢN LÝ LỚP HỌC PHẦN");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        String maGV = resolveMaGV(navigator, teacherUsername);
        TableView<LopHocPhan> table = buildClassTable(navigator, navigator.getDao().getClassesByTeacher(maGV));
        VBox.setVgrow(table, Priority.ALWAYS);
        content.getChildren().addAll(title, table);
        return content;
    }

    private static int countStudents(SceneNavigator navigator, List<LopHocPhan> classes) {
        int total = 0;
        for (LopHocPhan lhp : classes) {
            total += navigator.getDao().getStudentsInClass(lhp.getId()).size();
        }
        return total;
    }

    private static VBox statCard(String value, String label, String style) {
        VBox card = new VBox(6);
        card.getStyleClass().addAll("stat-card", style);
        Label v = new Label(value);
        v.getStyleClass().add("stat-value");
        Label n = new Label(label);
        n.getStyleClass().add("stat-title");
        card.getChildren().addAll(v, n);
        return card;
    }

    private static TableView<LopHocPhan> buildClassTable(SceneNavigator navigator, List<LopHocPhan> classes) {
        TableView<LopHocPhan> table = new TableView<>();
        table.getStyleClass().add("table-view");

        TableColumn<LopHocPhan, String> colId = new TableColumn<>("Mã LHP");
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMaLhp()));
        colId.setPrefWidth(90);

        TableColumn<LopHocPhan, String> colSub = new TableColumn<>("Môn học");
        colSub.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getMonHoc() != null ? c.getValue().getMonHoc().getTenMH() : ""));
        colSub.setPrefWidth(200);

        TableColumn<LopHocPhan, String> colRoom = new TableColumn<>("Phòng");
        colRoom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhongHoc()));

        TableColumn<LopHocPhan, String> colTime = new TableColumn<>("Lịch học");
        colTime.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLichHoc()));
        colTime.setPrefWidth(150);

        TableColumn<LopHocPhan, String> colSiSo = new TableColumn<>("Sĩ số");
        colSiSo.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getSiSoHienTai() + "/" + c.getValue().getSiSoToiDa()));

        TableColumn<LopHocPhan, Void> colAction = new TableColumn<>("Thao tác");
        colAction.setPrefWidth(320);
        colAction.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                LopHocPhan lhp = getTableView().getItems().get(getIndex());
                Button btnAtt = new Button("Điểm danh");
                btnAtt.getStyleClass().add("btn-primary");
                btnAtt.setOnAction(e -> navigator.showTeacherAttendance(lhp));

                Button btnGrade = new Button("Nhập điểm");
                btnGrade.getStyleClass().add("btn-success");
                btnGrade.setOnAction(e -> navigator.showTeacherGrading(lhp));

                Button btnManage = new Button("Quản lý lớp");
                btnManage.getStyleClass().add("btn-warning");
                btnManage.setOnAction(e -> navigator.showTeacherClassManager(lhp));

                btnAtt.setStyle("-fx-padding: 4 8; -fx-font-size: 11px;");
                btnGrade.setStyle("-fx-padding: 4 8; -fx-font-size: 11px;");
                btnManage.setStyle("-fx-padding: 4 8; -fx-font-size: 11px;");
                setGraphic(new HBox(6, btnAtt, btnGrade, btnManage));
            }
        });

        table.getColumns().addAll(colId, colSub, colRoom, colTime, colSiSo, colAction);
        table.setItems(FXCollections.observableArrayList(classes));
        return table;
    }

    public static BorderPane createAttendanceView(SceneNavigator navigator, LopHocPhan lhp) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Điểm danh — " + lhp.getMaLhp() + " | " +
                (lhp.getMonHoc() != null ? lhp.getMonHoc().getTenMH() : ""));
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        DatePicker datePicker = new DatePicker(LocalDate.now());

        TableView<DangKyHoc> table = new TableView<>();
        table.getStyleClass().add("table-view");
        TableColumn<DangKyHoc, String> colName = new TableColumn<>("Sinh viên");
        colName.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getSinhVien().getMaSV() + " — " + c.getValue().getSinhVien().getHoTen()));
        colName.setPrefWidth(350);

        TableColumn<DangKyHoc, Boolean> colCheck = new TableColumn<>("Có mặt");
        colCheck.setCellFactory(p -> new TableCell<>() {
            private final CheckBox cb = new CheckBox();
            {
                cb.setOnAction(e -> {
                    DangKyHoc item = getTableView().getItems().get(getIndex());
                    navigator.getDao().saveAttendance(lhp.getId(), item.getSinhVien().getMaSV(),
                            Date.valueOf(datePicker.getValue()), cb.isSelected());
                });
            }
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : cb);
            }
        });
        table.getColumns().addAll(colName, colCheck);
        table.setItems(FXCollections.observableArrayList(navigator.getDao().getStudentsInClass(lhp.getId())));

        VBox center = new VBox(15, title, new HBox(10, new Label("Ngày:"), datePicker), table);
        VBox.setVgrow(table, Priority.ALWAYS);
        root.setCenter(center);
        return root;
    }

    public static VBox createGradesHubContent(SceneNavigator navigator, String teacherUsername) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(25));

        Label title = new Label("QUẢN LÝ & XEM BẢNG ĐIỂM");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");

        String maGV = resolveMaGV(navigator, teacherUsername);
        List<LopHocPhan> classes = navigator.getDao().getClassesByTeacher(maGV);

        ComboBox<LopHocPhan> cbClass = new ComboBox<>(FXCollections.observableArrayList(classes));
        cbClass.getStyleClass().add("combo-box");
        cbClass.setPromptText("Chọn lớp học phần...");
        cbClass.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(LopHocPhan lhp) {
                if (lhp == null) return "";
                String ten = lhp.getMonHoc() != null ? lhp.getMonHoc().getTenMH() : "";
                return lhp.getMaLhp() + " — " + ten;
            }
            @Override
            public LopHocPhan fromString(String s) { return null; }
        });
        cbClass.setMaxWidth(600);

        TableView<Diem> table = buildGradeTable(navigator, null);
        table.setPrefHeight(400);
        VBox.setVgrow(table, Priority.ALWAYS);

        Label lblHint = new Label("Chọn lớp để xem/sửa điểm. Nhấp đúp ô QT hoặc Thi để nhập (0–10).");
        lblHint.setStyle("-fx-text-fill: #64748b;");

        Button btnLock = new Button("Khóa bảng điểm lớp đã chọn");
        btnLock.getStyleClass().add("btn-danger");
        btnLock.setOnAction(e -> {
            LopHocPhan sel = cbClass.getValue();
            if (sel == null) {
                new Alert(Alert.AlertType.WARNING, "Vui lòng chọn lớp học phần.").show();
                return;
            }
            navigator.getDao().lockGrades(sel.getId());
            table.setItems(FXCollections.observableArrayList(navigator.getDao().getGradesByClass(sel.getId())));
            new Alert(Alert.AlertType.INFORMATION, "Đã khóa điểm lớp " + sel.getMaLhp()).show();
        });

        Button btnOpen = new Button("Mở màn hình nhập điểm riêng");
        btnOpen.getStyleClass().add("btn-primary");
        btnOpen.setOnAction(e -> {
            if (cbClass.getValue() != null) {
                navigator.showTeacherGrading(cbClass.getValue());
            }
        });

        cbClass.setOnAction(e -> {
            LopHocPhan sel = cbClass.getValue();
            if (sel != null) {
                table.setItems(FXCollections.observableArrayList(navigator.getDao().getGradesByClass(sel.getId())));
            }
        });
        if (!classes.isEmpty()) {
            cbClass.getSelectionModel().selectFirst();
            LopHocPhan first = cbClass.getValue();
            if (first != null) {
                table.setItems(FXCollections.observableArrayList(navigator.getDao().getGradesByClass(first.getId())));
            }
        }

        root.getChildren().addAll(title, new HBox(10, new Label("Lớp:"), cbClass), lblHint, table,
                new HBox(12, btnOpen, btnLock));
        return root;
    }

    public static BorderPane createGradingView(SceneNavigator navigator, LopHocPhan lhp) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        String tenMon = lhp.getMonHoc() != null ? lhp.getMonHoc().getTenMH() : "";
        Label title = new Label("Nhập điểm — " + lhp.getMaLhp() + " | " + tenMon);
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        title.setWrapText(true);

        TableView<Diem> table = buildGradeTable(navigator, lhp);
        VBox.setVgrow(table, Priority.ALWAYS);

        Button btnLock = new Button("Khóa bảng điểm");
        btnLock.getStyleClass().add("btn-danger");
        btnLock.setOnAction(e -> {
            navigator.getDao().lockGrades(lhp.getId());
            table.setItems(FXCollections.observableArrayList(navigator.getDao().getGradesByClass(lhp.getId())));
            new Alert(Alert.AlertType.WARNING, "Đã khóa bảng điểm.").show();
        });

        Button btnBack = new Button("← Về quản lý điểm");
        btnBack.getStyleClass().add("btn-primary");
        btnBack.setOnAction(e -> navigator.showTeacherGrades());

        VBox bottom = new VBox(10, new Label("Nhấp đúp ô QT/Thi để sửa (0–10), Enter để lưu."), new HBox(10, btnBack, btnLock));
        root.setCenter(table);
        root.setTop(title);
        root.setBottom(bottom);
        BorderPane.setMargin(bottom, new Insets(10, 0, 0, 0));
        return root;
    }

    private static TableView<Diem> buildGradeTable(SceneNavigator navigator, LopHocPhan lhp) {
        TableView<Diem> table = new TableView<>();
        table.setEditable(true);
        table.getStyleClass().add("table-view");

        TableColumn<Diem, String> colMa = new TableColumn<>("Mã SV");
        colMa.setCellValueFactory(c -> new SimpleStringProperty(studentCode(c.getValue())));
        colMa.setPrefWidth(110);

        TableColumn<Diem, String> colSv = new TableColumn<>("Họ tên");
        colSv.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSinhVien().getHoTen()));
        colSv.setPrefWidth(180);

        TableColumn<Diem, String> colMon = new TableColumn<>("Môn học");
        colMon.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getLopHocPhan() != null && c.getValue().getLopHocPhan().getMonHoc() != null
                        ? c.getValue().getLopHocPhan().getMonHoc().getTenMH() : ""));
        colMon.setPrefWidth(220);

        TableColumn<Diem, Double> colQT = new TableColumn<>("QT");
        colQT.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getDiemQT()).asObject());
        colQT.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colQT.setOnEditCommit(e -> commitGrade(navigator, e.getRowValue(), e.getNewValue(), null, table));

        TableColumn<Diem, Double> colThi = new TableColumn<>("Thi");
        colThi.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getDiemThi()).asObject());
        colThi.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colThi.setOnEditCommit(e -> commitGrade(navigator, e.getRowValue(), null, e.getNewValue(), table));

        TableColumn<Diem, String> colTK = new TableColumn<>("Tổng");
        colTK.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getDiemTongKet())));

        TableColumn<Diem, String> colChu = new TableColumn<>("Chữ");
        colChu.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDiemChu() != null ? c.getValue().getDiemChu() : ""));

        TableColumn<Diem, String> colLock = new TableColumn<>("Trạng thái");
        colLock.setCellValueFactory(c -> new SimpleStringProperty(
                Boolean.TRUE.equals(c.getValue().getLocked()) ? "Đã khóa" : "Đang mở"));

        table.getColumns().addAll(colMa, colSv, colMon, colQT, colThi, colTK, colChu, colLock);
        if (lhp != null) {
            table.setItems(FXCollections.observableArrayList(navigator.getDao().getGradesByClass(lhp.getId())));
        }
        return table;
    }

    private static void commitGrade(SceneNavigator navigator, Diem d, Double qt, Double thi, TableView<Diem> table) {
        if (Boolean.TRUE.equals(d.getLocked())) {
            new Alert(Alert.AlertType.WARNING, "Bảng điểm đã khóa, không thể sửa.").show();
            table.refresh();
            return;
        }
        if (qt != null) d.setDiemQT(Math.min(10, Math.max(0, qt)));
        if (thi != null) d.setDiemThi(Math.min(10, Math.max(0, thi)));
        d.tinhDiem();
        navigator.getDao().updateGrade(d);
        table.refresh();
    }

    private static String studentCode(Diem diem) {
        if (diem == null || diem.getSinhVien() == null || diem.getSinhVien().getMaSV() == null) {
            return "";
        }
        return diem.getSinhVien().getMaSV();
    }
}

package com.example.dean12.desktop.view;
import com.example.dean12.desktop.controller.SceneNavigator;
import com.example.dean12.model.LopHocPhan;
import com.example.dean12.model.GiangVien;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import java.io.File;

public class TeacherAdvancedScenes {

    public static VBox createClassManagerView(SceneNavigator navigator, LopHocPhan lhp) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        
        Label title = new Label("Quản lý lớp: " + lhp.getMonHoc().getTenMH());
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Tabs for different features
        TabPane tabs = new TabPane();
        
        Tab tabMaterial = new Tab("Tài liệu học tập", createMaterialUploadContent(navigator, lhp));
        Tab tabNoti = new Tab("Gửi thông báo", createClassNotificationContent(navigator, lhp));
        
        tabMaterial.setClosable(false);
        tabNoti.setClosable(false);
        
        tabs.getTabs().addAll(tabMaterial, tabNoti);
        
        Button btnBack = new Button("Quay lại Dashboard");
        btnBack.setOnAction(e -> navigator.showTeacherDashboard());
        
        root.getChildren().addAll(title, btnBack, tabs);
        return root;
    }
    
    private static VBox createMaterialUploadContent(SceneNavigator navigator, LopHocPhan lhp) {
        VBox content = new VBox(15);
        content.setPadding(new Insets(15));
        
        Label lbl = new Label("Upload tài liệu cho sinh viên (Bài giảng, Bài tập...)");
        
        HBox uploadBox = new HBox(10);
        TextField txtPath = new TextField();
        txtPath.setPromptText("Đường dẫn file...");
        txtPath.setPrefWidth(300);
        
        Button btnBrowse = new Button("Chọn File");
        btnBrowse.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            File f = fc.showOpenDialog(null);
            if (f != null) {
                txtPath.setText(f.getAbsolutePath());
            }
        });
        
        TextField txtTitle = new TextField();
        txtTitle.setPromptText("Tên tài liệu (vd: Slide Chương 1)");
        
        Button btnUpload = new Button("Upload Lên Hệ Thống");
        btnUpload.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white;");
        btnUpload.setOnAction(e -> {
            if (txtPath.getText().isEmpty() || txtTitle.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Thiếu thông tin");
                return;
            }
            navigator.getTeacherController().uploadMaterial(lhp.getId(), txtTitle.getText(), txtPath.getText());
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã upload tài liệu: " + txtTitle.getText());
            txtPath.clear();
            txtTitle.clear();
        });
        
        uploadBox.getChildren().addAll(txtTitle, txtPath, btnBrowse);
        content.getChildren().addAll(lbl, uploadBox, btnUpload);
        return content;
    }
    
    private static VBox createClassNotificationContent(SceneNavigator navigator, LopHocPhan lhp) {
        VBox content = new VBox(15);
        content.setPadding(new Insets(15));
        
        Label lbl = new Label("Gửi thông báo riêng cho lớp này");
        
        TextField txtTitle = new TextField();
        txtTitle.setPromptText("Tiêu đề thông báo");
        
        TextArea txtMsg = new TextArea();
        txtMsg.setPromptText("Nội dung thông báo...");
        txtMsg.setPrefRowCount(4);
        
        Button btnSend = new Button("Gửi Ngay");
        btnSend.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white;");
        btnSend.setOnAction(e -> {
             if (txtTitle.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Thiếu tiêu đề");
                return;
            }
            // Target Role = CLASS, Target ID = lhp ID
            navigator.getTeacherController().createNotification(txtTitle.getText(), txtMsg.getText(), "CLASS", String.valueOf(lhp.getId()));
            showAlert(Alert.AlertType.INFORMATION, "Đã gửi", "Thông báo đã được gửi đến sinh viên lớp này.");
            txtTitle.clear();
            txtMsg.clear();
        });
        
        content.getChildren().addAll(lbl, txtTitle, txtMsg, btnSend);
        return content;
    }
    
    private static void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }
    private static void showAlert(Alert.AlertType type, String title) {
        showAlert(type, title, "");
    }
    public static VBox createProfileView(SceneNavigator navigator, GiangVien gv) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        
        Label title = new Label("Hồ sơ Giảng viên");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        grid.addRow(0, new Label("Mã GV:"), new Label(gv.getMaGV()));
        grid.addRow(1, new Label("Họ tên:"), new Label(gv.getHoTen()));
        
        TextField txtEmail = new TextField(gv.getEmail());
        TextField txtSdt = new TextField(gv.getSdt() != null ? gv.getSdt() : "");
        
        grid.addRow(2, new Label("Email:"), txtEmail);
        grid.addRow(3, new Label("SĐT:"), txtSdt);
        
        Button btnUpdate = new Button("Lưu Thay Đổi");
        btnUpdate.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white;");
        btnUpdate.setOnAction(e -> {
            navigator.getTeacherController().updateTeacherProfile(gv.getMaGV(), txtEmail.getText(), txtSdt.getText());
            gv.setEmail(txtEmail.getText());
            gv.setSdt(txtSdt.getText());
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật thông tin!");
        });
        
        root.getChildren().addAll(title, grid, btnUpdate);
        return root;
    }
}

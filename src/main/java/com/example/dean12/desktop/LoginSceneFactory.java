package com.example.dean12.desktop;

import com.example.dean12.model.User;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class LoginSceneFactory {

    public static Scene createLoginScene(SceneNavigator navigator) {
        VBox root = new VBox(14);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(32));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #102a43, #1f2933);");

        Label title = new Label("QLSV DESKTOP");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Segoe UI", 30));
        title.setStyle("-fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Ten dang nhap: admin, gv01, sv01...");
        usernameField.setMaxWidth(300);
        usernameField.setPrefHeight(42);
        usernameField.getStyleClass().add("text-field");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mat khau mau: 123");
        passwordField.setMaxWidth(300);
        passwordField.setPrefHeight(42);
        passwordField.getStyleClass().add("text-field");

        Label error = new Label();
        error.setTextFill(Color.web("#fecaca"));
        error.setFont(Font.font("Segoe UI", 12));
        error.setWrapText(true);
        error.setMaxWidth(320);
        error.setAlignment(Pos.CENTER);

        Button loginBtn = new Button("DANG NHAP");
        loginBtn.setDefaultButton(true);
        loginBtn.setMaxWidth(300);
        loginBtn.setPrefHeight(42);
        loginBtn.getStyleClass().add("btn-primary");

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                error.setText("Vui long nhap day du ten dang nhap va mat khau.");
                return;
            }

            loginBtn.setDisable(true);
            error.setText("");

            Task<User> loginTask = new Task<>() {
                @Override
                protected User call() {
                    return navigator.getDao().login(username, password);
                }
            };

            loginTask.setOnSucceeded(done -> {
                User user = loginTask.getValue();
                loginBtn.setDisable(false);
                if (user == null) {
                    error.setText("Dang nhap that bai. Hay chay START_SERVER.bat va kiem tra tai khoan.");
                    return;
                }
                navigator.showDashboard(user);
            });

            loginTask.setOnFailed(done -> {
                loginBtn.setDisable(false);
                error.setText("Loi ket noi server: " + loginTask.getException().getMessage());
            });

            Thread thread = new Thread(loginTask, "client-login-task");
            thread.setDaemon(true);
            thread.start();
        });

        Platform.runLater(usernameField::requestFocus);
        root.getChildren().addAll(title, usernameField, passwordField, loginBtn, error);

        Scene scene = new Scene(root, 520, 440);
        try {
            String cssPath = LoginSceneFactory.class.getResource("/com/example/dean12/desktop/style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception ex) {
            System.err.println("[UI] Stylesheet not loaded: " + ex.getMessage());
        }

        return scene;
    }
}

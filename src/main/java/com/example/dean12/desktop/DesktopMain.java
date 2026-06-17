package com.example.dean12.desktop;

import javafx.application.Application;
import javafx.stage.Stage;

/*Điểm vào cho ứng dụng desktop JavaFX .
  không cần mở giao diện web/HTML.
 */
public class DesktopMain extends Application {

    @Override
    public void start(Stage primaryStage) {
        new DesktopDao().initializeDatabaseSchema();
        
        SceneNavigator navigator = new SceneNavigator(primaryStage);
        navigator.showLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


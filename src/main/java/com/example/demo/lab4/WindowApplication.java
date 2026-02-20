package com.example.demo.lab4;

import javafx.application.Application;
import javafx.stage.Stage;

public class WindowApplication extends Application {

    @Override
    public void start(Stage stage) {
        AppState state = new AppState();
        WindowNavigator.open(null, "step1-function.fxml", "Крок 1: Вибір функції", state, 600, 300);
    }

    public static void main(String[] args) {
        launch();
    }
}

package com.example.demo.lab2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TableApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader =
                new FXMLLoader(TableApplication.class.getResource("table-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 900, 520);

        stage.setTitle("Таблиця значень функції");
        stage.setMinWidth(650);
        stage.setMinHeight(420);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

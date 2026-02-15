package com.example.demo.lab1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FunctionApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader =
                new FXMLLoader(FunctionApplication.class.getResource("function-view.fxml"));


        Scene scene = new Scene(fxmlLoader.load(), 850, 320);

        stage.setTitle("Обчислення функції");
        stage.setMinWidth(550);
        stage.setMinHeight(280);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

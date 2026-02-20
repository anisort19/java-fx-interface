package com.example.demo.lab3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PlotApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader =
                new FXMLLoader(PlotApplication.class.getResource("plot-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 900, 520);

        stage.setTitle("Графік функції");
        stage.setMinWidth(650);
        stage.setMinHeight(750);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

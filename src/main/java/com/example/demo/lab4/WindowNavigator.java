package com.example.demo.lab4;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WindowNavigator {

    public interface StateAware {
        void setState(AppState state);
        default void setStage(Stage stage) {}
    }

    public static void open(Stage current, String fxml, String title, AppState state,
                            double w, double h) {
        try {
            FXMLLoader loader = new FXMLLoader(WindowNavigator.class.getResource(fxml));
            Parent root = loader.load();

            Object controller = loader.getController();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, w, h));

            if (controller instanceof StateAware aware) {
                aware.setState(state);
                aware.setStage(stage);
            }

            stage.show();
            if (current != null) current.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

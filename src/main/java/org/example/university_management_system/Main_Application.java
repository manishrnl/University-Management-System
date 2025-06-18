package org.example.university_management_system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.university_management_system.ToolsClasses.NavigationManager;

import java.io.IOException;

public class Main_Application extends Application {

    NavigationManager navigationManager = NavigationManager.getInstance();

    @Override
    public void start(Stage stage) throws IOException {
//        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
//        Stage newStage = new Stage();
//        newStage.setScene(new Scene(root));
//        newStage.setTitle("Please Login to Access our System");
//        navigationManager.setPrimaryStage(stage);
//        newStage.showAndWait();

        navigationManager.getInstance().setPrimaryStage(stage);
        navigationManager.getInstance().navigateTo("Login.fxml"
        );
    }

    public static void main(String[] args) {
        launch();
    }
}
package org.example.university_management_system.ToolsClasses;

import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.university_management_system.Java_StyleSheet.Theme_Manager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NavigationManager {
    private static final NavigationManager instance = new NavigationManager();
    private Stage currentStage;
    private final List<String> history = new ArrayList<>();
    private int currentIndex = -1;

    private NavigationManager() {
    }

    public static NavigationManager getInstance() {
        return instance;
    }

    public void setPrimaryStage(Stage initialStage) {
        this.currentStage = initialStage;
    }

    public void navigateTo(String fxmlPath) {
        if (currentStage == null) {
            System.err.println("Error: Primary stage has not been set.");
            return;
        }

        // --- NEW --- Create and show the loading indicator window
        Stage loadingStage = createLoadingStage();
        loadingStage.show();

        // --- NEW --- Create a background task to load the FXML
        Task<Parent> loadTask = new Task<>() {
            @Override
            protected Parent call() throws IOException {
                URL resourceUrl = getClass().getResource("/org/example/university_management_system/" + fxmlPath);
                if (resourceUrl == null) {
                    throw new IOException("Could not find FXML file: " + fxmlPath);
                }
                FXMLLoader loader = new FXMLLoader(resourceUrl);
                return loader.load();
            }
        };

        // --- NEW --- Define what happens when the loading is successful
        loadTask.setOnSucceeded(event -> {
            Parent root = loadTask.getValue();
            loadingStage.close(); // Close the loading spinner
            currentStage.close(); // Close the old main window

            // All the logic to set up the new stage
            boolean isUndecorated = fxmlPath.endsWith("Login.fxml") || fxmlPath.endsWith("ForgetPassword.fxml"); // Simplified
            Stage newStage = new Stage();
            newStage.initStyle(isUndecorated ? StageStyle.TRANSPARENT : StageStyle.DECORATED);

            Scene scene = new Scene(root);
            scene.setFill(null);
            Theme_Manager.applyTheme(scene);
            newStage.setScene(scene);

            String title = fxmlPath.replace(".fxml", "");
            if (title.contains("/")) {
                title = title.substring(title.lastIndexOf('/') + 1);
            }
            newStage.setTitle(title + " - University App");
            newStage.show();
            this.currentStage = newStage;
            addToHistory(fxmlPath);
        });

        // --- NEW --- Define what happens if the loading fails
        loadTask.setOnFailed(event -> {
            loadingStage.close();
            Throwable e = loadTask.getException();
            System.err.println("Failed to load FXML file: " + fxmlPath);
            e.printStackTrace();
            // Optionally show an alert to the user
        });

        // --- NEW --- Start the background task
        new Thread(loadTask).start();
    }

    // --- NEW METHOD ---
    /**
     * Creates a simple, undecorated stage with a spinning ProgressIndicator.
     * @return A non-interactive Stage to be used as a loading indicator.
     */
    private Stage createLoadingStage() {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        if (currentStage != null) {
            stage.initOwner(currentStage);
        }

        ProgressIndicator pIndicator = new ProgressIndicator();
        pIndicator.setPrefSize(80, 80);

        VBox vbox = new VBox(pIndicator);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2);"); // Semi-transparent background

        Scene scene = new Scene(vbox, 200, 200);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);

        return stage;
    }


    private void addToHistory(String fxmlPath) {
        if (currentIndex < history.size() - 1) {
            history.subList(currentIndex + 1, history.size()).clear();
        }
        history.add(fxmlPath);
        currentIndex++;
    }

    public void goBack() {
        if (canGoBack()) {
            currentIndex--;
            // Re-use navigateTo so it shows the loading spinner on "back" too
            navigateTo(history.get(currentIndex));
        } else {
            System.out.println("No more history to go back to.");
        }
    }

    public void goForward() {
        if (canGoForward()) {
            currentIndex++;
            // Re-use navigateTo for "forward" navigation
            navigateTo(history.get(currentIndex));
        } else {
            System.out.println("No more history to go forward to.");
        }
    }

    public void loadNewFrame(Class<?> controllerClass, String fxmlFileName, String title, boolean hideHeader) throws IOException {
        URL resourceUrl = controllerClass.getResource("/org/example/university_management_system/" + fxmlFileName);
        if (resourceUrl == null) {
            System.err.println("Could not find FXML file: " + fxmlFileName);
            return;
        }

        Parent root = FXMLLoader.load(resourceUrl);
        Stage newStage = new Stage();

        if (hideHeader) {
            newStage.initStyle(StageStyle.UNDECORATED);
        } else {
            newStage.initStyle(StageStyle.DECORATED);
            newStage.setTitle(title);
        }
        newStage.setScene(new Scene(root));
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.initOwner(this.currentStage);
        newStage.showAndWait();
    }

    public boolean canGoBack() {
        return currentIndex > 0;
    }

    public boolean canGoForward() {
        return currentIndex < history.size() - 1;
    }
}
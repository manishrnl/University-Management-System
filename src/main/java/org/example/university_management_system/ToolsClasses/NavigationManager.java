package org.example.university_management_system.ToolsClasses;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NavigationManager {
    private String currentFxml = "/org/example/university_management_system/", fxmlPath = "/org/example/university_management_system/"; // Track what's actually loaded

    String UpdatedfxmlPath;
    private static NavigationManager instance; // Singleton instance
    private Stage primaryStage; // The main stage to update
    private List<String> history = new ArrayList<>(); // Stores FXML paths
    private int currentIndex = -1; // -1 means no page loaded yet

    private NavigationManager() {
    }

    public static NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void navigateTo(String fxmlPath) {
        if (primaryStage == null) {
            System.err.println("Error: Primary stage not set in NavigationManager.");
            return;
        }

        // ✅ Prevent reloading same FXML
        if (fxmlPath.equals(currentFxml)) {
            System.out.println("Already on this page: " + fxmlPath);

            // ✅ Ensure app window is visible and in front
            primaryStage.setIconified(false); // Restore from minimized if needed
            primaryStage.show();              // Show if hidden
            primaryStage.toFront();           // Bring to front
            primaryStage.requestFocus();      // Focus window

            return;
        }

        try {
            // Clear future history if navigating after going back
            if (currentIndex != -1 && currentIndex < history.size() - 1) {
                history = new ArrayList<>(history.subList(0, currentIndex + 1));
            }

            loadAndSetScene(fxmlPath);

        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public void goBack() {
        if (currentIndex > 0) {
            currentIndex--;
            String target = history.get(currentIndex);
            try {
                System.out.println("Going back to: " + target);
                if (!target.contains("Login.fxml") && !target.contains("Signup.fxml") && !target.contains("ForgetPassword.fxml")) {
                    loadAndSetScene(target);
                } else {
                    System.out.println("Back navigation to " + target + " is disabled.");
                    currentIndex++; // restore the index to stay on current page
                }
            } catch (IOException e) {
                System.err.println("Failed to load FXML for back navigation: " + target);
                e.printStackTrace();
            }
        } else {
            System.out.println("No more history to go back to.");
        }
    }


    public void goForward() {
        if (currentIndex < history.size() - 1) {
            currentIndex++;
            try {
                if (!history.contains(UpdatedfxmlPath))
                    loadAndSetScene(history.get(currentIndex));
            } catch (IOException e) {
                System.err.println("Failed to load FXML for forward navigation: " + history.get(currentIndex));
                e.printStackTrace();
            }
        } else {
            //  navigateForwards.setDisable(true);
            System.out.println("No more history to go forward to.");
        }
    }

    private void loadAndSetScene(String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/org/example/university_management_system/" + fxmlPath));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        String cleanName = fxmlPath.replace(".fxml", "").replace("-", " ");
        String title = cleanName.substring(0, 1).toUpperCase() + cleanName.substring(1);
        primaryStage.setTitle(title + " - Custom App");

        if (!primaryStage.isShowing()) {
            primaryStage.show();
        }

        // ✅ Update history and currentFxml
        if (!history.contains(fxmlPath)) {
            history.add(fxmlPath);
        }
        currentIndex = history.indexOf(fxmlPath);
        currentFxml = fxmlPath;  // <- important!
    }


    public boolean canGoBack() {
        return currentIndex > 0;
    }

    public boolean canGoForward() {
        return currentIndex < history.size() - 1;
    }
}
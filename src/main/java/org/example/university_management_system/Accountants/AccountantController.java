package org.example.university_management_system.Accountants;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.university_management_system.ToolsClasses.NavigationManager;

import javax.swing.text.html.ImageView;
import java.awt.*;

public class AccountantController {

    NavigationManager navigationManager = NavigationManager.getInstance();
    @FXML
    private MenuBar customMenuBar;

    @FXML
    private ImageView navigateBackward;

    @FXML
    private ImageView navigateForward;

    @FXML
    private VBox root;

    @FXML
    private HBox titleBar;



    @FXML
    void navigateBackward(javafx.scene.input.MouseEvent mouseEvent) {
        navigationManager.goBack();

    }

    @FXML
    void navigateForward(javafx.scene.input.MouseEvent mouseEvent) {
        navigationManager.goForward();

    }

    public void handleMenuOption(ActionEvent actionEvent) {
    }

    public void gotoOthersPage(MouseEvent mouseEvent) {
        Stage currentStage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        currentStage.close();
        navigationManager.navigateTo("Admin/MainDashboard.fxml");
    }
}

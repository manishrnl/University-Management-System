package org.example.university_management_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.university_management_system.Databases.DatabaseConnection;
import org.example.university_management_system.ToolsClasses.LoadFrame;

import java.io.IOException;
import java.sql.*;

public class ForgetPassword_Controller {
    LoadFrame loadFrame;
    @FXML
    private TextField aadharNumberField, UsernameField, mobileNumberField, panNumberField, registeredEmailField;

    @FXML
    private PasswordField confirmPasswordField, passwordField;

    @FXML
    private Label errorMessageLabel, statusMessageLabel;


    @FXML
    void handleBackToLoginButton(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        loadFrame.loadNewFrame(currentStage, getClass(), "Login.fxml", "Login to Access our " +
                "dashboard");

    }

    @FXML
    void handleResetPasswordButton(ActionEvent event) throws SQLException {
        Boolean validUser = false;
        statusMessageLabel.setText("");
        errorMessageLabel.setText("");

        String pan = panNumberField.getText().trim();
        // Corrected: Use emailUsernameField as per FXML for username/email input
        String username = UsernameField.getText().trim();
        String email = registeredEmailField.getText().trim();
        String mobileStr = mobileNumberField.getText().trim();
        String aadharStr = aadharNumberField.getText().trim();
        String newPassword = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        long mobile;
        long aadhar;

        // Basic validation: Check if all fields are filled
        if (pan.isEmpty() || email.isEmpty() || username.isEmpty() || mobileStr.isEmpty() || aadharStr.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            errorMessageLabel.setText("All fields are required.");
            return;
        }

        try {
            aadhar = Long.parseLong(aadharStr);
            mobile = Long.parseLong(mobileStr);
        } catch (NumberFormatException e) {
            errorMessageLabel.setText("Aadhar and Mobile Number must be numeric.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            errorMessageLabel.setText("New password and confirm password do not match.");
            return;
        }

        String verifyQuery = "SELECT User_Id FROM Users WHERE Pan = ? AND UserName = ? AND Email = ? AND Aadhar = ? AND Mobile = ?";
        String updatePasswordQuery = "UPDATE Authentication SET Password_Hash = ? WHERE User_Id = ?"; // Assuming UserID is primary key


        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(verifyQuery)) {
            pstmt.setString(1, pan);
            pstmt.setString(2, username);
            pstmt.setString(3, email);
            pstmt.setLong(4, aadhar);
            pstmt.setLong(5, mobile);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                validUser = true;
                int UserID = resultSet.getInt("User_Id");
                PreparedStatement pstmt1 = connection.prepareStatement(updatePasswordQuery);

                pstmt1.setString(1, newPassword);
                pstmt1.setInt(2, UserID);

                int rowAffected = pstmt1.executeUpdate();
                if (rowAffected > 0)
                    errorMessageLabel.setText("Credential is valid.Password Changed");
                else
                    errorMessageLabel.setText("Could not change PAssword");
            }
            if (!validUser)
                statusMessageLabel.setText("Not a valid User. Double Check that you have " +
                        "entered correct data");

        } catch (Exception e) {
            errorMessageLabel.setText("Something went wrong while trying to connect with " +
                    "databases" + e.getMessage());
        }


    }
}



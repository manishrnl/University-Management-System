package org.example.university_management_system;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.university_management_system.Databases.DatabaseConnection;
import org.example.university_management_system.ToolsClasses.LoadFrame;
import org.example.university_management_system.ToolsClasses.NavigationManager;
import org.example.university_management_system.ToolsClasses.SessionManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Login_Controller implements Initializable {

    NavigationManager navigationManager = NavigationManager.getInstance();
    LoadFrame loadFrame = new LoadFrame();
    SessionManager sessionManager = SessionManager.getInstance();

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField usernameField;
    @FXML
    public Label errorLabel;

    private static int passwordCount = 3;
    private static final int MAX_ATTEMPTS = 3;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        LoadFrame.setMessage(errorLabel, "", "GREEN");
        passwordCount = MAX_ATTEMPTS;
    }

    @FXML
    void handleForgotPassword(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            loadFrame.loadNewFrame(currentStage, getClass(), "ForgetPassword.fxml", "Password Reset");
        } catch (IOException ex) {
            LoadFrame.setMessage(errorLabel, "Error loading password reset page " + ex.getMessage(), "RED");

        }
    }

    @FXML
    void handleSignupButton(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            loadFrame.loadNewFrame(currentStage, getClass(), "Signup.fxml", "Welcome to Signup Page");
        } catch (IOException e) {
            LoadFrame.setMessage(errorLabel, "Error loading signup page.", "RED");
        }
    }

    private boolean validateUserName(String userName) {
        String query = "SELECT Lockout_Until FROM Authentication WHERE UserName = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Timestamp lockoutUntil = rs.getTimestamp("Lockout_Until");
                if (lockoutUntil != null && lockoutUntil.toLocalDateTime().isAfter(LocalDateTime.now())) {

                    LocalDateTime dateTime = lockoutUntil.toLocalDateTime();
                    LocalDate date = dateTime.toLocalDate();
                    LocalTime databaseTime = dateTime.toLocalTime();   // Time from DB
                    LocalTime currentTime = LocalTime.now();           // Current time

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                    String formattedDate = date.format(formatter);// Output: July 5, 2025

                    Duration duration = Duration.between(currentTime, databaseTime);
                    long minutes = duration.toMinutes();
                    LoadFrame.setMessage(errorLabel,"Account is locked for date  " + formattedDate + "  , Please  " +
                                    "Try again after time -  " + minutes + " minutes.","RED");
                    return false;
                }
                return true;
            } else {
                LoadFrame.setMessage(errorLabel, "Invalid Username.", "RED");
                return false;
            }
        } catch (SQLException e) {
            LoadFrame.setMessage(errorLabel, "Database error during username validation: " + e.getMessage(), "RED");

        }
        return false;
    }

    private boolean validatePasswordAndManageAttempts(String userName, String password) throws SQLException {
        String query = "SELECT * FROM Authentication WHERE UserName = ? AND Password_Hash = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                passwordCount = MAX_ATTEMPTS;
                clearLockout(userName, connection);
                LoadFrame.setMessage(errorLabel, "Initiating Login Process....", "GREEN");
                return true;
            } else {
                --passwordCount;
                if (passwordCount > 0) {
                    LoadFrame.setMessage(errorLabel, "Password Incorrect. You have " +
                            passwordCount + " attempts left.", "RED");
                } else {
                    setLockout(userName, connection);
                    LoadFrame.setMessage(errorLabel, "You have exhausted all attempts. " +
                            "Your account is locked for 2 hours. Please contact admin to reset your password or try again later.", "RED");
                    return false;
                }
            }
            return false;
        }
    }

    private void setLockout(String userName, Connection connection) {
        String updateQuery = "UPDATE Authentication SET Lockout_Until = ? WHERE UserName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now().plusHours(2)));
            pstmt.setString(2, userName);
            pstmt.executeUpdate();
            System.out.println("Login_Controller: User '" + userName + "' locked out.");
        } catch (SQLException ex) {
            LoadFrame.setMessage(errorLabel, "Error while locking user account: " + ex.getMessage(), "RED");
            System.err.println("Login_Controller: Exception while blocking user: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void clearLockout(String userName, Connection connection) {
        String updateQuery = "UPDATE Authentication SET Lockout_Until = NULL WHERE UserName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
            pstmt.setString(1, userName);
            pstmt.executeUpdate();
            System.out.println("Login_Controller: User '" + userName + "' lockout cleared.");
        } catch (SQLException ex) {
            LoadFrame.setMessage(errorLabel, "Error while clearing user lockout: " + ex.getMessage(), "RED");
            System.err.println("Login_Controller: Exception while clearing user lockout: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    void handleLoginButton(ActionEvent event) {
        String userName = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (userName.isEmpty() || password.isEmpty()) {
            LoadFrame.setMessage(errorLabel, "All Fields are required.", "RED");
            return;
        }
        try {
            if (!validateUserName(userName)) {
                return;
            }
            if (!validatePasswordAndManageAttempts(userName, password)) {
                return;
            }
            String authQuery = "SELECT User_Id FROM Authentication WHERE UserName = ?";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement authPstmt = connection.prepareStatement(authQuery)) {

                authPstmt.setString(1, userName);
                System.out.println("Login_Controller: Executing authentication query for User: " + userName);
                ResultSet authResultSet = authPstmt.executeQuery();

                if (authResultSet.next()) {
                    System.out.println("Login_Controller: User authenticated successfully.");

                    int userID = authResultSet.getInt("User_Id");
                    sessionManager.setUserID(userID);
                    sessionManager.setUserName(userName);

                    String userDetailsQuery = "SELECT Role, User_Status, Admin_Approval_Status, First_Name, Last_Name " +
                            "FROM Users WHERE User_Id = ?";

                    try (PreparedStatement userDetailsPstmt = connection.prepareStatement(userDetailsQuery)) {
                        userDetailsPstmt.setInt(1, userID);
                        System.out.println("Login_Controller: Fetching user details for User_Id: " + userID);
                        ResultSet userDetailsResultSet = userDetailsPstmt.executeQuery();

                        if (userDetailsResultSet.next()) {
                            String roleType = userDetailsResultSet.getString("Role");
                            String accountStatus = userDetailsResultSet.getString("User_Status");
                            String adminApproved = userDetailsResultSet.getString("Admin_Approval_Status");
                            String firstName = userDetailsResultSet.getString("First_Name");
                            String lastName = userDetailsResultSet.getString("Last_Name");

                            sessionManager.setFirstName(firstName);
                            sessionManager.setLastName(lastName);
                            sessionManager.setRole(roleType); // Store role in session

                            if (!"Active".equals(accountStatus)) {
                                LoadFrame.setMessage(errorLabel, "Account is not active. Contact admin to activate.", "RED");
                                return;
                            }
                            if ("Rejected".equals(adminApproved)) {
                                LoadFrame.setMessage(errorLabel, "Your Account is Rejected. Contact admin for more info.", "RED");
                                return;
                            }
                            if ("Pending".equals(adminApproved)) {
                                LoadFrame.setMessage(errorLabel, "Your Account is still in Pending Mode. Wait for admin approval or contact admin for more info.", "RED");
                                return;
                            }
                            if ("Approved".equals(adminApproved) && accountStatus.equals("Active")) {
                                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                String targetFxmlPath = "";
                                switch (roleType) {
                                    case "Student":
                                        targetFxmlPath = "Students/StudentsDashboard.fxml";
                                        break;
                                    case "Admin":
                                        targetFxmlPath = "Admin/AdminDashboard.fxml";
                                        break;
                                    case "Teacher":
                                        targetFxmlPath = "Teachers/TeachersDashboard.fxml";
                                        break;
                                    case "Staff":
                                        targetFxmlPath = "Staffs/StaffsDashboard.fxml";
                                        break;
                                    case "Accountant":
                                        targetFxmlPath = "Accountants/AccountantsDashboard.fxml";
                                        break;
                                    case "Librarian":
                                        targetFxmlPath = "Librarians/LibrariansDashboard.fxml";
                                        break;
                                    default:
                                        LoadFrame.setMessage(errorLabel, "Unknown user role " +
                                                "Type in Login Controller : " + roleType +
                                                ". Contact support.", "RED");
                                        return;
                                }

                                if (!targetFxmlPath.isEmpty()) {
                                    LoadFrame.setMessage(errorLabel, "Login successful!", "GREEN");
                                    currentStage.close();
                                    navigationManager.navigateTo(targetFxmlPath);
                                }
                            }

                        } else {
                            LoadFrame.setMessage(errorLabel, "Internal Error: User details missing. Please contact support.", "RED");
                        }
                    } catch (SQLException ex) {
                        LoadFrame.setMessage(errorLabel, "Error during dashboard loading: " + ex.getMessage(), "RED");
                    }
                } else {
                    LoadFrame.setMessage(errorLabel, "Authentication failed. Please try again.", "RED");
                }
            }
        } catch (SQLException ex) {
            LoadFrame.setMessage(errorLabel, "Database error during login process: " + ex.getMessage(), "RED");
        } catch (Exception ex) {
            LoadFrame.setMessage(errorLabel, "An unexpected error occurred during login: " + ex.getMessage(), "RED");
        }
    }


}


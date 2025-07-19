package org.example.university_management_system;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.university_management_system.CommonTable.GroundStaffTable;
import org.example.university_management_system.CommonTable.NotificationsTable;
import org.example.university_management_system.Databases.AuthenticationSQLQuery;
import org.example.university_management_system.Databases.DatabaseConnection;
import org.example.university_management_system.Databases.UsersSQLQuery;
import org.example.university_management_system.Java_StyleSheet.Theme_Manager;
import org.example.university_management_system.ToolsClasses.AlertManager;
import org.example.university_management_system.ToolsClasses.LoadFrame;
import org.example.university_management_system.ToolsClasses.SessionManager;
import org.example.university_management_system.ToolsClasses.WrappedTextCellFactory;

import javax.xml.transform.Result;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class Notification_Controller implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private DatePicker expiryDatePicker;
    @FXML
    private TextField targetUserNameField, messageField, txtSearch, titleField, targetUserIDField;
    @FXML
    private Button btnRefresh, markAsDoneButton;
    @FXML
    private TableColumn<NotificationsTable, String> colIsRead, colMessage, colTargetRole,
            colTitle, colCreatedOn, colExpiryDate, colTargetUserName;
    @FXML
    private TableColumn<NotificationsTable, Integer> colTargetUserID;
    @FXML
    private ComboBox<String> comboRoleFilter, targetRoleCombo;
    @FXML
    private TableView<NotificationsTable> notificationsTable;
    ObservableList<NotificationsTable> data = FXCollections.observableArrayList();

    SessionManager sessionManager = SessionManager.getInstance();
    LoadFrame loadFrame;
    AlertManager alertManager;
    Connection connection = DatabaseConnection.getConnection();
    UsersSQLQuery usersSQLQuery = new UsersSQLQuery();
    AuthenticationSQLQuery authenticationSQLQuery = new AuthenticationSQLQuery();

    public Notification_Controller() throws SQLException {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        targetUserNameField.setText("");

        targetUserIDField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            targetRoleCombo.setValue(usersSQLQuery.getRoleType(Integer.parseInt(targetUserIDField.getText()), errorMessageLabel));
            targetUserNameField.setText(authenticationSQLQuery.getUserNameFromAuthentication(Integer.parseInt(targetUserIDField.getText()), errorMessageLabel));
        });

        txtSearch.textProperty().addListener((observableValue, oldValue, newValue) -> searchData(newValue));
        colCreatedOn.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().createdOnDateProperty());
        colExpiryDate.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().expiryDateProperty().asString());
        colIsRead.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().isReadProperty());
        colMessage.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().messageProperty());
        // Styling for wrap column for Table View for Message Table
        // colMessage.setCellFactory(new WrappedTextCellFactory<NotificationsTable, String>());

        colMessage.setCellFactory(new WrappedTextCellFactory<NotificationsTable, String>());
        colTitle.setCellFactory(new WrappedTextCellFactory<NotificationsTable, String>());
        colIsRead.setCellFactory(new WrappedTextCellFactory<NotificationsTable, String>());
        colExpiryDate.setCellFactory(new WrappedTextCellFactory<NotificationsTable, String>());
        colTargetUserName.setCellFactory(new WrappedTextCellFactory<NotificationsTable, String>());
        colTargetUserID.setCellFactory(new WrappedTextCellFactory<NotificationsTable, Integer>());
        colCreatedOn.setCellFactory(new WrappedTextCellFactory<NotificationsTable, String>());
        colTargetRole.setCellFactory(new WrappedTextCellFactory<NotificationsTable, String>());

        colTargetRole.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().targetRoleProperty());
        colTitle.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().titleProperty());
        loadsNotifications("");
        colTargetUserID.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().targetUserIdProperty().asObject());
        colTargetUserName.cellValueFactoryProperty().setValue(cellData -> cellData.getValue().targetUserNameProperty());
        comboRoleFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            txtSearch.setText("");
            loadsNotifications(newValue);
        });
        //To highlight particular row based on condition item.getTargetUserId() == sessionManager
        // .getUserID()
        notificationsTable.setRowFactory(tv -> new TableRow<NotificationsTable>() {
            @Override
            protected void updateItem(NotificationsTable item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.getTargetUserId() == sessionManager.getUserID()) {
                    setStyle("-fx-background-color: #88ea77;");
                } else if (item.getTargetUserId() == 0 ) {
                    setStyle("-fx-background-color: #e1de3e;");
                }
                else {
                    setStyle("");
                }
            }
        });

        notificationsTable.setOnMouseClicked(mouseEvent -> {
            NotificationsTable selectedRow = notificationsTable.getSelectionModel().getSelectedItem();
            if (selectedRow != null) {
                errorMessageLabel.setText("");
                refreshMarkAsDoneButtonText(selectedRow.getIsRead());
                titleField.setText(selectedRow.getTitle());
                messageField.setText(selectedRow.getMessage());
                targetRoleCombo.setValue(selectedRow.getTargetRole());
                expiryDatePicker.setValue(selectedRow.getExpiryDate());
                targetUserIDField.setText(String.valueOf(selectedRow.getTargetUserId()));
                targetUserNameField.setText(selectedRow.getTargetUserName());
            }
        });
        Platform.runLater(() -> Theme_Manager.applyTheme(root.getScene()));
    }

    private void refreshMarkAsDoneButtonText(String isRead) {
        markAsDoneButton.setText("Read".equals(isRead) ? "Mark as Incomplete" : "Mark as " +
                "Complete");
    }

    private void searchData(String newValue) {
        int count = 0;
        ObservableList<NotificationsTable> filteredData = FXCollections.observableArrayList();
        String lowerNewValue = newValue.toLowerCase();

        for (NotificationsTable notification : data) {
            boolean matches = false;

            if (safeContains(notification.getTitle(), lowerNewValue) ||
                    safeContains(notification.getTargetUserName(), lowerNewValue) ||
                    safeContains(notification.getIsRead(), lowerNewValue) ||
                    safeContains(notification.getTargetRole(), lowerNewValue) ||
                    safeContains(notification.getMessage(), lowerNewValue)) {
                matches = true;
            }
            try {
                int userId = Integer.parseInt(newValue);
                if (notification.getTargetUserId() == userId) {
                    matches = true;
                }
            } catch (NumberFormatException ignored) {
            }

            if (matches) {
                filteredData.add(notification);
                count++;
            }
        }
        notificationsTable.setItems(filteredData);
        loadFrame.setMessage(errorMessageLabel,
                "Found total of " + count + " notifications for fields: " + newValue, "GREEN");
    }

    private boolean safeContains(String source, String targetLower) {
        return source != null && source.toLowerCase().contains(targetLower);
    }

    private void loadsNotifications(String filterRole) {
        data.clear();
        int count = 0;
        notificationsTable.getItems().clear();
        String query;
        if (filterRole == null || filterRole.isEmpty() || filterRole.equals("All Roles")) {
            query = "SELECT * FROM Notifications ";
        } else {
            query = "SELECT * FROM Notifications WHERE Target_Role = ?";
        }

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            if (filterRole != null && !filterRole.isEmpty() && !filterRole.equals("All Roles")) {
                pstmt.setString(1, filterRole);
            }


            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String Title = resultSet.getString("Title");
                String Message = resultSet.getString("Message");
                String TargetRole = resultSet.getString("Target_Role");
                String IsRead = resultSet.getString("Is_Read");
                String CreatedOnDate = resultSet.getString("Created_On");
                LocalDate ExpiryDate = resultSet.getDate("Expiry_Date").toLocalDate();
                String readOn = resultSet.getString("Read_On");
                int Notification_Id = resultSet.getInt("Notification_Id");
                int Created_By_User_Id = resultSet.getInt("Created_By_User_Id");
                int TargetUserId = resultSet.getInt("Target_User_Id");
                String TargetUserName = authenticationSQLQuery.getUserNameFromAuthentication(TargetUserId, errorMessageLabel);
                ++count;
                NotificationsTable table = new NotificationsTable(Title, Message, TargetRole,
                        IsRead, CreatedOnDate, ExpiryDate, readOn, Notification_Id,
                        Created_By_User_Id, TargetUserId, TargetUserName);
                data.add(table);
            }
            notificationsTable.setItems(data);
            loadFrame.setMessage(errorMessageLabel, "Found total of " + count + " notifications for fields : " + filterRole,
                    "GREEN");
        } catch (Exception ex) {
            loadFrame.setMessage(errorMessageLabel, "", "RED");
        }
    }

    public void handleCloseOperation(ActionEvent actionEvent) {
        Stage currentStage = (Stage) root.getScene().getWindow();
        currentStage.close(); // Close the current stage
    }

    public void handleRefresh(ActionEvent actionEvent) {
        txtSearch.setText("");
        loadsNotifications("");
        loadFrame.setMessage(errorMessageLabel, "Notifications Refreshed", "GREEN");
    }

    public void handleDeleteNotifications(ActionEvent actionEvent) {
        NotificationsTable selectedNotification = notificationsTable.getSelectionModel().getSelectedItem();
        if (selectedNotification == null) {
            loadFrame.setMessage(errorMessageLabel, "Please select a notification to delete", "RED");
            return;
        }
        Optional<ButtonType> response =
                alertManager.showResponseAlert(Alert.AlertType.CONFIRMATION, "Delete " +
                        "Notifications", "Do you really want to proceed  ? ", "On pressing OK " +
                        "button , the selected notifications will be deleted permanently .");
        if (response.isPresent() && response.get() != ButtonType.OK) {
            return;
        }
        String query = "DELETE FROM Notifications WHERE Title = ? AND Message = ? AND Target_Role = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, selectedNotification.getTitle());
            pstmt.setString(2, selectedNotification.getMessage());
            pstmt.setString(3, selectedNotification.getTargetRole());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                handleRefresh(actionEvent);
                loadFrame.setMessage(errorMessageLabel, "Notification deleted successfully", "GREEN");
            } else {
                loadFrame.setMessage(errorMessageLabel, "Failed to delete notification", "RED");
            }
        } catch (SQLException e) {
            loadFrame.setMessage(errorMessageLabel, "Error deleting notification: " + e.getMessage(), "RED");
        }
    }

    public void handleAddNotifications(ActionEvent actionEvent) throws SQLException {
        errorMessageLabel.setText("");
        String title = titleField.getText();
        String message = messageField.getText();
        String createdByUserId = String.valueOf(sessionManager.getUserID());
        LocalDate expiryDate = expiryDatePicker.getValue();
        String targetUserID = targetUserIDField.getText();

        if (title.isEmpty()) {
            loadFrame.setMessage(errorMessageLabel, "Title cannot be empty", "RED");
            return;
        }
        if (message.isEmpty()) {
            loadFrame.setMessage(errorMessageLabel, "Message cannot be empty", "RED");
            return;
        }
        if (expiryDate == null) {
            loadFrame.setMessage(errorMessageLabel, "Expiry Date cannot be empty", "RED");
            return;
        }
        if (targetUserID.isEmpty() || targetUserID.equals("")) {
            loadFrame.setMessage(errorMessageLabel, "Target User ID cannot be empty", "RED");
            return;
        }
        if (!getTitleMessage(title, message, targetUserID)) return;

        String query = "INSERT INTO Notifications (Title, Message, Expiry_Date, " +
                "Target_User_Id, Created_By_User_Id, Created_On, Target_Role) " +
                "VALUES (?, ?, ?,?, ?, ?,?)";


        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, title);
            pstmt.setString(2, message);
            pstmt.setDate(3, Date.valueOf(expiryDate));
            pstmt.setString(4, targetUserID);
            pstmt.setString(5, createdByUserId);
            Timestamp createdOn = Timestamp.valueOf(LocalDateTime.now());
            pstmt.setTimestamp(6, createdOn);
            pstmt.setString(7, getTargetRole(targetUserID));
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                loadFrame.setMessage(errorMessageLabel, "Record added to database", "GREEN");
                handleRefresh(actionEvent);
            } else {
                loadFrame.setMessage(errorMessageLabel, "Something weired happened while " +
                        "Adding Notifications", "RED");
            }

        } catch (Exception e) {
            loadFrame.setMessage(errorMessageLabel, "Something went Wrong : " + e.getMessage(),
                    "RED");

        }

    }

    private String getTargetRole(String targetUserID) {
        String query = "SELECT Target_Role from Notifications WHERE Target_User_Id=" + targetUserID;
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                return resultSet.getString("Target_Role");
            }
        } catch (SQLException e) {
            loadFrame.setMessage(errorMessageLabel, "Error fetching Roles", "RED");
        }
        return "";
    }

    public Boolean getTitleMessage(String title, String message, String targetUserID) {
        String query = "SELECT * FROM Notifications WHERE Title = ? AND Message = ? AND Target_User_Id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, title);
            pstmt.setString(2, message);
            pstmt.setString(3, targetUserID);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {  // duplicate found
                Optional<ButtonType> response = alertManager.showResponseAlert(
                        Alert.AlertType.CONFIRMATION,
                        "Duplicate Found",
                        "Duplicate entry found",
                        "Same Records exist with the same Title and Message. Do you still wish to continue?"
                );
                return response.isPresent() && response.get() == ButtonType.OK;
            } else {
                return true; // no duplicate found, allow insertion
            }
        } catch (Exception ex) {
            loadFrame.setMessage(errorMessageLabel,
                    "Something went wrong in getTitleMessage Function: " + ex.getMessage(), "RED");
            return false; // fail-safe in case of error
        }
    }

    public void handleMarkAsDoneNotifications(ActionEvent actionEvent) {
        NotificationsTable markAsDone = notificationsTable.getSelectionModel().getSelectedItem();
        if (markAsDone != null) {

            if (markAsDone.getTargetUserId() == sessionManager.getUserID() ) {

                String toggleReadUnread = markAsDone.getIsRead().equals("Read") ? "Un-Read" : "Read";
                Timestamp date = toggleReadUnread.equals("Read") ? Timestamp.valueOf(LocalDateTime.now()) : null;

                try (PreparedStatement pstmt = connection.prepareStatement(
                        "UPDATE Notifications SET Is_Read = ? ,Read_On = ? WHERE " +
                                "Notification_Id = ?")) {
                    pstmt.setString(1, toggleReadUnread);
                    pstmt.setTimestamp(2, date);
                    pstmt.setString(3, String.valueOf(markAsDone.getNotificationId()));

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        loadFrame.setMessage(errorMessageLabel, "Notification is marked as " + toggleReadUnread,
                                "GREEN");
                        markAsDone.setIsRead(toggleReadUnread);
                        refreshMarkAsDoneButtonText(toggleReadUnread);
                        notificationsTable.refresh(); // If needed
                    }
                } catch (Exception ex) {
                    loadFrame.setMessage(errorMessageLabel,
                            "Something went wrong while marking as read: " + ex.getMessage(), "RED");
                }
            } else loadFrame.setMessage(errorMessageLabel, "You do not have permission to mark Notifications as Read / Un-Read that is assigned for others", "RED");

        } else
            loadFrame.setMessage(errorMessageLabel, "Select a Notifications to Mark it read" +
                    " / Un-Read", "RED");
    }

    public void handleUpdateNotifications(ActionEvent actionEvent) {
        errorMessageLabel.setText("");
        String notificationId = String.valueOf(notificationsTable.getSelectionModel().getSelectedItem().getNotificationId());
        String title = titleField.getText();
        String message = messageField.getText();
        String targetRole = targetRoleCombo.getValue();
        LocalDate expiryDate = expiryDatePicker.getValue();
        String targetUserID = targetUserIDField.getText();
        String isRead = markAsDoneButton.getText().equals("Mark as Complete") ? "Read" : "Un-Read";


        String query = "UPDATE Notifications SET Title = ?, Message = ?, Target_Role = ?, Expiry_Date = ?, Is_Read = ?, Target_User_Id = ? WHERE Notification_Id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, title);
            pstmt.setString(2, message);
            pstmt.setString(3, targetRole);
            pstmt.setObject(4, expiryDate);
            pstmt.setString(5, isRead);
            pstmt.setInt(6, Integer.parseInt(targetUserID));
            pstmt.setInt(7, Integer.parseInt(notificationId));

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                handleRefresh(actionEvent); // Refresh the table after update
                loadFrame.setMessage(errorMessageLabel, "Notification updated successfully and Table is refreshed as well", "GREEN");

            } else {
                loadFrame.setMessage(errorMessageLabel, "Failed to update notification", "RED");
            }
        } catch (Exception e) {
            loadFrame.setMessage(errorMessageLabel, "Error updating notification: " + e.getMessage(), "RED");
        }
    }
}

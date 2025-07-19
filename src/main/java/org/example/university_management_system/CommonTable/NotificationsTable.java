package org.example.university_management_system.CommonTable;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

import java.time.LocalDate;

/*
 Notification_Id INT AUTO_INCREMENT PRIMARY KEY,
    Title VARCHAR(100) NOT NULL,
    Message TEXT NOT NULL,
    Target_Role ENUM('Student', 'Teacher', 'Staff', 'Admin', 'Accountant', 'Librarian', 'All Roles') DEFAULT 'All Roles',
    Target_User_Id INT,
    Created_By_User_Id INT NOT NULL,
    Created_On TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Expiry_Date DATE,
    Is_Read BOOLEAN DEFAULT FALSE,
    Read_On TIMESTAMP,
 */
public class NotificationsTable {
    private SimpleStringProperty title, message, targetRole, isRead, createdOnDate, readOn, targetUserName;
    private SimpleIntegerProperty notificationId, created_By_User_Id, targetUserId;
    private SimpleObjectProperty<LocalDate> expiryDate;


    public NotificationsTable(String title, String message, String targetRole, String isRead
            , String createdOnDate, LocalDate expiryDate, String readOn, int notificationId,
                              int created_By_User_Id, int targetUserId,
                              String targetUserName) {
        this.title = new SimpleStringProperty(title);
        this.message = new SimpleStringProperty(message);
        this.readOn = new SimpleStringProperty(readOn);
        this.notificationId = new SimpleIntegerProperty(notificationId);
        this.created_By_User_Id = new SimpleIntegerProperty(created_By_User_Id);
        this.targetRole = new SimpleStringProperty(targetRole);
        this.isRead = new SimpleStringProperty(isRead);
        this.createdOnDate = new SimpleStringProperty(createdOnDate);
        this.expiryDate = new SimpleObjectProperty<>(expiryDate);
        this.targetUserId = new SimpleIntegerProperty(targetUserId);
        this.targetUserName = new SimpleStringProperty(targetUserName);

    }

    public int getTargetUserId() {
        return targetUserId.get();
    }

    public SimpleIntegerProperty targetUserIdProperty() {
        return targetUserId;
    }

    public int getNotificationId() {
        return notificationId.get();
    }

    public int getCreated_By_User_Id() {
        return created_By_User_Id.get();
    }

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getMessage() {
        return message.get();
    }

    public SimpleStringProperty messageProperty() {
        return message;
    }

    public void setMessage(String message) {
        this.message.set(message);
    }

    public String getTargetRole() {
        return targetRole.get();
    }

    public SimpleStringProperty targetRoleProperty() {
        return targetRole;
    }

    public String getIsRead() {
        return isRead.get();
    }

    public SimpleStringProperty isReadProperty() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead.set(isRead);
    }

    public SimpleStringProperty createdOnDateProperty() {
        return createdOnDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate.get();
    }

    public SimpleObjectProperty<LocalDate> expiryDateProperty() {
        return expiryDate;
    }

    public String getTargetUserName() {
        return targetUserName.get();
    }

    public SimpleStringProperty targetUserNameProperty() {
        return targetUserName;
    }


}

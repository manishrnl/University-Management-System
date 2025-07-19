package org.example.university_management_system.Admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.university_management_system.CommonTable.LibrariansTable;
import org.example.university_management_system.Databases.DatabaseConnection;
import org.example.university_management_system.ToolsClasses.AlertManager;
import org.example.university_management_system.ToolsClasses.LoadFrame;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LibrariansManagement implements Initializable {
    Connection connection = DatabaseConnection.getConnection();

    public LibrariansManagement() throws SQLException {
    }

    ObservableList<LibrariansTable> tableData = FXCollections.observableArrayList();
    LoadFrame loadFrame = new LoadFrame();
    AlertManager alertManager = new AlertManager();

    @FXML
    private TableColumn<LibrariansTable, String> colPan, colUserName, colFullName;
    @FXML
    private TableColumn<LibrariansTable, Long> colAadhar, colMobile;
    @FXML
    private TableColumn<LibrariansTable, Integer> colUserId;
    @FXML
    private TableView<LibrariansTable> librariansTable;
    @FXML
    private ComboBox<String> genderCombo;
    @FXML
    private DatePicker dobPicker;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private TextField departmentIdField, designationField, emailField, emergencyMobileField, emergencyNameField, emergencyRelationField, experienceYearsField, fatherNameField, firstNameField, aadharField, altMobileField, bloodGroupField, certificationField, lastNameField, maritalStatusField, mobileField, motherNameField, nationalityField, panField, permAddressField, qualificationField, referencedViaField, salaryField, searchByAadharField, searchByUserIDField, searchByMobileField, searchByFullNameField, searchByPanField, searchByUsernameField, tempAddressField;

    List<Integer> searchResultID = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorMessageLabel.setText("");
        loadLibrariansTable();

        colPan.setCellValueFactory(cellData -> cellData.getValue().colPanProperty());
        colFullName.setCellValueFactory(cellData -> cellData.getValue().colFullNameProperty());
        colAadhar.setCellValueFactory(cellData -> cellData.getValue().colAadharProperty().asObject());
        colMobile.setCellValueFactory(cellData -> cellData.getValue().colMobileProperty().asObject());
        colUserId.setCellValueFactory(cellData -> cellData.getValue().colUserIdProperty().asObject());
        colUserName.setCellValueFactory(cellData -> cellData.getValue().colUserNameProperty());

        searchByFullNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null || !newValue.isEmpty()) {
                clearTextField(false, true, true, true, true, true);
                String query = "SELECT User_Id FROM Users WHERE CONCAT(First_Name, ' ', " +
                        "Last_Name) LIKE ?";
                List<Integer> resultIDs = SearchData(newValue, query);
                if (resultIDs != null && !resultIDs.isEmpty()) {
                    LoadFieldsIntoTable(resultIDs);
                } else {
                    librariansTable.getItems().clear(); // Clear table if no result
                }
            } else {
                librariansTable.getItems().clear(); // Clear table when input is empty
            }
        });
        searchByUsernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null || !newValue.isEmpty()) {
                clearTextField(true, false, true, true, true, true);
                String query = "SELECT * FROM Authentication WHERE UserName LIKE ? ";
                List<Integer> resultIDs = SearchData(newValue, query);
                if (resultIDs != null && !resultIDs.isEmpty()) {
                    LoadFieldsIntoTable(resultIDs);
                } else {
                    librariansTable.getItems().clear(); // Clear table if no result
                }
            } else {
                librariansTable.getItems().clear(); // Clear table when input is empty
            }
        });
        searchByUserIDField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                clearTextField(true, true, true, true, false, true);
                String query = "SELECT * from Users WHERE User_Id Like ? ";
                searchResultID = SearchData(newValue, query);
                if (searchResultID != null) {
                    LoadFieldsIntoTable(searchResultID);
                }
            }
        });
        searchByMobileField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                clearTextField(true, true, true, true, true, false);
                String query = "SELECT * FROM Users WHERE Mobile LIKE ?  ";
                searchResultID = SearchData(newValue, query);
                if (searchResultID != null) {
                    LoadFieldsIntoTable(searchResultID);
                }
            }
        });
        searchByAadharField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                clearTextField(true, true, true, false, true, true);
                String query = "SELECT * FROM Users WHERE Aadhar LIKE ?  ";
                searchResultID = SearchData(newValue, query);
                if (searchResultID != null) {
                    LoadFieldsIntoTable(searchResultID);
                }
            }
        });
        searchByPanField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                clearTextField(true, true, false, true, true, true);
                String query = "SELECT * FROM Users WHERE Pan LIKE ? ";
                searchResultID = SearchData(newValue, query);
                if (searchResultID != null) {
                    LoadFieldsIntoTable(searchResultID);
                }
            }
        });

        librariansTable.setOnMouseClicked(mouseEvent -> {
            LibrariansTable selectedLibrarians =
                    librariansTable.getSelectionModel().getSelectedItem();
            if (selectedLibrarians != null) {
                int userID = selectedLibrarians.getColUserId();
                loadAllDetails(userID);
            }
        });
    }

    private void clearTextField(Boolean clearName, Boolean clearUserName, Boolean clearPan, Boolean clearAadhar, Boolean clearUserID, Boolean clearMobile) {
        errorMessageLabel.setText("");
        if (clearName) searchByFullNameField.clear();
        if (clearPan) searchByPanField.clear();
        if (clearAadhar) searchByAadharField.clear();
        if (clearUserID) searchByUserIDField.clear();
        if (clearMobile) searchByMobileField.clear();
        if (clearUserName) searchByUsernameField.clear();

    }

    private List<Integer> SearchData(String newValue, String query) {
        errorMessageLabel.setText("");
        List<Integer> userIDs = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "%" + newValue + "%");
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                userIDs.add(resultSet.getInt("User_Id"));

            }
        } catch (Exception e) {
            loadFrame.setMessage(errorMessageLabel,
                    "Error executing search query: " + e.getMessage() +
                            " -> Something went wrong while searching for '" + newValue + "' in Users",
                    "RED");
            return null;
        }

        return userIDs;
    }

    private void LoadFieldsIntoTable(List<Integer> searchResultID) {
        errorMessageLabel.setText("");
        librariansTable.getItems().clear();
        tableData.clear();

        if (searchResultID == null || searchResultID.isEmpty()) {
            return;
        }

        // Create placeholder string like (?, ?, ?, ...)
        String placeholders = String.join(",", java.util.Collections.nCopies(searchResultID.size(), "?"));

        String query = "SELECT u.User_Id, u.Mobile, u.First_Name, u.Last_Name, u.Pan, u.Aadhar, a.UserName " +
                "FROM Users u " +
                "JOIN Authentication a ON u.User_Id = a.User_Id " +
                "WHERE u.Role = 'Librarian' AND u.User_Id IN (" + placeholders + ")";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            for (int i = 0; i < searchResultID.size(); i++) {
                pstmt.setInt(i + 1, searchResultID.get(i));
            }

            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String firstName = resultSet.getString("First_Name");
                String lastName = resultSet.getString("Last_Name");

                int userID = resultSet.getInt("User_Id");
                long mobile = resultSet.getLong("Mobile");
                String pan = resultSet.getString("Pan");
                long aadhar = resultSet.getLong("Aadhar");
                String userName = resultSet.getString("UserName");

                LibrariansTable table = new LibrariansTable(pan, userName,
                        firstName + " " + lastName, aadhar,
                        mobile, userID);
                tableData.add(table);
            }

            librariansTable.setItems(tableData);

        } catch (Exception e) {
            alertManager.showAlert(
                    Alert.AlertType.ERROR,
                    "Database Error",
                    "Error loading fields into table: " + e.getMessage(),
                    "Something went wrong while loading librarian data."
            );
        }
    }

    private void loadAllDetails(int userID) {
        String query = "SELECT u.First_Name, u.Last_Name, u.Aadhar, u.Pan, u.Mobile, u.Alternate_Mobile, " +
                "u.Email, u.Gender, u.DOB, u.Blood_Group, u.Marital_Status, u.Nationality, " +
                "u.Emergency_Contact_Name, u.Emergency_Contact_Relationship, u.Emergency_Contact_Mobile, " +
                "u.Temporary_Address, u.Permanent_Address, u.Fathers_Name, u.Mothers_Name, " +
                "u.Referenced_Via, a.UserName, l.Qualification, l.Certification, l.Experience_Years, " +
                "l.Designation, l.Department_Id, l.Salary " +
                "FROM Users u " +
                "LEFT JOIN Authentication a ON u.User_Id = a.User_Id " +
                "LEFT JOIN Librarians l ON u.User_Id = l.User_Id " +
                "WHERE u.User_Id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // User details
                firstNameField.setText(rs.getString("First_Name"));
                lastNameField.setText(rs.getString("Last_Name"));
                aadharField.setText(String.valueOf(rs.getLong("Aadhar")));
                panField.setText(rs.getString("Pan"));
                mobileField.setText(String.valueOf(rs.getLong("Mobile")));
                altMobileField.setText(String.valueOf(rs.getLong("Alternate_Mobile")));
                emailField.setText(rs.getString("Email"));
                genderCombo.setValue(rs.getString("Gender"));
                dobPicker.setValue(rs.getDate("DOB").toLocalDate());
                bloodGroupField.setText(rs.getString("Blood_Group"));
                maritalStatusField.setText(rs.getString("Marital_Status"));
                nationalityField.setText(rs.getString("Nationality"));

                emergencyNameField.setText(rs.getString("Emergency_Contact_Name"));
                emergencyRelationField.setText(rs.getString("Emergency_Contact_Relationship"));
                emergencyMobileField.setText(String.valueOf(rs.getLong("Emergency_Contact_Mobile")));

                tempAddressField.setText(rs.getString("Temporary_Address"));
                permAddressField.setText(rs.getString("Permanent_Address"));
                fatherNameField.setText(rs.getString("Fathers_Name"));
                motherNameField.setText(rs.getString("Mothers_Name"));
                referencedViaField.setText(rs.getString("Referenced_Via"));

                // Librarian-specific details
                qualificationField.setText(rs.getString("Qualification"));
                certificationField.setText(rs.getString("Certification"));
                experienceYearsField.setText(String.valueOf(rs.getInt("Experience_Years")));
                designationField.setText(rs.getString("Designation"));
                departmentIdField.setText(String.valueOf(rs.getInt("Department_Id")));
                salaryField.setText(String.valueOf(rs.getBigDecimal("Salary")));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // You can log this instead
        }
    }

    private void loadLibrariansTable() {
        String query = "SELECT u.User_Id, u.Mobile, u.Pan, u.Aadhar, a.UserName ,u" +
                ".First_Name,u.Last_Name  FROM Users u " +
                "JOIN Authentication a ON u.User_Id = a.User_Id " +
                "WHERE u.Role = 'Librarian'";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                String firstName = resultSet.getString("First_Name");
                String lastName = resultSet.getString("Last_Name");

                int userID = resultSet.getInt("User_Id");
                long mobile = resultSet.getLong("Mobile");
                String pan = resultSet.getString("Pan");
                long aadhar = resultSet.getLong("Aadhar");
                String userName = resultSet.getString("UserName");

                LibrariansTable table = new LibrariansTable(pan, userName,
                        firstName + " " + lastName, aadhar, mobile, userID);
                tableData.add(table);
            }
            librariansTable.setItems(tableData);
        } catch (SQLException e) {
            throw new RuntimeException("Error loading librarian data", e);
        }
    }

    @FXML
    void handleDeleteLibrarians(ActionEvent event) {
        LibrariansTable selected = librariansTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            loadFrame.setMessage(errorMessageLabel, "Please select a user to delete.", "RED");
            return;
        }

        int userID = selected.getColUserId();
        String deleteLibrarianQuery = "DELETE FROM Librarians WHERE User_Id=?";
        String deleteUserQuery = "DELETE FROM Users WHERE User_Id=?";

        try (PreparedStatement pstmtLib = connection.prepareStatement(deleteLibrarianQuery);
             PreparedStatement pstmtUsers = connection.prepareStatement(deleteUserQuery)) {
            pstmtLib.setInt(1, userID);
            pstmtLib.executeUpdate();
            pstmtUsers.setInt(1, userID);
            pstmtUsers.executeUpdate();
            loadFrame.setMessage(errorMessageLabel, "Librarian deleted successfully.", "GREEN");
            loadLibrariansTable();
        } catch (SQLException e) {
            e.printStackTrace();
            loadFrame.setMessage(errorMessageLabel, "Failed to delete librarian. Try again.", "RED");
        }
    }

    @FXML
    void handleUpdateLibrarians(ActionEvent event) {
        LibrariansTable selected = librariansTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            loadFrame.setMessage(errorMessageLabel, "Please select a user to update.", "RED");
            return;
        }
        int userID = selected.getColUserId();
        String updateUsersQuery = "UPDATE Users SET First_Name=?, Last_Name=?, Aadhar=?, Pan=?, Mobile=?, " +
                "Alternate_Mobile=?, Email=?, Gender=?, DOB=?, Blood_Group=?, Marital_Status=?, " +
                "Nationality=?, Emergency_Contact_Name=?, Emergency_Contact_Relationship=?, " +
                "Emergency_Contact_Mobile=?, Temporary_Address=?, Permanent_Address=?, Fathers_Name=?, " +
                "Mothers_Name=?, Referenced_Via=? WHERE User_Id=?";

        String updateLibrarianQuery = "UPDATE Librarians SET Qualification=?, Certification=?, Experience_Years=?, " +
                "Designation=?,  Salary=? WHERE User_Id=?";

        try (PreparedStatement pstmtUsers = connection.prepareStatement(updateUsersQuery);
             PreparedStatement pstmtLib = connection.prepareStatement(updateLibrarianQuery)) {
            pstmtUsers.setString(1, firstNameField.getText());
            pstmtUsers.setString(2, lastNameField.getText());
            pstmtUsers.setLong(3, Long.parseLong(aadharField.getText()));
            pstmtUsers.setString(4, panField.getText());
            pstmtUsers.setLong(5, Long.parseLong(mobileField.getText()));
            pstmtUsers.setLong(6, Long.parseLong(altMobileField.getText()));
            pstmtUsers.setString(7, emailField.getText());
            pstmtUsers.setString(8, genderCombo.getValue());
            pstmtUsers.setDate(9, Date.valueOf(dobPicker.getValue()));
            pstmtUsers.setString(10, bloodGroupField.getText());
            pstmtUsers.setString(11, maritalStatusField.getText());
            pstmtUsers.setString(12, nationalityField.getText());
            pstmtUsers.setString(13, emergencyNameField.getText());
            pstmtUsers.setString(14, emergencyRelationField.getText());
            pstmtUsers.setLong(15, Long.parseLong(emergencyMobileField.getText()));
            pstmtUsers.setString(16, tempAddressField.getText());
            pstmtUsers.setString(17, permAddressField.getText());
            pstmtUsers.setString(18, fatherNameField.getText());
            pstmtUsers.setString(19, motherNameField.getText());
            pstmtUsers.setString(20, referencedViaField.getText());
            pstmtUsers.setInt(21, userID);
            pstmtUsers.executeUpdate();

            pstmtLib.setString(1, qualificationField.getText());
            pstmtLib.setString(2, certificationField.getText());
            pstmtLib.setInt(3, Integer.parseInt(experienceYearsField.getText()));
            pstmtLib.setString(4, designationField.getText());
            pstmtLib.setDouble(5, Double.parseDouble(salaryField.getText()));
            pstmtLib.setInt(6, userID);
            pstmtLib.executeUpdate();

            loadFrame.setMessage(errorMessageLabel, "Librarian details updated successfully.", "GREEN");

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            loadFrame.setMessage(errorMessageLabel, "Failed to update librarian. Check input values.", "RED");
        }
    }
}

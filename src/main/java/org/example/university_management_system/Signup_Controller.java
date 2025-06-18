package org.example.university_management_system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.university_management_system.Databases.DatabaseConnection;
import org.example.university_management_system.ToolsClasses.LoadFrame;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.time.LocalDate; // Import LocalDate for default DatePicker value
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Signup_Controller implements Initializable {

    LoadFrame loadFrame;
    private final ObservableList<String> BTech = FXCollections.observableArrayList(
            "Computer Science & Engineering", "Electronics & Communication Engg.",
            "Mechanical Engineering", "Civil Engineering", "Electrical Engineering",
            "Information Technology", "Aerospace Engineering"
    );
    private final ObservableList<String> BE = BTech; // Or a separate list like: FXCollections.observableArrayList("Software Engineering", "Chemical Engineering");

    private final ObservableList<String> BSc = FXCollections.observableArrayList(
            "Physics", "Chemistry", "Mathematics", "Biology", "Computer Science", "Environmental Science"
    );
    private final ObservableList<String> BCA = FXCollections.observableArrayList(
            "Software Development", "Web Design", "Networking", "Database Management");
    private final ObservableList<String> BBA = FXCollections.observableArrayList(
            "Marketing", "Finance", "Human Resources", "Operations Management");
    private final ObservableList<String> BA = FXCollections.observableArrayList(
            "English Literature", "History", "Political Science", "Sociology", "Economics", "Psychology");
    private final ObservableList<String> BCom = FXCollections.observableArrayList(
            "Accounting", "Finance", "Taxation", "Banking & Insurance");
    private final ObservableList<String> LLB = FXCollections.observableArrayList(
            "Constitutional Law", "Criminal Law", "Corporate Law", "International Law");
    private final ObservableList<String> BEd = FXCollections.observableArrayList(
            "Primary Education", "Secondary Education", "Special Education", "Physical Education");
    private final ObservableList<String> BArch = FXCollections.observableArrayList(
            "Architectural Design", "Urban Design", "Sustainable Architecture", "Landscape Architecture");
    private final ObservableList<String> BDes = FXCollections.observableArrayList(
            "Graphic Design", "Fashion Design", "Product Design", "Interior Design", "UX/UI Design");
    private final ObservableList<String> BFA = FXCollections.observableArrayList(
            "Painting", "Sculpture", "Applied Arts", "Visual Communication", "Animation");
    private final ObservableList<String> BHM = FXCollections.observableArrayList(
            "Hotel Management", "Hospitality Administration", "Food & Beverage Services", "Culinary Arts");
    private final ObservableList<String> BVoc = FXCollections.observableArrayList(
            "Retail Management", "Healthcare", "Software Development", "Banking & Finance", "Tourism");

    // Postgraduate Degrees
    private final ObservableList<String> MTech = FXCollections.observableArrayList(
            "Computer Science", "VLSI", "Thermal Engineering", "Structural Engineering", "Embedded Systems");
    private final ObservableList<String> ME = FXCollections.observableArrayList(
            "Civil Engineering", "Mechanical Engineering", "Electrical Engineering", "Electronics Engineering");
    private final ObservableList<String> MSc = FXCollections.observableArrayList(
            "Mathematics", "Physics", "Chemistry", "Computer Science", "Biochemistry", "Microbiology");
    private final ObservableList<String> MCA = FXCollections.observableArrayList(
            "Software Development", "Data Science", "Cyber Security", "AI & ML");
    private final ObservableList<String> MBA = FXCollections.observableArrayList(
            "Finance", "Marketing", "HRM", "Business Analytics", "Operations Management", "International Business");
    private final ObservableList<String> MA = FXCollections.observableArrayList(
            "English", "Hindi", "History", "Sociology", "Political Science", "Psychology");
    private final ObservableList<String> MCom = FXCollections.observableArrayList(
            "Accountancy", "Finance", "Banking", "Taxation", "Business Law");
    private final ObservableList<String> LLM = FXCollections.observableArrayList(
            "Constitutional Law", "Business Law", "International Law", "Human Rights Law");
    private final ObservableList<String> MEd = FXCollections.observableArrayList(
            "Educational Leadership", "Inclusive Education", "Curriculum Studies", "Educational Technology");
    private final ObservableList<String> MArch = FXCollections.observableArrayList(
            "Urban Design", "Sustainable Architecture", "Heritage Conservation");
    private final ObservableList<String> MDes = FXCollections.observableArrayList(
            "Interaction Design", "Industrial Design", "Fashion Design", "Visual Communication");
    private final ObservableList<String> MFA = FXCollections.observableArrayList(
            "Painting", "Applied Arts", "Photography", "Animation", "Sculpture");
    private final ObservableList<String> MVoc = FXCollections.observableArrayList(
            "Retail Management", "Tourism & Hospitality", "Banking", "Software Development");

    // Diploma & Certificate
    private final ObservableList<String> Diploma = FXCollections.observableArrayList(
            "Mechanical Engineering", "Civil Engineering", "Computer Engineering", "Electrical Engineering", "Electronics");
    private final ObservableList<String> AdvancedDiploma = FXCollections.observableArrayList(
            "Web Development", "Networking", "Data Science", "Animation", "Fashion Design");
    private final ObservableList<String> PGDiploma = FXCollections.observableArrayList(
            "Management", "Data Analytics", "Journalism", "Counseling", "Business Analytics");
    private final ObservableList<String> CertificateCourse = FXCollections.observableArrayList(
            "Python", "Digital Marketing", "Graphic Design", "Spoken English", "MS Office");
    private final ObservableList<String> SkillDevelopmentProgram = FXCollections.observableArrayList(
            "Electrician", "Plumber", "Mobile Repair", "Beautician", "Welding");

    // Professional & Doctorate Degrees
    private final ObservableList<String> PhD = FXCollections.observableArrayList(
            "Computer Science", "Physics", "Chemistry", "Mathematics", "Management", "Law", "Literature");
    private final ObservableList<String> CA = FXCollections.observableArrayList(
            "Foundation", "Intermediate", "Final");
    private final ObservableList<String> CFA = FXCollections.observableArrayList(
            "Level I", "Level II", "Level III");
    private final ObservableList<String> CS = FXCollections.observableArrayList(
            "Foundation", "Executive", "Professional");
    private final ObservableList<String> ICWA = FXCollections.observableArrayList(
            "Foundation", "Intermediate", "Final");
    private final ObservableList<String> MBBS = FXCollections.observableArrayList(
            "General Medicine", "Pediatrics", "Orthopedics", "ENT", "Gynecology");
    private final ObservableList<String> BDS = FXCollections.observableArrayList(
            "Oral Surgery", "Orthodontics", "Periodontics", "Prosthodontics");
    private final ObservableList<String> MDS = FXCollections.observableArrayList(
            "Oral Medicine", "Pedodontics", "Conservative Dentistry", "Oral Surgery");
    private final ObservableList<String> BAMS = FXCollections.observableArrayList(
            "Kayachikitsa", "Shalya Tantra", "Panchakarma", "Rasashastra");
    private final ObservableList<String> BHMS = FXCollections.observableArrayList(
            "Materia Medica", "Organon of Medicine", "Repertory", "Surgery");
    private final ObservableList<String> BUMS = FXCollections.observableArrayList(
            "Ilmul Advia", "Tahaffuzi wa Samaji Tib", "Moalajat", "Ilmul Qabalat wa Amraze Niswan");
    private final ObservableList<String> BPharm = FXCollections.observableArrayList(
            "Pharmaceutics", "Pharmacology", "Pharmaceutical Chemistry", "Pharmacognosy");
    private final ObservableList<String> MPharm = FXCollections.observableArrayList(
            "Pharmaceutics", "Pharmacology", "Industrial Pharmacy", "Quality Assurance");
    private final ObservableList<String> DPharm = FXCollections.observableArrayList(
            "Pharmaceutics", "Pharmaceutical Chemistry", "Pharmacology", "Hospital Pharmacy");
    private final ObservableList<String> Nursing = FXCollections.observableArrayList(
            "General Nursing", "Midwifery", "Community Health", "Pediatric Nursing");
    private final ObservableList<String> GNM = FXCollections.observableArrayList(
            "Nursing Foundations", "Medical Surgical Nursing", "Mental Health Nursing");
    private final ObservableList<String> ANM = FXCollections.observableArrayList(
            "Community Health", "Child Health Nursing", "Midwifery", "Health Promotion");

    // Default stream list for courses without specific streams
    private final ObservableList<String> DEFAULT_STREAMS = FXCollections.observableArrayList("General", "Not Applicable", "No specific stream");

    private final Map<String, ObservableList<String>> courseStreamMap = new HashMap<>();

    private File selectedImageFile;  // Class-level variable
    @FXML
    private TextField firstNameField, lastNameField, emailField, mobileField, aadharField, panField, usernameField, emergencyContactNameField, emergencyContactNumberField, fathersNameField, mothersNameField, parentsMobileField, studentRegNumberField, studentRollNumberField, studentProgramField, studentCurrentAcademicYearField, studentCurrentSemesterField,
            school10NameField, school10PassingYearField, school10PercentageField, school12NameField, school12PassingYearField, school12PercentageField,
            teacherRegNumberField, teacherDesignationField, teacherQualificationField, teacherSpecializationField, teacherExperienceField,
            staffRegNumberField, staffDesignationField, staffExperienceField, accountantQualificationField, accountantCertificationField, accountantExperienceField, accountantDesignationField,
            librarianQualificationField, librarianCertificationField, librarianExperienceField, librarianDesignationField,
            adminDesignationField;

    @FXML
    private ComboBox<String> genderComboBox, nationalityComboBox, maritalStatusComboBox, roleComboBox,
            emergencyRelationComboBox, referencedViaComboBox, studentStreamCombo, courseComboBox, studentBatchCombo,
            teacherDepartmentComboBox, teacherEmploymentTypeComboBox, staffDepartmentComboBox, staffEmploymentTypeComboBox,
            accountantDepartmentComboBox, librarianDepartmentComboBox, adminDepartmentComboBox, adminAccessLevelComboBox,
            bloodGroupComboBox;

    @FXML
    private PasswordField passwordField, confirmPasswordField;

    @FXML
    private DatePicker dobPicker, studentEnrolledOnPicker;

    @FXML
    private TextArea permanentAddressArea, temporaryAddressArea;

    @FXML
    private ImageView profileImage;

    @FXML
    private Label errorMessageLabel;

    @FXML
    private VBox dynamicFieldsContainer, StudentsFields, teacherFields, staffFields, accountantFields, librarianFields, adminFields;

    String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])" +
            "[A-Za-z\\d@$!%*?&]{10,}$";
    String MOBILE_REGEX = "^(\\+\\d{1,3}[- ]?)?\\d{10}$";
    String AADHAR_REGEX = "^(\\d{4}-?\\d{4}-?\\d{4}|\\d{12})$";
    String PAN_REGEX = "^[A-Za-z]{5}[0-9]{4}[A-Za-z]$";
    String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // letters, 4 digits, 1 uppercase letter
    private ObservableList<String> getAllCourseNames() {
        return FXCollections.observableArrayList("B.Tech", "B.E", "B.Sc", "BCA", "BBA", "BA", "B.Com", "LLB", "B.Ed", "B.Arch", "B.Des", "BFA", "BHM", "B.Voc", "M.Tech", "M.E", "M.Sc", "MCA", "MBA", "MA", "M.Com", "LLM", "M.Ed", "M.Arch", "M.Des", "MFA", "M.Voc", "Diploma", "Advanced Diploma", "PG Diploma", "Certificate Course", "Skill Development Program", "Ph.D", "CA", "CFA", "CS", "ICWA", "MBBS", "BDS", "MDS", "BAMS", "BHMS", "BUMS", "B.Pharm", "M.Pharm", "D.Pharm", "Nursing", "GNM", "ANM");
    }

    private ObservableList<String> getAllNationality() {
        return FXCollections.observableArrayList("Indian", "American", "British", "Canadian", "Australian", "Chinese", "Japanese", "German", "French", "Italian", "Spanish", "Russian", "Brazilian", "South African", "Mexican", "Other");
    }

    private void populateCourseStreamMap() {
        // Undergraduate Degrees
        courseStreamMap.put("B.Tech", BTech);
        courseStreamMap.put("B.E", BE);
        courseStreamMap.put("B.Sc", BSc);
        courseStreamMap.put("BCA", BCA);
        courseStreamMap.put("BBA", BBA);
        courseStreamMap.put("BA", BA);
        courseStreamMap.put("B.Com", BCom);
        courseStreamMap.put("LLB", LLB);
        courseStreamMap.put("B.Ed", BEd);
        courseStreamMap.put("B.Arch", BArch);
        courseStreamMap.put("B.Des", BDes);
        courseStreamMap.put("BFA", BFA);
        courseStreamMap.put("BHM", BHM);
        courseStreamMap.put("B.Voc", BVoc);

        // Postgraduate Degrees
        courseStreamMap.put("M.Tech", MTech);
        courseStreamMap.put("M.E", ME);
        courseStreamMap.put("M.Sc", MSc);
        courseStreamMap.put("MCA", MCA);
        courseStreamMap.put("MBA", MBA);
        courseStreamMap.put("MA", MA);
        courseStreamMap.put("M.Com", MCom);
        courseStreamMap.put("LLM", LLM);
        courseStreamMap.put("M.Ed", MEd);
        courseStreamMap.put("M.Arch", MArch);
        courseStreamMap.put("M.Des", MDes);
        courseStreamMap.put("MFA", MFA);
        courseStreamMap.put("M.Voc", MVoc);

        // Diploma & Certificate
        courseStreamMap.put("Diploma", Diploma);
        courseStreamMap.put("Advanced Diploma", AdvancedDiploma);
        courseStreamMap.put("PG Diploma", PGDiploma);
        courseStreamMap.put("Certificate Course", CertificateCourse);
        courseStreamMap.put("Skill Development Program", SkillDevelopmentProgram);

        // Professional & Doctorate Degrees
        courseStreamMap.put("Ph.D", PhD);
        courseStreamMap.put("CA", CA);
        courseStreamMap.put("CFA", CFA);
        courseStreamMap.put("CS", CS);
        courseStreamMap.put("ICWA", ICWA);
        courseStreamMap.put("MBBS", MBBS);
        courseStreamMap.put("BDS", BDS);
        courseStreamMap.put("MDS", MDS);
        courseStreamMap.put("BAMS", BAMS);
        courseStreamMap.put("BHMS", BHMS);
        courseStreamMap.put("BUMS", BUMS);
        courseStreamMap.put("B.Pharm", BPharm);
        courseStreamMap.put("M.Pharm", MPharm);
        courseStreamMap.put("D.Pharm", DPharm);
        courseStreamMap.put("Nursing", Nursing);
        courseStreamMap.put("GNM", GNM);
        courseStreamMap.put("ANM", ANM);
    }

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        studentEnrolledOnPicker.setValue(LocalDate.now());
        LocalDate date = LocalDate.now();
        int currentYear = LocalDate.now().getYear();
        dobPicker.setValue(LocalDate.of(2000, 03, 14));
        String batch6Year = currentYear + "-" + (currentYear + 6);
        String batch5Year = currentYear + "-" + (currentYear + 5);
        String batch4year = currentYear + "-" + (currentYear + 4);
        String batch3Year = currentYear + "-" + (currentYear + 3);
        String batch2Year = currentYear + "-" + (currentYear + 2);
        studentBatchCombo.setItems(FXCollections.observableArrayList(batch2Year, batch3Year,
                batch4year, batch5Year, batch6Year));
        studentBatchCombo.setPromptText("Select Batch");
        genderComboBox.setValue("Male");
        maritalStatusComboBox.setValue("Single");
        nationalityComboBox.setValue("Indian");
        bloodGroupComboBox.setValue("A+");
        emergencyRelationComboBox.setValue("Father");

        school10PassingYearField.setText(String.valueOf(currentYear - 3));
        school12PassingYearField.setText(String.valueOf(currentYear - 1));

        populateCourseStreamMap();
        HideUnhideFields(false, false, false, false, false, false);

        nationalityComboBox.setItems(getAllNationality());
        courseComboBox.setItems(getAllCourseNames());
        courseComboBox.setPromptText("Select Course"); // Ensure prompt text is visible

        studentStreamCombo.setItems(FXCollections.observableArrayList("Select Stream"));
        studentStreamCombo.setPromptText("Select Stream");

        courseComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ObservableList<String> streams = courseStreamMap.getOrDefault(newValue, DEFAULT_STREAMS);
                studentStreamCombo.setItems(streams);
                studentStreamCombo.getSelectionModel().selectFirst(); // Select the first item or clear if needed
            } else {
                studentStreamCombo.setItems(FXCollections.observableArrayList("Select Stream"));
                studentStreamCombo.setPromptText("Select Stream");
            }
        });


    }

    @FXML
    private void handleRoleSelection(ActionEvent event) {
        HideUnhideFields(false, false, false, false, false, false);
        dynamicFieldsContainer.getChildren().clear(); // Clear previously added VBoxes

        String selectedRole = roleComboBox.getSelectionModel().getSelectedItem();
        if (selectedRole != null) {
            VBox fieldsToShow = null; // A temporary reference for the VBox to show
            switch (selectedRole) {
                case "Student":
                    fieldsToShow = StudentsFields;
                    courseComboBox.getSelectionModel().clearSelection();
                    studentStreamCombo.getSelectionModel().clearSelection();
                    studentStreamCombo.setItems(FXCollections.observableArrayList("Select Stream")); // Reset streams
                    studentBatchCombo.getSelectionModel().clearSelection();
                    break;
                case "Teacher":
                    fieldsToShow = teacherFields;
                    break;
                case "Staff":
                    fieldsToShow = staffFields;
                    break;
                case "Accountant":
                    fieldsToShow = accountantFields;
                    break;
                case "Librarian":
                    fieldsToShow = librarianFields;
                    break;
                case "Admin":
                    fieldsToShow = adminFields;
                    break;
                default:
                    break;
            }

            if (fieldsToShow != null) {
                fieldsToShow.setVisible(true);
                fieldsToShow.setManaged(true);
                dynamicFieldsContainer.getChildren().add(fieldsToShow);
            }
        }
    }


    private void HideUnhideFields(boolean studentVisible, boolean teacherVisible, boolean staffVisible, boolean accountantVisible, boolean librarianVisible, boolean adminVisible) {
        StudentsFields.setVisible(studentVisible);
        teacherFields.setVisible(teacherVisible);
        staffFields.setVisible(staffVisible);
        accountantFields.setVisible(accountantVisible);
        librarianFields.setVisible(librarianVisible);
        adminFields.setVisible(adminVisible);

        StudentsFields.setManaged(studentVisible);
        teacherFields.setManaged(teacherVisible);
        staffFields.setManaged(staffVisible);
        accountantFields.setManaged(accountantVisible);
        librarianFields.setManaged(librarianVisible);
        adminFields.setManaged(adminVisible);
    }


    @FXML
    void handleCancelButton(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        loadFrame.loadNewFrame(currentStage, getClass(), "Login.fxml", "Login to access our " +
                "dashboard");
    }

    @FXML
    void handleRegisterButton(ActionEvent event) {
        String role = roleComboBox.getSelectionModel().getSelectedItem();
        String username = usernameField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String studentsMobile = mobileField.getText();
        String parentsMobile = parentsMobileField.getText();
        String aadhar = aadharField.getText();
        String bloodGroup = bloodGroupComboBox.getSelectionModel().getSelectedItem();
        String pan = panField.getText();
        String gender = genderComboBox.getSelectionModel().getSelectedItem();
        String temporaryAddress = temporaryAddressArea.getText();
        String permanentAddress = permanentAddressArea.getText();
        String nationality = nationalityComboBox.getSelectionModel().getSelectedItem();
        String maritalStatus = maritalStatusComboBox.getSelectionModel().getSelectedItem();
        String emergencyContactName = emergencyContactNameField.getText();
        String emergencyContactNumber = emergencyContactNumberField.getText();
        String emergencyContactRelation = emergencyRelationComboBox.getSelectionModel().getSelectedItem();
        String referencedVia = referencedViaComboBox.getSelectionModel().getSelectedItem();
        String fathersName = fathersNameField.getText();
        String mothersName = mothersNameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        Image image = profileImage.getImage();
        String dob = dobPicker.getValue() != null ? dobPicker.getValue().toString() : ""; // YYYY-MM-DD
/*
        if (firstName.isEmpty() || lastName.isEmpty() || studentsMobile.isEmpty() || aadhar.isEmpty() || username.isEmpty() || pan.isEmpty() || parentsMobile.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || maritalStatus.isEmpty() || gender.isEmpty() || nationality.isEmpty() || fathersName.isEmpty() || mothersName.isEmpty() || emergencyContactName.isEmpty() || emergencyContactNumber.isEmpty() || emergencyContactRelation.isEmpty() || temporaryAddress.isEmpty() || permanentAddress.isEmpty() || bloodGroup == null || bloodGroup.isEmpty()) {
            loadFrame.setMessage(errorMessageLabel, "Please fill in all general required " +
                    "fields.", "RED");
            return;
        }*/
       /* if (referencedVia == null) {
            loadFrame.setMessage(errorMessageLabel, "Please select how you got to know about" + " us in reference via column", "RED");
            return;
        }
        if (role.isEmpty() || role == null) {
            loadFrame.setMessage(errorMessageLabel, "Please select a role to get Additional " + "details columns", "RED");
            return;
        }
        if (!studentsMobile.matches(MOBILE_REGEX)) {
            loadFrame.setMessage(errorMessageLabel, "Please enter a valid mobile number.", "RED");
            return;
        }
        if (!parentsMobile.matches(MOBILE_REGEX)) {
            loadFrame.setMessage(errorMessageLabel, "Please enter a valid parent's mobile number.", "RED");
            return;
        }
        if (!pan.matches(PAN_REGEX)) {
            loadFrame.setMessage(errorMessageLabel, "Please enter a valid PAN number having 5 Character followed by 4 digits and at last 1 character at the end", "RED");
            return;
        }
        if (!aadhar.matches(AADHAR_REGEX)) {
            loadFrame.setMessage(errorMessageLabel, "Please enter a valid Aadhar number.", "RED");
            return;
        }
        if (email.isEmpty() || !email.matches(EMAIL_REGEX)) {
            loadFrame.setMessage(errorMessageLabel, "Please enter a valid email address.",
                    "RED");
            return;
        }
        if (!password.equals(confirmPasswordField.getText())) { // Use getText() for confirmPasswordField
            loadFrame.setMessage(errorMessageLabel, "Passwords do not match.", "RED");
            return;
        }
        if (!password.matches(PASSWORD_REGEX)) {
            loadFrame.setMessage(errorMessageLabel, "Password must contain at least 10 " +
                    "character with a cpmbination of uppercase, lowercase, digit, and special character.", "RED");
            return;
        }


        */
        if (image == null || image.getUrl() == null || image.getUrl().isEmpty()) {
            loadFrame.setMessage(errorMessageLabel, "Please upload a profile picture.", "RED");
            return;
        }

        String insertStudents = "INSERT INTO Users (Role, First_Name, Last_Name, Aadhar,Pan," +
                "Mobile,Alternate_Mobile,Email,Gender,DOB,Blood_Group,Marital_Status," +
                "Nationality,Emergency_Contact_Name,Emergency_Contact_Relationship," +
                "Emergency_Contact_Mobile,Temporary_Address,Permanent_Address,Fathers_Name," +
                "Mothers_Name,Referenced_Via,Admin_Approval_Status,Photo_URL) VALUES(?,?,?," +
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(insertStudents);) {
            pstmt.setString(1, role);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, aadhar);
            pstmt.setString(5, pan);
            pstmt.setString(6, studentsMobile);
            pstmt.setString(7, parentsMobile);
            pstmt.setString(8, email);
            pstmt.setString(9, gender);
            pstmt.setString(10, dob);
            pstmt.setString(11, bloodGroup);
            pstmt.setString(12, maritalStatus);
            pstmt.setString(13, nationality);
            pstmt.setString(14, emergencyContactName);
            pstmt.setString(15, emergencyContactRelation);
            pstmt.setString(16, emergencyContactNumber);
            pstmt.setString(17, temporaryAddress);
            pstmt.setString(18, permanentAddress);
            pstmt.setString(19, fathersName);
            pstmt.setString(20, mothersName);
            pstmt.setString(21, referencedVia);
            String adminApprovalStatus = role.equals("Student") ? "Approved" : "Pending";
            pstmt.setString(22, adminApprovalStatus);


            try (InputStream is = new FileInputStream(selectedImageFile)) {
                pstmt.setBlob(23, is);
                int rowsAffected = pstmt.executeUpdate();  // 🔑 EXECUTE WHILE STREAM IS OPEN

                if (rowsAffected > 0) {
                    String fetchUserID = "SELECT User_Id from Users WHERE Email = ? AND Mobile = ? AND Pan = ? AND Role= ?";
                    try (PreparedStatement pstmt2 = connection.prepareStatement(fetchUserID)) {
                        pstmt2.setString(1, email);
                        pstmt2.setString(2, studentsMobile);
                        pstmt2.setString(3, pan);
                        pstmt2.setString(4, role);

                        ResultSet rs = pstmt2.executeQuery();
                        if (rs.next()) {
                            int userId = rs.getInt("User_Id");
                            System.out.println("User ID Assigned is: " + userId);
                            loadFrame.setMessage(errorMessageLabel, "Registration " +
                                    "successful! Please login to access your dashboard.", "GREEN");
                        } else {
                            loadFrame.setMessage(errorMessageLabel, "Error fetching User ID. Contact Admin.", "RED");
                        }
                    }

                }
            } catch (IOException ioEx) {
                pstmt.setNull(23, java.sql.Types.BLOB);
                System.out.println("Error reading image file : " + ioEx.getMessage());
            }


        } catch (Exception ex) {
            loadFrame.setMessage(errorMessageLabel, "Error while connecting to the database." + " Please try again later." + ex.getMessage(), "RED");
            System.out.println("Database connection error inside catch block : " + ex.getMessage());
        }

        switch (role) {
            case "Student":
            /*    String studentCourse = courseComboBox.getValue(); // Get selected course
                String stream = studentStreamCombo.getValue(); // Get selected stream
                String studentBatch = studentBatchCombo.getValue(); // Get selected batch

                if (studentCourse == null || studentCourse.isEmpty() || stream == null || stream.isEmpty() || studentBatch == null || studentBatch.isEmpty() ||
                        school10NameField.getText().isEmpty() || school10PassingYearField.getText().isEmpty() || school10PercentageField.getText().isEmpty() ||
                        school12NameField.getText().isEmpty() || school12PassingYearField.getText().isEmpty() || school12PercentageField.getText().isEmpty()) {
                    errorMessageLabel.setText("Please fill in all required student academic details.");
                    return;
                }

                // Collect other student details
                String studentRegNumber = studentRegNumberField.getText();
                String studentRollNumber = studentRollNumberField.getText();
                String studentProgram = studentProgramField.getText();
                String studentCurrentAcademicYear = studentCurrentAcademicYearField.getText();
                String studentCurrentSemester = studentCurrentSemesterField.getText();
                String school10Name = school10NameField.getText();
                String school10PassingYear = school10PassingYearField.getText();
                String school10Percentage = school10PercentageField.getText();
                String school12Name = school12NameField.getText();
                String school12PassingYear = school12PassingYearField.getText();
                String school12Percentage = school12PercentageField.getText();
                // ... (add logic to save student data)
            */
                break;
            case "Accountant":
                // Validate accountant-specific fields
                if (accountantQualificationField.getText().isEmpty() || accountantCertificationField.getText().isEmpty() ||
                        accountantExperienceField.getText().isEmpty() || accountantDesignationField.getText().isEmpty() ||
                        accountantDepartmentComboBox.getSelectionModel().getSelectedItem() == null) {
                    errorMessageLabel.setText("Please fill in all required accountant details.");
                    return;
                }
                String accountantQualification = accountantQualificationField.getText();
                String accountantCertification = accountantCertificationField.getText();
                String accountantExperience = accountantExperienceField.getText();
                String accountantDesignation = accountantDesignationField.getText();
                String accountantDepartment = accountantDepartmentComboBox.getSelectionModel().getSelectedItem();
                // ... (add logic to save accountant data)
                break;
            case "Admin":
                // Validate admin-specific fields
                if (adminDesignationField.getText().isEmpty() || adminAccessLevelComboBox.getSelectionModel().getSelectedItem() == null) {
                    errorMessageLabel.setText("Please fill in all required admin details.");
                    return;
                }
                String adminDesignation = adminDesignationField.getText();
                String adminDepartment = adminDepartmentComboBox.getSelectionModel().getSelectedItem();
                String adminAccessLevel = adminAccessLevelComboBox.getSelectionModel().getSelectedItem();
                // ... (add logic to save admin data)
                break;
            case "Librarian":
                // Validate librarian-specific fields
                if (librarianQualificationField.getText().isEmpty() || librarianCertificationField.getText().isEmpty() ||
                        librarianExperienceField.getText().isEmpty() || librarianDesignationField.getText().isEmpty() ||
                        librarianDepartmentComboBox.getSelectionModel().getSelectedItem() == null) {
                    errorMessageLabel.setText("Please fill in all required librarian details.");
                    return;
                }
                String librarianQualification = librarianQualificationField.getText();
                String librarianCertification = librarianCertificationField.getText();
                String librarianExperience = librarianExperienceField.getText();
                String librarianDesignation = librarianDesignationField.getText();
                String librarianDepartment = librarianDepartmentComboBox.getSelectionModel().getSelectedItem();
                // ... (add logic to save librarian data)
                break;
            case "Teacher":
                // Validate teacher-specific fields
                if (teacherRegNumberField.getText().isEmpty() || teacherDesignationField.getText().isEmpty() ||
                        teacherDepartmentComboBox.getSelectionModel().getSelectedItem() == null ||
                        teacherQualificationField.getText().isEmpty() || teacherExperienceField.getText().isEmpty()) {
                    errorMessageLabel.setText("Please fill in all required teacher details.");
                    return;
                }
                String teacherRegNumber = teacherRegNumberField.getText();
                String teacherDesignation = teacherDesignationField.getText();
                String teacherDepartment = teacherDepartmentComboBox.getSelectionModel().getSelectedItem();
                String teacherQualification = teacherQualificationField.getText();
                String teacherSpecialization = teacherSpecializationField.getText(); // Assuming this field is relevant
                String teacherExperience = teacherExperienceField.getText();
                // ... (add logic to save teacher data)
                break;
            case "Staff":
                // Validate staff-specific fields
                if (staffDesignationField.getText().isEmpty() || staffDepartmentComboBox.getSelectionModel().getSelectedItem() == null ||
                        staffEmploymentTypeComboBox.getSelectionModel().getSelectedItem() == null || staffExperienceField.getText().isEmpty()) {
                    errorMessageLabel.setText("Please fill in all required staff details.");
                    return;
                }
                String staffDesignation = staffDesignationField.getText();
                String staffDepartment = staffDepartmentComboBox.getSelectionModel().getSelectedItem();
                String staffEmploymentType = staffEmploymentTypeComboBox.getSelectionModel().getSelectedItem();
                String staffExperience = staffExperienceField.getText();
                // ... (add logic to save staff data)
                break;
        }
        // loadFrame.setMessage(statusMessageLabel, "Registration successful! " +
        //        "Please login to access your dashboard.", "GREEN");
    }


    public void UploadImage(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload your Profile Picture");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        Stage currentStage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(currentStage);

        if (selectedFile != null) {
            selectedImageFile = selectedFile;  // Save for later database use
            Image image = new Image(selectedFile.toURI().toString());
            profileImage.setImage(image);
        } else {
            System.out.println("No image selected.");
        }
    }

}




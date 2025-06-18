// ...existing code...
import database.DatabaseHelper;
// ...existing code...

public class Login_Controller {
    // ...existing code...

    void handleLoginButton(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        boolean isValid = DatabaseHelper.checkUserCredentials(username, password);

        if (isValid) {
            // ...proceed to next scene or show success...
        } else {
            // ...show error message...
        }
    }

    // ...existing code...
}

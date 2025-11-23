package boundary;

import controller_class.*;
import entity_class.*;
import java.util.Scanner;
import java.util.InputMismatchException;
import boundary.CompanyRepRegistrationUI;

/**
 * User interface for handling user authentication. Depends on the
 * {@link ILoginController} and {@link IFileHandler} abstractions
 * instead of concrete implementations.
 */
public class LoginUI {
    private final Scanner scanner;
    private final ILoginController loginController;
    private final IFileHandler fileHandler;
    private final CompanyRepRegistrationUI registrationUI;

    /** Constructs a new LoginUI.
     * @param loginController the login controller abstraction
     * @param fileHandler the file handler abstraction
     * @param registrationUI the registration UI for company representatives
     */
    public LoginUI(ILoginController loginController, IFileHandler fileHandler, CompanyRepRegistrationUI registrationUI) {
        this.scanner = new Scanner(System.in);
        this.loginController = loginController;
        this.fileHandler = fileHandler;
        this.registrationUI = registrationUI;
    }

    /** Display welcome screen and handle login/registration */
    public User displayWelcomeScreen() {
        while (true) {
            System.out.println("\nINTERNSHIP PLACEMENT MANAGEMENT SYSTEM");
            System.out.println("1. Login");
            System.out.println("2. Register (Company Representative)");
            System.out.println("3. Exit");
            System.out.print("\nSelect option: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        User user = handleLogin();
                        if (user != null) return user;
                        break;

                    case 2:
                        // Delegate registration to separate UI component
                        registrationUI.handleRegistration();
                        break;

                    case 3:
                        return null; //exit

                    default:
                        System.out.println("Invalid option");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear invalid input
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    /** Handle User Login */
    private User handleLogin() {
        System.out.println("----- LOGIN -----");
        System.out.print("User ID: ");
        String userID = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            User user = loginController.login(userID, password);

            if (user == null) {
                System.out.println("Login failed. Invalid credentials or account not approved.");
                return null;
            }

            System.out.println("Login successful! Welcome " + user.getName());
            return user;
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            return null;
        }
    }

    /** Log out message */
    public void displayLogoutMessage() {
        System.out.println("Logged out. Goodbye.");
    }
}

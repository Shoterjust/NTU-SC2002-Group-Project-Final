package boundary;

import controller_class.IDataRepo;
import boundary.IFileHandler;
import entity_class.CompanyRep;
import java.util.Scanner;

/**
 * Handles registration of new company representatives. This class is
 * responsible for gathering registration details from the user, creating
 * a new {@link CompanyRep} instance, adding it to the repository, and
 * saving the updated data via the {@link IFileHandler}. Splitting
 * registration into its own UI class keeps {@link LoginUI} focused
 * solely on authentication and adheres to the Single Responsibility
 * Principle (SRP).
 */
public class CompanyRepRegistrationUI {
    private final IDataRepo repo;
    private final IFileHandler fileHandler;

    /**
     * Constructs a new registration UI.
     *
     * @param repo        the data repository abstraction used to store new users
     * @param fileHandler the file handler used to persist changes
     */
    public CompanyRepRegistrationUI(IDataRepo repo, IFileHandler fileHandler) {
        this.repo = repo;
        this.fileHandler = fileHandler;
    }

    /**
     * Handles the registration process for a company representative.
     * Prompts the user for their details, validates input, adds the
     * representative to the repository, and persists data.
     */
    public void handleRegistration() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n------ REGISTER COMPANY REPRESENTATIVE -----");
        try {
            System.out.print("Email (company email): ");
            String email = scanner.nextLine().trim();
            // Validate email format
            if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                System.out.println("Invalid email format.");
                return;
            }
            System.out.print("Full Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Company Name: ");
            String companyName = scanner.nextLine().trim();
            System.out.print("Department: ");
            String department = scanner.nextLine().trim();
            System.out.print("Position: ");
            String position = scanner.nextLine().trim();
            // Create new company representative. Password defaults to "password".
            CompanyRep newRep = new CompanyRep(email, name, companyName, department, position);
            // Add to repository and save immediately
            repo.addUser(newRep);
            fileHandler.saveAllData(repo);
            System.out.println("Registration successful. " +
                    "Account is pending approval by Staff. " +
                    "You can log in once approved.");
        } catch (IllegalArgumentException e) {
            System.out.println("Registration failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }
}

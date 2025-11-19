package main;

import boundary.*;
import controller_class.*;
import entity_class.*;

public class MainApplication {
    public static void main(String[] args) {
        displayBanner();

        try {
            // Initialize data repository (use the IDataRepo abstraction)
            IDataRepo repo = DataRepo.getInstance();

            // Initialize CSV handler and load all data (use the IFileHandler abstraction)
            IFileHandler fileHandler = new FileHandler();

            boolean loadSuccess = fileHandler.loadAllData(repo);
            if (!loadSuccess) {
                System.err.println("Data loading failed (parsing errors)");
                return;
            }

            // Initialize controllers via interfaces
            ILoginController loginController = new LoginController(repo);
            IStudentController studentController = new StudentController(repo);
            ICompanyRepController companyRepController = new CompanyRepController(repo);
            ICareerStaffController careerStaffController = new CareerStaffController(repo);

            // Initialize registration UI for company representatives
            CompanyRepRegistrationUI registrationUI = new CompanyRepRegistrationUI(repo, fileHandler);

            // Initialize login UI (delegate registration to registrationUI)
            LoginUI loginUI = new LoginUI(loginController, fileHandler, registrationUI);
            // Provide file handler to login controller for password changes
            loginController.setFileHandler(fileHandler);

            // Main application loop 
            label:
            while (true) {
                // Display welcome screen and handle login
                User user = loginUI.displayWelcomeScreen();
                if (user == null) {
                    break label;
                } else if (user instanceof Student) {
                    Student student = (Student) user;
                    StudentUI menu = new StudentUI(studentController, loginController, student);
                    menu.displayMenu();
                } else if (user instanceof CompanyRep) {
                    CompanyRep companyRep = (CompanyRep) user;
                    CompanyRepUI menu = new CompanyRepUI(companyRepController, loginController, companyRep);
                    menu.displayMenu();
                } else if (user instanceof CareerStaff) {
                    CareerStaff careerStaff = (CareerStaff) user;
                    CareerStaffUI menu = new CareerStaffUI(careerStaffController, loginController, careerStaff);
                    menu.displayMenu();
                }

                // Display logout message
                loginUI.displayLogoutMessage();
                // Save all data after user logs out
                fileHandler.saveAllData(repo);
            }

            // Final save before exiting
            fileHandler.saveAllData(repo);
            displayExitMessage();
        } catch (Exception e) {
            System.err.println("\nCritical error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void displayBanner() {
        System.out.println("=================================================================");
        System.out.println();
        System.out.println("         INTERNSHIP PLACEMENT MANAGEMENT SYSTEM                ");
        System.out.println();
        System.out.println("=================================================================");
    }

    private static void displayExitMessage() {
        System.out.println("=================================================================");
        System.out.println();
        System.out.println("                    LOG OUT SUCCESSFULLY                ");
        System.out.println();
        System.out.println("=================================================================");
    }
}

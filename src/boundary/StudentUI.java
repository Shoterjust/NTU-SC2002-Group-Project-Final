package boundary;

import controller_class.*;
import entity_class.*;
import java.util.*;

/**
 * Student UI. Uses IStudentController and ILoginController (DIP)
 */
public class StudentUI {
    private final Scanner scanner;
    private final IStudentController studentController;
    private final ILoginController loginController;
    private final Student currentStudent;

    public StudentUI(IStudentController studentController,
                     ILoginController loginController,
                     Student student) {
        this.scanner = new Scanner(System.in);
        this.studentController = studentController;
        this.loginController = loginController;
        this.currentStudent = student;
    }

    /**
     * Display Student Menu
     */
    public void displayMenu() {
        System.out.println("Student: " + currentStudent.getName());
        while (true) {
            System.out.println("""
                    \n1. View Eligible Internships\

                    2. Apply for Internship\

                    3. View My Applications (Max 3)\

                    4. Accept Internship Offer\

                    5. Reject Internship Offer\

                    6. Request Withdrawal\

                    7. Change Password\

                    8. View my Profile\
                    
                    9. Logout"""
            );
            System.out.println("\nSelect option: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> viewInternships();
                    case 2 -> applyForInternship();
                    case 3 -> viewApplications();
                    case 4 -> acceptInternship();
                    case 5 -> rejectInternship();
                    case 6 -> requestWithdrawal();
                    case 7 -> changePassword();
                    case 8 -> viewProfile();
                    case 9 -> {
                        return; // Logout
                    }
                    default -> System.out.println("Invalid option.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Enter again");
                scanner.nextLine();  // clear invalid input
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void viewInternships() {
        System.out.println("\nEligible Internships for student:");

        List<Internship> internships = studentController.getEligibleInternships(currentStudent);

        if (internships == null || internships.isEmpty()) {
            System.out.println("No internships available for your current profile.");
            return;
        }

        int count = 1;
        for (Internship intern : internships) {
            System.out.println(count + ". " + intern.getTitle());
            System.out.println("   Company: " + intern.getCompanyName());
            System.out.println("   Level: " + intern.getLevel());
            System.out.println("   Majors: " + intern.getPreferredMajor());
            System.out.println("   Slots: " + (intern.getNumberOfSlots() - intern.getConfirmedSlots()) + " available");
            System.out.println("   ID: " + intern.getInternshipID());
            System.out.println();
            count++;
        }
    }

    private void applyForInternship() {
        System.out.print("\nEnter Internship ID to apply: ");
        String internshipID = scanner.nextLine().trim();

        try {
            Application app = studentController.applyInternship(currentStudent, internshipID);
            System.out.println("Application submitted!");
            System.out.println("Application ID: " + app.getApplicationID());
            System.out.println("Status: " + app.getStatus());
        } catch (Exception e) {
            System.out.println("Application failed: " + e.getMessage());
        }
    }

    private void viewApplications() {
        System.out.println("\nMy Applications: ");

        List<Application> applications = studentController.viewApplications(currentStudent);

        if (applications == null || applications.isEmpty()) {
            System.out.println("No applications to see.");
            return;
        }

        System.out.println();
        for (Application app : applications) {
            System.out.println("Application ID: " + app.getApplicationID());
            System.out.println("Internship: " + app.getInternship().getTitle());
            System.out.println("Company: " + app.getInternship().getCompanyName());
            System.out.println("Status: " + app.getStatus());
            System.out.println("Applied on : " + app.getApplicationDate());
            if (app.isAccepted()) {
                System.out.println("ACCEPTED - You have confirmed this internship placement");
            }
            System.out.println();
        }
    }

    private void acceptInternship() {
        System.out.print("\nEnter Application ID to accept: ");
        String applicationID = scanner.nextLine().trim();

        try {
            studentController.acceptInternship(currentStudent, applicationID);
            System.out.println("Internship offer accepted");
            System.out.println("All other applications have been withdrawn.");
        } catch (Exception e) {
            System.out.println("Failed to accept: " + e.getMessage());
        }
    }

    private void rejectInternship() {
        System.out.print("\nEnter Application ID to reject: ");
        String applicationID = scanner.nextLine().trim();

        try {
            studentController.rejectInternship(currentStudent, applicationID);
            System.out.println("Internship offer rejected.");
        } catch (Exception e) {
            System.out.println("Failed to reject: " + e.getMessage());
        }
    }

    private void requestWithdrawal() {
        System.out.print("\nEnter Application ID to withdraw: ");
        String applicationID = scanner.nextLine().trim();

        try {
            WithdrawalRequest wr = studentController.requestWithdrawal(currentStudent, applicationID);
            System.out.println("Withdrawal request submitted.");
            System.out.println("Request ID: " + wr.getRequestID());
            System.out.println("Status: " + wr.getStatus() + " (awaiting Career Center approval)");
        } catch (Exception e) {
            System.out.println("Failed to request withdrawal: " + e.getMessage());
        }
    }

    private void changePassword() {
        System.out.print("Enter old password: ");
        String oldPassword = scanner.nextLine();

        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();

        try {
            if (loginController.changePassword(currentStudent, oldPassword, newPassword)) {
                System.out.println("Password changed");
            } else {
                System.out.println("Failed to change password.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewProfile() {
        System.out.println(currentStudent.getInfo());
        System.out.println("\nRegistered: " + currentStudent.getRegistrationDate());
        System.out.println("Active Applications: " + currentStudent.getActiveApplicationCount() +
                "/" + currentStudent.getMaxApplications());

        if (currentStudent.getAcceptedInternship() != null) {
            System.out.println("\nAccepted Internship: " +
                    currentStudent.getAcceptedInternship().getInternship().getCompanyName() + " - " +
                    currentStudent.getAcceptedInternship().getInternship().getTitle());
        }
    }
}

package boundary;

import controller_class.*;
import entity_class.*;
import java.util.*;

/**
 * Student UI. Handles student interactions after login.
 * Depends on {@link IStudentController} and {@link ILoginController}
 * abstractions for business logic and authentication.
 */
public class StudentUI {
    private final Scanner scanner;
    private final IStudentController studentController;
    private final ILoginController loginController;
    private final Student currentStudent;
    private List<Types.Major> filterMajors = new ArrayList<>();
    private Types.InternshipLevel filterLevel = null;
    private Date filterClosingDate = null;

    /** Constructs a new StudentUI.
     * @param studentController the student controller abstraction
     * @param loginController the login controller abstraction
     * @param student the currently logged-in student
     */
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

    /** View eligible internships for the student */
    private void viewInternships() {
        System.out.println("\nEligible Internships for student:");

        List<Internship> internships = studentController.getEligibleInternships(currentStudent);

        if (internships == null || internships.isEmpty()) {
            System.out.println("No internships available for your current profile.");
            return;
        }

        // Show current filter settings
        System.out.println("Current Filters:");
        System.out.println("   Majors: " +
                (filterMajors == null || filterMajors.isEmpty() ? "Any" : filterMajors));
        System.out.println("   Level: " + (filterLevel == null ? "Any" : filterLevel));
        System.out.println("   Closing Date (on or before): " +
                (filterClosingDate == null
                        ? "Any"
                        : new java.text.SimpleDateFormat("yyyy-MM-dd").format(filterClosingDate)));

        // Ask if user wants to change filters
        System.out.print("Would you like to update filters? (yes/no): ");
        String updateChoice = scanner.nextLine().trim();
        if (updateChoice.equalsIgnoreCase("yes")) {
            // Majors filter
            System.out.print("Filter by preferred majors (separate by space, Enter for any): ");
            String majorsInput = scanner.nextLine().trim().toUpperCase();
            if (majorsInput.isEmpty()) {
                filterMajors.clear();
            } else {
                List<Types.Major> selected = new ArrayList<>();
                String[] tokens = majorsInput.split("\\s+");
                for (String token : tokens) {
                    try {
                        Types.Major major = Types.Major.valueOf(token);
                        if (!selected.contains(major)) {
                            selected.add(major);
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid major: " + token);
                    }
                }
                filterMajors = selected;
            }

            // Level filter
            System.out.print("Filter by level (BASIC/INTERMEDIATE/ADVANCED or Enter for any): ");
            String levelStr = scanner.nextLine().trim().toUpperCase();
            if (levelStr.isEmpty()) {
                filterLevel = null;
            } else {
                try {
                    filterLevel = Types.InternshipLevel.valueOf(levelStr);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid level. No level filter applied.");
                    filterLevel = null;
                }
            }

            // Closing date filter
            System.out.print("Filter by closing date (yyyy-MM-dd) or Enter for any: ");
            String dateStr = scanner.nextLine().trim();
            if (dateStr.isEmpty()) {
                filterClosingDate = null;
            } else {
                try {
                    filterClosingDate =
                            new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
                } catch (Exception e) {
                    System.out.println("Invalid date format. Closing date filter removed.");
                    filterClosingDate = null;
                }
            }
        }

        // Apply filters on the eligible internships
        List<Internship> filtered = new ArrayList<>();
        for (Internship intern : internships) {
            // Majors (at least one overlap)
            if (filterMajors != null && !filterMajors.isEmpty()) {
                boolean match = false;
                for (Types.Major m : filterMajors) {
                    if (intern.getPreferredMajor().contains(m)) {
                        match = true;
                        break;
                    }
                }
                if (!match) continue;
            }

            // Level
            if (filterLevel != null && intern.getLevel() != filterLevel) continue;
            
            // Closing date 
            if (filterClosingDate != null && !intern.getCloseDate().equals(filterClosingDate)) continue;
            
            filtered.add(intern);
        }

        // Default ordering: alphabetical by internship title (case-insensitive)
        filtered.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));

        if (filtered.isEmpty()) {
            System.out.println("No internships match the current filters.");
            return;
        }

        int count = 1;
        for (Internship intern : filtered) {
            System.out.println(count + ". " + intern.getTitle());
            System.out.println("   Company: " + intern.getCompanyName());
            System.out.println("   Level: " + intern.getLevel());
            System.out.println("   Majors: " + intern.getPreferredMajor());
            System.out.println("   Slots: " +
                    (intern.getNumberOfSlots() - intern.getConfirmedSlots()) + " available");
            System.out.println("   ID: " + intern.getInternshipID());
            System.out.println();
            count++;
        }
    }

    /** Apply for an internship */
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

    /** View student's applications */
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

    /** Accept an internship offer */
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

    /** Reject an internship offer */
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

    /** Request withdrawal of an application */
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

    /** Change student's password */
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

    /** View student profile */
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

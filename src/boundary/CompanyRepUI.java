package boundary;

import controller_class.*;
import entity_class.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Company representative UI. Uses ICompanyRepController and
 * ILoginController (DIP)
 */
public class CompanyRepUI {
    private final Scanner scanner;
    private final ICompanyRepController controller;
    private final ILoginController loginController;
    private final CompanyRep currentRep;

    public CompanyRepUI(ICompanyRepController controller,
                        ILoginController loginController,
                        CompanyRep rep) {
        this.scanner = new Scanner(System.in);
        this.controller = controller;
        this.loginController = loginController;
        this.currentRep = rep;
    }

    public void displayMenu() {
        System.out.println("Company Representative: " + currentRep.getName());
        while (true) {
            System.out.println("""
                    \n1. Create Internship\

                    2. View Created Internships\

                    3. Update Internship\

                    4. Delete Internship (only if Pending)\

                    5. View Applications\

                    6. Process Application\

                    7. Toggle Internship Visibility\

                    8. Change Password\

                    9. View my Profile\
                    
                    10. Logout"""
            );
            System.out.println("\nSelect option: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> createInternship();
                    case 2 -> viewInternships();
                    case 3 -> updateInternship();
                    case 4 -> deleteInternship();
                    case 5 -> viewApplications();
                    case 6 -> processApplication();
                    case 7 -> toggleVisibility();
                    case 8 -> changePassword();
                    case 9 -> viewProfile();
                    case 10 -> {
                        return; // Logout
                    }
                    default -> System.out.println("Invalid option.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input");
                scanner.nextLine();    // clear invalid input
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void createInternship() {
        System.out.println("\nCreate Internship: ");
        if (!currentRep.canCreateMoreInternships()) {
            System.out.println("Maximum active internship reached");
            return;
        }

        try {
            // Create internship with ID
            System.out.print("Title: ");
            String title = scanner.nextLine();

            System.out.print("Description: ");
            String description = scanner.nextLine();

            System.out.print("Level (BASIC/INTERMEDIATE/ADVANCED): ");
            Types.InternshipLevel level = Types.InternshipLevel.valueOf(scanner.nextLine().toUpperCase());

            System.out.print("Number of slots (1-10): ");
            int slots = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Opening date (yyyy-MM-dd): ");
            Date openDate = new SimpleDateFormat("yyyy-MM-dd").parse(scanner.nextLine());

            System.out.print("Closing date (yyyy-MM-dd): ");
            Date closeDate = new SimpleDateFormat("yyyy-MM-dd").parse(scanner.nextLine());

            // Add majors
            List<Types.Major> majors = new ArrayList<>();
            System.out.println("\nAdd preferred majors (available: CCDS, COE, NBS, SPMS, SBS, WKWSCI, COHASS)");
            System.out.println("Enter majors one by one. Press Enter without input to finish.");

            while (true) {
                System.out.print("Major: ");
                String majorStr = scanner.nextLine().trim().toUpperCase();
                if (majorStr.isEmpty()) break;

                try {
                    Types.Major major = Types.Major.valueOf(majorStr);
                    if (majors.contains(major)) {
                        System.out.println(major + " already added.");
                    } else {
                        majors.add(major);
                        System.out.println("Added " + major);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid major: " + majorStr);
                }
            }

            if (majors.isEmpty()) {
                System.out.println("No preferred majors selected. Internship will be open to all");
            }

            Internship internship = controller.createInternship(currentRep, title, description, level,
                    majors, openDate, closeDate, slots);

            System.out.println("\nInternship created");
            System.out.println("ID: " + internship.getInternshipID());
            System.out.println("Status: " + internship.getStatus() + " (awaiting Career Center approval)");
        } catch (ParseException e) {
            System.out.println("Invalid date format. Use yyyy-MM-dd.");
        } catch (Exception e) {
            System.out.println("Failed to create internship: " + e.getMessage());
        }
    }

    private void viewInternships() {
        System.out.println("\nMY INTERNSHIPS: ");

        System.out.print("""
        
        Filter by status:
        1. All Internships
        2. PENDING (awaiting Approval)
        3. APPROVED (visible to Students)
        4. REJECTED (by Staff)
        5. FILLED (all slots confirmed)""");

        System.out.println("\nSelect option: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            List<Internship> internships;
            String filter;

            switch (choice) {
                case 2 -> {
                    internships = controller.viewInternshipByStatus(currentRep, Types.InternshipStatus.PENDING);
                    filter = "PENDING";
                }
                case 3 -> {
                    internships = controller.viewInternshipByStatus(currentRep, Types.InternshipStatus.APPROVED);
                    filter = "APPROVED";
                }
                case 4 -> {
                    internships = controller.viewInternshipByStatus(currentRep, Types.InternshipStatus.REJECTED);
                    filter = "REJECTED";
                }
                case 5 -> {
                    internships = controller.viewInternshipByStatus(currentRep, Types.InternshipStatus.FILLED);
                    filter = "FILLED";
                }
                default -> {
                    internships = controller.viewInternships(currentRep);
                    filter = "ALL";
                }
            }

            if (internships == null || internships.isEmpty()) {
                System.out.println("No internships found (" + filter + ")");
                return;
            }

            System.out.println("Showing: " + filter + " (" + internships.size() + " internship(s))");
            System.out.println();

            for (Internship i : internships) {
                System.out.println("ID: " + i.getInternshipID());
                System.out.println("Title: " + i.getTitle());
                System.out.println("Status: " + i.getStatus());
                System.out.println("Level: " + i.getLevel());
                System.out.println("Visible: " + (i.isVisible() ? "Yes" : "No"));
                System.out.println("Slots: " + i.getConfirmedSlots() + "/" + i.getNumberOfSlots());
                System.out.println("Applications: " + i.getApplications().size());
                System.out.println("Open: " + i.getOpenDate() + "; Close: " + i.getCloseDate());
                System.out.println();
            }

            int totalCreated = currentRep.getCreatedInternships().size();
            long rejectedCount = currentRep.getCreatedInternships().stream()
                    .filter(i -> i.getStatus() == Types.InternshipStatus.REJECTED)
                    .count();
            int activeCount = totalCreated - (int) rejectedCount;

            System.out.println("Total created: " + totalCreated +
                    " (Rejected: " + rejectedCount + ", Active: " + activeCount + "/" +
                    CompanyRep.getMaxApprovedInternships() + ")");
            System.out.println("Can create more? " + (currentRep.canCreateMoreInternships() ? "Yes" : "No"));
        } catch (InputMismatchException e) {
            System.out.println("Invalid input");
            scanner.nextLine();
        }
    }

    private void updateInternship() {
        System.out.print("\nEnter Internship ID to update: ");
        String internshipID = scanner.nextLine().trim();

        try {
            Internship internship = currentRep.getCreatedInternships().stream()
                    .filter(i -> i.getInternshipID().equals(internshipID))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Internship not found or not owned by you"));

            System.out.printf("""
        Current Details:
          Title: %s
          Status: %s
          Level: %s
          Current Majors: %s
          Slots: %d
          Visible: %b%n
        """,
                    internship.getTitle(),
                    internship.getStatus(),
                    internship.getLevel(),
                    internship.getPreferredMajor(),
                    internship.getNumberOfSlots(),
                    internship.isVisible()
            );

            System.out.print("""
        
        What would you like to update?
        1. Basic details (Title, Description, Level, Slots)
        2. Add preferred major
        3. Remove preferred major
        4. Update dates
        5. Go back""");

            System.out.println("\nSelect option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> updateBasicDetails(internshipID);
                case 2 -> addMajor(internshipID);
                case 3 -> removeMajor(internshipID);
                case 4 -> updateDates(internshipID);
                case 5 -> {}
                default -> System.out.println("✗ Invalid option.");
            }

        } catch (Exception e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    // Update helpers
    private void updateBasicDetails(String internshipID) {
        System.out.print("New title (or press Enter to skip): ");
        String title = scanner.nextLine().trim();

        System.out.print("New description (or press Enter to skip): ");
        String description = scanner.nextLine().trim();

        System.out.print("New level (BASIC/INTERMEDIATE/ADVANCED or Enter to skip): ");
        String levelStr = scanner.nextLine().trim();
        Types.InternshipLevel level = levelStr.isEmpty()
                ? null
                : Types.InternshipLevel.valueOf(levelStr.toUpperCase());

        System.out.print("New number of slots (or 0 to skip): ");
        int slots = scanner.nextInt();
        scanner.nextLine();

        try {
            controller.updateInternship(currentRep, internshipID,
                    title.isEmpty() ? null : title,
                    description.isEmpty() ? null : description,
                    level, null, null,
                    slots > 0 ? slots : null
            );

            System.out.println("\nInternship updated");
        } catch (Exception e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    private void addMajor(String internshipID) {
        Internship internship = currentRep.getCreatedInternships().stream()
                .filter(i -> i.getInternshipID().equals(internshipID))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Internship not found"));

        System.out.println("Available majors: CCDS, COE, NBS, SPMS, SBS, WKWSCI, COHASS");
        System.out.println("\nEnter majors to add (separate by space)");
        System.out.print("Majors: ");
        String input = scanner.nextLine().trim().toUpperCase();

        if (input.isEmpty()) {
            System.out.println("No majors entered.");
            return;
        }

        String[] majors = input.split("\\s+");

        for (String majorStr : majors) {
            try {
                Types.Major major = Types.Major.valueOf(majorStr);
                if (!internship.getPreferredMajor().contains(major)) {
                    controller.addPreferredMajor(currentRep, internshipID, major);
                    System.out.println(major + " added");
                }
            } catch (IllegalArgumentException e) {
                System.out.println(majorStr + ": invalid major");
            } catch (Exception e) {
                System.out.println("Failed: " + e.getMessage());
            }
        }
    }

    private void removeMajor(String internshipID) {
        try {
            Internship internship = currentRep.getCreatedInternships().stream()
                    .filter(i -> i.getInternshipID().equals(internshipID))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Internship not found"));

            if (internship.getPreferredMajor().isEmpty()) {
                System.out.println("\nThis internship has no preferred majors");
                return;
            }

            System.out.println("\nEnter majors to remove (separate by space)");
            System.out.print("Majors: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.isEmpty()) {
                System.out.println("No majors entered.");
                return;
            }

            String[] majors = input.split("\\s+");
            for (String majorStr : majors) {
                try {
                    Types.Major major = Types.Major.valueOf(majorStr);
                    // Check if exists
                    if (!internship.getPreferredMajor().contains(major)) {
                        System.out.println(major + " is not in list");
                    } else {
                        controller.removePreferredMajor(currentRep, internshipID, major);
                        System.out.println(major + " - removed");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(majorStr + ": invalid major");
                } catch (Exception e) {
                    System.out.println("Failed: " + e.getMessage());
                }
            }

            System.out.println("Remaining majors: " + internship.getPreferredMajor());
            if (internship.getPreferredMajor().isEmpty()) {
                System.out.println("\nThis internship now has NO preferred majors, open to all students");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateDates(String internshipID) {
        try {
            System.out.print("\nNew opening date (yyyy-MM-dd or Enter to skip): ");
            String openStr = scanner.nextLine().trim();
            Date openDate = openStr.isEmpty()
                    ? null
                    : new SimpleDateFormat("yyyy-MM-dd").parse(openStr);

            System.out.print("New closing date (yyyy-MM-dd or Enter to skip): ");
            String closeStr = scanner.nextLine().trim();
            Date closeDate = closeStr.isEmpty()
                    ? null
                    : new SimpleDateFormat("yyyy-MM-dd").parse(closeStr);

            controller.updateInternship(currentRep, internshipID,
                    null, null, null, openDate, closeDate, null);

            System.out.println("\nDates updated successfully.");

        } catch (ParseException e) {
            System.out.println("Invalid date format. Valid format: 2025-12-31.");
        } catch (Exception e) {
            System.out.println("Update failed: " + e.getMessage());
        }
    }

    private void deleteInternship() {
        System.out.print("\nEnter Internship ID to delete: ");
        String internshipID = scanner.nextLine().trim();

        System.out.println("Deletion cannot be undone. Type Yes to confirm");
        String confirm = scanner.nextLine().trim();

        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        try {
            controller.deleteInternship(currentRep, internshipID);
            System.out.println("\nInternship deleted");
        } catch (Exception e) {
            System.out.println("Fail to delete internship: " + e.getMessage());
        }
    }

    private void viewApplications() {
        System.out.print("\nEnter Internship ID: ");
        String internshipID = scanner.nextLine().trim();

        try {
            List<Application> applications = controller.viewApplications(currentRep, internshipID);

            if (applications == null || applications.isEmpty()) {
                System.out.println("No applications yet.");
                return;
            }

            System.out.println("Applications for the chosen Internship: ");
            for (Application app : applications) {
                System.out.println("Application ID: " + app.getApplicationID());
                System.out.println("Student: " + app.getStudent().getName() +
                        " (" + app.getStudent().getUserID() + ")");
                System.out.println("Major: " + app.getStudent().getMajor());
                System.out.println("Year: " + app.getStudent().getYearOfStudy());
                System.out.println("Status: " + app.getStatus());
                System.out.println("Applied: " + app.getApplicationDate());
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void processApplication() {
        System.out.print("\nEnter Internship ID: ");
        String internshipID = scanner.nextLine().trim();

        System.out.print("Enter Application ID: ");
        String applicationID = scanner.nextLine().trim();

        System.out.print("Decision (APPROVE/REJECT): ");
        String decision = scanner.nextLine().trim().toUpperCase();

        try {
            Types.ApplicationStatus status = decision.equals("APPROVE")
                    ? Types.ApplicationStatus.SUCCESSFUL
                    : Types.ApplicationStatus.UNSUCCESSFUL;

            controller.processApplication(currentRep, internshipID, applicationID, status);
            System.out.println("Application processed successfully.");

            if (decision.equals("APPROVE")) {
                System.out.println("Student will give accept/reject decision for this offer.");
            }
        } catch (Exception e) {
            System.out.println("Failed: " + e.getMessage());
        }
    }

    private void toggleVisibility() {
        System.out.print("\nEnter Internship ID: ");
        String internshipID = scanner.nextLine().trim();

        try {
            controller.toggleVisibility(currentRep, internshipID);
            System.out.println("Visibility changed");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void changePassword() {
        System.out.print("Enter old password: ");
        String oldPassword = scanner.nextLine();

        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();

        try {
            if (loginController.changePassword(currentRep, oldPassword, newPassword)) {
                System.out.println("Password updated");
            } else {
                System.out.println("Failed to change password");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewProfile() {
        System.out.println(currentRep.getInfo());
        System.out.println();

        System.out.println("Registration Date: " + currentRep.getRegistrationDate());
        System.out.println("Approval Status: " + (currentRep.isApproved() ? "Approved" : "Pending"));
        System.out.println("Internships Created: " + currentRep.getCreatedInternships().size() +
                "/" + CompanyRep.getMaxApprovedInternships());
        System.out.println("Can Create More? " + (currentRep.canCreateMoreInternships() ? "Yes" : "No"));

        if (!currentRep.isApproved()) {
            System.out.println("Your account is pending approval");
        }
    }
}

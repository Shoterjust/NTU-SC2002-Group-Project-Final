package boundary;

import controller_class.*;
import entity_class.*;
import java.util.*;
import java.io.*;

/**
 * Career Center Staff UI.
 * Uses ICareerStaffController + ILoginController (DIP)
 */
public class CareerStaffUI {
    private final Scanner scanner;
    private final ICareerStaffController controller;
    private final ILoginController loginController;
    private final CareerStaff currentStaff;

    public CareerStaffUI(ICareerStaffController controller,
                         ILoginController loginController,
                         CareerStaff staff) {
        this.scanner = new Scanner(System.in);
        this.controller = controller;
        this.loginController = loginController;
        this.currentStaff = staff;
    }

    public void displayMenu() {
        System.out.println(currentStaff.getName() + ": Career Center Menu");
        while (true) {
            System.out.println("""
                    \n1. Manage Company Representatives (Approve/Reject)\
                    
                    2. Approve/Reject Internships\
                    
                    3. Process Withdrawal Requests\
                    
                    4. View All Users\
                    
                    5. View All Internships\
                    
                    6. View Statistics\
                    
                    7. Generate Report\
                    
                    8. Change Password\
                    
                    9. View my Profile\
                    
                    10. Logout"""
            );
            System.out.println("\nSelect option: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> authorizeCompanyReps();
                    case 2 -> approveRejectInternships();
                    case 3 -> processWithdrawals();
                    case 4 -> viewAllUsers();
                    case 5 -> viewAllInternships();
                    case 6 -> viewStatistics();
                    case 7 -> generateReport();
                    case 8 -> changePassword();
                    case 9 -> viewProfile();
                    case 10 -> {
                        return; // Logout
                    }
                    default -> System.out.println("Invalid option.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input.");
                scanner.nextLine();  // clear invalid input
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void authorizeCompanyReps() {
        List<CompanyRep> pending = controller.getPendingCompanyReps();

        if (pending == null || pending.isEmpty()) {
            System.out.println("No pending company representatives.");
            return;
        }

        System.out.println("\nPending Company Representatives: ");
        for (CompanyRep rep : pending) {
            System.out.println("Email: " + rep.getUserID());
            System.out.println("Name: " + rep.getName());
            System.out.println("Company: " + rep.getCompanyName());
            System.out.println("Department: " + rep.getDepartment());
            System.out.println("Position: " + rep.getPosition());
            System.out.println();
        }

        System.out.print("\nEnter email to process (or 'skip'): ");
        String email = scanner.nextLine().trim();

        if (email.equalsIgnoreCase("skip")) return;

        System.out.print("Decision (APPROVE/REJECT): ");
        String decision = scanner.nextLine().trim().toUpperCase();

        System.out.print("Confirm " + decision + " for " + email + "? (yes/no): ");
        String confirm = scanner.nextLine().trim();

        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Processing cancelled.");
            return;
        }

        try {
            if (decision.equals("APPROVE")) {
                controller.approveCompanyRep(email);
                System.out.println("\nCompany representative APPROVED.");
                System.out.println("They can now create and manage internship postings.");
            } else if (decision.equals("REJECT")) {
                controller.rejectCompanyRep(email);
                System.out.println("\nCompany representative REJECTED and removed from system.");
                System.out.println("They will need to re-register if they wish to access");
            } else {
                System.out.println("Invalid decision. APPROVE or REJECT only.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void approveRejectInternships() {
        List<Internship> pending = controller.getPendingInternships();

        if (pending == null || pending.isEmpty()) {
            System.out.println("No pending internships.");
            return;
        }

        System.out.println("\nPending Internships: ");
        for (Internship i : pending) {
            System.out.println("ID: " + i.getInternshipID());
            System.out.println("Title: " + i.getTitle());
            System.out.println("Company: " + i.getCompanyName());
            System.out.println("Level: " + i.getLevel());
            System.out.println("Majors: " + i.getPreferredMajor());
            System.out.println("Slots: " + i.getNumberOfSlots());
            System.out.println();
        }

        System.out.print("\nEnter Internship ID to process (or 'skip'): ");
        String id = scanner.nextLine().trim();

        if (id.equalsIgnoreCase("skip")) return;

        System.out.print("Decision (APPROVE/REJECT): ");
        String decision = scanner.nextLine().trim().toUpperCase();

        System.out.print("Confirm " + decision + " for internship " + id + "? (yes/no): ");
        String confirm = scanner.nextLine().trim();

        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Approval process cancelled.");
            return;
        }

        try {
            if (decision.equals("APPROVE")) {
                controller.approveInternship(id);
                System.out.println("Internship approved. It is now visible to eligible students.");
            } else if (decision.equals("REJECT")) {
                controller.rejectInternship(id);
                System.out.println("Internship rejected");
            } else {
                System.out.println("Invalid decision. APPROVE or REJECT only");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void processWithdrawals() {
        List<WithdrawalRequest> pending = controller.getPendingWithdrawals();

        if (pending == null || pending.isEmpty()) {
            System.out.println("No pending withdrawal requests.");
            return;
        }

        System.out.println("\nPending Withdrawal Requests:");
        int count = 1;
        for (WithdrawalRequest wr : pending) {
            System.out.println(count + ". Request ID: " + wr.getRequestID());
            System.out.println("   Student: " + wr.getStudent().getName() +
                    " (" + wr.getStudent().getUserID() + ")");
            System.out.println("   Application ID: " + wr.getApplication().getApplicationID());
            System.out.println("   Internship: " + wr.getApplication().getInternship().getTitle());
            System.out.println("   Company: " + wr.getApplication().getInternship().getCompanyName());
            System.out.println("   Requested: " + wr.getRequestDate());
            System.out.println("   Application Status: " + wr.getApplication().getStatus());
            System.out.println();
            count++;
        }

        System.out.print("\nEnter Request ID to process (or 'skip'): ");
        String id = scanner.nextLine().trim();

        if (id.equalsIgnoreCase("skip")) return;

        System.out.print("Decision (APPROVE/REJECT): ");
        String decision = scanner.nextLine().trim().toUpperCase();

        System.out.print("Confirm " + decision + " for request " + id + "? (yes/no): ");
        String confirm = scanner.nextLine().trim();

        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("Withdraw request process cancelled.");
            return;
        }

        try {
            controller.processWithdrawal(id, decision.equals("APPROVE"));
            System.out.println("Withdrawal request processed");

            if (decision.equals("APPROVE")) {
                System.out.println("Application has been withdrawn");
            } else {
                System.out.println("Withdrawal request rejected");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewAllUsers() {
        List<User> allUsers = controller.viewAllUsers();

        if (allUsers == null || allUsers.isEmpty()) {
            System.out.println("\nNo users in the system.");
            return;
        }

        System.out.println("\n*************************************************");  
        // Separate by user type
        List<Student> students = new ArrayList<>();
        List<CompanyRep> reps = new ArrayList<>();
        List<CareerStaff> staff = new ArrayList<>();

        for (User user : allUsers) {
            if (user instanceof Student s) students.add(s);
            else if (user instanceof CompanyRep r) reps.add(r);
            else if (user instanceof CareerStaff cs) staff.add(cs);
        }

        //Display students
        System.out.println("\nSTUDENTS (" + students.size() + ")");
        for (Student s : students) {
            System.out.println(s.getUserID() + " - " +
                    s.getName() + " (" + s.getMajor() + ", Year "
                    + s.getYearOfStudy() + ")");
        }

        // Display company reps
        System.out.println("\nCOMPANY REPRESENTATIVES (" + reps.size() + ")");
        for (CompanyRep r : reps) {
            String status = r.isApproved() ? "Approved" : "Pending";
            System.out.println(r.getUserID() + " - " +
                    r.getName() + " (" + r.getCompanyName() + ") - " +
                    status);
        }

         // Display Staff
        System.out.println("\nCAREER CENTER STAFF (" + staff.size() + ")");
        for (CareerStaff cs : staff) {
            System.out.println(cs.getUserID() + " - " + cs.getName() +
                    " (" + cs.getDepartment() + ")");
        }

        System.out.println();
        System.out.println("Total Users: " + allUsers.size());
    }

    private void viewAllInternships() {
        List<Internship> allInternships = controller.viewAllInternships();

        if (allInternships == null || allInternships.isEmpty()) {
            System.out.println("No internships in the system.");
            return;
        }

        System.out.println("INTERNSHIP LISTINGS: ");

        //Group by status
        Map<Types.InternshipStatus, List<Internship>> iByStatus = new HashMap<>();
        for (Types.InternshipStatus status : Types.InternshipStatus.values()) {
            iByStatus.put(status, new ArrayList<>());
        }

        for (Internship i : allInternships) {
            iByStatus.get(i.getStatus()).add(i);
        }

        // Print them
        for (Types.InternshipStatus status : Types.InternshipStatus.values()) {
            List<Internship> internships = iByStatus.get(status);
            if (!internships.isEmpty()) {
                System.out.println(status + " (" + internships.size() + ")");
                for (Internship i : internships) {
                    String visible = i.isVisible() ? "Visible" : "Hidden";
                    System.out.println(i.getInternshipID() + " - " + i.getTitle() +
                            " (" + i.getCompanyName() + ") - " + i.getLevel() +
                            " - Slots: " + i.getConfirmedSlots() + "/" + i.getNumberOfSlots() +
                            " - " + visible);
                }
            }
        }

        System.out.println("\nTotal Internships: " + allInternships.size());
    }

    private void viewStatistics() {
        DataRepo repo = DataRepo.getInstance();

        // Count users by type
        long students = repo.getAllUsers().stream().filter(u -> u instanceof Student).count();
        long companyReps = repo.getAllUsers().stream().filter(u -> u instanceof CompanyRep).count();
        long staff = repo.getAllUsers().stream().filter(u -> u instanceof CareerStaff).count();
        long approvedReps = repo.getAllUsers().stream()
                .filter(u -> u instanceof CompanyRep && ((CompanyRep) u).isApproved()).count();

        // Count internships by status
        List<Internship> allInternships = repo.getAllInternships();
        long pending = allInternships.stream()
                .filter(i -> i.getStatus() == Types.InternshipStatus.PENDING).count();
        long approved = allInternships.stream()
                .filter(i -> i.getStatus() == Types.InternshipStatus.APPROVED).count();
        long rejected = allInternships.stream()
                .filter(i -> i.getStatus() == Types.InternshipStatus.REJECTED).count();
        long filled = allInternships.stream()
                .filter(i -> i.getStatus() == Types.InternshipStatus.FILLED).count();

        System.out.println("USERS:");
        System.out.println("  Students: " + students);
        System.out.println("  Company Representatives: " + companyReps + " (Approved: " + approvedReps + ")");
        System.out.println("  Career Center Staff: " + staff);
        System.out.println("  Total Users: " + repo.getAllUsers().size());

        System.out.println("\nINTERNSHIPS:");
        System.out.println("  Pending Approval: " + pending);
        System.out.println("  Approved: " + approved);
        System.out.println("  Rejected: " + rejected);
        System.out.println("  Filled: " + filled);
        System.out.println("  Total: " + allInternships.size());

        System.out.println("\nWITHDRAWAL REQUESTS:");
        System.out.println("  Pending: " + controller.getPendingWithdrawals().size());
    }

    private void generateReport() {
        final String REPORTS_DIR = "reports/";
        File reportsFolder = new File(REPORTS_DIR);
        if (!reportsFolder.exists()) {
            reportsFolder.mkdirs();
            System.out.println("Created reports directory: " + REPORTS_DIR);
        }

        try {
            System.out.print("Filter by Major (or Enter for all): ");
            String majorStr = scanner.nextLine().trim().toUpperCase();
            Types.Major major = majorStr.isEmpty() ? null : Types.Major.valueOf(majorStr);

            System.out.print("Filter by Level (or Enter for all): ");
            String levelStr = scanner.nextLine().trim().toUpperCase();
            Types.InternshipLevel level = levelStr.isEmpty() ? null :
                    Types.InternshipLevel.valueOf(levelStr);

            System.out.print("Filter by Status (or Enter for all): ");
            String statusStr = scanner.nextLine().trim().toUpperCase();
            Types.InternshipStatus status = statusStr.isEmpty() ? null :
                    Types.InternshipStatus.valueOf(statusStr);

            System.out.print("Filename (e.g: report.txt): ");
            String filename = scanner.nextLine().trim();
            if (filename.isEmpty()) {
                filename = "internship_report.txt";
            }

            String path = REPORTS_DIR + filename;

            // Get filtered internships using controller method
            List<Internship> internships = controller.getFilteredInternships(
                    major != null ? List.of(major) : null,
                    null, level, null, null, status
            );

            // Save to file
            try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
                writer.println("              INTERNSHIP PLACEMENT MANAGEMENT REPORT");
                writer.println();
                writer.println("Generated By: " + currentStaff.getName() + " (" + currentStaff.getUserID() + ")");
                writer.println("Department: " + currentStaff.getDepartment());
                writer.println();
                writer.println("Filters Applied:");
                writer.println("  Major: " + (major != null ? major : "All"));
                writer.println("  Level: " + (level != null ? level : "All"));
                writer.println("  Status: " + (status != null ? status : "All"));
                writer.println();

                writer.println("***********************************************************");
                writer.println();
                for (Internship i : internships) {
                    writer.println(i.getInternshipID() + " | " + i.getCompanyName() + " | " +
                            i.getTitle() + " | " + i.getStatus() + " | " + i.getLevel() +
                            " | Slots: " + i.getConfirmedSlots() + "/" + i.getNumberOfSlots());
                }
                writer.println();
                writer.println("Total Internships: " + internships.size());
            }

            System.out.println("\nReport saved to " + path);

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid filter value: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    private void changePassword() {
        System.out.print("Enter old password: ");
        String oldPassword = scanner.nextLine();

        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();

        System.out.print("Confirm new password: ");
        String confirmPassword = scanner.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            return;
        }

        try {
            if (loginController.changePassword(currentStaff, oldPassword, newPassword)) {
                System.out.println("Password changed successfully.");
            } else {
                System.out.println("Failed to change password. Check old password again.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewProfile() {
        System.out.println(currentStaff.getInfo());
        System.out.println("Registration Date: " + currentStaff.getRegistrationDate());
    }
}

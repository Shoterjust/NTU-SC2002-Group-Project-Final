package boundary;

import controller_class.IDataRepo;
import boundary.IFileHandler;
import entity_class.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Handles reading from and writing to CSV files.
 *
 * This class implements the {@link IFileHandler} interface and
 * interacts with the {@link IDataRepo} abstraction rather than the
 * concrete {@code DataRepo} implementation. This design adheres to
 * the Dependency Inversion Principle (DIP), allowing UIs and
 * controllers to depend on abstractions instead of concrete
 * classes.
 */
public class FileHandler implements IFileHandler {
    // Directory containing CSV data files.
    private static final String DATA_DIR = "src/data/";
    private static final String STUDENT_FILE = DATA_DIR + "sample_student_list.csv";
    private static final String STAFF_FILE = DATA_DIR + "sample_staff_list.csv";
    private static final String COMPANYREP_FILE = DATA_DIR + "sample_company_representative_list.csv";
    private static final String INTERNSHIP_FILE = DATA_DIR + "Internship.csv";
    private static final String APPLICATION_FILE = DATA_DIR + "Application.csv";
    private static final String WITHDRAWAL_FILE = DATA_DIR + "WithdrawalRequest.csv";

    // Store passwords and student emails in memory
    private final Map<String, String> userPasswords = new HashMap<>();
    private final Map<String, String> studentEmails = new HashMap<>();

    private boolean loadedSuccessfully = false;

    /** Load all data from CSV files into the provided repository.
     * @param repo the data repository abstraction to load data into
     * @return true if data loaded successfully without errors
     */
    @Override
    public boolean loadAllData(IDataRepo repo) {
        System.out.println("Loading data from CSV files");
        loadedSuccessfully = false;
        userPasswords.clear();
        studentEmails.clear();

        int studentErrors = loadStudents(repo);
        int staffErrors = loadStaff(repo);
        int repErrors = loadCompanyReps(repo);
        int internshipErrors = loadInternships(repo);
        int applicationErrors = loadApplications(repo);
        int withdrawalErrors = loadWithdrawalRequests(repo);

        int totalErrors = studentErrors + staffErrors + repErrors
                          + internshipErrors + applicationErrors + withdrawalErrors;

        if (totalErrors > 0) {
            System.err.println(totalErrors + " error(s) occurred during loading!");
            System.err.println("Data will NOT be saved on exit to prevent data loss.");
            loadedSuccessfully = false;
        } else {
            System.out.println("data loaded!");
            loadedSuccessfully = true;
        }
        return loadedSuccessfully;
    }

    /** Load students from CSV */
    private int loadStudents(IDataRepo repo) {
        int success = 0, errors = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(STUDENT_FILE))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 6) {
                    System.err.println("Error parsing student (insufficient fields): " + line);
                    errors++;
                    continue;
                }
                try {
                    String studentID = parts[0].trim();
                    String password = parts[1].trim();
                    String name = parts[2].trim();
                    String majorStr = parts[3].trim().toUpperCase();
                    int year = Integer.parseInt(parts[4].trim());
                    String email = parts[5].trim();
                    if (studentID.isEmpty() || name.isEmpty() || majorStr.isEmpty()) continue;
                    Types.Major major = mapMajor(majorStr);
                    Student student = new Student(studentID, name, year, major, password);
                    userPasswords.put(studentID, password);
                    studentEmails.put(studentID, email);
                    repo.addUser(student);
                    success++;
                } catch (Exception e) {
                    System.err.println("Error parsing student: " + parts[0] + " - " + e.getMessage());
                    errors++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading student file: " + e.getMessage());
            return 999;
        }
        System.out.println("  Students loaded: " + success + (errors > 0 ? " (errors: " + errors + ")" : ""));
        return errors;
    }

    /** Load staff from CSV */
    private int loadStaff(IDataRepo repo) {
        int success = 0, errors = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(STAFF_FILE))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 6) {
                    System.err.println("Error parsing staff (insufficient fields): " + line);
                    errors++;
                    continue;
                }
                try {
                    String staffID = parts[0].trim(); 
                    String password = parts[1].trim();
                    String name = parts[2].trim();
                    String department = parts[4].trim();
                    CareerStaff staff = new CareerStaff(staffID, name, department, password);
                    userPasswords.put(staffID, password);
                    repo.addUser(staff);
                    success++;
                } catch (Exception e) {
                    System.err.println("Error parsing staff: " + line);
                    System.err.println("  Reason: " + e.getMessage());
                    errors++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading staff file: " + e.getMessage());
            return 999;
        }
        System.out.println("  Staff loaded: " + success + (errors > 0 ? " (errors: " + errors + ")" : ""));
        return errors;
    }

    /** Load company representatives from CSV */
    private int loadCompanyReps(IDataRepo repo) {
        int success = 0, errors = 0;
        File file = new File(COMPANYREP_FILE);
        if (!file.exists()) {
            System.out.println("No company rep file found. Creating empty file.");
            createEmptyCompanyRepFile();
            return 0;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(COMPANYREP_FILE))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 8) {
                    System.err.println("Error parsing company rep (insufficient fields): " + line);
                    errors++;
                    continue;
                }
                try {
                    String email = parts[0].trim();
                    String password = parts[1].trim();
                    String name = parts[2].trim();
                    String companyName = parts[3].trim();
                    String department = parts[4].trim();
                    String position = parts[5].trim();
                    boolean approved = parts[7].trim().equalsIgnoreCase("APPROVED");
                    CompanyRep rep = new CompanyRep(email, name, companyName, department, position, password);
                    rep.setApproved(approved);
                    userPasswords.put(email, password);
                    repo.addUser(rep);
                    success++;
                } catch (Exception e) {
                    System.err.println("Error parsing company rep: " + line);
                    System.err.println("  Reason: " + e.getMessage());
                    errors++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading company rep file: " + e.getMessage());
            return 999;
        }
        System.out.println("  Company reps loaded: " + success + (errors > 0 ? " (errors: " + errors + ")" : ""));
        return errors;
    }

    /** Load internships from CSV */
    private int loadInternships(IDataRepo repo) {
        File file = new File(INTERNSHIP_FILE);
        if (!file.exists()) {
            System.out.println("  No internship file found. Starting with empty internships.");
            createEmptyInternshipFile();
            return 0;
        }
        int success = 0, errors = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(INTERNSHIP_FILE))) {
            br.readLine(); // skip header
            String line;
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length < 10) {
                    System.err.println("Error line " + lineNumber + ": insufficient fields (found " +
                            parts.length + ", need 10)");
                    errors++;
                    continue;
                }
                try {
                    String internshipID = parts[0].trim();
                    String title = parts[1].trim();
                    String description = parts[2].trim();
                    Types.InternshipLevel level = Types.InternshipLevel.valueOf(parts[3].trim().toUpperCase());
                    String majorStr = parts[4].trim();
                    Date openDate = new SimpleDateFormat("yyyy-MM-dd").parse(parts[5].trim());
                    Date closeDate = new SimpleDateFormat("yyyy-MM-dd").parse(parts[6].trim());
                    String companyName = parts[7].trim();
                    Types.InternshipStatus status = Types.InternshipStatus.valueOf(parts[8].trim().toUpperCase());
                    int slots = Integer.parseInt(parts[9].trim());
                    int confirmed = parts.length > 10 ? Integer.parseInt(parts[10].trim()) : 0;
                    boolean visible = parts.length <= 11 || Boolean.parseBoolean(parts[11].trim());

                    Internship internship = new Internship(internshipID);
                    internship.setTitle(title);
                    internship.setDescription(description);
                    internship.setLevel(level);
                    internship.setOpenDate(openDate);
                    internship.setCloseDate(closeDate);
                    internship.setCompanyName(companyName);
                    internship.setStatus(status);
                    internship.setNumberOfSlots(slots);
                    internship.setVisible(visible);

                    // preferred majors
                    if (!majorStr.isEmpty()) {
                        for (String m : majorStr.split(";")) {
                            try {
                                internship.getPreferredMajor().add(Types.Major.valueOf(m.trim().toUpperCase()));
                            } catch (Exception ignored) { }
                        }
                    }

                    // link to company rep
                    User user = repo.getAllUsers().stream()
                            .filter(u -> u instanceof CompanyRep &&
                                    ((CompanyRep) u).getCompanyName().equals(companyName))
                            .findFirst()
                            .orElse(null);

                    if (user instanceof CompanyRep rep) {
                        internship.setCompanyRepresentative(rep);
                        rep.getCreatedInternships().add(internship);
                    } else {
                        System.err.println("Warning line " + lineNumber + " (" + internshipID +
                                "): Company rep not found for '" + companyName);
                    }

                    repo.addInternship(internship);
                    success++;
                } catch (Exception e) {
                    System.err.println("Error line " + lineNumber + ": " + e.getMessage());
                    errors++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading internship file: " + e.getMessage());
            return 999;
        }
        if (success > 0 || errors > 0) {
            System.out.println("  Internships loaded: " + success +
                    (errors > 0 ? " (errors: " + errors + ")" : ""));
        }
        return errors;
    }

    /** Load applications from CSV */
    private int loadApplications(IDataRepo repo) {
        File file = new File(APPLICATION_FILE);
        if (!file.exists()) {
            System.out.println("  No application file found. Starting with empty applications.");
            createEmptyApplicationFile();
            return 0;
        }
        int success = 0, errors = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(APPLICATION_FILE))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 4) continue;
                try {
                    String applicationID = parts[0].trim();
                    String studentID = parts[1].trim();
                    String internshipID = parts[2].trim();
                    Types.ApplicationStatus status = Types.ApplicationStatus.valueOf(parts[3].trim().toUpperCase());
                    boolean accepted = parts.length > 4 && Boolean.parseBoolean(parts[4].trim());
                    User userObj = repo.findUser(studentID);
                    Internship internship = repo.findInternship(internshipID);
                    if (userObj instanceof Student student && internship != null) {
                        Application app = new Application(applicationID, internship, student);
                        app.updateStatus(status);
                        app.setAccepted(accepted);
                        student.getApplications().add(app);
                        internship.getApplications().add(app);
                        if (accepted) {
                            student.setAcceptedInternship(app);
                            internship.getInterns().add(student);
                        }
                        success++;
                    }
                } catch (Exception e) {
                    errors++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading application file: " + e.getMessage());
            return 999;
        }
        if (success > 0) {
            System.out.println("  Applications loaded: " + success);
        }
        return errors;
    }

    /** Save all data from the repository back to CSV files.
     * @param repo the data repository abstraction to save data from
     */
    @Override
    public void saveAllData(IDataRepo repo) {
        if (!loadedSuccessfully) {
            System.err.println("data not loaded properly, files will not be overwritten");
            return;
        }
        System.out.println("\nSaving data to CSV files");
        saveStudents(repo);
        saveStaff(repo);
        saveCompanyReps(repo);
        saveInternships(repo);
        saveApplications(repo);
        saveWithdrawalRequest(repo);
        System.out.println("All data saved successfully!");
    }

    /** Save students */
    private void saveStudents(IDataRepo repo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(STUDENT_FILE))) {
            writer.println("StudentID,Password,Name,Major,Year,Email");
            for (User user : repo.getAllUsers()) {
                if (user instanceof Student s) {
                    String password = userPasswords.getOrDefault(s.getUserID(), "password");
                    String email = studentEmails.get(s.getUserID());
                    writer.printf("%s,%s,%s,%s,%d,%s%n",
                            s.getUserID(),
                            password,
                            s.getName(),
                            s.getMajor(),
                            s.getYearOfStudy(),
                            email);
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving students: " + e.getMessage());
        }
    }

    /** Save staff */
    private void saveStaff(IDataRepo repo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(STAFF_FILE))) {
            writer.println("StaffID,Password,Name,Role,Department,Email");
            for (User user : repo.getAllUsers()) {
                if (user instanceof CareerStaff c) {
                    String password = userPasswords.getOrDefault(c.getUserID(), "password");
                    writer.printf("%s,%s,%s,%s,%s,%s%n",
                            c.getUserID(),
                            password,
                            c.getName(),
                            c.getUserRole(),
                            c.getDepartment(),
                            c.getUserID() + "@ntu.edu.sg");
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving staff: " + e.getMessage());
        }
    }

    /** Save company reps */
    private void saveCompanyReps(IDataRepo repo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(COMPANYREP_FILE))) {
            writer.println("CompanyRepID,Password,Name,CompanyName,Department,Position,Email,Status");
            for (User user : repo.getAllUsers()) {
                if (user instanceof CompanyRep r) {
                    String password = userPasswords.getOrDefault(r.getUserID(), "password");
                    writer.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                            r.getUserID(),
                            password,
                            r.getName(),
                            r.getCompanyName(),
                            r.getDepartment(),
                            r.getPosition(),
                            r.getUserID(),
                            r.isApproved() ? "APPROVED" : "PENDING");
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving company reps: " + e.getMessage());
        }
    }

    /** Save internships */
    private void saveInternships(IDataRepo repo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(INTERNSHIP_FILE))) {
            writer.println("InternshipID,Title,Description,Level,PreferredMajor,OpenDate,CloseDate,CompanyName,Status,Slots,Confirmed,Visible");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (Internship i : repo.getAllInternships()) {
                String majors = String.join(";",
                        i.getPreferredMajor().stream().map(Enum::toString).toArray(String[]::new));
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%d,%d,%s%n",
                        i.getInternshipID(),
                        i.getTitle(),
                        i.getDescription(),
                        i.getLevel(),
                        majors,
                        i.getOpenDate() != null ? dateFormat.format(i.getOpenDate()) : "",
                        i.getCloseDate() != null ? dateFormat.format(i.getCloseDate()) : "",
                        i.getCompanyName(),
                        i.getStatus(),
                        i.getNumberOfSlots(),
                        i.getConfirmedSlots(),
                        i.isVisible());
            }
        } catch (IOException e) {
            System.err.println("Error saving internships: " + e.getMessage());
        }
    }

    /** Save applications */
    private void saveApplications(IDataRepo repo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(APPLICATION_FILE))) {
            writer.println("ApplicationID,StudentID,InternshipID,ApplicationStatus,IsAccepted");
            for (User user : repo.getAllUsers()) {
                if (user instanceof Student student) {
                    for (Application app : student.getApplications()) {
                        writer.printf("%s,%s,%s,%s,%s%n",
                                app.getApplicationID(),
                                app.getStudent().getUserID(),
                                app.getInternship().getInternshipID(),
                                app.getStatus(),
                                app.isAccepted());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving applications: " + e.getMessage());
        }
    }

    /** Withdrawal loading */
    private int loadWithdrawalRequests(IDataRepo repo) {
        File file = new File(WITHDRAWAL_FILE);
        if (!file.exists()) {
            System.out.println("  No withdrawal request file found. Starting empty.");
            createEmptyWithdrawalFile();
            return 0;
        }
        int success = 0, errors = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(WITHDRAWAL_FILE))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length < 6) {
                    System.err.println("Error : insufficient fields");
                    errors++;
                    continue;
                }
                try {
                    String requestID = parts[0].trim();
                    String applicationID = parts[1].trim();
                    String studentID = parts[2].trim();
                    String internshipID = parts[3].trim(); // reference only
                    String requestDateStr = parts[4].trim();
                    String statusStr = parts[5].trim();

                    LocalDateTime requestDate = LocalDateTime.parse(requestDateStr);
                    Types.WithdrawalStatus status = Types.WithdrawalStatus.valueOf(statusStr.toUpperCase());
                    Internship internship = repo.findInternship(internshipID);
                    if (internship == null) {
                        System.err.println("Internship " + internshipID + " not found");
                        errors++;
                        continue;
                    }
                    Application app = internship.getApplications().stream()
                            .filter(a -> a.getApplicationID().equals(applicationID))
                            .findFirst()
                            .orElse(null);
                    if (app == null) {
                        System.err.println("Application " + applicationID + " not found");
                        errors++;
                        continue;
                    }
                    User user = repo.findUser(studentID);
                    if (!(user instanceof Student)) {
                        System.err.println("Student " + studentID + " not found");
                        errors++;
                        continue;
                    }
                    Student student = (Student) user;
                    WithdrawalRequest wr = new WithdrawalRequest(student, app);
                    wr.setRequestID(requestID);
                    wr.setRequestDate(requestDate);
                    wr.setStatus(status);
                    repo.addWithdrawal(wr);
                    success++;
                } catch (DateTimeParseException e) {
                    System.err.println("Invalid date format: " + e.getMessage());
                    errors++;
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid status: " + e.getMessage());
                    errors++;
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                    errors++;
                }
            }
        } catch (IOException e) {
            System.err.println("CRITICAL: Error reading withdrawal file: " + e.getMessage());
            return 999;
        }
        if (success > 0 || errors > 0) {
            System.out.println("  Withdrawal requests loaded: " + success +
                    (errors > 0 ? " (errors: " + errors + ")" : ""));
        }
        return errors;
    }

    /** Save withdrawal requests */
    private void saveWithdrawalRequest(IDataRepo repo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(WITHDRAWAL_FILE))) {
            writer.println("RequestID,ApplicationID,StudentID,InternshipID,RequestDate,Status");
            for (WithdrawalRequest wr : repo.getAllWithdrawals()) {
                writer.printf("%s,%s,%s,%s,%s,%s%n",
                        wr.getRequestID(),
                        wr.getApplication().getApplicationID(),
                        wr.getStudent().getUserID(),
                        wr.getApplication().getInternship().getInternshipID(),
                        wr.getRequestDate().toString(),
                        wr.getStatus().toString());
            }
        } catch (IOException e) {
            System.err.println("Error saving requests: " + e.getMessage());
        }
    }

    /** Update user password in memory */
    @Override
    public void updateUserPassword(String userID, String newPassword) {
        userPasswords.put(userID, newPassword);
    }

    /** Get password for user */
    @Override
    public String getPasswordForUser(String userID) {
        return userPasswords.get(userID);
    }

    /** Get student email */
    @Override
    public String getStudentEmail(String studentID) {
        return studentEmails.get(studentID);
    }

    /** Map string to Major enum, with special cases */
    private Types.Major mapMajor(String majorStr) {
        Map<String, Types.Major> majorMap = new HashMap<>();
        for (Types.Major major : Types.Major.values()) {
            majorMap.put(major.name(), major);
        }
        majorMap.put("WKW", Types.Major.WKWSCI);
        String majorUpper = majorStr.toUpperCase().trim();
        Types.Major result = majorMap.get(majorUpper);
        if (result == null) {
            System.err.println("Unknown major: " + majorStr + ", defaulting to CCDS");
            return Types.Major.CCDS;
        }
        return result;
    }

    /** Helpers to create empty data files if missing */
    private void createEmptyCompanyRepFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(COMPANYREP_FILE))) {
            writer.println("CompanyRepID,Password,Name,CompanyName,Department,Position,Email,Status");
        } catch (IOException ignored) {}
    }
    private void createEmptyInternshipFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(INTERNSHIP_FILE))) {
            writer.println("InternshipID,Title,Description,Level,PreferredMajor,OpenDate,CloseDate,CompanyName,Status,Slots,Confirmed,Visible");
        } catch (IOException ignored) {}
    }
    private void createEmptyApplicationFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(APPLICATION_FILE))) {
            writer.println("ApplicationID,StudentID,InternshipID,ApplicationStatus,IsAccepted");
        } catch (IOException ignored) {}
    }
    private void createEmptyWithdrawalFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(WITHDRAWAL_FILE))) {
            writer.println("RequestID,ApplicationID,StudentID,InternshipID,RequestDate,Status");
        } catch (IOException e) {
            System.err.println("Error creating withdrawal file: " + e.getMessage());
        }
    }
}

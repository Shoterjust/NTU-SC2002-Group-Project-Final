package entity_class;

import java.util.ArrayList;
import java.util.List;

public class Student extends User implements IStudent {
    private int yearOfStudy;
    private final Types.Major major;
    private List<Application> applications;
    private Application acceptedInternship;
    private static final int MAX_APPLICATIONS = 3;

    /**
     * Constructor with default password
     * @param userID unique student ID
     * @param name student name
     * @param yearOfStudy student year of study
     * @param major student major
     */
    public Student(String userID, String name, int yearOfStudy, Types.Major major){
        super(userID, name, "password");
        this.yearOfStudy = yearOfStudy;
        this.major = major;
        this.applications = new ArrayList<>();
        this.acceptedInternship = null;
        //validate ID
        if (!validateID()) {
            throw new IllegalArgumentException("Invalid student ID format");
        }
    }

    /**
     * Constructor with specified password
     * @param studentID unique student ID
     * @param name student name
     * @param yearOfStudy student year of study
     * @param major student major
     * @param password student password
     */
    public Student(String studentID, String name, int yearOfStudy, Types.Major major, String password) {
        super(studentID, name, password);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
        this.applications = new ArrayList<>();
        this.acceptedInternship = null;
        //validate ID
        if (!validateID()) {
            throw new IllegalArgumentException("Invalid student ID format");
        }
    }

    /**
     * Validates student ID format (e.g., U1234567A)
     * @return true if valid, false otherwise
     */
    @Override
    public boolean validateID() {
        return userID != null && userID.matches("^U\\d{7}[A-Z]$");
    }

    /**
     * Gets the user role
     * @return user role as STUDENT
     */
    @Override
    public Types.UserRole getUserRole() {
        return Types.UserRole.STUDENT;
    }

    /** 
     * Gets the student information
     * @return formatted student information
     */
    @Override
    public String getInfo() {
        return "User ID: " + getUserID() +
                "\nRole: " + getUserRole() +
                "\nName: " + getName() +
                "\nYear of Study: " + yearOfStudy +
                "\nMajor: " + major;
    }

    /** 
     * Check eligibility for internship level
     * @param level the internship level to check
     * @return true if eligible, false otherwise
     */
    public boolean isEligibleForLevel(Types.InternshipLevel level) {
        if (yearOfStudy <= 2) {
            return level == Types.InternshipLevel.BASIC;
        }
        return true; // Year 3+ can apply for all levels
    }

    /** 
     * Count active applications
     * @return number of active applications
     */
    public int getActiveApplicationCount() {
        int count = 0;
        for (Application app : applications) {
            Types.ApplicationStatus status = app.getStatus();
            if (status == Types.ApplicationStatus.PENDING ||
                    status == Types.ApplicationStatus.SUCCESSFUL) {
                count++;
            }
        }
        return count;
    }

    /**
     * Check if student can apply for more internships
     * @return true if eligible to apply, false otherwise
     */
    public boolean canApplyMore() {
        return getActiveApplicationCount() < MAX_APPLICATIONS && acceptedInternship == null;
    }

    /**
     * Find application by ID
     * @param applicationID the ID of the application to find
     * @return the Application object if found, null otherwise
     */
    public Application findApplicationByID(String applicationID) {
        return applications.stream()
                .filter(app -> app.getApplicationID().equals(applicationID))
                .findFirst()
                .orElse(null);
    }

    /**
     * Getters and setters
     */
    public Types.Major getMajor() { return major; }
    public int getYearOfStudy() { return yearOfStudy; }
    public List<Application> getApplications() { return applications; }
    public Application getAcceptedInternship() { return acceptedInternship; }
    public void setAcceptedInternship(Application app) { this.acceptedInternship = app; }
    public int getMaxApplications() { return MAX_APPLICATIONS; }

}

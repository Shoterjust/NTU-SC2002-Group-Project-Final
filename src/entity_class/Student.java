package entity_class;

import java.util.ArrayList;
import java.util.List;

public class Student extends User implements IStudent {
    private int yearOfStudy;
    private final Types.Major major;
    private List<Application> applications;
    private Application acceptedInternship;
    private static final int MAX_APPLICATIONS = 3;

    //Constructor
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
    @Override
    public boolean validateID() {
        return userID != null && userID.matches("^U\\d{7}[A-Z]$");
    }

    @Override
    public Types.UserRole getUserRole() {
        return Types.UserRole.STUDENT;
    }

    public String getInfo() {
        return "User ID: " + getUserID() +
                "\nRole: " + getUserRole() +
                "\nName: " + getName() +
                "\nYear of Study: " + yearOfStudy +
                "\nMajor: " + major;
    }

    // Domain validation - check eligibility for internship level
    public boolean isEligibleForLevel(Types.InternshipLevel level) {
        if (yearOfStudy <= 2) {
            return level == Types.InternshipLevel.BASIC;
        }
        return true; // Year 3+ can apply for all levels
    }

    // Domain query - count active applications
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

    // Domain validation - can apply more?
    public boolean canApplyMore() {
        return getActiveApplicationCount() < MAX_APPLICATIONS && acceptedInternship == null;
    }

    //searching own collection
    public Application findApplicationByID(String applicationID) {
        return applications.stream()
                .filter(app -> app.getApplicationID().equals(applicationID))
                .findFirst()
                .orElse(null);
    }

    // Getters and setters
    public Types.Major getMajor() { return major; }
    public int getYearOfStudy() { return yearOfStudy; }
    public List<Application> getApplications() { return applications; }
    public Application getAcceptedInternship() { return acceptedInternship; }
    public void setAcceptedInternship(Application app) { this.acceptedInternship = app; }
    public int getMaxApplications() {return MAX_APPLICATIONS;}

}

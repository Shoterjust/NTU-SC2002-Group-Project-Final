package entity_class;
import java.time.LocalDateTime;

public class Application {
    private String applicationID;
    private Internship internship;
    private Student student;
    private LocalDateTime applicationDate;
    private Types.ApplicationStatus status;
    private boolean isAccepted;

    // Constructor
    public Application(String applicationID, Internship internship, Student student) {
        this.applicationID = applicationID;
        this.internship = internship;
        this.student = student;
        this.applicationDate = LocalDateTime.now();
        this.status = Types.ApplicationStatus.PENDING;
        this.isAccepted = false;
    }

    /** Domain behavior - update application status */
    public void updateStatus(Types.ApplicationStatus newStatus) {
        this.status = newStatus;
        // Mark isAccepted based on status
        //this.isAccepted = (newStatus == Types.ApplicationStatus.SUCCESSFUL);
        //only update isAccepted if student accepts, not when company rep processes
    }

    /** Domain behavior: Withdraws application and update */
    public void withdraw() {
        this.isAccepted = false;
        status = Types.ApplicationStatus.UNSUCCESSFUL;
    }

    /** Find the Internship by ID
    public Internship findInternshipByID(String internshipID) {
        return CareerStaff.findInternshipByID(internshipID);
    }*/

    // Getters and setters
    public String getApplicationID() { return applicationID; }
    public Internship getInternship() { return internship; }
    public Student getStudent() { return student; }
    public LocalDateTime getApplicationDate() { return applicationDate; }
    public Types.ApplicationStatus getStatus() { return status; }
    public boolean isAccepted() { return isAccepted; }
    public void setAccepted(boolean accepted) { this.isAccepted = accepted; }

}


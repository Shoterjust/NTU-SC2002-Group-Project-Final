package entity_class;
import java.time.LocalDateTime;

public class Application {
    private String applicationID;
    private Internship internship;
    private Student student;
    private LocalDateTime applicationDate;
    private Types.ApplicationStatus status;
    private boolean isAccepted;

    /**
     * Constructor
     *
     * @param applicationID unique ID for the application
     * @param internship the Internship being applied for
     * @param student the Student submitting the application
     */
    public Application(String applicationID, Internship internship, Student student) {
        this.applicationID = applicationID;
        this.internship = internship;
        this.student = student;
        this.applicationDate = LocalDateTime.now();
        this.status = Types.ApplicationStatus.PENDING;
        this.isAccepted = false;
    }

    /** 
     * Update application status 
     * @param newStatus the new application status
    */
    public void updateStatus(Types.ApplicationStatus newStatus) {
        this.status = newStatus;
    }

    /** 
     * Withdraws application and update 
     * application status to UNSUCCESSFUL
     */
    public void withdraw() {
        this.isAccepted = false;
        status = Types.ApplicationStatus.UNSUCCESSFUL;
    }

    /** 
     * Getters and setters
     */
    public String getApplicationID() { return applicationID; }
    public Internship getInternship() { return internship; }
    public Student getStudent() { return student; }
    public LocalDateTime getApplicationDate() { return applicationDate; }
    public Types.ApplicationStatus getStatus() { return status; }
    public boolean isAccepted() { return isAccepted; }
    public void setAccepted(boolean accepted) { this.isAccepted = accepted; }

}


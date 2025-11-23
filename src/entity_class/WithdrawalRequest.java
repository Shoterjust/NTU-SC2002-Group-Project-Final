package entity_class;
import java.time.LocalDateTime;

public class WithdrawalRequest {
    private String requestID;
    private Application application;
    private Student student;
    private LocalDateTime requestDate;
    private Types.WithdrawalStatus status;

    /** 
     * Constructor for WithdrawalRequest
     *
     * @param student the student making the withdrawal request
     * @param application the application to be withdrawn
     */
    public WithdrawalRequest(Student student, Application application) {
        this.requestID = "WR-" + application.getApplicationID();
        this.application = application;
        this.student = student;
        this.requestDate = LocalDateTime.now();
        this.status = Types.WithdrawalStatus.PENDING;
    }

    /**
     * Getters and setters
     */
    public String getRequestID() { return requestID; }
    public Application getApplication() { return application; }
    public Student getStudent() { return student; }
    public LocalDateTime getRequestDate() { return requestDate; }
    public Types.WithdrawalStatus getStatus() { return status; }
    
    public void setStatus(Types.WithdrawalStatus status) { this.status = status; }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }
}

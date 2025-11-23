package entity_class;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Internship {
    private String internshipID;
    private String title;
    private String description;
    private Types.InternshipLevel level;
    private List<Types.Major> preferredMajor;
    private Date openDate;
    private Date closeDate;
    private Types.InternshipStatus status;
    private CompanyRep companyRepresentative;
    private String companyName;
    private int numberOfSlots; // max 10
    private int confirmedSlots;
    private List<Student> interns; // students who accepted offer
    private List<Application> applications;
    private boolean isVisible;

    /**  
     * Constructor using only an ID
     * Fills other fields with default values
    */
    public Internship(String internshipID) {
        this.internshipID = internshipID;
        this.title = "Default Title";
        this.description = "Default Description";
        this.level = Types.InternshipLevel.BASIC;
        this.preferredMajor = new ArrayList<>();
        this.status = Types.InternshipStatus.PENDING;
        this.numberOfSlots = 5;
        this.confirmedSlots = 0;
        this.interns = new ArrayList<>();
        this.applications = new ArrayList<>();
        this.isVisible = true;
    }

    /** 
     * Constructor 
     * Validations are performed on required fields 
     */
    public Internship(String internshipID, String title, String description,
                      Types.InternshipLevel level, List<Types.Major> preferredMajor,
                      Date openDate, Date closeDate, String companyName,
                      CompanyRep companyRep, int numberOfSlots) {
        // Validation
        if (internshipID == null || internshipID.trim().isEmpty()) {
            throw new IllegalArgumentException("Internship ID cannot be empty");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (openDate == null || closeDate == null) {
            throw new IllegalArgumentException("Open date and close date are required");
        }
        if (!closeDate.after(openDate)) {
            throw new IllegalArgumentException("Close date must be after open date");
        }
        if (numberOfSlots < 1 || numberOfSlots > 10) {
            throw new IllegalArgumentException("Number of slots must be between 1 and 10");
        }
        if (companyName == null || companyName.trim().isEmpty()) {
            throw new IllegalArgumentException("Company name cannot be empty");
        }
        if (companyRep == null) {
            throw new IllegalArgumentException("Company representative cannot be null");
        }

        // Initialize
        this.internshipID = internshipID;
        this.title = title;
        this.description = description;
        this.level = level != null ? level : Types.InternshipLevel.BASIC;
        this.preferredMajor = preferredMajor != null ? new ArrayList<>(preferredMajor) : new ArrayList<>();
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.companyName = companyName;
        this.companyRepresentative = companyRep;
        this.numberOfSlots = numberOfSlots;
        this.status = Types.InternshipStatus.PENDING;
        this.confirmedSlots = 0;
        this.interns = new ArrayList<>();
        this.applications = new ArrayList<>();
        this.isVisible = true;
    }

    /** 
     * Checks if the internship is eligible for the given student (based on year and major) 
     * @param student the student to check eligibility for
     * @return true if eligible, false otherwise
     */
    public boolean isEligibleForStudent(Student student) {
        if (student == null) return false;
        // Major match
        if (!preferredMajor.contains(student.getMajor())) {
            return false;
        }
        // Year eligibility: Y1 & Y2 only for BASIC, Y3+ for all
        if (!student.isEligibleForLevel(level)) {
            return false;
        }
        // Currently Open? include open&close Date, available slots, approval status.
        return isOpen();
    }

    /** 
     * Check if internship is currently open according to date and status 
     * @return true if open, false otherwise
     */
    public boolean isOpen() {
        Date now = new Date();
        return status == Types.InternshipStatus.APPROVED &&
                isVisible &&
                openDate.before(now) &&
                closeDate.after(now) &&
                confirmedSlots < numberOfSlots;
    }

    /** 
     * Add confirmed intern 
     * @param student the student to add as confirmed intern
     */
    public void addSlot(Student student) {
        if (confirmedSlots >= numberOfSlots) {
            throw new IllegalStateException("No available slots");
        }
        confirmedSlots++;
        interns.add(student);
        // Automatically update status if filled
        if (confirmedSlots == numberOfSlots) {
            status = Types.InternshipStatus.FILLED;
        }
    }

    /** 
     * Remove confirmed intern 
     * @param student the student to remove from confirmed interns
     */
    public void removeSlot(Student student) {
        if (!interns.remove(student)) {
            throw new IllegalArgumentException("Student not in confirmed interns list");
        }

        confirmedSlots--;

        // Revert status if no longer filled
        if (status == Types.InternshipStatus.FILLED) {
            status = Types.InternshipStatus.APPROVED;
        }
    }

    /**
     * Getters and setters
     */
    public String getInternshipID() { return internshipID; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Types.InternshipLevel getLevel() { return level; }
    public void setLevel(Types.InternshipLevel level) { this.level = level; }

    public List<Types.Major> getPreferredMajor() { return preferredMajor; }

    public Date getOpenDate() { return openDate; }
    public void setOpenDate(Date openDate) { this.openDate = openDate; }

    public Date getCloseDate() { return closeDate; }
    public void setCloseDate(Date closeDate) { this.closeDate = closeDate; }

    public Types.InternshipStatus getStatus() { return status; }
    public void setStatus(Types.InternshipStatus status) { this.status = status; }

    public CompanyRep getCompanyRepresentative() { return companyRepresentative; }
    public void setCompanyRepresentative(CompanyRep rep) { this.companyRepresentative = rep; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String name) { this.companyName = name; }

    public int getNumberOfSlots() { return numberOfSlots; }
    public void setNumberOfSlots(int slots) {
        if (slots < 1 || slots > 10) {
            throw new IllegalArgumentException("Slots must be between 1 and 10");
        }
        if (slots < confirmedSlots) {
            throw new IllegalStateException("Cannot set slots below confirmed count");
        }
        this.numberOfSlots = slots;
    }

    public int getConfirmedSlots() { return confirmedSlots; }

    public List<Student> getInterns() { return interns; }

    public List<Application> getApplications() { return applications; }

    public boolean isVisible() { return isVisible; }
    public void setVisible(boolean visible) { this.isVisible = visible; }

}



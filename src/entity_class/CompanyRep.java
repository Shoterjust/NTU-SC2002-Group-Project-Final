package entity_class;
import java.util.ArrayList;
import java.util.List;

public class CompanyRep extends User implements ICompanyRep {
    private String companyName;
    private boolean isApproved = false;
    private String department;
    private String position;
    private List<Internship> createdInternships;
    private static final int MAX_APPROVED_INTERNSHIPS = 5;

    /**
     * Constructor with default password
     * @param userID unique ID for the company representative
     * @param name full name of the company representative
     * @param companyName name of the company
     * @param department department within the company
     * @param position position held by the representative
     */
    public CompanyRep(String userID, String name, String companyName, String department, String position) {
        super(userID, name);
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.createdInternships = new ArrayList<>();
        if (!validateID()) {
            throw new IllegalArgumentException("Invalid ID format");
        }
    }

    /**
     * Constructor with specified password
     * @param userID unique ID for the company representative
     * @param name full name of the company representative
     * @param companyName name of the company
     * @param department department within the company
     * @param position position held by the representative
     * @param password password for the representative's account
     */
    public CompanyRep(String userID, String name, String companyName,
                      String department, String position, String password) {
        super(userID, name, password);
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.createdInternships = new ArrayList<>();
        if (!validateID()) {
            throw new IllegalArgumentException("Invalid ID format");
        }
    }

    /**
     * Validates the company representative ID format (email format)
     * @return true if valid, false otherwise
     */
    @Override
    public boolean validateID() {
        return userID != null && userID.trim().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * Gets the company representative's information
     * @return formatted string of the representative's information
     */
    @Override
    public String getInfo() {
        return "User ID: " + getUserID() +
                "\nRole: " + getUserRole() +
                "\nName: " + getName() +
                "\nCompany: " + getCompanyName() +
                "\nDepartment: " + getDepartment() +
                "\nPosition: " + getPosition();
    }

    /** 
     * Gets the user role
     * @return user role as COMPANY_REPRESENTATIVE
     */
    @Override
    public Types.UserRole getUserRole() {
        return Types.UserRole.COMPANY_REPRESENTATIVE;
    }

    /**
     * Checks if the company representative can create more internships
     * @return true if they can create more, false otherwise
     */
    public boolean canCreateMoreInternships() {
        long approvedCount = createdInternships.stream()
                .filter(i -> i.getStatus() != Types.InternshipStatus.REJECTED)
                .count();
        return approvedCount < MAX_APPROVED_INTERNSHIPS;
    }

    /**
     * Finds an internship by its ID among the created internships
     * @param internshipID the ID of the internship to find
     */
    public Internship findInternshipByID(String internshipID) {
        return createdInternships.stream()
                .filter(i -> i.getInternshipID().equals(internshipID))
                .findFirst()
                .orElse(null);
    }

    /**
     * Getters and setters
     */
    public String getCompanyName() { return companyName; }
    public String getDepartment() { return department; }
    public String getPosition() { return position; }
    public boolean isApproved() { return isApproved; }
    public void setApproved(boolean approval) { this.isApproved = approval; }
    public List<Internship> getCreatedInternships() { return createdInternships; }
    public static int getMaxApprovedInternships() { return MAX_APPROVED_INTERNSHIPS; }
}




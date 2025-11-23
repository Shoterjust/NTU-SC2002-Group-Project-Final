package entity_class;
import java.util.*;
import java.time.LocalDateTime;

public class CareerStaff extends User implements ICareerStaff {
    private String department;

    /**
     * Constructor with default password
     *
     * @param userID unique ID for the staff member
     * @param name full name of the staff member
     * @param department department name of the staff member
     * @throws IllegalArgumentException if the provided userID is invalid
     */
    public CareerStaff(String userID, String name, String department) {
        super(userID, name);
        this.department = department;
        if (!validateID()) {
            throw new IllegalArgumentException("Invalid Staff ID");
        }
    }

    /**
     * Constructor with specified password
     *
     * @param staffID unique ID for the staff member
     * @param name full name of the staff member        
     * @param department department name of the staff member
     * @param password password for the staff member's account
     * @throws IllegalArgumentException if the provided userID is invalid
     */
    public CareerStaff(String staffID, String name, String department, String password) {
        super(staffID, name, password);
        this.department = department;
        if (!validateID()) {
            throw new IllegalArgumentException("Invalid Staff ID");
        }
    }

    /** 
     * Validates the staff ID format (alphanumeric, 3-10 characters)
     * @return true if valid, false otherwise
     */
    @Override
    public boolean validateID() {
        return userID != null && userID.matches("^[a-zA-Z0-9]{3,10}$");
    }

    /**
     * Gets the user role
     * @return user role as CAREER_CENTER_STAFF
     */
    @Override
    public Types.UserRole getUserRole() {
        return Types.UserRole.CAREER_CENTER_STAFF;
    }

    /** 
     * Gets the staff member's information
     * @return formatted string of the staff member's information
     */
    @Override
    public String getInfo() {
        return "User ID: " + getUserID() +
                "\nRole: " + getUserRole() +
                "\nName: " + getName() +
                "\nDepartment: " + getDepartment();
    }

    /** 
     * Getters and setters 
     */
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

}

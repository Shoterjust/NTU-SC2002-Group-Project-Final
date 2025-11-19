package entity_class;
import java.util.*;
import java.time.LocalDateTime;

public class CareerStaff extends User implements ICareerStaff {
    private String department;

    public CareerStaff(String userID, String name, String department) {
        super(userID, name);
        this.department = department;
        if (!validateID()) {
            throw new IllegalArgumentException("Invalid Staff ID");
        }
    }

    public CareerStaff(String staffID, String name, String department, String password) {
        super(staffID, name, password);
        this.department = department;
        if (!validateID()) {
            throw new IllegalArgumentException("Invalid Staff ID");
        }
    }

    @Override
    public boolean validateID() {
        return userID != null && userID.matches("^[a-zA-Z0-9]{3,10}$");
    }

    @Override
    public Types.UserRole getUserRole() {
        return Types.UserRole.CAREER_CENTER_STAFF;
    }

    @Override
    public String getInfo() {
        return "User ID: " + getUserID() +
                "\nRole: " + getUserRole() +
                "\nName: " + getName() +
                "\nDepartment: " + getDepartment();
    }

    /** Getters and Setters */
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

}

package entity_class;

import java.time.LocalDateTime;

/* No password getter and no setters for security/accessibility */

public abstract class User implements IUser {
    protected String userID;
    protected String name;
    protected String password;
    protected LocalDateTime registrationDate;

    /** 
     * Constructors 
     */
    public User(String userID, String name) {
        this(userID, name, "password");
    }

    public User(String userID, String name, String password) {
        if (userID == null || userID.isEmpty()) {
            throw new IllegalArgumentException("userID cannot be empty");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be empty");
        }
        this.userID = userID;
        this.name = name;
        this.password = (password == null || password.isEmpty()) ? "password" : password;
        this.registrationDate = LocalDateTime.now();
    }

    /** Return true if credentials match */
    public boolean loginCheck(String userID, String pw) {
        return this.userID.equals(userID) && password.equals(pw);
    }

    /** Return true if oldPassword matches current this.password */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (!this.password.equals(oldPassword)) return false;
        this.password = newPassword;
        return true;
    }

    /** Abstract methods */
    public abstract boolean validateID();
    public abstract Types.UserRole getUserRole();
    public abstract String getInfo();

    /** Getters */
    public String getUserID() { return userID; }
    public String getName() { return name; }
    public LocalDateTime getRegistrationDate() { return registrationDate; }
}

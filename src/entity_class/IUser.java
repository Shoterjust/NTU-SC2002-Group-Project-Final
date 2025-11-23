package entity_class;

import java.time.LocalDateTime;

/**
 * This interface supports the Interface Segregation Principle by
 * separating user responsibilities from role-specific interfaces. All
 * concrete user types (students, company representatives, career
 * centre staff) implement this interface.
 */
public interface IUser {
    /**
     * Validates that the user ID adheres to the format required for
     * the specific user role.
     *
     * @return true if the ID is valid; false otherwise
     */
    boolean validateID();

    /**
     * Returns the role of the user 
     *
     * @return the user role
     */
    Types.UserRole getUserRole();

    /**
     * Returns a formatted string containing detailed information about the user.
     *
     * @return a multi-line description of the user
     */
    String getInfo();

    /**
     * Gets the unique identifier for the user.
     *
     * @return the user ID
     */
    String getUserID();

    /**
     * Gets the name of the user.
     *
     * @return the user's full name
     */
    String getName();

    /**
     * Returns the registration timestamp for the user.
     *
     * @return the registration date and time
     */
    LocalDateTime getRegistrationDate();

    /**
     * Checks whether the provided credentials match those of the user.
     *
     * @param userID the user ID to check
     * @param password the password to verify
     * @return true if credentials match; false otherwise
     */
    boolean loginCheck(String userID, String password);

    /**
     * Changes the user’s password.
     *
     * @param oldPassword the current password
     * @param newPassword the new password to set
     * @return true if the password was changed successfully; false otherwise
     */
    boolean changePassword(String oldPassword, String newPassword);
}

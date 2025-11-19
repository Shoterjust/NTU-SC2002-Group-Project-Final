package controller_class;

import boundary.IFileHandler;
import entity_class.*;

/**
 * Implementation of the {@link ILoginController} interface. This controller
 * manages authentication-related use cases and depends only on the
 * {@link IDataRepo} and {@link IFileHandler} abstractions, adhering to
 * the Dependency Inversion Principle.
 */
public class LoginController implements ILoginController {
    private final IDataRepo repo;
    private IFileHandler fileHandler;

    /**
     * Constructs a new LoginController with the given data repository.
     *
     * @param repo the repository used to find users and persist changes
     */
    public LoginController(IDataRepo repo) {
        this.repo = repo;
    }

    /**
     * Sets the file handler used to persist password changes. The handler
     * may be provided after construction to facilitate testing.
     *
     * @param fileHandler the file handler to use, may be {@code null}
     */
    @Override
    public void setFileHandler(IFileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    /**
     * Authenticates a user.
     *
     * @param userID   the user ID
     * @param password the provided password
     * @return the authenticated user, or {@code null} if credentials are invalid
     */
    @Override
    public User login(String userID, String password) {
        User user = repo.findUser(userID);
        if (user == null) {
            return null; // User not found
        }

        if (!user.loginCheck(userID, password)) {
            return null; // Wrong password
        }
        // Additional check for company reps - must be approved
        if (user instanceof CompanyRep) {
            if (!((CompanyRep) user).isApproved()) {
                return null; // Not approved yet
            }
        }
        return user;
    }

    /**
     * Changes a user’s password.
     *
     * @param user        the user whose password should be changed
     * @param oldPassword the current password
     * @param newPassword the new password
     * @return {@code true} if the password was changed; {@code false} otherwise
     */
    @Override
    public boolean changePassword(User user, String oldPassword, String newPassword) {
        if (user.changePassword(oldPassword, newPassword)) {
            // Update password in FileHandler
            if (fileHandler != null) {
                fileHandler.updateUserPassword(user.getUserID(), newPassword);
            }
            return true;
        }
        return false;
    }
}

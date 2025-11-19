package controller_class;

import boundary.IFileHandler;
import entity_class.User;

/** Authentication controller abstraction. */
public interface ILoginController {
    void setFileHandler(IFileHandler fileHandler);
    User login(String userID, String password);
    boolean changePassword(User user, String oldPassword, String newPassword);
}

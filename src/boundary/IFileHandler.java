package boundary;

import controller_class.IDataRepo;

/** Abstraction for file loading/saving and credential management. */
public interface IFileHandler {
    boolean loadAllData(IDataRepo repo);
    void saveAllData(IDataRepo repo);
    void updateUserPassword(String userID, String newPassword);
    String getPasswordForUser(String userID);
    String getStudentEmail(String studentID);
}

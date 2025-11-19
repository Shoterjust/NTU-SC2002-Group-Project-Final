package controller_class;

import entity_class.*;
import java.util.List;

/** Abstraction for the data repository. */
public interface IDataRepo {
    void addUser(User user);
    void removeUser(String userID);
    User findUser(String userID);
    List<User> getAllUsers();

    void addInternship(Internship internship);
    void removeInternship(String internshipID);
    Internship findInternship(String internshipID);
    List<Internship> getAllInternships();

    void addWithdrawal(WithdrawalRequest request);
    WithdrawalRequest findWithdrawal(String requestID);
    List<WithdrawalRequest> getAllWithdrawals();
}

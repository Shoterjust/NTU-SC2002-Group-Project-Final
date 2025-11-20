package controller_class;

import entity_class.*;
import java.util.*;

/** Centralised repository; implements IDataRepo. */
public class DataRepo implements IDataRepo {
    private static DataRepo instance;
    private final Map<String, User> userMap;
    private final Map<String, Internship> internshipMap;
    private final Map<String, WithdrawalRequest> withdrawalMap;

    private DataRepo() {
        this.userMap = new HashMap<>();
        this.internshipMap = new HashMap<>();
        this.withdrawalMap = new HashMap<>();
    }

    public static DataRepo getInstance() {
        if (instance == null) {
            instance = new DataRepo();
        }
        return instance;
    }

    // User operations

    @Override
    public void addUser(User user) { userMap.put(user.getUserID(), user); }

    @Override
    public void removeUser(String userID) { userMap.remove(userID); }

    @Override
    public User findUser(String userID) { return userMap.get(userID); }

    @Override
    public List<User> getAllUsers() { return new ArrayList<>(userMap.values()); }

    // Internship operations

    @Override
    public void addInternship(Internship internship) { internshipMap.put(internship.getInternshipID(), internship); }

    @Override
    public void removeInternship(String internshipID) { internshipMap.remove(internshipID); }

    @Override
    public Internship findInternship(String internshipID) { return internshipMap.get(internshipID); }

    @Override
    public List<Internship> getAllInternships() { return new ArrayList<>(internshipMap.values()); }

    // Withdrawal request operations
    
    @Override
    public void addWithdrawal(WithdrawalRequest request) { withdrawalMap.put(request.getRequestID(), request); }

    @Override
    public WithdrawalRequest findWithdrawal(String requestID) { return withdrawalMap.get(requestID); }

    @Override
    public List<WithdrawalRequest> getAllWithdrawals() { return new ArrayList<>(withdrawalMap.values()); }
}

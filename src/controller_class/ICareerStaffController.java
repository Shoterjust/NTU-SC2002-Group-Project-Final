package controller_class;

import entity_class.*;
import java.util.Date;
import java.util.List;

public interface ICareerStaffController {
    List<User> viewAllUsers();
    List<Internship> viewAllInternships();
    List<CompanyRep> getPendingCompanyReps();
    void approveCompanyRep(String userID);
    void rejectCompanyRep(String userID);
    List<Internship> getPendingInternships();
    void approveInternship(String internshipID);
    void rejectInternship(String internshipID);
    List<WithdrawalRequest> getPendingWithdrawals();
    void processWithdrawal(String requestID, boolean approve);
    List<Internship> getFilteredInternships(List<Types.Major> majors,
                                            String company,
                                            Types.InternshipLevel level,
                                            Date open, Date close,
                                            Types.InternshipStatus status);
}

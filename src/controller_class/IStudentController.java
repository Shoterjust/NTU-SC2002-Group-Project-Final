package controller_class;

import entity_class.*;
import java.util.List;

/** Student controller abstraction. */
public interface IStudentController {
    List<Internship> getEligibleInternships(Student student);
    Application applyInternship(Student student, String internshipID);
    List<Application> viewApplications(Student student);
    void acceptInternship(Student student, String applicationID);
    void rejectInternship(Student student, String applicationID);
    WithdrawalRequest requestWithdrawal(Student student, String applicationID);
}

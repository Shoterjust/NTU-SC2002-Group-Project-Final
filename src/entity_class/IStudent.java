package entity_class;

import java.util.List;

/**
 * Student-specific interface extending {@link IUser}. This interface
 * exposes operations and attributes that only apply to student
 * accounts, supporting the Interface Segregation Principle by
 * preventing other roles from depending on student-specific methods.
 */
public interface IStudent extends IUser {
    Types.Major getMajor();
    int getYearOfStudy();
    List<Application> getApplications();
    Application getAcceptedInternship();
    void setAcceptedInternship(Application app);
    int getMaxApplications();
    int getActiveApplicationCount();
    boolean canApplyMore();
    boolean isEligibleForLevel(Types.InternshipLevel level);
    Application findApplicationByID(String applicationID);
}

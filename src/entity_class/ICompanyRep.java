package entity_class;

import java.util.List;

/**
 * Company representative-specific interface extending {@link IUser}. This
 * interface exposes operations and attributes unique to company
 * representatives, supporting the Interface Segregation Principle by
 * ensuring other user types do not depend on unnecessary methods.
 */
public interface ICompanyRep extends IUser {
    String getCompanyName();
    String getDepartment();
    String getPosition();
    boolean isApproved();
    void setApproved(boolean approval);
    List<Internship> getCreatedInternships();
    boolean canCreateMoreInternships();
    Internship findInternshipByID(String internshipID);
}

package entity_class;

/**
 * Career staff-specific interface extending {@link IUser}. This
 * interface exposes operations for career centre staff, adhering to
 * the Interface Segregation Principle by preventing other user types
 * from depending on staff-specific methods.
 */
public interface ICareerStaff extends IUser {
    String getDepartment();
    void setDepartment(String department);
}

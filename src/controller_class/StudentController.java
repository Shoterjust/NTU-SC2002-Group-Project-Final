package controller_class;

import entity_class.*;
import java.util.*;
import java.util.stream.Collectors;

/** Handles student actions; depends on IDataRepo; implements IStudentController. */
public class StudentController implements IStudentController {
    private final IDataRepo repo;

    public StudentController(IDataRepo repo) { this.repo = repo; }

    /** #6: View all currently visible, eligible internships for the logged-in student.
     Internship opportunities are visible to students based on their
     year of study, major, internship level eligibility, and the
     visibility setting */
    @Override
    public List<Internship> getEligibleInternships(Student student) {
        return repo.getAllInternships().stream()
                .filter(i -> i.isEligibleForStudent(student))
                .collect(Collectors.toList());
    }

    /** #7: Students can only apply for internship opportunities relevant
     to their profile (correct major preference, appropriate level for
     their year of study) and when visibility is on*/
    @Override
    public Application applyInternship(Student student, String internshipID) {
        // Validate internship exists
        Internship internship = repo.findInternship(internshipID);
        if (internship == null) throw new IllegalArgumentException("Internship not found");
        
        // Check if student can apply more
        if (!student.canApplyMore())
            throw new IllegalStateException("Max applications reached or already accepted an internship");
        
        // Check eligibility
        if (!internship.isEligibleForStudent(student))
            throw new IllegalStateException("Student not eligible for this internship");
        
        // Check for duplicate applications
        for (Application app : student.getApplications()) {
            if (app.getInternship().equals(internship))
                throw new IllegalArgumentException("Already applied for this internship");
        }

        // Create application if nothing is wrong
        String appID = student.getUserID() + "-" + internshipID;
        Application app = new Application(appID, internship, student);
        student.getApplications().add(app); // Add to both student and internship
        internship.getApplications().add(app);

        return app;
    }

    /** #8: Students continue to have access to their application details
     regardless of internship opportunities’ visibility*/
    @Override
    public List<Application> viewApplications(Student student) {
        return new ArrayList<>(student.getApplications());
    }

    /** #10: Accept Internship flow
     System allows accepting one internship placement and
     automatically withdraws all other applications once a placement is accepted*/
    @Override
    public void acceptInternship(Student student, String applicationID) {
        // Find the application
        Application app = student.getApplications().stream()
                .filter(a -> a.getApplicationID().equals(applicationID))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
            
        // Validate student hasn't already accepted
        if (student.getAcceptedInternship() != null)
            throw new IllegalStateException("Already accepted an internship");

        // Can only accept successful application
        if (app.getStatus() != Types.ApplicationStatus.SUCCESSFUL)
            throw new IllegalArgumentException("Can only accept successful applications");

        // Withdraw all other applications
        for (Application other : student.getApplications()) {
            if (!other.equals(app) && other.getStatus() != Types.ApplicationStatus.UNSUCCESSFUL) {
                other.withdraw();
            }
        }

        // Confirm placement
        student.setAcceptedInternship(app);
        app.setAccepted(true);
        app.getInternship().addSlot(student);
    }

    /** Reject an Internship Offer*/
    @Override
    public void rejectInternship(Student student, String applicationID) {
        Application app = student.getApplications().stream()
                .filter(a -> a.getApplicationID().equals(applicationID))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        if (app.getStatus() != Types.ApplicationStatus.SUCCESSFUL)
            throw new IllegalArgumentException("Can only reject successful applications");

        // Mark as unsuccessful (rejected by student)
        app.updateStatus(Types.ApplicationStatus.UNSUCCESSFUL);

        // If this was the accepted internship, clear it and free slot
        if (app == student.getAcceptedInternship()) {
            student.setAcceptedInternship(null);
            if (app.isAccepted()) {
                app.getInternship().removeSlot(student);
                app.setAccepted(false);
            }
        }
    }

    /** Request Withdrawal flow
    Allow to request withdrawal for internship application
    before/after placement confirmation*/
    @Override
    public WithdrawalRequest requestWithdrawal(Student student, String applicationID) {
        // Find the application
        Application app = student.getApplications().stream()
                .filter(a -> a.getApplicationID().equals(applicationID))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        // Create withdrawal request
        WithdrawalRequest wr = new WithdrawalRequest(student, app);
        repo.addWithdrawal(wr);

        return wr;
    }
}

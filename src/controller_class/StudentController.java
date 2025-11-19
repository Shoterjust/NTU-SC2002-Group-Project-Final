package controller_class;

import entity_class.*;
import java.util.*;
import java.util.stream.Collectors;

/** Handles student actions; depends on IDataRepo; implements IStudentController. */
public class StudentController implements IStudentController {
    private final IDataRepo repo;

    public StudentController(IDataRepo repo) { this.repo = repo; }

    @Override
    public List<Internship> getEligibleInternships(Student student) {
        return repo.getAllInternships().stream()
                .filter(i -> i.isEligibleForStudent(student))
                .collect(Collectors.toList());
    }

    @Override
    public Application applyInternship(Student student, String internshipID) {
        Internship internship = repo.findInternship(internshipID);
        if (internship == null) throw new IllegalArgumentException("Internship not found");
        if (!student.canApplyMore())
            throw new IllegalStateException("Max applications reached or already accepted an internship");
        if (!internship.isEligibleForStudent(student))
            throw new IllegalStateException("Student not eligible for this internship");
        for (Application app : student.getApplications()) {
            if (app.getInternship().equals(internship))
                throw new IllegalArgumentException("Already applied for this internship");
        }
        String appID = student.getUserID() + "-" + internshipID;
        Application app = new Application(appID, internship, student);
        student.getApplications().add(app);
        internship.getApplications().add(app);
        return app;
    }

    @Override
    public List<Application> viewApplications(Student student) {
        return new ArrayList<>(student.getApplications());
    }

    @Override
    public void acceptInternship(Student student, String applicationID) {
        Application app = student.getApplications().stream()
                .filter(a -> a.getApplicationID().equals(applicationID))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        if (student.getAcceptedInternship() != null)
            throw new IllegalStateException("Already accepted an internship");
        if (app.getStatus() != Types.ApplicationStatus.SUCCESSFUL)
            throw new IllegalArgumentException("Can only accept successful applications");
        for (Application other : student.getApplications()) {
            if (!other.equals(app) && other.getStatus() != Types.ApplicationStatus.UNSUCCESSFUL) {
                other.withdraw();
            }
        }
        student.setAcceptedInternship(app);
        app.setAccepted(true);
        app.getInternship().addSlot(student);
    }

    @Override
    public void rejectInternship(Student student, String applicationID) {
        Application app = student.getApplications().stream()
                .filter(a -> a.getApplicationID().equals(applicationID))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        if (app.getStatus() != Types.ApplicationStatus.SUCCESSFUL)
            throw new IllegalArgumentException("Can only reject successful applications");
        app.updateStatus(Types.ApplicationStatus.UNSUCCESSFUL);
        if (app == student.getAcceptedInternship()) {
            student.setAcceptedInternship(null);
            if (app.isAccepted()) {
                app.getInternship().removeSlot(student);
                app.setAccepted(false);
            }
        }
    }

    @Override
    public WithdrawalRequest requestWithdrawal(Student student, String applicationID) {
        Application app = student.getApplications().stream()
                .filter(a -> a.getApplicationID().equals(applicationID))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        WithdrawalRequest wr = new WithdrawalRequest(student, app);
        repo.addWithdrawal(wr);
        return wr;
    }
}

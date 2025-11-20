package controller_class;

import java.util.*;
import java.util.stream.Collectors;
import entity_class.*;

/**
 * Implementation of the {@link ICompanyRepController} interface. This
 * controller manages internship opportunities created by company
 * representatives and depends only on the {@link IDataRepo} abstraction.
 */
public class CompanyRepController implements ICompanyRepController {
    private final IDataRepo repo;

    /**
     * Constructs a new CompanyRepController with the given data repository.
     *
     * @param repo the data repository abstraction used by this controller
     */
    public CompanyRepController(IDataRepo repo) {
        this.repo = repo;
    }

    /**
     * #13: Create a new internship opportunity (with validation)
     */
    @Override
    public Internship createInternship(CompanyRep rep, String title, String description,
                                       Types.InternshipLevel level, List<Types.Major> majors,
                                       Date openDate, Date closeDate, int slots) {
        // Validate rep can create more
        if (!rep.canCreateMoreInternships()) {
            throw new IllegalStateException("Maximum number of approved internships reached");
        }

        // Validate rep is approved
        if (!rep.isApproved()) {
            throw new IllegalStateException("Company representative not approved yet");
        }

        // Generate internship ID
        String id = rep.getCompanyName().replaceAll("\\s+", "") +
                "-" + rep.getUserID().split("@")[0] +
                "-" + (rep.getCreatedInternships().size() + 1);

        Internship internship = new Internship(id, title, description, level, majors,
                openDate, closeDate, rep.getCompanyName(), rep, slots
        );

        // Add to rep's list and data repo
        rep.getCreatedInternships().add(internship);
        repo.addInternship(internship);

        return internship;
    }

    /**
     * #15: Access all Internship created by this Rep, regardless of visibility
     */
    @Override
    public List<Internship> viewInternships(CompanyRep rep) {
        return new ArrayList<>(rep.getCreatedInternships());
    }

    /** #14: Company Representatives can view pending, approved, or rejected
     * status updates for their submitted opportunities */
    @Override
    public List<Internship>  viewInternshipByStatus(CompanyRep rep, Types.InternshipStatus status) {
        return rep.getCreatedInternships().stream()
                .filter(i -> i.getStatus() == status)
                .collect(Collectors.toList());
    }

    /** #16: Restriction on Editing Approved Opportunities
     * (Only if PENDING) */
    @Override
    public void updateInternship(CompanyRep rep, String internshipID,
                                 String title, String description,
                                 Types.InternshipLevel level,
                                 Date openDate, Date closeDate,
                                 Integer numberOfSlots) {
        Internship internship = findRepInternship(rep, internshipID);
        if (internship.getStatus() != Types.InternshipStatus.PENDING) {
            throw new IllegalStateException("Cannot edit internship after approval decision");
        }

        // Update fields if provided
        if (title != null) internship.setTitle(title);
        if (description != null) internship.setDescription(description);
        if (level != null) internship.setLevel(level);
        if (openDate != null) internship.setOpenDate(openDate);
        if (closeDate != null) internship.setCloseDate(closeDate);
        if (numberOfSlots != null) internship.setNumberOfSlots(numberOfSlots);
    }

    @Override
    public void addPreferredMajor(CompanyRep rep, String internshipID, Types.Major major) {
        Internship internship = findRepInternship(rep, internshipID);

        if (internship.getStatus() != Types.InternshipStatus.PENDING) {
            throw new IllegalStateException("Cannot edit internship after approval decision");
        }

        if (!internship.getPreferredMajor().contains(major)) {
            internship.getPreferredMajor().add(major);
        }
    }

    @Override
    public void removePreferredMajor(CompanyRep rep, String internshipID, Types.Major major) {
        Internship internship = findRepInternship(rep, internshipID);

        if (internship.getStatus() != Types.InternshipStatus.PENDING) {
            throw new IllegalStateException("Cannot edit internship after approval decision");
        }
        if (!internship.getPreferredMajor().remove(major)) {
            throw new IllegalArgumentException("Major not in preferred list");
        }
    }

    /**
     * #18, 19: Application Management and Placement Confirmation
     */
    // View all applications for a specific internship
    @Override
    public List<Application> viewApplications(CompanyRep rep, String internshipID) {
        Internship internship = findRepInternship(rep, internshipID);
        return new ArrayList<>(internship.getApplications());
    }

    // Process application outcome (mark SUCCESSFUL/UNSUCCESSFUL)
    @Override
    public void processApplication(CompanyRep rep, String internshipID,
                                   String applicationID, Types.ApplicationStatus decision) {
        Internship internship = findRepInternship(rep, internshipID);
        Application app = internship.getApplications().stream()
                .filter(a -> a.getApplicationID().equals(applicationID))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        app.updateStatus(decision);
    }

    /** #20: Delete Internship Opportunity Listings */
    @Override
    public void deleteInternship(CompanyRep rep, String internshipID) {
        Internship internship = findRepInternship(rep, internshipID);
        if (internship.getStatus() != Types.InternshipStatus.PENDING) {
            throw new IllegalStateException("Cannot delete internship after approval decision");
        }

        // Withdraw all applications
        for (Application app : internship.getApplications()) {
            app.withdraw();
        }

        // Remove from rep and repository
        rep.getCreatedInternships().remove(internship);
        repo.removeInternship(internshipID);
    }

    /** #22: Toggle Internship Opportunity Visibility*/
    @Override
    public void toggleVisibility(CompanyRep rep, String internshipID) {
        Internship internship = findRepInternship(rep, internshipID);
        internship.setVisible(!internship.isVisible());
    }

    // Helper
    private Internship findRepInternship(CompanyRep rep, String internshipID) {
        return rep.getCreatedInternships().stream()
                .filter(i -> i.getInternshipID().equals(internshipID))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Internship not found"));
    }
}

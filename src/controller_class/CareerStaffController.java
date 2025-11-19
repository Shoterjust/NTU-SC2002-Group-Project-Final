package controller_class;

import java.util.*;
import java.util.stream.Collectors;
import entity_class.*;

/**
 * Implementation of the {@link ICareerStaffController} interface. This
 * controller manages administrative operations for career staff and
 * depends only on the {@link IDataRepo} abstraction.
 */
public class CareerStaffController implements ICareerStaffController {
    private final IDataRepo repo;

    /**
     * Constructs a new CareerStaffController.
     *
     * @param repo the data repository abstraction used by this controller
     */
    public CareerStaffController(IDataRepo repo) {
        this.repo = repo;
    }

    /** View all users in the system */
    @Override
    public List<User> viewAllUsers() {
        return repo.getAllUsers();
    }

    /** View all internships (created/pending/approved/rejected) */
    @Override
    public List<Internship> viewAllInternships() {
        return repo.getAllInternships();
    }

    /** Get all pending company reps */
    @Override
    public List<CompanyRep> getPendingCompanyReps() {
        return repo.getAllUsers().stream()
                .filter(u -> u instanceof CompanyRep)
                .map(u -> (CompanyRep) u)
                .filter(r -> !r.isApproved())
                .collect(Collectors.toList());
    }

    /** Approve company rep */
    @Override
    public void approveCompanyRep(String userID) {
        User user = repo.findUser(userID);
        if (!(user instanceof CompanyRep)) {
            throw new IllegalArgumentException("User is not a company representative");
        }
        ((CompanyRep) user).setApproved(true);
    }

    /** Reject company rep and remove from system */
    @Override
    public void rejectCompanyRep(String userID) {
        User user = repo.findUser(userID);
        if (!(user instanceof CompanyRep)) {
            throw new IllegalArgumentException("User is not a company representative");
        }
        repo.removeUser(userID);
    }

    /** Get all pending internship */
    @Override
    public List<Internship> getPendingInternships() {
        return repo.getAllInternships().stream()
                .filter(i -> i.getStatus() == Types.InternshipStatus.PENDING)
                .collect(Collectors.toList());
    }

    /** Approve internship created by Company Rep */
    @Override
    public void approveInternship(String internshipID) {
        Internship internship = repo.findInternship(internshipID);
        if (internship == null) {
            throw new IllegalArgumentException("Internship not found");
        }
        internship.setStatus(Types.InternshipStatus.APPROVED);
    }

    /** Reject internship created by Company Rep */
    @Override
    public void rejectInternship(String internshipID) {
        Internship internship = repo.findInternship(internshipID);
        if (internship == null) {
            throw new IllegalArgumentException("Internship not found");
        }
        internship.setStatus(Types.InternshipStatus.REJECTED);
    }

    /** Get all pending withdrawal requests */
    @Override
    public List<WithdrawalRequest> getPendingWithdrawals() {
        return repo.getAllWithdrawals().stream()
                .filter(w -> w.getStatus() == Types.WithdrawalStatus.PENDING)
                .collect(Collectors.toList());
    }

    /** Process withdrawal approval/rejection */
    @Override
    public void processWithdrawal(String requestID, boolean approve) {
        WithdrawalRequest wr = repo.findWithdrawal(requestID);
        if (wr == null) {
            throw new IllegalArgumentException("Withdrawal request not found");
        }
        wr.setStatus(approve ? Types.WithdrawalStatus.APPROVED : Types.WithdrawalStatus.REJECTED);

        if (approve) {
            Application app = wr.getApplication();
            app.withdraw();
            if (app.isAccepted()) {
                app.getInternship().removeSlot(app.getStudent());
                app.getStudent().setAcceptedInternship(null);
            }
        }
    }

    /** Filter Internships by criteria */
    @Override
    public List<Internship> getFilteredInternships(List<Types.Major> majors,
                                                   String company,
                                                   Types.InternshipLevel level,
                                                   Date open,
                                                   Date close,
                                                   Types.InternshipStatus status) {
        return repo.getAllInternships().stream()
                .filter(i -> status == null || i.getStatus() == status)
                .filter(i -> level == null || i.getLevel() == level)
                .filter(i -> majors == null || majors.isEmpty() ||
                        majors.stream().anyMatch(m -> i.getPreferredMajor().contains(m)))
                .filter(i -> company == null || company.isEmpty() ||
                        i.getCompanyName().toLowerCase().contains(company.toLowerCase()))
                .filter(i -> open == null ||
                        (i.getOpenDate() != null && !i.getOpenDate().before(open)))
                .filter(i -> close == null ||
                        (i.getCloseDate() != null && !i.getCloseDate().after(close)))
                .collect(Collectors.toList());
    }
}

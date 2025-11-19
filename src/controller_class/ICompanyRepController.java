package controller_class;

import entity_class.*;
import java.util.Date;
import java.util.List;

public interface ICompanyRepController {
    Internship createInternship(CompanyRep rep, String title, String description,
                                Types.InternshipLevel level, List<Types.Major> majors,
                                Date openDate, Date closeDate, int slots);
    List<Internship> viewInternships(CompanyRep rep);
    List<Internship> viewInternshipByStatus(CompanyRep rep, Types.InternshipStatus status);
    void updateInternship(CompanyRep rep, String internshipID, String title,
                          String description, Types.InternshipLevel level,
                          Date openDate, Date closeDate, Integer numberOfSlots);
    void addPreferredMajor(CompanyRep rep, String internshipID, Types.Major major);
    void removePreferredMajor(CompanyRep rep, String internshipID, Types.Major major);
    List<Application> viewApplications(CompanyRep rep, String internshipID);
    void processApplication(CompanyRep rep, String internshipID, String applicationID, Types.ApplicationStatus decision);
    void deleteInternship(CompanyRep rep, String internshipID);
    void toggleVisibility(CompanyRep rep, String internshipID);
}

# NTU-SC2002-Group-Project-Final
# Internship Management System

This is a console-based internship management system for **Students**, **Company Representatives**, and **Career Staff**.  
Data is stored **in CSV files** and **in Java HashMaps** (via `DataRepo`) during runtime.

---

## Project Structure

### 1. Entry Point

- **`MainApplication.java`**
    - Start the program.
    - Create `DataRepo`, `FileHandler`, controllers, and UIs.
    - Load data from CSV into memory when started and save back to CSV on exit.

---

### 2. Data Storage

- **`IDataRepo.java` / `DataRepo.java`**
    - In-memory “database” 
    - Store:
      - `Map<String, User>` – key: **userID** (`String`), value: **User** object
      - `Map<String, Internship>` – key: **internshipID** (`String`), value: **Internship** object
      - `Map<String, WithdrawalRequest>` – key: **requestID** (`String`), value: **WithdrawalRequest** object

    - Provide `add/find/getAll` methods for controllers.


- **`IFileHandler.java` / `FileHandler.java`**
    - Read from and write to CSV files:
        - `sample_student_list.csv`
        - `sample_company_representative_list.csv`
        - `sample_staff_list.csv`
        - `Internship.csv`
        - `Application.csv`
        - `WithdrawalRequest.csv`
    - Also manages:
        - Passwords (`getPasswordForUser`, `updateUserPassword`)
        - Student emails (`getStudentEmail`)

---

### 3. Entity Classes

- **Users & Roles**
    - `User`, `IUser` – abstract base user.
    - `Student`, `IStudent` – student details, submitted applications.
    - `CompanyRep`, `ICompanyRep` – company representative, created internships.
    - `CareerStaff`, `ICareerStaff` – staff users.


- **Domain Objects**
    - `Internship` – internship details, status, slots, visibility.
    - `Application` – student’s application to an internship.
    - `WithdrawalRequest` – request to withdraw from an accepted internship.
    - `Types` – enums (roles, majors, statuses, levels, etc.).

Entities mainly hold **data + simple methods** (e.g., validate IDs, change password, manage lists) that relate directly to domain logic, not application workflow logic.

---

### 4. Controller Classes

- **`ILoginController` / `LoginController`**
    - Handle login and password changes.
    - Talk to `DataRepo` + `FileHandler`.


- **`IStudentController` / `StudentController`**
    - Student actions:
        - View eligible internships
        - Apply for internships
        - View/accept/reject applications
        - Request withdrawals


- **`ICompanyRepController` / `CompanyRepController`**
    - Company rep actions:
        - Create/update/delete internships
        - Toggle visibility
        - View and process applications
        - Filter internships


- **`ICareerStaffController` / `CareerStaffController`**
    - Staff (admin) actions:
        - Approve/reject company reps
        - Approve/reject internships
        - Process withdrawal requests
        - View all users/internships
        - Filter internships

Controllers coordinate between **UI**, **DataRepo**, and **entities**.

---

### 5. Boundary Classes (UI)

- **`LoginUI`**
    - Welcome screen, role selection, login, logout.


- **`StudentUI`**
    - Student Menu for viewing internships, applying, managing applications, requesting withdrawals, changing password, viewing profile.


- **`CompanyRepUI`**
    - Company Representative Menu for managing internships, viewing/processing applications, toggling visibility, changing password, viewing profile.


- **`CompanyRepRegistrationUI`**
    - Handle registration of new company reps (before approval).


- **`CareerStaffUI`**
    - Career Staff Menu for approving reps and internships, processing withdrawals, viewing system statistics, generating reports, changing password, viewing profile.

UIs are responsible only for **input/output**; they call controllers for actual logic.

---

## Runtime Workflow (CSV ↔ HashMap)

1. **Start**
    - `MainApplication` creates `DataRepo` and `FileHandler`.
    - `FileHandler.loadAllData(repo)`:
        - Read all CSV files.
        - Create `User`, `Internship`, `Application`, `WithdrawalRequest` objects.
        - Store them in `DataRepo`’s HashMaps.
        - Cache passwords and emails.


2. **During Execution**
    - UIs interact with the user (menus, prompts).
    - UIs call controllers (e.g., `StudentController.applyInternship(...)`).
    - Controllers:
        - Use `DataRepo` to fetch and update entities in HashMaps.
        - Apply business rules (limits, statuses, dates, etc.).
    - All changes happen in memory (in the maps).


3. **Exit / Save**
    - When the user exits the system:
        - `FileHandler.saveAllData(repo)` is called.
        - Current contents of `DataRepo` (users, internships, applications, withdrawals) are written back to CSV.
        - Any updated passwords or statuses are persisted.

Overall Flow: **CSV → HashMaps (on run)** → system runs on HashMaps → **HashMaps → CSV (on exit)**.

import java.util.List;
import java.util.Scanner;

/**
 * Handles terminal input and output for the assignment tracker.
 * DatabaseManager handles persistence, while this class controls the menu flow.
 */
public class AssignmentManager {
    private final DatabaseManager databaseManager;
    private final Scanner scanner;

    public AssignmentManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");
            System.out.println();

            switch (choice) {
                case 1 -> addStudent();
                case 2 -> viewAllStudents();
                case 3 -> addCourseForStudent();
                case 4 -> viewCoursesForStudent();
                case 5 -> addAssignmentType();
                case 6 -> viewAssignmentTypes();
                case 7 -> addAssignmentToCourse();
                case 8 -> viewAssignmentsForCourse();
                case 9 -> updateAssignmentCompletionStatus();
                case 10 -> updateAssignmentDetails();
                case 11 -> deleteAssignment();
                case 12 -> viewOverdueAssignmentsForCourse();
                case 13 -> viewUpcomingAssignmentsForCourse();
                case 0 -> running = false;
                default -> System.out.println("Invalid menu option.\n");
            }
        }

        System.out.println("Exiting Student Assignment Tracker.");
    }

    private void printMenu() {
        System.out.println("===== STUDENT ASSIGNMENT TRACKER =====");
        System.out.println("1. Add student");
        System.out.println("2. View all students");
        System.out.println("3. Add course for a student");
        System.out.println("4. View courses for a student");
        System.out.println("5. Add assignment type");
        System.out.println("6. View assignment types");
        System.out.println("7. Add assignment to a course");
        System.out.println("8. View assignments for a course");
        System.out.println("9. Update assignment completion status");
        System.out.println("10. Update assignment details");
        System.out.println("11. Delete assignment");
        System.out.println("12. View overdue assignments for a course");
        System.out.println("13. View upcoming assignments for a course");
        System.out.println("0. Exit");
    }

    private void addStudent() {
        System.out.println("ADD STUDENT");
        String firstName = readRequiredText("First name: ");
        String lastName = readRequiredText("Last name: ");
        String email = readRequiredText("Email: ");

        Student student = new Student(0, firstName, lastName, email);
        int studentId = databaseManager.addStudent(student);
        System.out.println("Student added with ID " + studentId + ".\n");
    }

    private void viewAllStudents() {
        System.out.println("ALL STUDENTS");
        List<Student> students = databaseManager.getAllStudents();

        if (students.isEmpty()) {
            System.out.println("No students found.\n");
            return;
        }

        for (Student student : students) {
            System.out.println(student.getStudentDetails());
            System.out.println();
        }
    }

    private void addCourseForStudent() {
        System.out.println("ADD COURSE");
        int studentId = readInt("Student ID: ");
        Student student = databaseManager.getStudentById(studentId);

        if (student == null) {
            System.out.println("Student not found.\n");
            return;
        }

        String courseName = readRequiredText("Course name: ");
        String courseCode = readRequiredText("Course code: ");
        String semester = readRequiredText("Semester: ");
        String termStartDate = readDate("Term start date (YYYY-MM-DD): ");
        String termEndDate = readDate("Term end date (YYYY-MM-DD): ");

        Course course = new Course(0, studentId, courseName, courseCode, semester, termStartDate, termEndDate);
        int courseId = databaseManager.addCourse(course);
        System.out.println("Course added with ID " + courseId + ".\n");
    }

    private void viewCoursesForStudent() {
        System.out.println("VIEW COURSES");
        int studentId = readInt("Student ID: ");
        Student student = databaseManager.getStudentById(studentId);

        if (student == null) {
            System.out.println("Student not found.\n");
            return;
        }

        List<Course> courses = student.getCourses();
        if (courses.isEmpty()) {
            System.out.println("No courses found for this student.\n");
            return;
        }

        System.out.println("Courses for " + student.getFirstName() + " " + student.getLastName() + ":");
        for (Course course : courses) {
            System.out.println(course.getCourseDetails());
            System.out.println();
        }
    }

    private void addAssignmentType() {
        System.out.println("ADD ASSIGNMENT TYPE");
        String typeName = readRequiredText("Type name: ");
        int assignmentTypeId = databaseManager.addAssignmentType(new AssignmentType(0, typeName));
        System.out.println("Assignment type added with ID " + assignmentTypeId + ".\n");
    }

    private void viewAssignmentTypes() {
        System.out.println("ASSIGNMENT TYPES");
        List<AssignmentType> assignmentTypes = databaseManager.getAllAssignmentTypes();

        if (assignmentTypes.isEmpty()) {
            System.out.println("No assignment types found.\n");
            return;
        }

        for (AssignmentType assignmentType : assignmentTypes) {
            System.out.println(assignmentType);
        }
        System.out.println();
    }

    private void addAssignmentToCourse() {
        System.out.println("ADD ASSIGNMENT");
        int courseId = readInt("Course ID: ");
        Course course = databaseManager.getCourseById(courseId);

        if (course == null) {
            System.out.println("Course not found.\n");
            return;
        }

        int assignmentTypeId = readInt("Assignment type ID: ");
        AssignmentType assignmentType = databaseManager.getAssignmentTypeById(assignmentTypeId);

        if (assignmentType == null) {
            System.out.println("Assignment type not found.\n");
            return;
        }

        String title = readRequiredText("Title: ");
        String description = readOptionalText("Description: ");
        String dueDate = readDate("Due date (YYYY-MM-DD): ");
        boolean completed = readYesNo("Is the assignment complete? (y/n): ");

        Assignment assignment;
        if (isDiscussionBoardType(assignmentType.getTypeName())) {
            String replyDueDate = readDate("Reply due date (YYYY-MM-DD): ");
            assignment = new DiscussionBoardAssignment(
                0,
                title,
                description,
                dueDate,
                completed,
                assignmentTypeId,
                courseId,
                replyDueDate
            );
        } else {
            assignment = new StandardAssignment(
                0,
                title,
                description,
                dueDate,
                completed,
                assignmentTypeId,
                courseId,
                assignmentType.getTypeName()
            );
        }

        int assignmentId = databaseManager.addAssignment(assignment);
        System.out.println("Assignment added with ID " + assignmentId + ".\n");
    }

    private void viewAssignmentsForCourse() {
        System.out.println("VIEW ASSIGNMENTS");
        int courseId = readInt("Course ID: ");
        Course course = databaseManager.getCourseById(courseId);

        if (course == null) {
            System.out.println("Course not found.\n");
            return;
        }

        List<Assignment> assignments = course.getAssignments();
        if (assignments.isEmpty()) {
            System.out.println("No assignments found for this course.\n");
            return;
        }

        System.out.println("Assignments for " + course.getCourseName() + ":");
        for (Assignment assignment : assignments) {
            System.out.println(assignment.getAssignmentDetails());
            System.out.println("Overdue: " + assignment.isOverdue());
            System.out.println("Upcoming: " + assignment.isUpcoming());
            System.out.println();
        }
    }

    private void updateAssignmentCompletionStatus() {
        System.out.println("UPDATE COMPLETION STATUS");
        int assignmentId = readInt("Assignment ID: ");
        Assignment assignment = databaseManager.getAssignmentById(assignmentId);

        if (assignment == null) {
            System.out.println("Assignment not found.\n");
            return;
        }

        boolean completed = readYesNo("Mark assignment as completed? (y/n): ");
        databaseManager.updateAssignmentCompletionStatus(assignmentId, completed);
        System.out.println("Assignment completion status updated.\n");
    }

    private void updateAssignmentDetails() {
        System.out.println("UPDATE ASSIGNMENT DETAILS");
        int assignmentId = readInt("Assignment ID: ");
        Assignment currentAssignment = databaseManager.getAssignmentById(assignmentId);

        if (currentAssignment == null) {
            System.out.println("Assignment not found.\n");
            return;
        }

        String title = readOptionalText("New title (press Enter to keep current): ");
        String description = readOptionalText("New description (press Enter to keep current): ");
        String dueDate = readOptionalDate("New due date (YYYY-MM-DD, press Enter to keep current): ");
        String assignmentTypeInput = readOptionalText("New assignment type ID (press Enter to keep current): ");

        AssignmentType assignmentType = databaseManager.getAssignmentTypeById(currentAssignment.getAssignmentTypeId());
        int assignmentTypeId = currentAssignment.getAssignmentTypeId();

        if (!assignmentTypeInput.isBlank()) {
            try {
                assignmentTypeId = Integer.parseInt(assignmentTypeInput);
            } catch (NumberFormatException exception) {
                System.out.println("Assignment type ID must be a whole number.\n");
                return;
            }

            assignmentType = databaseManager.getAssignmentTypeById(assignmentTypeId);
            if (assignmentType == null) {
                System.out.println("Assignment type not found.\n");
                return;
            }
        }

        String updatedTitle = title.isBlank() ? currentAssignment.getTitle() : title;
        String updatedDescription = description.isBlank() ? currentAssignment.getDescription() : description;
        String updatedDueDate = dueDate.isBlank() ? currentAssignment.getDueDateAsString() : dueDate;

        Assignment updatedAssignment;
        if (isDiscussionBoardType(assignmentType.getTypeName())) {
            String currentReplyDueDate = currentAssignment.getReplyDueDateForDatabase();
            String replyDueDate = readOptionalDate(
                "New reply due date (YYYY-MM-DD, press Enter to keep current): "
            );
            String updatedReplyDueDate = replyDueDate.isBlank() ? currentReplyDueDate : replyDueDate;

            updatedAssignment = new DiscussionBoardAssignment(
                assignmentId,
                updatedTitle,
                updatedDescription,
                updatedDueDate,
                currentAssignment.isCompleted(),
                assignmentTypeId,
                currentAssignment.getCourseId(),
                updatedReplyDueDate
            );
        } else {
            updatedAssignment = new StandardAssignment(
                assignmentId,
                updatedTitle,
                updatedDescription,
                updatedDueDate,
                currentAssignment.isCompleted(),
                assignmentTypeId,
                currentAssignment.getCourseId(),
                assignmentType.getTypeName()
            );
        }

        databaseManager.updateAssignment(updatedAssignment);
        System.out.println("Assignment details updated.\n");
    }

    private void deleteAssignment() {
        System.out.println("DELETE ASSIGNMENT");
        int assignmentId = readInt("Assignment ID: ");
        Assignment assignment = databaseManager.getAssignmentById(assignmentId);

        if (assignment == null) {
            System.out.println("Assignment not found.\n");
            return;
        }

        databaseManager.deleteAssignment(assignmentId);
        System.out.println("Assignment deleted.\n");
    }

    private void viewOverdueAssignmentsForCourse() {
        System.out.println("OVERDUE ASSIGNMENTS");
        int courseId = readInt("Course ID: ");
        Course course = databaseManager.getCourseById(courseId);

        if (course == null) {
            System.out.println("Course not found.\n");
            return;
        }

        boolean foundAny = false;
        for (Assignment assignment : course.getAssignments()) {
            if (assignment.isOverdue()) {
                System.out.println(assignment.getTitle() + " | Due: " + assignment.getDueDateAsString());
                foundAny = true;
            }
        }

        if (!foundAny) {
            System.out.println("No overdue assignments found.");
        }
        System.out.println();
    }

    private void viewUpcomingAssignmentsForCourse() {
        System.out.println("UPCOMING ASSIGNMENTS");
        int courseId = readInt("Course ID: ");
        Course course = databaseManager.getCourseById(courseId);

        if (course == null) {
            System.out.println("Course not found.\n");
            return;
        }

        boolean foundAny = false;
        for (Assignment assignment : course.getAssignments()) {
            if (assignment.isUpcoming()) {
                System.out.println(assignment.getTitle() + " | Due: " + assignment.getDueDateAsString());
                foundAny = true;
            }
        }

        if (!foundAny) {
            System.out.println("No upcoming assignments found.");
        }
        System.out.println();
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private String readRequiredText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isBlank()) {
                return input;
            }
            System.out.println("This value is required.");
        }
    }

    private String readOptionalText(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private String readDate(String prompt) {
        while (true) {
            String value = readRequiredText(prompt);
            if (isValidDate(value)) {
                return value;
            }
            System.out.println("Use the format YYYY-MM-DD.");
        }
    }

    private String readOptionalDate(String prompt) {
        while (true) {
            String value = readOptionalText(prompt);
            if (value.isBlank() || isValidDate(value)) {
                return value;
            }
            System.out.println("Use the format YYYY-MM-DD.");
        }
    }

    private boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            }
            if (input.equals("n") || input.equals("no")) {
                return false;
            }
            System.out.println("Enter y or n.");
        }
    }

    private boolean isValidDate(String dateValue) {
        try {
            java.time.LocalDate.parse(dateValue);
            return true;
        } catch (java.time.format.DateTimeParseException exception) {
            return false;
        }
    }

    private boolean isDiscussionBoardType(String typeName) {
        return typeName.equalsIgnoreCase("Discussion Board");
    }
}

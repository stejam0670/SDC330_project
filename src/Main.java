/**
 * Simple terminal demo for the Student Assignment Tracker.
 * This week focuses on OOP design only, so all data stays in memory.
 */
public class Main {
    public static void main(String[] args) {
        AssignmentManager manager = new AssignmentManager();

        AssignmentType guidedPractice = new AssignmentType(1, "Guided Practice");
        AssignmentType project = new AssignmentType(2, "Project");
        AssignmentType discussionBoard = new AssignmentType(3, "Discussion Board");

        Student studentOne = new Student(1, "Jordan", "Lee", "jordan.lee@example.edu");
        Student studentTwo = new Student(2, "Maya", "Patel", "maya.patel@example.edu");

        Course javaCourse = new Course(101, "Advanced Object-Oriented Programming",
            "SDC330", "Spring 2026", "2026-01-15", "2026-05-01");
        Course databaseCourse = new Course(102, "Database Concepts",
            "SDC340", "Spring 2026", "2026-01-15", "2026-05-01");
        Course webCourse = new Course(103, "Web Development",
            "SDC220", "Spring 2026", "2026-01-15", "2026-05-01");

        manager.addStudent(studentOne);
        manager.addStudent(studentTwo);

        manager.addCourseToStudent(1, javaCourse);
        manager.addCourseToStudent(1, databaseCourse);
        manager.addCourseToStudent(2, webCourse);

        Assignment oopWorksheet = new StandardAssignment(
            1001,
            "OOP Worksheet",
            "Complete the class design worksheet.",
            "2026-03-20",
            false,
            guidedPractice.getId(),
            101,
            guidedPractice.getTypeName()
        );

        Assignment trackerProject = new StandardAssignment(
            1002,
            "Tracker Project Part A",
            "Build the first version of the assignment tracker.",
            "2026-03-29",
            false,
            project.getId(),
            101,
            project.getTypeName()
        );

        Assignment introDiscussion = new DiscussionBoardAssignment(
            1003,
            "Week 3 Discussion",
            "Post your OOP design and reply to classmates.",
            "2026-03-26",
            false,
            discussionBoard.getId(),
            101,
            "2026-03-30",
            2,
            1
        );

        Assignment sqlReading = new StandardAssignment(
            1004,
            "Normalization Reading",
            "Read Chapter 5 and answer review questions.",
            "2026-04-01",
            false,
            guidedPractice.getId(),
            102,
            guidedPractice.getTypeName()
        );

        Assignment htmlProject = new StandardAssignment(
            1005,
            "Portfolio Mockup",
            "Create a wireframe for the course portfolio page.",
            "2026-03-27",
            true,
            project.getId(),
            103,
            project.getTypeName()
        );

        manager.addAssignmentToCourse(1, 101, oopWorksheet);
        manager.addAssignmentToCourse(1, 101, trackerProject);
        manager.addAssignmentToCourse(1, 101, introDiscussion);
        manager.addAssignmentToCourse(1, 102, sqlReading);
        manager.addAssignmentToCourse(2, 103, htmlProject);

        System.out.println("===== ASSIGNMENT TYPES =====");
        System.out.println(guidedPractice);
        System.out.println(project);
        System.out.println(discussionBoard);
        System.out.println();

        System.out.println("===== STUDENTS =====");
        manager.viewAllStudents();

        System.out.println("===== COURSES FOR JORDAN =====");
        manager.viewCoursesForStudent(1);

        System.out.println("===== ASSIGNMENTS FOR SDC330 =====");
        manager.viewAssignmentsForCourse(1, 101);

        System.out.println("===== POLYMORPHISM DEMO =====");
        System.out.println("The list below stores different assignment objects in the same Assignment type.");
        for (Assignment assignment : javaCourse.getAssignments()) {
            System.out.println();
            System.out.println(assignment.getClass().getSimpleName());
            System.out.println(assignment.getAssignmentDetails());
        }
        System.out.println();

        System.out.println("===== OVERDUE CHECK =====");
        manager.viewOverdueAssignmentsByCourse(1, 101);

        System.out.println("===== UPCOMING CHECK =====");
        manager.viewUpcomingAssignmentsByCourse(1, 101);

        System.out.println("===== COMPLETION STATUS DEMO =====");
        System.out.println(trackerProject.getTitle() + " completed before marking: " + trackerProject.isCompleted());
        trackerProject.markComplete();
        System.out.println(trackerProject.getTitle() + " completed after marking: " + trackerProject.isCompleted());
        trackerProject.markIncomplete();
        System.out.println(trackerProject.getTitle() + " completed after resetting: " + trackerProject.isCompleted());
    }
}

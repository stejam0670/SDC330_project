import java.util.ArrayList;
import java.util.List;

/**
 * Manager class keeps the in-memory application logic straightforward.
 */
public class AssignmentManager {
    private List<Student> students;

    public AssignmentManager() {
        students = new ArrayList<>();
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void addCourseToStudent(int studentId, Course course) {
        Student student = findStudentById(studentId);
        if (student != null) {
            student.addCourse(course);
        }
    }

    public void addAssignmentToCourse(int studentId, int courseId, Assignment assignment) {
        Course course = findCourseForStudent(studentId, courseId);
        if (course != null) {
            course.addAssignment(assignment);
        }
    }

    public void viewAllStudents() {
        for (Student student : students) {
            System.out.println(student.getStudentDetails());
            System.out.println();
        }
    }

    public void viewCoursesForStudent(int studentId) {
        Student student = findStudentById(studentId);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        System.out.println("Courses for student ID " + studentId + ":");
        for (Course course : student.getCourses()) {
            System.out.println(course.getCourseDetails());
            System.out.println();
        }
    }

    public void viewAssignmentsForCourse(int studentId, int courseId) {
        Course course = findCourseForStudent(studentId, courseId);
        if (course == null) {
            System.out.println("Course not found for that student.");
            return;
        }

        System.out.println("Assignments for " + course.getCourseName() + ":");
        for (Assignment assignment : course.getAssignments()) {
            System.out.println(assignment.getAssignmentDetails());
            System.out.println("Overdue: " + assignment.isOverdue());
            System.out.println("Upcoming: " + assignment.isUpcoming());
            System.out.println();
        }
    }

    public void viewOverdueAssignmentsByCourse(int studentId, int courseId) {
        Course course = findCourseForStudent(studentId, courseId);
        if (course == null) {
            System.out.println("Course not found for that student.");
            return;
        }

        System.out.println("Overdue assignments for " + course.getCourseName() + ":");
        boolean foundAny = false;
        for (Assignment assignment : course.getAssignments()) {
            if (assignment.isOverdue()) {
                System.out.println("- " + assignment.getTitle());
                foundAny = true;
            }
        }

        if (!foundAny) {
            System.out.println("None");
        }
        System.out.println();
    }

    public void viewUpcomingAssignmentsByCourse(int studentId, int courseId) {
        Course course = findCourseForStudent(studentId, courseId);
        if (course == null) {
            System.out.println("Course not found for that student.");
            return;
        }

        System.out.println("Upcoming assignments for " + course.getCourseName() + ":");
        boolean foundAny = false;
        for (Assignment assignment : course.getAssignments()) {
            if (assignment.isUpcoming()) {
                System.out.println("- " + assignment.getTitle());
                foundAny = true;
            }
        }

        if (!foundAny) {
            System.out.println("None");
        }
        System.out.println();
    }

    private Student findStudentById(int studentId) {
        for (Student student : students) {
            if (student.getId() == studentId) {
                return student;
            }
        }
        return null;
    }

    private Course findCourseForStudent(int studentId, int courseId) {
        Student student = findStudentById(studentId);
        if (student == null) {
            return null;
        }

        for (Course course : student.getCourses()) {
            if (course.getId() == courseId) {
                return course;
            }
        }
        return null;
    }
}

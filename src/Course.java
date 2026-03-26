import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Course demonstrates composition because a course contains assignments.
 */
public class Course {
    private int id;
    private String courseName;
    private String courseCode;
    private String semester;
    private String termStartDate;
    private String termEndDate;
    private List<Assignment> assignments;

    public Course(int id, String courseName, String courseCode, String semester,
                  String termStartDate, String termEndDate) {
        this.id = id;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.semester = semester;
        this.termStartDate = termStartDate;
        this.termEndDate = termEndDate;
        this.assignments = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
    }

    public void removeAssignment(Assignment assignment) {
        assignments.remove(assignment);
    }

    public List<Assignment> getAssignments() {
        return Collections.unmodifiableList(assignments);
    }

    public String getCourseDetails() {
        return "Course ID: " + id
            + "\nCourse Name: " + courseName
            + "\nCourse Code: " + courseCode
            + "\nSemester: " + semester
            + "\nTerm Start Date: " + termStartDate
            + "\nTerm End Date: " + termEndDate
            + "\nAssignment Count: " + assignments.size();
    }
}

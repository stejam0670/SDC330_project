import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Student demonstrates composition because a student contains courses.
 */
public class Student {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private List<Course> courses;

    public Student(int id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.courses = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void addCourse(Course course) {
        courses.add(course);
    }

    public void removeCourse(Course course) {
        courses.remove(course);
    }

    public List<Course> getCourses() {
        return Collections.unmodifiableList(courses);
    }

    public String getStudentDetails() {
        return "Student ID: " + id
            + "\nName: " + firstName + " " + lastName
            + "\nEmail: " + email
            + "\nCourse Count: " + courses.size();
    }
}

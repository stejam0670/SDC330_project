import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all SQLite database work for the console application.
 * The OOP model classes still represent the data in memory.
 */
public class DatabaseManager {
    private final String databaseUrl;

    public DatabaseManager(String databaseFilePath) {
        this.databaseUrl = "jdbc:sqlite:" + databaseFilePath;
    }

    public void initializeDatabase() {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS students (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    email TEXT NOT NULL
                )
                """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS courses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_id INTEGER NOT NULL,
                    course_name TEXT NOT NULL,
                    course_code TEXT NOT NULL,
                    semester TEXT NOT NULL,
                    term_start_date TEXT NOT NULL,
                    term_end_date TEXT NOT NULL,
                    FOREIGN KEY (student_id) REFERENCES students(id)
                )
                """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS assignment_types (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    type_name TEXT NOT NULL
                )
                """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS assignments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    course_id INTEGER NOT NULL,
                    assignment_type_id INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    description TEXT,
                    due_date TEXT NOT NULL,
                    reply_due_date TEXT,
                    completed INTEGER NOT NULL,
                    FOREIGN KEY (course_id) REFERENCES courses(id),
                    FOREIGN KEY (assignment_type_id) REFERENCES assignment_types(id)
                )
                """);
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to initialize the SQLite database.", exception);
        }
    }

    public int addStudent(Student student) {
        String sql = "INSERT INTO students (first_name, last_name, email) VALUES (?, ?, ?)";

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, student.getFirstName());
            statement.setString(2, student.getLastName());
            statement.setString(3, student.getEmail());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to add student.", exception);
        }

        return -1;
    }

    public Student getStudentById(int studentId) {
        String sql = "SELECT id, first_name, last_name, email FROM students WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                Student student = new Student(
                    resultSet.getInt("id"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("email")
                );

                for (Course course : getCoursesForStudent(student.getId())) {
                    student.addCourse(course);
                }
                return student;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to read student.", exception);
        }
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, email FROM students ORDER BY last_name, first_name";

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Student student = new Student(
                    resultSet.getInt("id"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("email")
                );

                for (Course course : getCoursesForStudent(student.getId())) {
                    student.addCourse(course);
                }
                students.add(student);
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to read students.", exception);
        }

        return students;
    }

    public int addCourse(Course course) {
        String sql = """
            INSERT INTO courses (
                student_id, course_name, course_code, semester, term_start_date, term_end_date
            ) VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, course.getStudentId());
            statement.setString(2, course.getCourseName());
            statement.setString(3, course.getCourseCode());
            statement.setString(4, course.getSemester());
            statement.setString(5, course.getTermStartDate());
            statement.setString(6, course.getTermEndDate());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to add course.", exception);
        }

        return -1;
    }

    public Course getCourseById(int courseId) {
        String sql = """
            SELECT id, student_id, course_name, course_code, semester, term_start_date, term_end_date
            FROM courses
            WHERE id = ?
            """;

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                Course course = buildCourse(resultSet);
                for (Assignment assignment : getAssignmentsForCourse(course.getId())) {
                    course.addAssignment(assignment);
                }
                return course;
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to read course.", exception);
        }
    }

    public List<Course> getCoursesForStudent(int studentId) {
        List<Course> courses = new ArrayList<>();
        String sql = """
            SELECT id, student_id, course_name, course_code, semester, term_start_date, term_end_date
            FROM courses
            WHERE student_id = ?
            ORDER BY course_code
            """;

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Course course = buildCourse(resultSet);
                    for (Assignment assignment : getAssignmentsForCourse(course.getId())) {
                        course.addAssignment(assignment);
                    }
                    courses.add(course);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to read courses.", exception);
        }

        return courses;
    }

    public int addAssignmentType(AssignmentType assignmentType) {
        String sql = "INSERT INTO assignment_types (type_name) VALUES (?)";

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, assignmentType.getTypeName());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to add assignment type.", exception);
        }

        return -1;
    }

    public AssignmentType getAssignmentTypeById(int assignmentTypeId) {
        String sql = "SELECT id, type_name FROM assignment_types WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, assignmentTypeId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new AssignmentType(
                        resultSet.getInt("id"),
                        resultSet.getString("type_name")
                    );
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to read assignment type.", exception);
        }

        return null;
    }

    public List<AssignmentType> getAllAssignmentTypes() {
        List<AssignmentType> assignmentTypes = new ArrayList<>();
        String sql = "SELECT id, type_name FROM assignment_types ORDER BY type_name";

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                assignmentTypes.add(new AssignmentType(
                    resultSet.getInt("id"),
                    resultSet.getString("type_name")
                ));
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to read assignment types.", exception);
        }

        return assignmentTypes;
    }

    public int addAssignment(Assignment assignment) {
        String sql = """
            INSERT INTO assignments (
                course_id, assignment_type_id, title, description, due_date, reply_due_date, completed
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillAssignmentStatement(statement, assignment);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to add assignment.", exception);
        }

        return -1;
    }

    public Assignment getAssignmentById(int assignmentId) {
        String sql = """
            SELECT a.id, a.course_id, a.assignment_type_id, a.title, a.description,
                   a.due_date, a.reply_due_date, a.completed, at.type_name
            FROM assignments a
            JOIN assignment_types at ON a.assignment_type_id = at.id
            WHERE a.id = ?
            """;

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, assignmentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return buildAssignment(resultSet);
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to read assignment.", exception);
        }

        return null;
    }

    public List<Assignment> getAssignmentsForCourse(int courseId) {
        List<Assignment> assignments = new ArrayList<>();
        String sql = """
            SELECT a.id, a.course_id, a.assignment_type_id, a.title, a.description,
                   a.due_date, a.reply_due_date, a.completed, at.type_name
            FROM assignments a
            JOIN assignment_types at ON a.assignment_type_id = at.id
            WHERE a.course_id = ?
            ORDER BY a.due_date
            """;

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    assignments.add(buildAssignment(resultSet));
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to read assignments.", exception);
        }

        return assignments;
    }

    public void updateAssignmentCompletionStatus(int assignmentId, boolean completed) {
        String sql = "UPDATE assignments SET completed = ? WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, completed ? 1 : 0);
            statement.setInt(2, assignmentId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to update assignment completion status.", exception);
        }
    }

    public void updateAssignment(Assignment assignment) {
        String sql = """
            UPDATE assignments
            SET course_id = ?, assignment_type_id = ?, title = ?, description = ?,
                due_date = ?, reply_due_date = ?, completed = ?
            WHERE id = ?
            """;

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            fillAssignmentStatement(statement, assignment);
            statement.setInt(8, assignment.getId());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to update assignment.", exception);
        }
    }

    public void deleteAssignment(int assignmentId) {
        String sql = "DELETE FROM assignments WHERE id = ?";

        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, assignmentId);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw new RuntimeException("Unable to delete assignment.", exception);
        }
    }

    private Connection connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException exception) {
            throw new SQLException(
                "SQLite JDBC driver not found. Add the sqlite-jdbc JAR to the lib folder.", exception);
        }

        Connection connection = DriverManager.getConnection(databaseUrl);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    private Course buildCourse(ResultSet resultSet) throws SQLException {
        return new Course(
            resultSet.getInt("id"),
            resultSet.getInt("student_id"),
            resultSet.getString("course_name"),
            resultSet.getString("course_code"),
            resultSet.getString("semester"),
            resultSet.getString("term_start_date"),
            resultSet.getString("term_end_date")
        );
    }

    private Assignment buildAssignment(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int courseId = resultSet.getInt("course_id");
        int assignmentTypeId = resultSet.getInt("assignment_type_id");
        String title = resultSet.getString("title");
        String description = resultSet.getString("description");
        String dueDate = resultSet.getString("due_date");
        String replyDueDate = resultSet.getString("reply_due_date");
        boolean completed = resultSet.getInt("completed") == 1;
        String typeName = resultSet.getString("type_name");

        if (isDiscussionBoardType(typeName)) {
            return new DiscussionBoardAssignment(
                id,
                title,
                description,
                dueDate,
                completed,
                assignmentTypeId,
                courseId,
                replyDueDate
            );
        }

        return new StandardAssignment(
            id,
            title,
            description,
            dueDate,
            completed,
            assignmentTypeId,
            courseId,
            typeName
        );
    }

    private void fillAssignmentStatement(PreparedStatement statement, Assignment assignment) throws SQLException {
        statement.setInt(1, assignment.getCourseId());
        statement.setInt(2, assignment.getAssignmentTypeId());
        statement.setString(3, assignment.getTitle());
        statement.setString(4, assignment.getDescription());
        statement.setString(5, assignment.getDueDateAsString());

        String replyDueDate = assignment.getReplyDueDateForDatabase();
        if (replyDueDate == null || replyDueDate.isBlank()) {
            statement.setNull(6, java.sql.Types.VARCHAR);
        } else {
            statement.setString(6, replyDueDate);
        }

        statement.setInt(7, assignment.isCompleted() ? 1 : 0);
    }

    private boolean isDiscussionBoardType(String typeName) {
        return typeName != null && typeName.equalsIgnoreCase("Discussion Board");
    }
}

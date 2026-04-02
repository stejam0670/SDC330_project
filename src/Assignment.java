import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Abstract base class for assignments.
 * This demonstrates abstraction by defining shared state and behavior
 * while requiring each subclass to provide its own details output.
 */
public abstract class Assignment implements Trackable {
    private int id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private boolean completed;
    private int assignmentTypeId;
    private int courseId;

    public Assignment(int id, String title, String description, String dueDate,
                      boolean completed, int assignmentTypeId, int courseId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = parseDate(dueDate);
        this.completed = completed;
        this.assignmentTypeId = assignmentTypeId;
        this.courseId = courseId;
    }

    private LocalDate parseDate(String dateValue) {
        try {
            return LocalDate.parse(dateValue);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(
                "Dates must use the format YYYY-MM-DD. Invalid value: " + dateValue, ex);
        }
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getDueDateAsString() {
        return dueDate.toString();
    }

    public void setDueDate(String dueDate) {
        this.dueDate = parseDate(dueDate);
    }

    public int getAssignmentTypeId() {
        return assignmentTypeId;
    }

    public void setAssignmentTypeId(int assignmentTypeId) {
        this.assignmentTypeId = assignmentTypeId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    @Override
    public void markComplete() {
        completed = true;
    }

    @Override
    public void markIncomplete() {
        completed = false;
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    @Override
    public boolean isOverdue() {
        return !completed && dueDate.isBefore(LocalDate.now());
    }

    @Override
    public boolean isUpcoming() {
        LocalDate today = LocalDate.now();
        LocalDate upcomingWindowEnd = today.plusDays(7);
        return !completed && (dueDate.isEqual(today)
            || (dueDate.isAfter(today) && (dueDate.isBefore(upcomingWindowEnd) || dueDate.isEqual(upcomingWindowEnd))));
    }

    /**
     * Standard assignments do not use an extra deadline column.
     * Discussion board assignments override this for reply_due_date storage.
     */
    public String getReplyDueDateForDatabase() {
        return null;
    }

    public abstract String getAssignmentDetails();
}

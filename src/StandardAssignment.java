/**
 * Concrete class for a regular assignment.
 * This exists so the demo can create normal assignments from the abstract base class.
 */
public class StandardAssignment extends Assignment {
    private String typeName;

    public StandardAssignment(int id, String title, String description, String dueDate,
                              boolean completed, int assignmentTypeId, int courseId, String typeName) {
        super(id, title, description, dueDate, completed, assignmentTypeId, courseId);
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String getAssignmentDetails() {
        return "Assignment ID: " + getId()
            + "\nTitle: " + getTitle()
            + "\nDescription: " + getDescription()
            + "\nDue Date: " + getDueDateAsString()
            + "\nType: " + typeName
            + "\nCompleted: " + isCompleted();
    }
}

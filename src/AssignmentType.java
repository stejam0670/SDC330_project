public class AssignmentType {
    private int id;
    private String typeName;

    public AssignmentType(int id, String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    public int getId() {
        return id;
    }

    public String getTypeName() {
        return typeName;
    }

    @Override
    public String toString() {
        return "Assignment Type ID: " + id + " | Name: " + typeName;
    }
}

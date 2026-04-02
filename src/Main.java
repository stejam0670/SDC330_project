/**
 * Week 4 entry point for the Student Assignment Tracker.
 * This version keeps the OOP design but stores data in SQLite
 * and uses a menu-driven terminal interface.
 */
public class Main {
    public static void main(String[] args) {
        DatabaseManager databaseManager = new DatabaseManager("student_assignment_tracker.db");
        databaseManager.initializeDatabase();

        AssignmentManager assignmentManager = new AssignmentManager(databaseManager);
        assignmentManager.run();
    }
}

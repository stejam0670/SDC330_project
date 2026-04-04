## Project Summary

### Project Name

Student Assignment Tracker

### Project Description

Student Assignment Tracker is a menu-driven Java console application that helps users manage students, courses, and assignments in one place. The project uses object-oriented design for modeling students, courses, assignment types, and assignments, while SQLite is used for persistent data storage so information is saved between runs.

### Project Tasks

- Set up the Java project structure in VS Code with `src`, `bin`, and dependency configuration.
- Design object-oriented classes for students, courses, assignments, and assignment types.
- Build a terminal-based menu system for user interaction.
- Implement features to add and view students and their courses.
- Implement features to create assignment types and add assignments to courses.
- Support different assignment behaviors, including discussion board assignments with reply due dates.
- Add update and delete functionality for assignment records.
- Track assignment completion, overdue items, and upcoming due dates.
- Connect the application to SQLite for persistent database storage.
- Test the application by running the menu flow and validating database operations.

### Project Skills Learned

- Java object-oriented programming
- Class design and inheritance
- Console-based user interface development
- SQLite database integration with JDBC
- CRUD operations and data persistence
- Input validation and menu-driven program flow
- Organizing a small Java application in VS Code

### Language Used

- **Java**: Used to build the application logic, object models, and terminal interface.
- **SQLite**: Used as the database for storing students, courses, assignment types, and assignments.

### Development Process Used

- **Incremental development**: Building the project feature by feature, starting with the application structure and extending it with persistence and assignment tracking functionality.

### Notes

- The project entry point is [Main.java](c:/Users/Alex/Documents/GitHub/SDC330_project/src/Main.java).
- The application creates and uses `student_assignment_tracker.db` when it runs.
- The SQLite JDBC driver is referenced in [settings.json](c:/Users/Alex/Documents/GitHub/SDC330_project/.vscode/settings.json) from an absolute local path, so that dependency may need to be updated on another machine.
- Compile and run the project through VS Code Java support or with a Java command that includes the SQLite JDBC jar on the classpath.

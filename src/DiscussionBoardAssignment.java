import java.time.LocalDate;

/**
 * Specialized assignment type that has both an initial post deadline
 * and a later reply deadline.
 */
public class DiscussionBoardAssignment extends Assignment {
    private LocalDate replyDueDate;

    public DiscussionBoardAssignment(int id, String title, String description, String dueDate,
                                     boolean completed, int assignmentTypeId, int courseId,
                                     String replyDueDate) {
        super(id, title, description, dueDate, completed, assignmentTypeId, courseId);
        this.replyDueDate = parseReplyDueDate(replyDueDate);
    }

    private LocalDate parseReplyDueDate(String replyDueDate) {
        if (replyDueDate == null || replyDueDate.isBlank()) {
            return null;
        }
        return LocalDate.parse(replyDueDate);
    }

    public String getReplyDueDateAsString() {
        return replyDueDate == null ? "" : replyDueDate.toString();
    }

    public void setReplyDueDate(String replyDueDate) {
        this.replyDueDate = parseReplyDueDate(replyDueDate);
    }

    /**
     * For discussion boards, the final reply deadline is the meaningful deadline
     * for overdue status, not just the initial post date.
     */
    @Override
    public boolean isOverdue() {
        LocalDate deadlineToCheck = replyDueDate == null ? getDueDate() : replyDueDate;
        return !isCompleted() && deadlineToCheck.isBefore(LocalDate.now());
    }

    /**
     * For discussion boards, the assignment is still upcoming if either the
     * initial post or reply deadline is within the next 7 days.
     */
    @Override
    public boolean isUpcoming() {
        if (isCompleted()) {
            return false;
        }

        LocalDate today = LocalDate.now();
        LocalDate upcomingWindowEnd = today.plusDays(7);

        boolean postUpcoming = getDueDate().isEqual(today)
            || (getDueDate().isAfter(today)
            && (getDueDate().isBefore(upcomingWindowEnd) || getDueDate().isEqual(upcomingWindowEnd)));

        boolean replyUpcoming = false;
        if (replyDueDate != null) {
            replyUpcoming = replyDueDate.isEqual(today)
                || (replyDueDate.isAfter(today)
                && (replyDueDate.isBefore(upcomingWindowEnd) || replyDueDate.isEqual(upcomingWindowEnd)));
        }

        return postUpcoming || replyUpcoming;
    }

    @Override
    public String getReplyDueDateForDatabase() {
        return getReplyDueDateAsString();
    }

    @Override
    public String getAssignmentDetails() {
        return "Discussion Board ID: " + getId()
            + "\nTitle: " + getTitle()
            + "\nDescription: " + getDescription()
            + "\nInitial Post Due Date: " + getDueDateAsString()
            + "\nReply Due Date: " + (replyDueDate == null ? "Not set" : getReplyDueDateAsString())
            + "\nCompleted: " + isCompleted();
    }
}

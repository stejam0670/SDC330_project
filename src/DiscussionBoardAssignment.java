import java.time.LocalDate;

/**
 * Specialized assignment type that has both an initial post deadline
 * and a later reply deadline.
 */
public class DiscussionBoardAssignment extends Assignment {
    private LocalDate replyDueDate;
    private int requiredReplies;
    private int submittedReplies;

    public DiscussionBoardAssignment(int id, String title, String description, String dueDate,
                                     boolean completed, int assignmentTypeId, int courseId,
                                     String replyDueDate, int requiredReplies, int submittedReplies) {
        super(id, title, description, dueDate, completed, assignmentTypeId, courseId);
        this.replyDueDate = LocalDate.parse(replyDueDate);
        this.requiredReplies = requiredReplies;
        this.submittedReplies = submittedReplies;
    }

    public String getReplyDueDateAsString() {
        return replyDueDate.toString();
    }

    public int getRequiredReplies() {
        return requiredReplies;
    }

    public int getSubmittedReplies() {
        return submittedReplies;
    }

    public void submitReply() {
        if (submittedReplies < requiredReplies) {
            submittedReplies++;
        }
    }

    /**
     * For discussion boards, the final reply deadline is the meaningful deadline
     * for overdue status, not just the initial post date.
     */
    @Override
    public boolean isOverdue() {
        return !isCompleted() && replyDueDate.isBefore(LocalDate.now());
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

        boolean replyUpcoming = replyDueDate.isEqual(today)
            || (replyDueDate.isAfter(today)
            && (replyDueDate.isBefore(upcomingWindowEnd) || replyDueDate.isEqual(upcomingWindowEnd)));

        return postUpcoming || replyUpcoming;
    }

    @Override
    public String getAssignmentDetails() {
        return "Discussion Board ID: " + getId()
            + "\nTitle: " + getTitle()
            + "\nDescription: " + getDescription()
            + "\nInitial Post Due Date: " + getDueDateAsString()
            + "\nReply Due Date: " + getReplyDueDateAsString()
            + "\nRequired Replies: " + requiredReplies
            + "\nSubmitted Replies: " + submittedReplies
            + "\nCompleted: " + isCompleted();
    }
}

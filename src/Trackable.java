public interface Trackable {
    boolean isOverdue();

    boolean isUpcoming();

    void markComplete();

    void markIncomplete();

    boolean isCompleted();
}

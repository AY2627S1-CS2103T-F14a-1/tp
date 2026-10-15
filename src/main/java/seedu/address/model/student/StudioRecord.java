package seedu.address.model.student;

/**
 * Represents an immutable record of a student's weekly studio status.
 */
public final class StudioRecord {

    private final boolean isPresent;
    private final int participation;
    private final String studioRecord;

    public static final int MIN_WEEK = 3;
    public static final int MAX_WEEK = 13;
    private static final String PRESENT_MARKER = "A: ";
    private static final String PARTICIPATION_MARKER = "P: ";
    public static final String MESSAGE_CONSTRAINTS =
            "Tutorial week must be between " + MIN_WEEK + " and " + MAX_WEEK + " inclusive";

    public StudioRecord(boolean isPresent, int participation) {
        this.isPresent = isPresent;
        this.participation = participation;
        this.studioRecord = toString();
    }

    /**
     * Returns whether the given tutorial week is within the valid range
     *
     * @param week Tutorial week to check.
     * @return True if the week is between {@value #MIN_WEEK} and {@value #MAX_WEEK}, inclusive.
     */
    public static boolean isValidWeek(int week) {
        return week >= MIN_WEEK && week <= MAX_WEEK;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof StudioRecord otherStudioRecord)) {
            return false;
        }

        return isPresent == otherStudioRecord.isPresent && participation == otherStudioRecord.participation;
    }

    @Override
    public String toString() {
        return "(" + PRESENT_MARKER + isPresent + " | " + PARTICIPATION_MARKER + participation + ")";
    }
}

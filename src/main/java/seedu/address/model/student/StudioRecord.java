package seedu.address.model.student;

import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents an immutable record of a student's weekly studio status.
 */
public final class StudioRecord {

    private final int effortScore;
    private final String studioRecord;

    private static final String PRESENT_MARKER = "A: ";
    private static final String PARTICIPATION_MARKER = "P: ";
    public static final String MESSAGE_CONSTRAINTS = "Effort score must be a non-negative integer";

    public StudioRecord(int effortScore) {
        checkArgument(isValidEffortScore(effortScore), MESSAGE_CONSTRAINTS);
        this.effortScore = effortScore;
        this.studioRecord = toString();
    }

    /**
     * Returns the student's effort score for the tutorial week.
     *
     * @return The effort score.
     */
    public int getEffortScore() {
        return effortScore;
    }

    /**
     * Returns whether the given studio is within the valid range
     *
     * @param effortScore Studio week to check.
     * @return True if the effort score is a non-negative integer.
     */
    public static boolean isValidEffortScore(int effortScore) {
        return effortScore >= 0;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof StudioRecord otherStudioRecord)) {
            return false;
        }

        return effortScore == otherStudioRecord.effortScore;
    }

    @Override
    public String toString() {
        return PARTICIPATION_MARKER + effortScore;
    }
}

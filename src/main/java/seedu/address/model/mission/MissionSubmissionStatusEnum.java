package seedu.address.model.mission;

/**
 * Represents a student's recorded or deadline-derived submission status.
 */
public enum MissionSubmissionStatusEnum {
    SUBMITTED("Submitted"),
    NOT_SUBMITTED("Not submitted"),
    OVERDUE("Overdue");

    private final String displayName;

    /**
     * Creates a status with its user-facing name.
     *
     * @param displayName Text displayed for this status.
     */
    MissionSubmissionStatusEnum(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Determines status from the submission record and deadline.
     *
     * @param hasSubmission Whether a submission is recorded.
     * @param isPastDeadline Whether the deadline has passed.
     * @return Submitted when recorded; otherwise overdue or not submitted.
     */
    public static MissionSubmissionStatusEnum from(boolean hasSubmission, boolean isPastDeadline) {
        if (hasSubmission) {
            return SUBMITTED;
        }
        return isPastDeadline ? OVERDUE : NOT_SUBMITTED;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

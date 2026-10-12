package seedu.address.model.mission;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents an assignment shared by the students in the address book.
 */
public final class Mission {

    public static final String MISSING_DESCRIPTION =
            "Mission description cannot be blank.";

    private final MissionId id;
    private final String description;
    private final LocalDate deadline;

    /**
     * Creates a mission.
     *
     * @param missionId Unique mission identifier.
     * @param description Nonblank mission description.
     * @param deadline Date through which submissions are considered on time.
     * @throws NullPointerException If any argument is null.
     * @throws IllegalArgumentException If the description is blank.
     */
    public Mission(MissionId missionId, String description, LocalDate deadline) {
        requireNonNull(missionId);
        requireNonNull(description);
        requireNonNull(deadline);
        checkArgument(!description.isBlank(), MISSING_DESCRIPTION);

        this.id = missionId;
        this.description = description.strip();
        this.deadline = deadline;
    }

    /**
     * Returns this mission's identifier.
     *
     * @return Mission identifier.
     */
    public MissionId getId() {
        return id;
    }

    /**
     * Returns this mission's description.
     *
     * @return Mission description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the submission deadline.
     *
     * @return Deadline date.
     */
    public LocalDate getDeadline() {
        return deadline;
    }

    /**
     * Checks whether the deadline has passed on the supplied date.
     *
     * @param date Date on which to evaluate the deadline.
     * @return True if the supplied date is after the deadline.
     * @throws NullPointerException If {@code date} is null.
     */
    public boolean isPastDeadline(LocalDate date) {
        requireNonNull(date);
        return date.isAfter(deadline);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        return other instanceof Mission otherMission
                && id.equals(otherMission.id)
                && description.equals(otherMission.description)
                && deadline.equals(otherMission.deadline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, deadline);
    }
}

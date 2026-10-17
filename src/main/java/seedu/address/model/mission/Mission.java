package seedu.address.model.mission;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDate;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents an assignment shared by the students in the address book.
 */
public final class Mission {

    public static final String MISSING_NAME =
            "Mission description cannot be blank.";

    private final MissionId id;
    private final String name;
    private final LocalDate deadline;

    /**
     * Creates a mission.
     *
     * @param missionId Unique mission identifier.
     * @param name Nonblank mission name.
     * @param deadline Date through which submissions are considered on time.
     * @throws NullPointerException If any argument is null.
     * @throws IllegalArgumentException If the name is blank.
     */
    public Mission(MissionId missionId, String name, LocalDate deadline) {
        requireNonNull(missionId);
        requireNonNull(name);
        requireNonNull(deadline);
        String normalizedName = normalizeName(name);
        checkArgument(!normalizedName.isBlank(), MISSING_NAME);

        this.id = missionId;
        this.name = normalizedName;
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
    public String getName() {
        return name;
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

    /**
     * Compares names ignoring case and runs of Unicode whitespace without changing display text.
     * Punctuation and word boundaries remain significant.
     *
     * @param otherName Name to compare with this mission.
     * @return True if both names identify the same assignment.
     * @throws NullPointerException If the other name is null.
     */
    public boolean hasSameName(String otherName) {
        requireNonNull(otherName);
        return normalizeName(name).equalsIgnoreCase(normalizeName(otherName));
    }

    /**
     * Collapses whitespace for stored names and duplicate comparison.
     *
     * @param value Name whose whitespace should be normalized.
     * @return Name with Unicode whitespace collapsed and surrounding spaces removed.
     */
    private static String normalizeName(String value) {
        return value.replaceAll("(?U)\\s+", " ").strip();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        return other instanceof Mission otherMission
                && id.equals(otherMission.id)
                && name.equals(otherMission.name)
                && deadline.equals(otherMission.deadline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, deadline);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("deadline", deadline)
                .toString();
    }
}

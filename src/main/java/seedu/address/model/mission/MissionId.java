package seedu.address.model.mission;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Identifies a mission independently of its name and deadline.
 */
public final class MissionId {

    public static final String MESSAGE_CONSTRAINTS =
            "Invalid Mission ID. Mission IDs must start with 'M' followed by a "
                    + "positive integer of at least two digits, with no unnecessary "
                    + "leading zeros (e.g., M01, M02, M105).";

    private static final String VALIDATION_REGEX = "M(?:0[1-9]|[1-9][0-9]+)";

    private final String value;

    /**
     * Creates a mission ID.
     *
     * @param value Canonical mission ID.
     * @throws NullPointerException If {@code value} is null.
     * @throws IllegalArgumentException If {@code value} is invalid.
     */
    public MissionId(String value) {
        requireNonNull(value);
        checkArgument(value.matches(VALIDATION_REGEX), MESSAGE_CONSTRAINTS);
        try {
            Long.parseLong(value.substring(1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS, e);
        }
        this.value = value;
    }

    /**
     * Returns the numeric part used to validate the allocation counter.
     *
     * @return Positive mission number.
     */
    public long getNumber() {
        return Long.parseLong(value.substring(1));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        return other instanceof MissionId otherId
                && value.equals(otherId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}

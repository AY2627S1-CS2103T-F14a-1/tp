package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a Student's Studio Group in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidStudioGroup(String)}
 */
public class StudioGroup {
    public static final String MESSAGE_CONSTRAINTS =
            "Studio Group should be a number followed by a capitalized letter, and should not be blank";

    /*
     * Studio Group e.g. 1A, 10M etc. No spaces allowed.
     */
    public static final String VALIDATION_REGEX = "^\\d+[A-Z]$";

    public final String studioGroup;

    /**
     * Constructs a {@code StudioGroup}.
     *
     * @param studioGroup A valid Studio Group.
     */
    public StudioGroup(String studioGroup) {
        requireNonNull(studioGroup);
        checkArgument(isValidStudioGroup(studioGroup), MESSAGE_CONSTRAINTS);
        this.studioGroup = studioGroup;
    }

    /**
     * Returns true if a given string is a valid Studio Group.
     */
    public static boolean isValidStudioGroup(String test) {
        return test.matches(VALIDATION_REGEX);
    }


    @Override
    public String toString() {
        return studioGroup;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof StudioGroup otherStudioGroup)) {
            return false;
        }

        return studioGroup.equals(otherStudioGroup.studioGroup);
    }

    @Override
    public int hashCode() {
        return studioGroup.hashCode();
    }
}

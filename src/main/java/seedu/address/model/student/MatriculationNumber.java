package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a matriculation number with an uppercase A, seven digits, and an uppercase final letter.
 */
public class MatriculationNumber {
    public static final String MESSAGE_CONSTRAINTS =
            "Matriculation numbers must start with A, followed by exactly 7 digits, "
                    + "and end with an uppercase letter A-Z";
    public static final String VALIDATION_REGEX = "A[0-9]{7}[A-Z]";
    public final String matricNumber;

    /**
     * Constructs a {@code MatriculationNumber}.
     *
     * @param matricNum A valid matriculation number.
     */
    public MatriculationNumber(String matricNum) {
        requireNonNull(matricNum);
        checkArgument(isValidMatricNumber(matricNum), MESSAGE_CONSTRAINTS);
        matricNumber = matricNum;
    }

    /**
     * Returns true if a given string is a valid matriculation number.
     */
    public static boolean isValidMatricNumber(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return matricNumber;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof MatriculationNumber otherMatricNum)) {
            return false;
        }

        return matricNumber.equals(otherMatricNum.matricNumber);
    }

    @Override
    public int hashCode() {
        return matricNumber.hashCode();
    }
}

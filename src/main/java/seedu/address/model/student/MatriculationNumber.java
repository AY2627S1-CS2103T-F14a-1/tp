package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents an immutable matriculation number: uppercase A, seven ASCII digits, and an uppercase letter A-Z.
 * Values are validated as supplied without trimming whitespace or changing letter case.
 */
public class MatriculationNumber {
    public static final String MESSAGE_CONSTRAINTS =
            "Matriculation numbers must start with the letter A, followed by exactly 7 digits, "
                    + "and end with an uppercase letter A-Z";
    public static final String VALIDATION_REGEX = "A[0-9]{7}[A-Z]";
    public final String matricNumber;

    /**
     * Constructs a matriculation number from text in the required uppercase format.
     *
     * @param matricNum A valid matriculation number, for example A1234567B.
     * @throws NullPointerException If matricNum is null.
     * @throws IllegalArgumentException If matricNum does not match the required format.
     */
    public MatriculationNumber(String matricNum) {
        requireNonNull(matricNum);
        checkArgument(isValidMatricNumber(matricNum), MESSAGE_CONSTRAINTS);
        matricNumber = matricNum;
    }

    /**
     * Checks whether the entire string matches the required matriculation number format.
     * Whitespace and lowercase letters are rejected.
     *
     * @param test Text to validate.
     * @return True if the text contains uppercase A, exactly seven ASCII digits, and one uppercase letter A-Z.
     * @throws NullPointerException If test is null.
     */
    public static boolean isValidMatricNumber(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns the validated matriculation number unchanged.
     *
     * @return The matriculation number text.
     */
    @Override
    public String toString() {
        return matricNumber;
    }

    /**
     * Compares matriculation numbers by their validated text.
     *
     * @param other Object to compare with this matriculation number.
     * @return True if the other object is a matriculation number with the same text.
     */
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

    /**
     * Returns a hash code based on the matriculation number text.
     *
     * @return Hash code consistent with value equality.
     */
    @Override
    public int hashCode() {
        return matricNumber.hashCode();
    }
}

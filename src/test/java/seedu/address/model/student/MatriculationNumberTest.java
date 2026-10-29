package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests matriculation number construction and strict uppercase format validation. */
public class MatriculationNumberTest {
    private static final List<String> VALID_NUMBERS = List.of("A0000000A", "A1234567B", "A9999999Z");
    private static final List<String> INVALID_NUMBERS = List.of(
            "", " ", "1234567", "A123456B", "A12345678B", "B1234567A", "a1234567B", "A1234567b",
            "A1234567", "1234567B", "A12345678", "A1234567!", "A123X567B", " A1234567B", "A1234567B ",
            "A123 567B", "A1234567B\n", "A１２３４５６７B");

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MatriculationNumber(null));
    }

    @Test
    public void constructor_validNumbers_preservesValue() {
        for (String number : VALID_NUMBERS) {
            MatriculationNumber matriculationNumber = new MatriculationNumber(number);
            assertEquals(number, matriculationNumber.matricNumber);
            assertEquals(number, matriculationNumber.toString());
        }
    }

    @Test
    public void constructor_invalidNumbers_throwsIllegalArgumentException() {
        for (String number : INVALID_NUMBERS) {
            assertThrows(IllegalArgumentException.class, MatriculationNumber.MESSAGE_CONSTRAINTS, () ->
                    new MatriculationNumber(number));
        }
    }

    @Test
    public void isValidMatricNumber_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> MatriculationNumber.isValidMatricNumber(null));
    }

    @Test
    public void isValidMatricNumber_validNumbersAndAllUppercaseSuffixes_returnsTrue() {
        for (String number : VALID_NUMBERS) {
            assertTrue(MatriculationNumber.isValidMatricNumber(number));
        }
        for (char suffix = 'A'; suffix <= 'Z'; suffix++) {
            assertTrue(MatriculationNumber.isValidMatricNumber("A1234567" + suffix));
        }
    }

    @Test
    public void isValidMatricNumber_invalidNumbers_returnsFalse() {
        for (String number : INVALID_NUMBERS) {
            assertFalse(MatriculationNumber.isValidMatricNumber(number));
        }
    }
}

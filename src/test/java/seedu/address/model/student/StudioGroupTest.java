package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests studio group construction, validation, and value equality.
 */
public class StudioGroupTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new StudioGroup(null));
    }

    @Test
    public void constructor_invalidStudioGroup_throwsIllegalArgumentException() {
        String invalidStudioGroup = "";
        assertThrows(IllegalArgumentException.class, () -> new StudioGroup(invalidStudioGroup));
    }

    @Test
    public void isValidStudioGroup_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> StudioGroup.isValidStudioGroup(null));
    }

    @Test
    public void isValidStudioGroup_invalidValues_returnsFalse() {
        assertFalse(StudioGroup.isValidStudioGroup("")); // empty string
        assertFalse(StudioGroup.isValidStudioGroup(" ")); // spaces only
        assertFalse(StudioGroup.isValidStudioGroup("A1")); // reversed order
        assertFalse(StudioGroup.isValidStudioGroup("1a")); // lowercase letter
        assertFalse(StudioGroup.isValidStudioGroup("1")); // missing letter
        assertFalse(StudioGroup.isValidStudioGroup("A")); // missing number
        assertFalse(StudioGroup.isValidStudioGroup("1AB")); // multiple letters
        assertFalse(StudioGroup.isValidStudioGroup("1A!")); // punctuation
        assertFalse(StudioGroup.isValidStudioGroup("1 A")); // internal space
        assertFalse(StudioGroup.isValidStudioGroup(" 1A")); // leading space
        assertFalse(StudioGroup.isValidStudioGroup("1A ")); // trailing space
    }

    @Test
    public void isValidStudioGroup_validValues_returnsTrue() {
        assertTrue(StudioGroup.isValidStudioGroup("1A"));
        assertTrue(StudioGroup.isValidStudioGroup("10M")); // multiple digits
        assertTrue(StudioGroup.isValidStudioGroup("99Z"));
    }

    @Test
    public void equals_variousComparisons_returnsExpectedResult() {
        StudioGroup studioGroup = new StudioGroup("1A");

        // same values -> returns true
        assertTrue(studioGroup.equals(new StudioGroup("1A")));

        // same object -> returns true
        assertTrue(studioGroup.equals(studioGroup));

        // null -> returns false
        assertFalse(studioGroup.equals(null));

        // different types -> returns false
        assertFalse(studioGroup.equals(5.0f));

        // different values -> returns false
        assertFalse(studioGroup.equals(new StudioGroup("2B")));
    }
}

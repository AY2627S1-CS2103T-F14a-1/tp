package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Tests studio effort scores and record equality.
 */
public class StudioRecordTest {

    @Test
    public void constructor_validScore_storesEffortScore() {
        int[] effortScores = {0, 1, 100, Integer.MAX_VALUE - 1, Integer.MAX_VALUE};
        for (int effortScore : effortScores) {
            assertEquals(effortScore, new StudioRecord(effortScore).getEffortScore());
        }
    }

    @Test
    public void constructor_invalidScore_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new StudioRecord(-1));
    }

    @Test
    public void isValidEffortScore() {
        // invalid effort scores
        int[] effortScores = {0, 1, 100, Integer.MAX_VALUE - 1, Integer.MAX_VALUE};
        for (int effortScore : effortScores) {
            assertTrue(StudioRecord.isValidEffortScore(effortScore));
        }

        // valid effort scores
        assertFalse(StudioRecord.isValidEffortScore(-1));
    }

    @Test
    public void equals() {
        StudioRecord studioRecord = new StudioRecord(50);

        // same values -> returns true
        assertTrue(studioRecord.equals(new StudioRecord(50)));

        // same object -> returns true
        assertTrue(studioRecord.equals(studioRecord));

        // null -> returns false
        assertFalse(studioRecord.equals(null));

        // different types -> returns false
        assertFalse(studioRecord.equals("50"));

        // different values -> returns false
        assertFalse(studioRecord.equals(new StudioRecord(0)));
    }

    @Test
    public void hashCode_equalScores_returnsSameHashCode() {
        int[] effortScores = {0, 50, Integer.MAX_VALUE};
        for (int effortScore : effortScores) {
            assertEquals(new StudioRecord(effortScore).hashCode(), new StudioRecord(effortScore).hashCode());
        }
    }

    @Test
    public void hashCode_hashSetLookup_respectsEquality() {
        Set<StudioRecord> records = new HashSet<>();
        records.add(new StudioRecord(50));

        assertTrue(records.contains(new StudioRecord(50)));
        assertFalse(records.contains(new StudioRecord(100)));
        assertFalse(records.add(new StudioRecord(50)));
        assertEquals(1, records.size());
    }

    @Test
    public void toString_validScore_returnsEffortScore() {
        assertEquals("E: 0", new StudioRecord(0).toString());
        assertEquals("E: 50", new StudioRecord(50).toString());
    }
}

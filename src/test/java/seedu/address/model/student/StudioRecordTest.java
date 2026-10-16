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
    public void constructor_validScores_storesEffortScore() {
        int[] effortScores = {0, 1, 100, Integer.MAX_VALUE};
        for (int effortScore : effortScores) {
            assertEquals(effortScore, new StudioRecord(effortScore).getEffortScore());
        }
    }

    @Test
    public void constructor_negativeScore_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new StudioRecord(-1));
        assertThrows(IllegalArgumentException.class, () -> new StudioRecord(Integer.MIN_VALUE));
    }

    @Test
    public void isValidEffortScore_boundaryValues_returnsExpectedValidity() {
        assertTrue(StudioRecord.isValidEffortScore(0));
        assertTrue(StudioRecord.isValidEffortScore(Integer.MAX_VALUE));
        assertFalse(StudioRecord.isValidEffortScore(-1));
    }

    @Test
    public void equals_sameAndDifferentScores_returnsExpectedEquality() {
        StudioRecord studioRecord = new StudioRecord(50);

        assertTrue(studioRecord.equals(studioRecord));
        assertTrue(studioRecord.equals(new StudioRecord(50)));
        assertFalse(studioRecord.equals(new StudioRecord(0)));
        assertFalse(studioRecord.equals(null));
        assertFalse(studioRecord.equals("50"));
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
    public void toString_validScore_returnsParticipationScore() {
        assertEquals("P: 0", new StudioRecord(0).toString());
        assertEquals("P: 50", new StudioRecord(50).toString());
    }
}

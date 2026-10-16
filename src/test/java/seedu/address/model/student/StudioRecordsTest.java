package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Tests the behavior of {@link StudioRecords}.
 */
public class StudioRecordsTest {

    private static final StudioRecord ZERO_EFFORT_RECORD = new StudioRecord(0);
    private static final StudioRecord PARTIAL_EFFORT_RECORD = new StudioRecord(50);
    private static final StudioRecord FULL_EFFORT_RECORD = new StudioRecord(100);

    @Test
    public void constructor_noArguments_createsEmptyStudioRecords() {
        StudioRecords studioRecords = new StudioRecords();

        assertTrue(studioRecords.getRecordsByWeek().isEmpty());
    }

    @Test
    public void constructor_validRecords_storesAllRecords() {
        Map<Integer, StudioRecord> records = Map.of(
                StudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD,
                StudioRecords.MAX_WEEK, FULL_EFFORT_RECORD);

        StudioRecords studioRecords = new StudioRecords(records);

        assertEquals(records, studioRecords.getRecordsByWeek());
    }

    @Test
    public void constructor_invalidWeek_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new StudioRecords(
                Map.of(StudioRecords.MIN_WEEK - 1, ZERO_EFFORT_RECORD)));
        assertThrows(IllegalArgumentException.class, () -> new StudioRecords(
                Map.of(StudioRecords.MAX_WEEK + 1, ZERO_EFFORT_RECORD)));
    }

    @Test
    public void constructor_nullMap_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new StudioRecords(null));
    }

    @Test
    public void constructor_nullWeek_throwsNullPointerException() {
        Map<Integer, StudioRecord> records = new HashMap<>();
        records.put(null, ZERO_EFFORT_RECORD);

        assertThrows(NullPointerException.class, () -> new StudioRecords(records));
    }

    @Test
    public void constructor_nullStudioRecord_throwsNullPointerException() {
        Map<Integer, StudioRecord> records = new HashMap<>();
        records.put(StudioRecords.MIN_WEEK, null);

        assertThrows(NullPointerException.class, () -> new StudioRecords(records));
    }

    @Test
    public void constructor_inputMapModified_preservesOriginalRecords() {
        Map<Integer, StudioRecord> records = new HashMap<>();
        records.put(StudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD);
        StudioRecords studioRecords = new StudioRecords(records);

        records.put(StudioRecords.MAX_WEEK, FULL_EFFORT_RECORD);

        assertEquals(Map.of(StudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD),
                studioRecords.getRecordsByWeek());
    }

    @Test
    public void withStudioRecord_newWeek_returnsUpdatedCopy() {
        StudioRecords original = new StudioRecords(
                Map.of(StudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD));

        StudioRecords updated = original.withStudioRecord(
                StudioRecords.MAX_WEEK, FULL_EFFORT_RECORD);

        assertEquals(Map.of(StudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD),
                original.getRecordsByWeek());
        assertEquals(Map.of(
                StudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD,
                StudioRecords.MAX_WEEK, FULL_EFFORT_RECORD),
                updated.getRecordsByWeek());
    }

    @Test
    public void withStudioRecord_existingWeek_returnsSameInstance() {
        StudioRecords original = new StudioRecords(
                Map.of(StudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD));

        StudioRecords updated = original.withStudioRecord(
                StudioRecords.MIN_WEEK, FULL_EFFORT_RECORD);

        assertSame(original, updated);
        assertEquals(ZERO_EFFORT_RECORD,
                updated.getRecordsByWeek().get(StudioRecords.MIN_WEEK));
    }

    @Test
    public void withStudioRecord_nullRecord_throwsNullPointerException() {
        StudioRecords studioRecords = new StudioRecords();

        assertThrows(NullPointerException.class, () ->
                studioRecords.withStudioRecord(StudioRecords.MIN_WEEK, null));
    }

    @Test
    public void withStudioRecord_invalidWeek_throwsIllegalArgumentException() {
        StudioRecords studioRecords = new StudioRecords();

        assertThrows(IllegalArgumentException.class, () -> studioRecords.withStudioRecord(
                StudioRecords.MIN_WEEK - 1, ZERO_EFFORT_RECORD));
        assertThrows(IllegalArgumentException.class, () -> studioRecords.withStudioRecord(
                StudioRecords.MAX_WEEK + 1, ZERO_EFFORT_RECORD));
    }

    @Test
    public void withoutStudioRecord_existingWeek_returnsUpdatedCopy() {
        StudioRecords original = new StudioRecords(Map.of(
                StudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD,
                StudioRecords.MAX_WEEK, FULL_EFFORT_RECORD));

        StudioRecords updated = original.withoutStudioRecord(
                StudioRecords.MIN_WEEK);

        assertEquals(Map.of(
                StudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD,
                StudioRecords.MAX_WEEK, FULL_EFFORT_RECORD),
                original.getRecordsByWeek());
        assertEquals(Map.of(StudioRecords.MAX_WEEK, FULL_EFFORT_RECORD),
                updated.getRecordsByWeek());
    }

    @Test
    public void withoutStudioRecord_missingWeek_returnsSameInstance() {
        StudioRecords studioRecords = new StudioRecords(
                Map.of(StudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD));

        StudioRecords updated = studioRecords.withoutStudioRecord(
                StudioRecords.MAX_WEEK);

        assertSame(studioRecords, updated);
    }

    @Test
    public void withoutStudioRecord_invalidWeek_throwsIllegalArgumentException() {
        StudioRecords studioRecords = new StudioRecords();

        assertThrows(IllegalArgumentException.class, () -> studioRecords.withoutStudioRecord(
                StudioRecords.MIN_WEEK - 1));
        assertThrows(IllegalArgumentException.class, () -> studioRecords.withoutStudioRecord(
                StudioRecords.MAX_WEEK + 1));
    }

    @Test
    public void hasStudioRecord_recordExists_returnsTrue() {
        StudioRecords studioRecords = new StudioRecords(
                Map.of(StudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD));

        assertTrue(studioRecords.hasStudioRecord(StudioRecords.MIN_WEEK));
    }

    @Test
    public void hasStudioRecord_recordDoesNotExist_returnsFalse() {
        StudioRecords studioRecords = new StudioRecords(
                Map.of(StudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD));

        assertFalse(studioRecords.hasStudioRecord(StudioRecords.MAX_WEEK));
    }

    @Test
    public void hasStudioRecord_invalidWeek_throwsIllegalArgumentException() {
        StudioRecords studioRecords = new StudioRecords();

        assertThrows(IllegalArgumentException.class, () ->
                studioRecords.hasStudioRecord(StudioRecords.MIN_WEEK - 1));
        assertThrows(IllegalArgumentException.class, () ->
                studioRecords.hasStudioRecord(StudioRecords.MAX_WEEK + 1));
    }

    @Test
    public void getRecordsByWeek_modifyMap_throwsUnsupportedOperationException() {
        StudioRecords studioRecords = new StudioRecords(
                Map.of(StudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD));

        assertThrows(UnsupportedOperationException.class, () ->
                studioRecords.getRecordsByWeek().put(StudioRecords.MAX_WEEK, FULL_EFFORT_RECORD));
        assertEquals(Map.of(StudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD),
                studioRecords.getRecordsByWeek());
    }

    @Test
    public void isValidWeek_boundaryWeeks_returnsTrue() {
        assertTrue(StudioRecords.isValidWeek(StudioRecords.MIN_WEEK));
        assertTrue(StudioRecords.isValidWeek(StudioRecords.MAX_WEEK));
    }

    @Test
    public void isValidWeek_outOfRangeWeeks_returnsFalse() {
        assertFalse(StudioRecords.isValidWeek(StudioRecords.MIN_WEEK - 1));
        assertFalse(StudioRecords.isValidWeek(StudioRecords.MAX_WEEK + 1));
    }

    @Test
    public void equals_sameRecords_returnsTrue() {
        StudioRecords firstStudioRecords = new StudioRecords(
                Map.of(StudioRecords.MIN_WEEK, PARTIAL_EFFORT_RECORD));
        StudioRecords secondStudioRecords = new StudioRecords(
                Map.of(StudioRecords.MIN_WEEK, new StudioRecord(50)));

        assertEquals(firstStudioRecords, secondStudioRecords);
    }

    @Test
    public void equals_differentRecords_returnsFalse() {
        StudioRecords studioRecords = new StudioRecords(
                Map.of(StudioRecords.MIN_WEEK, PARTIAL_EFFORT_RECORD));
        StudioRecords differentWeek = new StudioRecords(
                Map.of(StudioRecords.MAX_WEEK, PARTIAL_EFFORT_RECORD));
        StudioRecords differentEffort = new StudioRecords(
                Map.of(StudioRecords.MIN_WEEK, FULL_EFFORT_RECORD));

        assertNotEquals(studioRecords, differentWeek);
        assertNotEquals(studioRecords, differentEffort);
        assertNotEquals(studioRecords, null);
        assertNotEquals(studioRecords, Map.of(StudioRecords.MIN_WEEK, PARTIAL_EFFORT_RECORD));
    }

    @Test
    public void toString_recordsAndMissingWeeks_returnsCompleteWeeklySummary() {
        StudioRecords studioRecords = new StudioRecords(Map.of(
                3, new StudioRecord(80),
                4, new StudioRecord(25),
                13, FULL_EFFORT_RECORD));

        String expected = "3: A | 80, 4: A | 25, 5: X | 0, 6: X | 0, 7: X | 0, "
                + "8: X | 0, 9: X | 0, 10: X | 0, 11: X | 0, 12: X | 0, 13: A | 100";

        assertEquals(expected, studioRecords.toString());
    }
}

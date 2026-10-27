package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Tests the behavior of {@link RegisteredStudioRecords}.
 */
public class RegisteredStudioRecordsTest {

    private static final StudioRecord ZERO_EFFORT_RECORD = new StudioRecord(0);
    private static final StudioRecord PARTIAL_EFFORT_RECORD = new StudioRecord(50);
    private static final StudioRecord FULL_EFFORT_RECORD = new StudioRecord(100);

    @Test
    public void constructor_noArguments_createsEmptyRegisteredStudioRecords() {
        RegisteredStudioRecords registeredStudioRecords = new RegisteredStudioRecords();

        assertTrue(registeredStudioRecords.getRecordsByWeek().isEmpty());
    }

    @Test
    public void constructor_validRecords_storesAllRecords() {
        Map<Integer, StudioRecord> records = Map.of(
                RegisteredStudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD,
                RegisteredStudioRecords.MAX_WEEK, FULL_EFFORT_RECORD);

        RegisteredStudioRecords registeredStudioRecords = new RegisteredStudioRecords(records);

        assertEquals(records, registeredStudioRecords.getRecordsByWeek());
    }

    @Test
    public void constructor_invalidWeek_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new RegisteredStudioRecords(
                Map.of(RegisteredStudioRecords.MIN_WEEK - 1, ZERO_EFFORT_RECORD)));
        assertThrows(IllegalArgumentException.class, () -> new RegisteredStudioRecords(
                Map.of(RegisteredStudioRecords.MAX_WEEK + 1, ZERO_EFFORT_RECORD)));
    }

    @Test
    public void constructor_nullMap_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RegisteredStudioRecords(null));
    }

    @Test
    public void constructor_nullWeek_throwsNullPointerException() {
        Map<Integer, StudioRecord> records = new HashMap<>();
        records.put(null, ZERO_EFFORT_RECORD);

        assertThrows(NullPointerException.class, () -> new RegisteredStudioRecords(records));
    }

    @Test
    public void constructor_nullStudioRecord_throwsNullPointerException() {
        Map<Integer, StudioRecord> records = new HashMap<>();
        records.put(RegisteredStudioRecords.MIN_WEEK, null);

        assertThrows(NullPointerException.class, () -> new RegisteredStudioRecords(records));
    }

    @Test
    public void constructor_inputMapModified_preservesOriginalRecords() {
        Map<Integer, StudioRecord> records = new HashMap<>();
        records.put(RegisteredStudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD);
        RegisteredStudioRecords registeredStudioRecords = new RegisteredStudioRecords(records);

        records.put(RegisteredStudioRecords.MAX_WEEK, FULL_EFFORT_RECORD);

        assertEquals(Map.of(RegisteredStudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD),
                registeredStudioRecords.getRecordsByWeek());
    }

    @Test
    public void withStudioRecord_newWeek_returnsUpdatedCopy() {
        RegisteredStudioRecords original = new RegisteredStudioRecords(
                Map.of(RegisteredStudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD));

        RegisteredStudioRecords updated = original.withStudioRecord(
                RegisteredStudioRecords.MAX_WEEK, FULL_EFFORT_RECORD);

        assertEquals(Map.of(RegisteredStudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD),
                original.getRecordsByWeek());
        assertEquals(Map.of(
                RegisteredStudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD,
                RegisteredStudioRecords.MAX_WEEK, FULL_EFFORT_RECORD),
                updated.getRecordsByWeek());
    }

    @Test
    public void withStudioRecord_existingWeek_returnsSameInstance() {
        RegisteredStudioRecords original = new RegisteredStudioRecords(
                Map.of(RegisteredStudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD));

        RegisteredStudioRecords updated = original.withStudioRecord(
                RegisteredStudioRecords.MIN_WEEK, FULL_EFFORT_RECORD);

        assertSame(original, updated);
        assertEquals(ZERO_EFFORT_RECORD,
                updated.getRecordsByWeek().get(RegisteredStudioRecords.MIN_WEEK));
    }

    @Test
    public void withStudioRecord_nullRecord_throwsNullPointerException() {
        RegisteredStudioRecords registeredStudioRecords = new RegisteredStudioRecords();

        assertThrows(NullPointerException.class, () ->
                registeredStudioRecords.withStudioRecord(RegisteredStudioRecords.MIN_WEEK, null));
    }

    @Test
    public void withStudioRecord_invalidWeek_throwsIllegalArgumentException() {
        RegisteredStudioRecords registeredStudioRecords = new RegisteredStudioRecords();

        assertThrows(IllegalArgumentException.class, () -> registeredStudioRecords.withStudioRecord(
                RegisteredStudioRecords.MIN_WEEK - 1, ZERO_EFFORT_RECORD));
        assertThrows(IllegalArgumentException.class, () -> registeredStudioRecords.withStudioRecord(
                RegisteredStudioRecords.MAX_WEEK + 1, ZERO_EFFORT_RECORD));
    }

    @Test
    public void withoutStudioRecord_existingWeek_returnsUpdatedCopy() {
        RegisteredStudioRecords original = new RegisteredStudioRecords(Map.of(
                RegisteredStudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD,
                RegisteredStudioRecords.MAX_WEEK, FULL_EFFORT_RECORD));

        RegisteredStudioRecords updated = original.withoutStudioRecord(
                RegisteredStudioRecords.MIN_WEEK);

        assertEquals(Map.of(
                RegisteredStudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD,
                RegisteredStudioRecords.MAX_WEEK, FULL_EFFORT_RECORD),
                original.getRecordsByWeek());
        assertEquals(Map.of(RegisteredStudioRecords.MAX_WEEK, FULL_EFFORT_RECORD),
                updated.getRecordsByWeek());
    }

    @Test
    public void withoutStudioRecord_missingWeek_returnsSameInstance() {
        RegisteredStudioRecords registeredStudioRecords = new RegisteredStudioRecords(
                Map.of(RegisteredStudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD));

        RegisteredStudioRecords updated = registeredStudioRecords.withoutStudioRecord(
                RegisteredStudioRecords.MAX_WEEK);

        assertSame(registeredStudioRecords, updated);
    }

    @Test
    public void withoutStudioRecord_invalidWeek_throwsIllegalArgumentException() {
        RegisteredStudioRecords registeredStudioRecords = new RegisteredStudioRecords();

        assertThrows(IllegalArgumentException.class, () -> registeredStudioRecords.withoutStudioRecord(
                RegisteredStudioRecords.MIN_WEEK - 1));
        assertThrows(IllegalArgumentException.class, () -> registeredStudioRecords.withoutStudioRecord(
                RegisteredStudioRecords.MAX_WEEK + 1));
    }

    @Test
    public void hasStudioRecord_recordExists_returnsTrue() {
        RegisteredStudioRecords registeredStudioRecords = new RegisteredStudioRecords(
                Map.of(RegisteredStudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD));

        assertTrue(registeredStudioRecords.hasStudioRecord(RegisteredStudioRecords.MIN_WEEK));
    }

    @Test
    public void hasStudioRecord_recordDoesNotExist_returnsFalse() {
        RegisteredStudioRecords registeredStudioRecords = new RegisteredStudioRecords(
                Map.of(RegisteredStudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD));

        assertFalse(registeredStudioRecords.hasStudioRecord(RegisteredStudioRecords.MAX_WEEK));
    }

    @Test
    public void hasStudioRecord_invalidWeek_throwsIllegalArgumentException() {
        RegisteredStudioRecords registeredStudioRecords = new RegisteredStudioRecords();

        assertThrows(IllegalArgumentException.class, () ->
                registeredStudioRecords.hasStudioRecord(RegisteredStudioRecords.MIN_WEEK - 1));
        assertThrows(IllegalArgumentException.class, () ->
                registeredStudioRecords.hasStudioRecord(RegisteredStudioRecords.MAX_WEEK + 1));
    }

    @Test
    public void studioRecordMatches_matchingRecord_returnsTrue() {
        RegisteredStudioRecords registeredStudioRecords = new RegisteredStudioRecords(
                Map.of(RegisteredStudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD));

        assertTrue(registeredStudioRecords.studioRecordMatches(
                RegisteredStudioRecords.MIN_WEEK, new StudioRecord(0)));
    }

    @Test
    public void studioRecordMatches_differentRecord_returnsFalse() {
        RegisteredStudioRecords registeredStudioRecords = new RegisteredStudioRecords(
                Map.of(RegisteredStudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD));

        assertFalse(registeredStudioRecords.studioRecordMatches(
                RegisteredStudioRecords.MIN_WEEK, FULL_EFFORT_RECORD));
    }

    @Test
    public void studioRecordMatches_missingWeek_returnsFalse() {
        RegisteredStudioRecords registeredStudioRecords = new RegisteredStudioRecords(
                Map.of(RegisteredStudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD));

        assertFalse(registeredStudioRecords.studioRecordMatches(
                RegisteredStudioRecords.MAX_WEEK, ZERO_EFFORT_RECORD));
    }

    @Test
    public void editStudioRecord_weekExists_returnsNewRegisteredStudentRecords() {
        RegisteredStudioRecords registeredStudioRecords = new RegisteredStudioRecords()
                .withStudioRecord(RegisteredStudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD);
        RegisteredStudioRecords updated = registeredStudioRecords.editStudioRecord(
                RegisteredStudioRecords.MIN_WEEK, FULL_EFFORT_RECORD
        );
        assertEquals(updated.getRecordsByWeek(), Map.of(
                RegisteredStudioRecords.MIN_WEEK, FULL_EFFORT_RECORD
        ));
    }

    @Test
    public void editStudioRecord_weekDoesNotExist_returnsSameRegisteredStudentRecords() {
        RegisteredStudioRecords registeredStudioRecords = new RegisteredStudioRecords();
        RegisteredStudioRecords updated = registeredStudioRecords.editStudioRecord(
                RegisteredStudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD
        );
        assertEquals(updated.getRecordsByWeek(), Map.of());
    }

    @Test
    public void editStudioRecord_sameStudioRecord_returnsSameRegisteredStudentRecords() {
        RegisteredStudioRecords registeredStudioRecords = new RegisteredStudioRecords()
                .withStudioRecord(RegisteredStudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD);
        RegisteredStudioRecords updated = registeredStudioRecords.editStudioRecord(
                RegisteredStudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD
        );
        assertEquals(updated.getRecordsByWeek(), Map.of(
                RegisteredStudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD
        ));
    }

    @Test
    public void getRecordsByWeek_modifyMap_throwsUnsupportedOperationException() {
        RegisteredStudioRecords registeredStudioRecords = new RegisteredStudioRecords(
                Map.of(RegisteredStudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD));

        assertThrows(UnsupportedOperationException.class, () ->
                registeredStudioRecords.getRecordsByWeek().put(RegisteredStudioRecords.MAX_WEEK, FULL_EFFORT_RECORD));
        assertEquals(Map.of(RegisteredStudioRecords.MIN_WEEK, ZERO_EFFORT_RECORD),
                registeredStudioRecords.getRecordsByWeek());
    }

    @Test
    public void isValidWeek_boundaryWeeks_returnsTrue() {
        assertTrue(RegisteredStudioRecords.isValidWeek(RegisteredStudioRecords.MIN_WEEK));
        assertTrue(RegisteredStudioRecords.isValidWeek(RegisteredStudioRecords.MAX_WEEK));
    }

    @Test
    public void isValidWeek_outOfRangeWeeks_returnsFalse() {
        assertFalse(RegisteredStudioRecords.isValidWeek(RegisteredStudioRecords.MIN_WEEK - 1));
        assertFalse(RegisteredStudioRecords.isValidWeek(RegisteredStudioRecords.MAX_WEEK + 1));
    }

    @Test
    public void equals_sameRecords_returnsTrue() {
        RegisteredStudioRecords firstRegisteredStudioRecords = new RegisteredStudioRecords(
                Map.of(RegisteredStudioRecords.MIN_WEEK, PARTIAL_EFFORT_RECORD));
        RegisteredStudioRecords secondRegisteredStudioRecords = new RegisteredStudioRecords(
                Map.of(RegisteredStudioRecords.MIN_WEEK, new StudioRecord(50)));

        assertEquals(firstRegisteredStudioRecords, secondRegisteredStudioRecords);
    }

    @Test
    public void equals_differentRecords_returnsFalse() {
        RegisteredStudioRecords registeredStudioRecords = new RegisteredStudioRecords(
                Map.of(RegisteredStudioRecords.MIN_WEEK, PARTIAL_EFFORT_RECORD));
        RegisteredStudioRecords differentWeek = new RegisteredStudioRecords(
                Map.of(RegisteredStudioRecords.MAX_WEEK, PARTIAL_EFFORT_RECORD));
        RegisteredStudioRecords differentEffort = new RegisteredStudioRecords(
                Map.of(RegisteredStudioRecords.MIN_WEEK, FULL_EFFORT_RECORD));

        assertNotEquals(registeredStudioRecords, differentWeek);
        assertNotEquals(registeredStudioRecords, differentEffort);
        assertNotEquals(registeredStudioRecords, null);
        assertNotEquals(registeredStudioRecords, Map.of(RegisteredStudioRecords.MIN_WEEK, PARTIAL_EFFORT_RECORD));
    }

    @Test
    public void hashCode_emptyRecords_returnsSameHashCode() {
        assertEquals(new RegisteredStudioRecords().hashCode(), new RegisteredStudioRecords(Map.of()).hashCode());
    }

    @Test
    public void hashCode_equalRecordsInDifferentOrders_returnsSameHashCode() {
        Map<Integer, StudioRecord> firstRecords = new LinkedHashMap<>();
        firstRecords.put(RegisteredStudioRecords.MIN_WEEK, new StudioRecord(0));
        firstRecords.put(RegisteredStudioRecords.MAX_WEEK, new StudioRecord(Integer.MAX_VALUE));
        Map<Integer, StudioRecord> secondRecords = new LinkedHashMap<>();
        secondRecords.put(RegisteredStudioRecords.MAX_WEEK, new StudioRecord(Integer.MAX_VALUE));
        secondRecords.put(RegisteredStudioRecords.MIN_WEEK, new StudioRecord(0));
        RegisteredStudioRecords first = new RegisteredStudioRecords(firstRecords);
        RegisteredStudioRecords second = new RegisteredStudioRecords(secondRecords);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void hashCode_hashSetLookup_respectsEquality() {
        Set<RegisteredStudioRecords> records = new HashSet<>();
        records.add(new RegisteredStudioRecords(Map.of(RegisteredStudioRecords.MIN_WEEK, new StudioRecord(50))));

        assertTrue(records.contains(new RegisteredStudioRecords(
                Map.of(RegisteredStudioRecords.MIN_WEEK, new StudioRecord(50)))));
        assertFalse(records.contains(new RegisteredStudioRecords(
                Map.of(RegisteredStudioRecords.MIN_WEEK, new StudioRecord(100)))));
        assertFalse(records.contains(new RegisteredStudioRecords(
                Map.of(RegisteredStudioRecords.MAX_WEEK, new StudioRecord(50)))));
        assertFalse(records.add(new RegisteredStudioRecords(
                Map.of(RegisteredStudioRecords.MIN_WEEK, new StudioRecord(50)))));
        assertEquals(1, records.size());
    }

    @Test
    public void toString_recordsAndMissingWeeks_returnsCompleteWeeklySummary() {
        RegisteredStudioRecords registeredStudioRecords = new RegisteredStudioRecords(Map.of(
                3, new StudioRecord(80),
                4, new StudioRecord(25),
                13, FULL_EFFORT_RECORD));

        String expected = "0: X | 0, 1: X | 0, 2: X | 0, 3: A | 80, 4: A | 25, 5: X | 0, 6: X | 0, 7: X | 0, "
                + "8: X | 0, 9: X | 0, 10: X | 0, 11: X | 0, 12: X | 0, 13: A | 100";

        assertEquals(expected, registeredStudioRecords.toString());
    }
}

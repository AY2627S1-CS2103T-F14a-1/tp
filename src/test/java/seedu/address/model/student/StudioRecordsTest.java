package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

public class StudioRecordsTest {

    private StudioRecords studioRecords = new StudioRecords();

    @Test
    public void constructor_noArguments_createsEmptyAttendance() {
        StudioRecords studioRecords = new StudioRecords();

        assertTrue(studioRecords.getAttendedWeeks().isEmpty());
    }

    @Test
    public void constructor_validWeeks_recordsAttendance() {
        for (int i = StudioRecords.MIN_WEEK; i <= StudioRecords.MAX_WEEK; i++) {
            StudioRecords studioRecords = new StudioRecords(Set.of(i));
            assertEquals(Set.of(i), studioRecords.getAttendedWeeks());
        }

        // multiple valid weeks
        Set<Integer> numbers = IntStream.rangeClosed(StudioRecords.MIN_WEEK, StudioRecords.MAX_WEEK)
                .boxed()
                .collect(Collectors.toSet());
        assertEquals(numbers, new StudioRecords(numbers).getAttendedWeeks());
    }

    @Test
    public void constructor_invalidWeeks_throwsIllegalArgumentException() {

        for (int i = -1; i < StudioRecords.MIN_WEEK; i++) {
            int week = i;
            assertThrows(IllegalArgumentException.class, () ->
                    new StudioRecords(Set.of(week)));
        }
        assertThrows(IllegalArgumentException.class, () ->
                new StudioRecords(Set.of(StudioRecords.MAX_WEEK + 1)));
    }

    @Test
    public void constructor_nullAttendanceWeek_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new StudioRecords(null));
    }

    @Test
    public void constructor_setContainingNull_throwsNullPointerException() {
        Set<Integer> attendedWeeks = new HashSet<>();
        attendedWeeks.add(null);

        assertThrows(NullPointerException.class, () -> new StudioRecords(attendedWeeks));
    }

    @Test
    public void constructor_inputSetModified_preservesRecordedWeeks() {
        Set<Integer> attendedWeeks = new HashSet<>(Set.of(StudioRecords.MIN_WEEK));
        StudioRecords studioRecords = new StudioRecords(attendedWeeks);

        attendedWeeks.add(StudioRecords.MAX_WEEK);

        assertEquals(Set.of(StudioRecords.MIN_WEEK), studioRecords.getAttendedWeeks());
    }

    @Test
    public void withAttendedWeek_newWeek_preservesOriginal() {
        StudioRecords original = new StudioRecords(Set.of(StudioRecords.MIN_WEEK));

        StudioRecords updated = original.withAttendedWeek(StudioRecords.MAX_WEEK);

        assertEquals(Set.of(StudioRecords.MIN_WEEK), original.getAttendedWeeks());
        assertEquals(Set.of(StudioRecords.MIN_WEEK, StudioRecords.MAX_WEEK), updated.getAttendedWeeks());
    }

    @Test
    public void withAttendedWeek_existingWeek_returnsSameInstance() {
        StudioRecords original = new StudioRecords(Set.of(StudioRecords.MIN_WEEK));

        StudioRecords updated = original.withAttendedWeek(StudioRecords.MIN_WEEK);

        assertSame(original, updated);
    }

    @Test
    public void withAttendedWeek_invalidWeek_throwsIllegalArgumentException() {
        StudioRecords original = new StudioRecords(Set.of(StudioRecords.MIN_WEEK));

        for (int i = -1; i < StudioRecords.MIN_WEEK; i++) {
            int week = i;
            IllegalArgumentException invalidWeekException = assertThrows(IllegalArgumentException.class, () ->
                    original.withAttendedWeek(week));
            assertEquals(StudioRecords.MESSAGE_CONSTRAINTS, invalidWeekException.getMessage());
        }

        IllegalArgumentException invalidWeekException = assertThrows(IllegalArgumentException.class, () ->
                original.withAttendedWeek(StudioRecords.MAX_WEEK + 1));
        assertEquals(StudioRecords.MESSAGE_CONSTRAINTS, invalidWeekException.getMessage());
    }

    @Test
    public void withAttendedWeek_invalidWeek_preservesOriginal() {
        StudioRecords original = new StudioRecords(Set.of(3));

        assertThrows(IllegalArgumentException.class, () ->
                original.withAttendedWeek(2));
        assertThrows(IllegalArgumentException.class, () ->
                original.withAttendedWeek(14));

        assertEquals(Set.of(3), original.getAttendedWeeks());
    }

    @Test
    public void withoutAttendance_weekExists_returnsWithoutWeek() {
        StudioRecords studioRecords = new StudioRecords(Set.of(StudioRecords.MIN_WEEK));
        StudioRecords removedStudioRecords = studioRecords.withoutAttendedWeek(StudioRecords.MIN_WEEK);
        assertEquals(removedStudioRecords, new StudioRecords());

        StudioRecords multipleStudioRecords = new StudioRecords(Set.of(StudioRecords.MIN_WEEK, StudioRecords.MAX_WEEK));
        StudioRecords removedOneStudioRecords = multipleStudioRecords.withoutAttendedWeek(StudioRecords.MIN_WEEK);
        assertEquals(removedOneStudioRecords, new StudioRecords(Set.of(StudioRecords.MAX_WEEK)));
    }

    @Test
    public void withoutAttendance_weekNotExists_returnsSameInstance() {
        // deleting a week where no studioRecords was marked at all
        StudioRecords studioRecords = new StudioRecords();
        StudioRecords removedNonexistentStudioRecords = studioRecords.withoutAttendedWeek(StudioRecords.MIN_WEEK);
        assertSame(studioRecords, removedNonexistentStudioRecords);

        // deleting a week where studioRecords was not marked for that week
        StudioRecords populatedStudioRecords = new StudioRecords(Set.of(StudioRecords.MIN_WEEK));
        StudioRecords removedNonexistentStudioRecords2 = populatedStudioRecords.withoutAttendedWeek(StudioRecords.MAX_WEEK);
        assertSame(populatedStudioRecords, removedNonexistentStudioRecords2);
    }

    @Test
    public void withoutAttendedWeek_invalidWeek_throwsIllegalArgumentException() {
        StudioRecords populatedStudioRecords2 = new StudioRecords(Set.of(StudioRecords.MIN_WEEK, StudioRecords.MAX_WEEK));
        assertThrows(IllegalArgumentException.class, () -> populatedStudioRecords2.withoutAttendedWeek(-1));
    }

    @Test
    public void hasAttendance_hasAttendance_returnsTrue() {
        StudioRecords studioRecords = new StudioRecords();
        StudioRecords withMinStudioRecords = studioRecords.withAttendedWeek(StudioRecords.MIN_WEEK);
        assertTrue(withMinStudioRecords.hasAttendance(StudioRecords.MIN_WEEK));

        StudioRecords studioRecords2 = new StudioRecords();
        StudioRecords withMaxStudioRecords = studioRecords2.withAttendedWeek(StudioRecords.MAX_WEEK);
        assertTrue(withMaxStudioRecords.hasAttendance(StudioRecords.MAX_WEEK));

        StudioRecords withAllStudioRecords = new StudioRecords();
        for (int i = StudioRecords.MIN_WEEK; i <= StudioRecords.MAX_WEEK; i++) {
            withAllStudioRecords = withAllStudioRecords.withAttendedWeek(i);
        }

        for (int i = StudioRecords.MIN_WEEK; i <= StudioRecords.MAX_WEEK; i++) {
            assertTrue(withAllStudioRecords.hasAttendance(i));
        }

        StudioRecords withMultipleSameStudioRecords = withMinStudioRecords.withAttendedWeek(StudioRecords.MIN_WEEK);
        assertTrue(withMultipleSameStudioRecords.hasAttendance(StudioRecords.MIN_WEEK));
    }

    @Test
    public void hasAttendance_noAttendance_returnsFalse() {
        StudioRecords studioRecords = new StudioRecords();
        assertFalse(studioRecords.hasAttendance(StudioRecords.MIN_WEEK));
        assertFalse(studioRecords.hasAttendance(StudioRecords.MAX_WEEK));

        StudioRecords withUnrelatedStudioRecords = studioRecords.withAttendedWeek(StudioRecords.MAX_WEEK);
        assertFalse(withUnrelatedStudioRecords.hasAttendance(StudioRecords.MIN_WEEK));
    }

    @Test
    public void hasAttendance_invalidWeek_exceptionThrown() {
        StudioRecords studioRecords = new StudioRecords();
        try {
            studioRecords.hasAttendance(-1);
            fail();
        } catch (Exception e) {
            assertEquals(StudioRecords.MESSAGE_CONSTRAINTS, e.getMessage());
        }

        try {
            studioRecords.hasAttendance(0);
            fail();
        } catch (Exception e) {
            assertEquals(StudioRecords.MESSAGE_CONSTRAINTS, e.getMessage());
        }

        try {
            studioRecords.hasAttendance(1);
            fail();
        } catch (Exception e) {
            assertEquals(StudioRecords.MESSAGE_CONSTRAINTS, e.getMessage());
        }

        try {
            studioRecords.hasAttendance(2);
            fail();
        } catch (Exception e) {
            assertEquals(StudioRecords.MESSAGE_CONSTRAINTS, e.getMessage());
        }

        try {
            studioRecords.hasAttendance(14);
            fail();
        } catch (Exception e) {
            assertEquals(StudioRecords.MESSAGE_CONSTRAINTS, e.getMessage());
        }
    }

    @Test
    public void getAttendedWeeks_modifySet_throwsUnsupportedOperationException() {
        StudioRecords studioRecords = new StudioRecords(Set.of(3));

        assertThrows(UnsupportedOperationException.class, () ->
                studioRecords.getAttendedWeeks().add(5));

        assertEquals(Set.of(3), studioRecords.getAttendedWeeks());
    }

    @Test
    public void getAttendedWeeks_existingAttendance_returnsRecordedWeeks() {
        StudioRecords withOneStudioRecords = new StudioRecords(Set.of(StudioRecords.MIN_WEEK));
        assertEquals(Set.of(StudioRecords.MIN_WEEK), withOneStudioRecords.getAttendedWeeks());
    }

    @Test
    public void getAttendedWeeks_noAttendance_returnsRecordedWeeks() {
        assertEquals(Set.of(), studioRecords.getAttendedWeeks());
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        StudioRecords studioRecords = new StudioRecords(Set.of(StudioRecords.MIN_WEEK));

        assertTrue(studioRecords.equals(studioRecords));
    }

    @Test
    public void equals_matchingWeeks_returnsTrue() {
        StudioRecords firstStudioRecords = new StudioRecords(Set.of(StudioRecords.MIN_WEEK, StudioRecords.MAX_WEEK));
        StudioRecords secondStudioRecords = new StudioRecords(Set.of(StudioRecords.MIN_WEEK, StudioRecords.MAX_WEEK));

        assertTrue(firstStudioRecords.equals(secondStudioRecords));
        assertNotEquals(firstStudioRecords, new StudioRecords(Set.of(StudioRecords.MIN_WEEK)));
        assertNotEquals(firstStudioRecords, null);
        assertNotEquals(firstStudioRecords, Set.of(3, 5));
    }

    @Test
    public void equals_differentAttendedWeeks_returnsFalse() {
        StudioRecords firstStudioRecords = new StudioRecords(Set.of(StudioRecords.MIN_WEEK));
        StudioRecords secondStudioRecords = new StudioRecords(Set.of(StudioRecords.MAX_WEEK));

        assertFalse(firstStudioRecords.equals(secondStudioRecords));
    }

    @Test
    public void equals_null_returnsFalse() {
        StudioRecords studioRecords = new StudioRecords(Set.of(StudioRecords.MIN_WEEK));

        assertFalse(studioRecords.equals(null));
    }

    @Test
    public void equals_differentType_returnsFalse() {
        StudioRecords studioRecords = new StudioRecords(Set.of(StudioRecords.MIN_WEEK));

        assertFalse(studioRecords.equals(Set.of(StudioRecords.MIN_WEEK)));
    }

    @Test
    public void isValidWeek_validWeek_returnsTrue() {
        StudioRecords studioRecords = new StudioRecords();
        for (int i = StudioRecords.MIN_WEEK; i <= StudioRecords.MAX_WEEK; i++) {
            assertTrue(studioRecords.isValidWeek(i));
        }
    }

    @Test
    public void isValidWeek_invalidWeek_returnsFalse() {
        assertFalse(studioRecords.isValidWeek(-3));

        assertFalse(studioRecords.isValidWeek(0));

        assertFalse(studioRecords.isValidWeek(1));

        assertFalse(studioRecords.isValidWeek(2));

        assertFalse(studioRecords.isValidWeek(14));
    }

    @Test
    public void toString_unorderedWeeks_returnsAscendingOrder() {
        StudioRecords studioRecords = new StudioRecords(Set.of(5, 3, 13));

        assertEquals("[3, 5, 13]", studioRecords.toString());
    }
}

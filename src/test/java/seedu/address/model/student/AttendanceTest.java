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

public class AttendanceTest {

    private Attendance attendance = new Attendance();

    @Test
    public void constructor_noArguments_createsEmptyAttendance() {
        Attendance attendance = new Attendance();

        assertTrue(attendance.getAttendedWeeks().isEmpty());
    }

    @Test
    public void constructor_validWeeks_recordsAttendance() {
        for (int i = Attendance.MIN_WEEK; i <= Attendance.MAX_WEEK; i++) {
            Attendance attendance = new Attendance(Set.of(i));
            assertEquals(Set.of(i), attendance.getAttendedWeeks());
        }

        // multiple valid weeks
        Set<Integer> numbers = IntStream.rangeClosed(Attendance.MIN_WEEK, Attendance.MAX_WEEK)
                .boxed()
                .collect(Collectors.toSet());
        assertEquals(numbers, new Attendance(numbers).getAttendedWeeks());
    }

    @Test
    public void constructor_invalidWeeks_throwsIllegalArgumentException() {

        for (int i = -1; i < Attendance.MIN_WEEK; i++) {
            int week = i;
            assertThrows(IllegalArgumentException.class, () ->
                    new Attendance(Set.of(week)));
        }
        assertThrows(IllegalArgumentException.class, () ->
                new Attendance(Set.of(Attendance.MAX_WEEK + 1)));
    }

    @Test
    public void constructor_nullAttendanceWeek_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Attendance(null));
    }

    @Test
    public void constructor_setContainingNull_throwsNullPointerException() {
        Set<Integer> attendedWeeks = new HashSet<>();
        attendedWeeks.add(null);

        assertThrows(NullPointerException.class, () -> new Attendance(attendedWeeks));
    }

    @Test
    public void constructor_inputSetModified_preservesRecordedWeeks() {
        Set<Integer> attendedWeeks = new HashSet<>(Set.of(Attendance.MIN_WEEK));
        Attendance attendance = new Attendance(attendedWeeks);

        attendedWeeks.add(Attendance.MAX_WEEK);

        assertEquals(Set.of(Attendance.MIN_WEEK), attendance.getAttendedWeeks());
    }

    @Test
    public void withAttendedWeek_newWeek_preservesOriginal() {
        Attendance original = new Attendance(Set.of(Attendance.MIN_WEEK));

        Attendance updated = original.withAttendedWeek(Attendance.MAX_WEEK);

        assertEquals(Set.of(Attendance.MIN_WEEK), original.getAttendedWeeks());
        assertEquals(Set.of(Attendance.MIN_WEEK, Attendance.MAX_WEEK), updated.getAttendedWeeks());
    }

    @Test
    public void withAttendedWeek_existingWeek_returnsSameInstance() {
        Attendance original = new Attendance(Set.of(Attendance.MIN_WEEK));

        Attendance updated = original.withAttendedWeek(Attendance.MIN_WEEK);

        assertSame(original, updated);
    }

    @Test
    public void withAttendedWeek_invalidWeek_throwsIllegalArgumentException() {
        Attendance original = new Attendance(Set.of(Attendance.MIN_WEEK));

        for (int i = -1; i < Attendance.MIN_WEEK; i++) {
            int week = i;
            IllegalArgumentException invalidWeekException = assertThrows(IllegalArgumentException.class, () ->
                    original.withAttendedWeek(week));
            assertEquals(Attendance.MESSAGE_CONSTRAINTS, invalidWeekException.getMessage());
        }

        IllegalArgumentException invalidWeekException = assertThrows(IllegalArgumentException.class, () ->
                original.withAttendedWeek(Attendance.MAX_WEEK + 1));
        assertEquals(Attendance.MESSAGE_CONSTRAINTS, invalidWeekException.getMessage());
    }

    @Test
    public void withAttendedWeek_invalidWeek_preservesOriginal() {
        Attendance original = new Attendance(Set.of(3));

        assertThrows(IllegalArgumentException.class, () ->
                original.withAttendedWeek(2));
        assertThrows(IllegalArgumentException.class, () ->
                original.withAttendedWeek(14));

        assertEquals(Set.of(3), original.getAttendedWeeks());
    }

    @Test
    public void withoutAttendance_weekExists_returnsWithoutWeek() {
        Attendance attendance = new Attendance(Set.of(Attendance.MIN_WEEK));
        Attendance removedAttendance = attendance.withoutAttendedWeek(Attendance.MIN_WEEK);
        assertEquals(removedAttendance, new Attendance());

        Attendance multipleAttendance = new Attendance(Set.of(Attendance.MIN_WEEK, Attendance.MAX_WEEK));
        Attendance removedOneAttendance = multipleAttendance.withoutAttendedWeek(Attendance.MIN_WEEK);
        assertEquals(removedOneAttendance, new Attendance(Set.of(Attendance.MAX_WEEK)));
    }

    @Test
    public void withoutAttendance_weekNotExists_returnsSameInstance() {
        // deleting a week where no attendance was marked at all
        Attendance attendance = new Attendance();
        Attendance removedNonexistentAttendance = attendance.withoutAttendedWeek(Attendance.MIN_WEEK);
        assertSame(attendance, removedNonexistentAttendance);

        // deleting a week where attendance was not marked for that week
        Attendance populatedAttendance = new Attendance(Set.of(Attendance.MIN_WEEK));
        Attendance removedNonexistentAttendance2 = populatedAttendance.withoutAttendedWeek(Attendance.MAX_WEEK);
        assertSame(populatedAttendance, removedNonexistentAttendance2);
    }

    @Test
    public void withoutAttendedWeek_invalidWeek_throwsIllegalArgumentException() {
        Attendance populatedAttendance2 = new Attendance(Set.of(Attendance.MIN_WEEK, Attendance.MAX_WEEK));
        assertThrows(IllegalArgumentException.class, () -> populatedAttendance2.withoutAttendedWeek(-1));
    }

    @Test
    public void hasAttendance_hasAttendance_returnsTrue() {
        Attendance attendance = new Attendance();
        Attendance withMinAttendance = attendance.withAttendedWeek(Attendance.MIN_WEEK);
        assertTrue(withMinAttendance.hasAttendance(Attendance.MIN_WEEK));

        Attendance attendance2 = new Attendance();
        Attendance withMaxAttendance = attendance2.withAttendedWeek(Attendance.MAX_WEEK);
        assertTrue(withMaxAttendance.hasAttendance(Attendance.MAX_WEEK));

        Attendance withAllAttendance = new Attendance();
        for (int i = Attendance.MIN_WEEK; i <= Attendance.MAX_WEEK; i++) {
            withAllAttendance = withAllAttendance.withAttendedWeek(i);
        }

        for (int i = Attendance.MIN_WEEK; i <= Attendance.MAX_WEEK; i++) {
            assertTrue(withAllAttendance.hasAttendance(i));
        }

        Attendance withMultipleSameAttendance = withMinAttendance.withAttendedWeek(Attendance.MIN_WEEK);
        assertTrue(withMultipleSameAttendance.hasAttendance(Attendance.MIN_WEEK));
    }

    @Test
    public void hasAttendance_noAttendance_returnsFalse() {
        Attendance attendance = new Attendance();
        assertFalse(attendance.hasAttendance(Attendance.MIN_WEEK));
        assertFalse(attendance.hasAttendance(Attendance.MAX_WEEK));

        Attendance withUnrelatedAttendance = attendance.withAttendedWeek(Attendance.MAX_WEEK);
        assertFalse(withUnrelatedAttendance.hasAttendance(Attendance.MIN_WEEK));
    }

    @Test
    public void hasAttendance_invalidWeek_exceptionThrown() {
        Attendance attendance = new Attendance();
        try {
            attendance.hasAttendance(-1);
            fail();
        } catch (Exception e) {
            assertEquals(Attendance.MESSAGE_CONSTRAINTS, e.getMessage());
        }

        try {
            attendance.hasAttendance(0);
            fail();
        } catch (Exception e) {
            assertEquals(Attendance.MESSAGE_CONSTRAINTS, e.getMessage());
        }

        try {
            attendance.hasAttendance(1);
            fail();
        } catch (Exception e) {
            assertEquals(Attendance.MESSAGE_CONSTRAINTS, e.getMessage());
        }

        try {
            attendance.hasAttendance(2);
            fail();
        } catch (Exception e) {
            assertEquals(Attendance.MESSAGE_CONSTRAINTS, e.getMessage());
        }

        try {
            attendance.hasAttendance(14);
            fail();
        } catch (Exception e) {
            assertEquals(Attendance.MESSAGE_CONSTRAINTS, e.getMessage());
        }
    }

    @Test
    public void getAttendedWeeks_modifySet_throwsUnsupportedOperationException() {
        Attendance attendance = new Attendance(Set.of(3));

        assertThrows(UnsupportedOperationException.class, () ->
                attendance.getAttendedWeeks().add(5));

        assertEquals(Set.of(3), attendance.getAttendedWeeks());
    }

    @Test
    public void getAttendedWeeks_existingAttendance_returnsRecordedWeeks() {
        Attendance withOneAttendance = new Attendance(Set.of(Attendance.MIN_WEEK));
        assertEquals(Set.of(Attendance.MIN_WEEK), withOneAttendance.getAttendedWeeks());
    }

    @Test
    public void getAttendedWeeks_noAttendance_returnsRecordedWeeks() {
        assertEquals(Set.of(), attendance.getAttendedWeeks());
    }

    @Test
    public void equals_sameObject_returnsTrue() {
        Attendance attendance = new Attendance(Set.of(Attendance.MIN_WEEK));

        assertTrue(attendance.equals(attendance));
    }

    @Test
    public void equals_matchingWeeks_returnsTrue() {
        Attendance firstAttendance = new Attendance(Set.of(Attendance.MIN_WEEK, Attendance.MAX_WEEK));
        Attendance secondAttendance = new Attendance(Set.of(Attendance.MIN_WEEK, Attendance.MAX_WEEK));

        assertTrue(firstAttendance.equals(secondAttendance));
        assertNotEquals(firstAttendance, new Attendance(Set.of(Attendance.MIN_WEEK)));
        assertNotEquals(firstAttendance, null);
        assertNotEquals(firstAttendance, Set.of(3, 5));
    }

    @Test
    public void equals_differentAttendedWeeks_returnsFalse() {
        Attendance firstAttendance = new Attendance(Set.of(Attendance.MIN_WEEK));
        Attendance secondAttendance = new Attendance(Set.of(Attendance.MAX_WEEK));

        assertFalse(firstAttendance.equals(secondAttendance));
    }

    @Test
    public void equals_null_returnsFalse() {
        Attendance attendance = new Attendance(Set.of(Attendance.MIN_WEEK));

        assertFalse(attendance.equals(null));
    }

    @Test
    public void equals_differentType_returnsFalse() {
        Attendance attendance = new Attendance(Set.of(Attendance.MIN_WEEK));

        assertFalse(attendance.equals(Set.of(Attendance.MIN_WEEK)));
    }

    @Test
    public void isValidWeek_validWeek_returnsTrue() {
        Attendance attendance = new Attendance();
        for (int i = Attendance.MIN_WEEK; i <= Attendance.MAX_WEEK; i++) {
            assertTrue(attendance.isValidWeek(i));
        }
    }

    @Test
    public void isValidWeek_invalidWeek_returnsFalse() {
        assertFalse(attendance.isValidWeek(-3));

        assertFalse(attendance.isValidWeek(0));

        assertFalse(attendance.isValidWeek(1));

        assertFalse(attendance.isValidWeek(2));

        assertFalse(attendance.isValidWeek(14));
    }

    @Test
    public void toString_unorderedWeeks_returnsAscendingOrder() {
        Attendance attendance = new Attendance(Set.of(5, 3, 13));

        assertEquals("[3, 5, 13]", attendance.toString());
    }
}

package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class AttendanceTest {

    private Attendance attendance = new Attendance();

    @Test
    public void isValidWeek_validWeek_returnsTrue() {
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
    public void hasAttendance_invalidAttendance_exceptionThrown() {
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
}

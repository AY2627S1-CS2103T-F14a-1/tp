package seedu.address.model.masterycheck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/** Tests appointment values, completion transitions, equality, and diagnostic output. */
public class MasteryCheckTest {
    private static final LocalDateTime APPOINTMENT = LocalDateTime.of(2026, 8, 18, 14, 30);

    @Test
    public void constructor_nullAppointment_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MasteryCheck(null));
    }

    @Test
    public void constructor_validAppointment_storesTimeAndStartsIncomplete() {
        for (LocalDateTime time : new LocalDateTime[] {LocalDateTime.MIN, APPOINTMENT, LocalDateTime.MAX}) {
            MasteryCheck check = new MasteryCheck(time);
            assertEquals(time, check.getAppointmentTime());
            assertFalse(check.isComplete());
        }
    }

    @Test
    public void markComplete_repeatedCalls_preservesTimeAndCompleteState() {
        MasteryCheck check = new MasteryCheck(APPOINTMENT);
        check.markComplete();
        assertTrue(check.isComplete());
        check.markComplete();
        assertTrue(check.isComplete());
        assertEquals(APPOINTMENT, check.getAppointmentTime());
    }

    @Test
    public void markIncomplete_completedCheck_restoresIncompleteState() {
        MasteryCheck check = new MasteryCheck(APPOINTMENT);
        check.markComplete();
        check.markIncomplete();
        assertFalse(check.isComplete());
        check.markIncomplete();
        assertFalse(check.isComplete());
        assertEquals(APPOINTMENT, check.getAppointmentTime());
    }

    @Test
    public void equals_timeAndCompletionDifferences_returnsExpectedEquality() {
        MasteryCheck check = new MasteryCheck(APPOINTMENT);
        MasteryCheck copy = new MasteryCheck(APPOINTMENT);
        assertEquals(check, check);
        assertEquals(check, copy);
        assertNotEquals(check, new MasteryCheck(APPOINTMENT.plusMinutes(1)));
        assertNotEquals(null, check);
        assertNotEquals(APPOINTMENT, check);
        copy.markComplete();
        assertNotEquals(check, copy);
        check.markComplete();
        assertEquals(check, copy);
    }

    @Test
    public void hashCode_equalStates_returnsEqualHashCodes() {
        MasteryCheck first = new MasteryCheck(APPOINTMENT);
        MasteryCheck second = new MasteryCheck(APPOINTMENT);
        assertEquals(first.hashCode(), second.hashCode());
        first.markComplete();
        second.markComplete();
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void hashCode_hashSetLookup_respectsEquality() {
        Set<MasteryCheck> checks = new HashSet<>();
        checks.add(new MasteryCheck(APPOINTMENT));
        assertTrue(checks.contains(new MasteryCheck(APPOINTMENT)));
        assertFalse(checks.contains(new MasteryCheck(APPOINTMENT.plusMinutes(1))));
        MasteryCheck completed = new MasteryCheck(APPOINTMENT);
        completed.markComplete();
        assertFalse(checks.contains(completed));
        assertFalse(checks.add(new MasteryCheck(APPOINTMENT)));
        assertEquals(1, checks.size());
    }

    @Test
    public void toString_completionChanges_includesCurrentTimeAndStatus() {
        MasteryCheck check = new MasteryCheck(APPOINTMENT);
        String prefix = MasteryCheck.class.getCanonicalName() + "{appointment time=" + APPOINTMENT;
        assertEquals(prefix + ", is complete=false}", check.toString());
        check.markComplete();
        assertEquals(prefix + ", is complete=true}", check.toString());
    }
}

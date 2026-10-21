package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.masterycheck.MasteryCheck;

/** Tests numbered registration validation, copying, updates, and value semantics. */
public class RegisteredMasteryChecksTest {
    private static final LocalDateTime APPOINTMENT = LocalDateTime.of(2026, 8, 18, 14, 30);

    @Test
    public void constructor_noArguments_returnsEmptyRegistrations() {
        assertTrue(new RegisteredMasteryChecks().getMasteryCheckAppointments().isEmpty());
    }

    @Test
    public void constructor_boundaryNumbers_storesAppointments() {
        Map<Integer, MasteryCheck> appointments = Map.of(1, new MasteryCheck(APPOINTMENT),
                Integer.MAX_VALUE, new MasteryCheck(APPOINTMENT.plusDays(1)));
        assertEquals(appointments, new RegisteredMasteryChecks(appointments).getMasteryCheckAppointments());
    }

    @Test
    public void constructor_nullMap_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RegisteredMasteryChecks(null));
    }

    @Test
    public void constructor_nullKeyOrValue_throwsNullPointerException() {
        Map<Integer, MasteryCheck> nullKey = new HashMap<>();
        nullKey.put(null, new MasteryCheck(APPOINTMENT));
        assertThrows(NullPointerException.class, () -> new RegisteredMasteryChecks(nullKey));
        Map<Integer, MasteryCheck> nullValue = new HashMap<>();
        nullValue.put(1, null);
        assertThrows(NullPointerException.class, () -> new RegisteredMasteryChecks(nullValue));
    }

    @Test
    public void constructor_invalidNumbers_throwsIllegalArgumentException() {
        for (int number : new int[] {Integer.MIN_VALUE, -1, 0}) {
            assertThrows(IllegalArgumentException.class, () ->
                    new RegisteredMasteryChecks(Map.of(number, new MasteryCheck(APPOINTMENT))));
        }
    }

    @Test
    public void constructor_sourceMapChanged_retainsOriginalMapping() {
        MasteryCheck check = new MasteryCheck(APPOINTMENT);
        Map<Integer, MasteryCheck> appointments = new HashMap<>(Map.of(1, check));
        RegisteredMasteryChecks registrations = new RegisteredMasteryChecks(appointments);
        appointments.clear();
        appointments.put(2, new MasteryCheck(APPOINTMENT.plusDays(1)));
        assertEquals(Map.of(1, check), registrations.getMasteryCheckAppointments());
    }

    @Test
    public void withMasteryCheck_newNumber_returnsUpdatedCopy() {
        MasteryCheck first = new MasteryCheck(APPOINTMENT);
        MasteryCheck second = new MasteryCheck(APPOINTMENT.plusDays(1));
        RegisteredMasteryChecks original = new RegisteredMasteryChecks(Map.of(1, first));
        RegisteredMasteryChecks updated = original.withMasteryCheck(Integer.MAX_VALUE, second);
        assertEquals(Map.of(1, first), original.getMasteryCheckAppointments());
        assertEquals(Map.of(1, first, Integer.MAX_VALUE, second), updated.getMasteryCheckAppointments());
    }

    @Test
    public void withMasteryCheck_existingNumber_retainsOriginalAppointment() {
        MasteryCheck first = new MasteryCheck(APPOINTMENT);
        RegisteredMasteryChecks original = new RegisteredMasteryChecks(Map.of(1, first));
        assertSame(original, original.withMasteryCheck(1, new MasteryCheck(APPOINTMENT.plusDays(1))));
        assertEquals(Map.of(1, first), original.getMasteryCheckAppointments());
    }

    @Test
    public void withMasteryCheck_nullNewAppointment_throwsNullPointerException() {
        RegisteredMasteryChecks registrations = new RegisteredMasteryChecks();
        assertThrows(NullPointerException.class, () -> registrations.withMasteryCheck(1, null));
        assertTrue(registrations.getMasteryCheckAppointments().isEmpty());
    }

    @Test
    public void withMasteryCheck_invalidNumber_leavesRegistrationsUnchanged() {
        RegisteredMasteryChecks registrations = new RegisteredMasteryChecks();
        for (int number : new int[] {0, -1, Integer.MIN_VALUE}) {
            assertThrows(IllegalArgumentException.class, () ->
                    registrations.withMasteryCheck(number, new MasteryCheck(APPOINTMENT)));
            assertTrue(registrations.getMasteryCheckAppointments().isEmpty());
        }
    }

    @Test
    public void withoutMasteryCheck_existingNumber_returnsUpdatedCopy() {
        MasteryCheck first = new MasteryCheck(APPOINTMENT);
        MasteryCheck second = new MasteryCheck(APPOINTMENT.plusDays(1));
        RegisteredMasteryChecks original = new RegisteredMasteryChecks(Map.of(1, first, 2, second));
        assertEquals(Map.of(2, second), original.withoutMasteryCheck(1).getMasteryCheckAppointments());
        assertEquals(Map.of(1, first, 2, second), original.getMasteryCheckAppointments());
        assertTrue(original.withoutMasteryCheck(1).withoutMasteryCheck(2).getMasteryCheckAppointments().isEmpty());
    }

    @Test
    public void withoutMasteryCheck_missingNumber_returnsSameInstance() {
        RegisteredMasteryChecks registrations = new RegisteredMasteryChecks(Map.of(1, new MasteryCheck(APPOINTMENT)));
        assertSame(registrations, registrations.withoutMasteryCheck(2));
    }

    @Test
    public void withoutMasteryCheck_invalidNumber_leavesRegistrationsUnchanged() {
        RegisteredMasteryChecks registrations = new RegisteredMasteryChecks(Map.of(1, new MasteryCheck(APPOINTMENT)));
        for (int number : new int[] {0, -1, Integer.MIN_VALUE}) {
            assertThrows(IllegalArgumentException.class, () -> registrations.withoutMasteryCheck(number));
            assertTrue(registrations.hasMasteryCheck(1));
        }
    }

    @Test
    public void hasMasteryCheck_presentAndAbsentNumbers_returnsExpectedResult() {
        RegisteredMasteryChecks registrations = new RegisteredMasteryChecks(Map.of(1, new MasteryCheck(APPOINTMENT)));
        assertTrue(registrations.hasMasteryCheck(1));
        assertFalse(registrations.hasMasteryCheck(Integer.MAX_VALUE));
    }

    @Test
    public void hasMasteryCheck_invalidNumber_throwsIllegalArgumentException() {
        RegisteredMasteryChecks registrations = new RegisteredMasteryChecks();
        for (int number : new int[] {0, -1, Integer.MIN_VALUE}) {
            assertThrows(IllegalArgumentException.class, () -> registrations.hasMasteryCheck(number));
        }
    }

    @Test
    public void getMasteryCheckAppointments_modifyMapping_throwsUnsupportedOperationException() {
        MasteryCheck check = new MasteryCheck(APPOINTMENT);
        RegisteredMasteryChecks registrations = new RegisteredMasteryChecks(Map.of(1, check));
        Map<Integer, MasteryCheck> appointments = registrations.getMasteryCheckAppointments();
        assertThrows(UnsupportedOperationException.class, () -> appointments.put(2, check));
        assertThrows(UnsupportedOperationException.class, () -> appointments.remove(1));
        assertThrows(UnsupportedOperationException.class, () ->
                appointments.entrySet().iterator().next().setValue(check));
        assertEquals(Map.of(1, check), appointments);
    }

    @Test
    public void getMasteryCheckAppointments_completionChanged_exposesSharedCheckStatus() {
        MasteryCheck check = new MasteryCheck(APPOINTMENT);
        RegisteredMasteryChecks registrations = new RegisteredMasteryChecks(Map.of(1, check));
        check.markComplete();
        assertTrue(registrations.getMasteryCheckAppointments().get(1).isComplete());
        check.markIncomplete();
        assertFalse(registrations.getMasteryCheckAppointments().get(1).isComplete());
    }

    @Test
    public void isValidMasteryCheckNumber_boundaryNumbers_returnsExpectedValidity() {
        assertTrue(RegisteredMasteryChecks.isValidMasteryCheckNumber(1));
        assertTrue(RegisteredMasteryChecks.isValidMasteryCheckNumber(Integer.MAX_VALUE));
        assertFalse(RegisteredMasteryChecks.isValidMasteryCheckNumber(0));
        assertFalse(RegisteredMasteryChecks.isValidMasteryCheckNumber(Integer.MIN_VALUE));
    }

    @Test
    public void equals_numberTimeAndCompletionDifferences_returnsExpectedEquality() {
        RegisteredMasteryChecks registrations = new RegisteredMasteryChecks(Map.of(1, new MasteryCheck(APPOINTMENT)));
        assertEquals(registrations, registrations);
        assertEquals(registrations, new RegisteredMasteryChecks(Map.of(1, new MasteryCheck(APPOINTMENT))));
        assertNotEquals(registrations, new RegisteredMasteryChecks(Map.of(2, new MasteryCheck(APPOINTMENT))));
        assertNotEquals(registrations, new RegisteredMasteryChecks(Map.of(1,
                new MasteryCheck(APPOINTMENT.plusMinutes(1)))));
        MasteryCheck complete = new MasteryCheck(APPOINTMENT);
        complete.markComplete();
        assertNotEquals(registrations, new RegisteredMasteryChecks(Map.of(1, complete)));
        assertNotEquals(registrations, new RegisteredMasteryChecks());
        assertNotEquals(registrations, null);
        assertNotEquals(registrations, Map.of());
    }

    @Test
    public void hashCode_equalMappingsInDifferentOrders_returnsEqualHashes() {
        Map<Integer, MasteryCheck> first = new LinkedHashMap<>();
        first.put(1, new MasteryCheck(APPOINTMENT));
        first.put(2, new MasteryCheck(APPOINTMENT.plusDays(1)));
        Map<Integer, MasteryCheck> second = new LinkedHashMap<>();
        second.put(2, new MasteryCheck(APPOINTMENT.plusDays(1)));
        second.put(1, new MasteryCheck(APPOINTMENT));
        assertEquals(new RegisteredMasteryChecks(first).hashCode(), new RegisteredMasteryChecks(second).hashCode());
        assertEquals(new RegisteredMasteryChecks().hashCode(), new RegisteredMasteryChecks(Map.of()).hashCode());
    }

    @Test
    public void hashCode_hashSetLookup_respectsEquality() {
        Set<RegisteredMasteryChecks> registrations = new HashSet<>();
        registrations.add(new RegisteredMasteryChecks(Map.of(1, new MasteryCheck(APPOINTMENT))));
        assertTrue(registrations.contains(new RegisteredMasteryChecks(Map.of(1, new MasteryCheck(APPOINTMENT)))));
        assertFalse(registrations.contains(new RegisteredMasteryChecks(Map.of(2, new MasteryCheck(APPOINTMENT)))));
        assertFalse(registrations.add(new RegisteredMasteryChecks(Map.of(1, new MasteryCheck(APPOINTMENT)))));
        assertEquals(1, registrations.size());
    }

    @Test
    public void toString_unorderedAppointments_returnsSortedTimesAndStatuses() {
        MasteryCheck complete = new MasteryCheck(APPOINTMENT.plusDays(1));
        complete.markComplete();
        RegisteredMasteryChecks registrations = new RegisteredMasteryChecks(Map.of(2, complete,
                1, new MasteryCheck(APPOINTMENT)));
        assertEquals("1. 2026-08-18T14:30 false, 2. 2026-08-19T14:30 true", registrations.toString());
        assertEquals("", new RegisteredMasteryChecks().toString());
    }
}

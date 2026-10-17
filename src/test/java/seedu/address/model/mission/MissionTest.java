package seedu.address.model.mission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests mission validation, normalized names, deadline boundaries, and value equality. */
public class MissionTest {
    private static final MissionId ID = new MissionId("M01");
    private static final LocalDate DEADLINE = LocalDate.of(2024, 2, 29);

    @Test
    public void constructor_whitespaceAndPunctuation_normalizesNameOnly() {
        Mission mission = new Mission(ID, " \tMission 1: \u00a0 Rune\nReading!\u2003 ", DEADLINE);
        assertEquals(ID, mission.getId());
        assertEquals("Mission 1: Rune Reading!", mission.getName());
        assertEquals(DEADLINE, mission.getDeadline());
    }

    @Test
    public void constructor_blankOrNullArguments_rejectsInput() {
        for (String name : List.of("", " ", "\t\n", "\u00a0", "\u2003", " \u00a0\t")) {
            IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () ->
                    new Mission(ID, name, DEADLINE));
            assertEquals(Mission.MISSING_NAME, error.getMessage());
        }
        assertThrows(NullPointerException.class, () -> new Mission(null, "Runes", DEADLINE));
        assertThrows(NullPointerException.class, () -> new Mission(ID, null, DEADLINE));
        assertThrows(NullPointerException.class, () -> new Mission(ID, "Runes", null));
    }

    @Test
    public void hasSameName_caseAndWhitespaceVariants_matchesWithoutChangingName() {
        Mission mission = new Mission(ID, "Rune Reading!", DEADLINE);
        for (String name : List.of("rune reading!", " RUNE  READING! ", "Rune\tReading!", "Rune\u00a0Reading!")) {
            assertTrue(mission.hasSameName(name), name);
        }
        for (String name : List.of("RuneReading!", "Rune Reading", "Rune Reading! Part 2", "", "\u00a0")) {
            assertFalse(mission.hasSameName(name), name);
        }
        assertThrows(NullPointerException.class, () -> mission.hasSameName(null));
        assertEquals("Rune Reading!", mission.getName());
    }

    @Test
    public void isPastDeadline_beforeOnAndAfterDeadline_usesFollowingDay() {
        Mission mission = new Mission(ID, "Runes", DEADLINE);
        assertFalse(mission.isPastDeadline(DEADLINE.minusDays(1)));
        assertFalse(mission.isPastDeadline(DEADLINE));
        assertTrue(mission.isPastDeadline(DEADLINE.plusDays(1)));
        assertThrows(NullPointerException.class, () -> mission.isPastDeadline(null));
    }

    @Test
    public void equals_eachFieldCompared_obeysValueContract() {
        Mission mission = new Mission(ID, "Rune Reading", DEADLINE);
        Mission same = new Mission(new MissionId("M01"), " Rune  Reading ", DEADLINE);
        assertTrue(mission.equals(mission));
        assertTrue(mission.equals(same));
        assertTrue(same.equals(mission));
        assertEquals(mission.hashCode(), same.hashCode());
        assertFalse(mission.equals(new Mission(new MissionId("M02"), "Rune Reading", DEADLINE)));
        assertFalse(mission.equals(new Mission(ID, "Other", DEADLINE)));
        assertFalse(mission.equals(new Mission(ID, "Rune Reading", DEADLINE.plusDays(1))));
        assertFalse(mission.equals(new Mission(ID, "rune reading", DEADLINE)));
        assertFalse(mission.equals(null));
        assertFalse(mission.equals("Rune Reading"));
    }

    @Test
    public void toString_validMission_includesNameAndDeadline() {
        Mission mission = new Mission(ID, "Runes", DEADLINE);
        assertEquals(Mission.class.getCanonicalName() + "{name=Runes, deadline=2024-02-29}", mission.toString());
    }
}

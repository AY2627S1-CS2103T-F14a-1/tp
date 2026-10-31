package seedu.address.model.mission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests catalogue validation independently of CLI parsing. */
public class MissionCatalogueTest {

    private static final LocalDate DEADLINE = LocalDate.of(2026, 10, 15);

    @Test
    public void missionId_invalidValues_rejectsInput() {
        for (String value : List.of("", "M00", "M1", "M001", " M01", "M-1", "M99999999999999999999")) {
            assertThrows(IllegalArgumentException.class, () -> new MissionId(value));
        }
        assertThrows(NullPointerException.class, () -> new MissionId(null));
        assertEquals(105, new MissionId("M105").getNumber());
        assertEquals(new MissionId("M01"), new MissionId("M01"));
    }

    @Test
    public void mission_invalidDescriptionOrNull_rejectsInput() {
        MissionId id = new MissionId("M01");
        assertThrows(IllegalArgumentException.class, () -> new Mission(id, " \t", DEADLINE));
        assertThrows(NullPointerException.class, () -> new Mission(null, "Runes", DEADLINE));
        assertThrows(NullPointerException.class, () -> new Mission(id, null, DEADLINE));
        assertThrows(NullPointerException.class, () -> new Mission(id, "Runes", null));
        assertEquals("Runes", new Mission(id, " Runes ", DEADLINE).getName());
    }

    @Test
    public void isPastDeadline_boundaryDates_usesFollowingDay() {
        Mission mission = new Mission(new MissionId("M01"), "Runes", DEADLINE);
        assertFalse(mission.isPastDeadline(DEADLINE.minusDays(1)));
        assertFalse(mission.isPastDeadline(DEADLINE));
        assertTrue(mission.isPastDeadline(DEADLINE.plusDays(1)));
        assertThrows(NullPointerException.class, () -> mission.isPastDeadline(null));
    }

    @Test
    public void add_invalidDescription_doesNotChangeCatalogue() {
        MissionCatalogue catalogue = new MissionCatalogue();
        MissionId id = new MissionId("M01");
        assertThrows(IllegalArgumentException.class, () -> catalogue.add(id, " ", DEADLINE));
        assertTrue(catalogue.getMissions().isEmpty());
        Mission mission = catalogue.add(id, "Runes", DEADLINE);
        assertEquals(id, mission.getId());
        assertEquals(mission, catalogue.find(mission.getId()).orElseThrow());
        assertTrue(catalogue.find(new MissionId("M02")).isEmpty());
    }

    @Test
    public void add_manualId_rejectsDuplicatesAtomically() {
        MissionCatalogue catalogue = new MissionCatalogue();
        MissionId chosenId = new MissionId("M10");
        Mission added = catalogue.add(chosenId, "Runes", DEADLINE);
        assertEquals(chosenId, added.getId());

        assertThrows(IllegalArgumentException.class, () ->
                catalogue.add(chosenId, "Curves", DEADLINE));
        assertThrows(IllegalArgumentException.class, () ->
                catalogue.add(new MissionId("M11"), "runes", DEADLINE));
        assertEquals(List.of(added), catalogue.getMissions());
    }

    @Test
    public void restore_invalidReplacement_preservesOriginalCatalogue() {
        MissionCatalogue catalogue = new MissionCatalogue();
        Mission first = catalogue.add(new MissionId("M01"), "Runes", DEADLINE);
        assertThrows(IllegalArgumentException.class, () -> catalogue.restore(List.of(first, first)));
        assertEquals(List.of(first), catalogue.getMissions());
        assertThrows(UnsupportedOperationException.class, () -> catalogue.getMissions().clear());
    }
}

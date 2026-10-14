package seedu.address.model.mission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests catalogue validation and ID allocation independently of CLI parsing. */
public class MissionCatalogueTest {

    private static final LocalDate DEADLINE = LocalDate.of(2026, 10, 15);

    @Test
    public void missionId_invalidValues_rejectsInput() {
        for (String value : List.of("", "M00", "M1", "M001", "m01", " M01", "M-1", "M99999999999999999999")) {
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
        assertEquals("Runes", new Mission(id, " Runes ", DEADLINE).getDescription());
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
    public void add_invalidDescription_doesNotConsumeId() {
        MissionCatalogue catalogue = new MissionCatalogue();
        assertThrows(IllegalArgumentException.class, () -> catalogue.add(" ", DEADLINE));
        assertEquals(1, catalogue.getNextMissionNumber());
        assertTrue(catalogue.getMissions().isEmpty());
        Mission mission = catalogue.add("Runes", DEADLINE);
        assertEquals(new MissionId("M01"), mission.getId());
        assertEquals(mission, catalogue.find(mission.getId()).orElseThrow());
        assertTrue(catalogue.find(new MissionId("M02")).isEmpty());
    }

    @Test
    public void restore_invalidReplacement_preservesOriginalCatalogue() {
        MissionCatalogue catalogue = new MissionCatalogue();
        Mission first = catalogue.add("Runes", DEADLINE);
        assertThrows(IllegalArgumentException.class, () -> catalogue.restore(List.of(first, first), 2));
        assertThrows(IllegalArgumentException.class, () -> catalogue.restore(List.of(first), 1));
        assertThrows(IllegalArgumentException.class, () -> catalogue.restore(List.of(), 0));
        assertEquals(List.of(first), catalogue.getMissions());
        assertEquals(2, catalogue.getNextMissionNumber());
        assertThrows(UnsupportedOperationException.class, () -> catalogue.getMissions().clear());
    }

    @Test
    public void restore_emptyCatalogueWithUsedIds_doesNotReuseIds() {
        MissionCatalogue catalogue = new MissionCatalogue();
        catalogue.restore(List.of(), 100);
        assertEquals(new MissionId("M100"), catalogue.add("Curves", DEADLINE).getId());
        assertEquals(101, catalogue.getNextMissionNumber());
    }

    @Test
    public void add_exhaustedCounter_preservesState() {
        MissionCatalogue catalogue = new MissionCatalogue();
        catalogue.restore(List.of(), Long.MAX_VALUE);
        assertThrows(IllegalStateException.class, () -> catalogue.add("Runes", DEADLINE));
        assertTrue(catalogue.getMissions().isEmpty());
        assertEquals(Long.MAX_VALUE, catalogue.getNextMissionNumber());
    }
}

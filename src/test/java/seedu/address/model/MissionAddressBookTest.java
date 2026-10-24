package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalStudents.ALICE;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.mission.Mission;
import seedu.address.model.mission.MissionId;

/** Tests integration of mission state with address-book copies and model operations. */
public class MissionAddressBookTest {

    @Test
    public void copyAndReset_catalogue_preservesStateIndependently() {
        AddressBook original = new AddressBook();
        original.addStudent(ALICE);
        Mission mission = original.addMission(new MissionId("M05"), "Runes", LocalDate.of(2026, 10, 15));
        AddressBook copy = new AddressBook(original);
        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());
        original.addMission(new MissionId("M10"), "Curves", LocalDate.of(2026, 10, 16));
        assertEquals(List.of(mission), copy.getMissionList());
        assertNotEquals(original, copy);
        copy.resetData(original);
        assertEquals(original, copy);
        copy.resetData(copy);
        assertEquals(original, copy);
    }

    @Test
    public void modelMissionOperations_validMission_preservesCatalogueOnReplacement() {
        Model model = new ModelManager();
        Mission mission = model.addMission(new MissionId("M01"), "Runes", LocalDate.of(2026, 10, 15));
        assertEquals(mission, model.findMission(new MissionId("M01")).orElseThrow());
        model.setAddressBook(new AddressBook(model.getAddressBook()));
        assertEquals(List.of(mission), model.getMissionList());
        assertThrows(UnsupportedOperationException.class, () -> model.getMissionList().clear());
    }
}

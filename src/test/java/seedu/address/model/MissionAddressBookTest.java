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
    public void copyAndReset_catalogueWithCounter_preservesStateIndependently() {
        AddressBook original = new AddressBook();
        original.addStudent(ALICE);
        original.restoreMissions(List.of(), 5);
        Mission mission = original.addMission("Runes", LocalDate.of(2026, 10, 15));
        AddressBook copy = new AddressBook(original);
        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());
        original.addMission("Curves", LocalDate.of(2026, 10, 16));
        assertEquals(List.of(mission), copy.getMissionList());
        assertEquals(6, copy.getNextMissionNumber());
        assertNotEquals(original, copy);
        copy.resetData(original);
        assertEquals(original, copy);
        copy.resetData(copy);
        assertEquals(original, copy);
    }

    @Test
    public void resetData_invalidCatalogue_doesNotReplaceStudents() {
        AddressBook original = new AddressBook();
        original.addStudent(ALICE);
        AddressBook invalid = new AddressBook() {
            @Override
            public long getNextMissionNumber() {
                return 0;
            }
        };
        assertThrows(IllegalArgumentException.class, () -> original.resetData(invalid));
        assertEquals(List.of(ALICE), original.getStudentList());
    }

    @Test
    public void modelMissionOperations_validMission_preservesCatalogueOnReplacement() {
        Model model = new ModelManager();
        Mission mission = model.addMission("Runes", LocalDate.of(2026, 10, 15));
        assertEquals(mission, model.findMission(new MissionId("M01")).orElseThrow());
        model.setAddressBook(new AddressBook(model.getAddressBook()));
        assertEquals(List.of(mission), model.getMissionList());
        assertThrows(UnsupportedOperationException.class, () -> model.getMissionList().clear());
    }
}

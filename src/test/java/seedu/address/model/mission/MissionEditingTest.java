package seedu.address.model.mission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.student.Student;
import seedu.address.testutil.StudentBuilder;

/**
 * Tests mission replacement through the public model API.
 */
public class MissionEditingTest {

    private Model model;
    private Mission target;
    private Student john;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        target = model.addMission("Runes", LocalDate.of(2026, 10, 20));
        model.addMission("Curves", LocalDate.of(2026, 10, 21));
        john = new StudentBuilder().withName("John Doe")
                .withMissionSubmissions("M01").build();
        model.addStudent(john);
        model.updateFilteredStudentList(student -> false);
    }

    @Test
    public void setMission_validReplacement_preservesSubmissionsFilterPositionAndCounter() {
        Mission editedMission = new Mission(target.getId(), "New Runes",
                LocalDate.of(2026, 10, 22));

        model.setMission(target, editedMission);

        assertEquals(editedMission, model.getMissionList().getFirst());
        assertEquals("Curves", model.getMissionList().get(1).getName());
        assertEquals(List.of(john), model.getAddressBook().getStudentList());
        assertTrue(model.getAddressBook().getStudentList().getFirst()
                .getMissionSubmissions().hasSubmission(target.getId()));
        assertTrue(model.getFilteredStudentList().isEmpty());
        assertEquals(3, model.getAddressBook().getNextMissionNumber());
    }

    @Test
    public void setMission_nullArguments_preservesState() {
        AddressBook original = new AddressBook(model.getAddressBook());

        assertThrows(NullPointerException.class, () -> model.setMission(null, target));
        assertEquals(original, model.getAddressBook());

        assertThrows(NullPointerException.class, () -> model.setMission(target, null));
        assertEquals(original, model.getAddressBook());
        assertTrue(model.getFilteredStudentList().isEmpty());
    }

    @Test
    public void setMission_unknownTarget_preservesState() {
        AddressBook original = new AddressBook(model.getAddressBook());
        Mission unknown = new Mission(new MissionId("M99"), "Unknown",
                LocalDate.of(2026, 10, 22));

        assertThrows(IllegalArgumentException.class, () -> model.setMission(unknown, unknown));

        assertEquals(original, model.getAddressBook());
        assertTrue(model.getFilteredStudentList().isEmpty());
    }

    @Test
    public void setMission_changedId_preservesState() {
        AddressBook original = new AddressBook(model.getAddressBook());
        Mission editedMission = new Mission(new MissionId("M03"), "New Runes",
                LocalDate.of(2026, 10, 22));

        assertThrows(IllegalArgumentException.class, () -> model.setMission(target, editedMission));

        assertEquals(original, model.getAddressBook());
        assertTrue(model.getFilteredStudentList().isEmpty());
    }

    @Test
    public void setMission_duplicateName_preservesState() {
        AddressBook original = new AddressBook(model.getAddressBook());
        Mission editedMission = new Mission(target.getId(), "  CURVES  ",
                LocalDate.of(2026, 10, 22));

        assertThrows(IllegalArgumentException.class, () -> model.setMission(target, editedMission));

        assertEquals(original, model.getAddressBook());
        assertTrue(model.getFilteredStudentList().isEmpty());
    }

    @Test
    public void setMission_sameMission_acceptsItsOwnName() {
        AddressBook original = new AddressBook(model.getAddressBook());

        model.setMission(target, target);

        assertEquals(original, model.getAddressBook());
        assertTrue(model.getFilteredStudentList().isEmpty());
    }
}

package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.Student;
import seedu.address.testutil.StudentBuilder;

/**
 * Tests submission listing and preservation of the student filter and records.
 */
public class ListMissionsCommandTest {

    private static final MissionId MISSION_ID = new MissionId("M01");

    private Model model;
    private Student john;
    private Student betsy;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        model.addMission(MISSION_ID, "Rune Reading", LocalDate.of(2026, 10, 20));
        john = new StudentBuilder().withName("John Doe").build();
        betsy = new StudentBuilder().withName("Betsy Crowe")
                .withStudioGroup("1A")
                .withMissionSubmissions("M01").build();
        model.addStudent(john);
        model.addStudent(betsy);
    }

    @Test
    public void execute_submittedStudent_preservesDisplayedIndexAndRecords() {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new ListMissionsCommand(MISSION_ID), model,
                "Submissions for M01 · Rune Reading (Due: 2026-10-20)"
                        + " (current student list):\n2. Betsy Crowe | Studio group: 1A",
                expectedModel);
    }

    @Test
    public void execute_filteredSubmittedStudent_usesFilteredIndex() {
        model.updateFilteredStudentList(student -> student.equals(betsy));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredStudentList(student -> student.equals(betsy));

        assertCommandSuccess(new ListMissionsCommand(MISSION_ID), model,
                "Submissions for M01 · Rune Reading (Due: 2026-10-20)"
                        + " (current student list):\n1. Betsy Crowe | Studio group: 1A",
                expectedModel);
    }

    @Test
    public void execute_submittedStudentsInDifferentGroups_showsEachStudentsGroup() {
        Student brian = new StudentBuilder().withName("Brian Lim")
                .withStudioGroup("1B")
                .withMissionSubmissions("M01").build();
        model.addStudent(brian);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new ListMissionsCommand(MISSION_ID), model,
                "Submissions for M01 · Rune Reading (Due: 2026-10-20)"
                        + " (current student list):\n"
                        + "2. Betsy Crowe | Studio group: 1A\n"
                        + "3. Brian Lim | Studio group: 1B",
                expectedModel);
    }

    @Test
    public void execute_hiddenSubmittedStudent_reportsNoSubmissions() {
        model.updateFilteredStudentList(student -> student.equals(john));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredStudentList(student -> student.equals(john));

        assertCommandSuccess(new ListMissionsCommand(MISSION_ID), model,
                "No submissions recorded for M01 in the current student list.",
                expectedModel);
    }

    @Test
    public void execute_emptyFilteredList_reportsNoSubmissions() {
        model.updateFilteredStudentList(student -> false);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.updateFilteredStudentList(student -> false);

        assertCommandSuccess(new ListMissionsCommand(MISSION_ID), model,
                "No submissions recorded for M01 in the current student list.",
                expectedModel);
    }

    @Test
    public void execute_unknownMission_preservesState() {
        model.updateFilteredStudentList(student -> student.equals(betsy));

        assertCommandFailure(new ListMissionsCommand(new MissionId("M99")),
                model, "Mission M99 does not exist.");
    }

    @Test
    public void constructorAndExecute_nullArguments_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListMissionsCommand(null));
        assertThrows(NullPointerException.class, () -> new ListMissionsCommand(MISSION_ID).execute(null));
    }

    @Test
    public void equals_sameAndDifferentMissionIds_comparesMissionIds() {
        ListMissionsCommand command = new ListMissionsCommand(MISSION_ID);
        ListMissionsCommand equalCommand = new ListMissionsCommand(new MissionId("M01"));

        assertEquals(command, command);
        assertEquals(command, equalCommand);
        assertEquals(command.hashCode(), equalCommand.hashCode());
        assertNotEquals(command, new ListMissionsCommand(new MissionId("M02")));
        assertNotEquals(command, null);
        assertNotEquals(command, "M01");
    }
}

package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showStudentAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_STUDENT;
import static seedu.address.testutil.TypicalStudents.getTypicalAddressBook;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.mission.Mission;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.Student;
import seedu.address.testutil.StudentBuilder;

/**
 * Tests mission submission execution and preservation of existing records.
 */
public class UnmarkMissionCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @BeforeEach
    public void setUpMissions() {
        AddressBook book = new AddressBook(model.getAddressBook());
        book.restoreMissions(List.of(3, 4, 5, 13).stream()
                .map(number -> new Mission(new MissionId(String.format("M%02d", number)),
                        "Mission " + number, LocalDate.of(2026, 10, 15))).toList(), 14);
        model.setAddressBook(book);
    }

    @Test
    public void constructor_nullArguments_throwsException() {
        assertThrows(NullPointerException.class, () -> new UnmarkMissionCommand(null, new MissionId("M03")));
        assertThrows(NullPointerException.class, () -> new UnmarkMissionCommand(INDEX_FIRST_STUDENT, null));
    }

    @Test
    public void execute_unknownMission_leavesModelUnchanged() {
        assertCommandFailure(new UnmarkMissionCommand(INDEX_FIRST_STUDENT, new MissionId("M99")),
                model, "Mission M99 does not exist.");
    }

    @Test
    public void execute_validIndex_preservesOtherMissionsAndStudents() {
        Student original = model.getFilteredStudentList().get(0);
        Student student = new StudentBuilder(original).withMissionSubmissions("M05", "M03", "M04").build();
        model.setStudent(original, student);

        Student updated = new StudentBuilder(student).withMissionSubmissions("M05", "M04").build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStudent(student, updated);

        String feedback = String.format(UnmarkMissionCommand.MESSAGE_SUCCESS, student.getName(), "M03");

        assertCommandSuccess(new UnmarkMissionCommand(INDEX_FIRST_STUDENT, new MissionId("M03")),
                model, feedback, expectedModel);
        assertTrue(student.getMissionSubmissions().hasSubmission(new MissionId("M03")));
    }

    @Test
    public void execute_boundaryMissions_removesBothMissions() throws Exception {
        Student original = model.getFilteredStudentList().get(0);
        Student student = new StudentBuilder(original).withMissionSubmissions("M03", "M13").build();
        model.setStudent(original, student);

        CommandResult firstResult = new UnmarkMissionCommand(INDEX_FIRST_STUDENT, new MissionId("M03")).execute(model);
        CommandResult lastResult = new UnmarkMissionCommand(INDEX_FIRST_STUDENT, new MissionId("M13")).execute(model);

        assertEquals(String.format(UnmarkMissionCommand.MESSAGE_SUCCESS, student.getName(), "M03"),
                firstResult.getFeedbackToUser());
        assertEquals(String.format(UnmarkMissionCommand.MESSAGE_SUCCESS, student.getName(), "M13"),
                lastResult.getFeedbackToUser());

        Student expected = new StudentBuilder(student).withMissionSubmissions().build();
        assertEquals(expected, model.getFilteredStudentList().get(0));
    }

    @Test
    public void execute_missingSubmission_leavesModelUnchanged() {
        Student original = model.getFilteredStudentList().get(0);
        Student student = new StudentBuilder(original).withMissionSubmissions("M05").build();
        model.setStudent(original, student);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        String feedback = String.format(UnmarkMissionCommand.MESSAGE_NOT_RECORDED, student.getName(), "M03");

        assertCommandSuccess(new UnmarkMissionCommand(INDEX_FIRST_STUDENT, new MissionId("M03")),
                model, feedback, expectedModel);
    }

    @Test
    public void execute_invalidIndex_leavesModelUnchanged() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);

        assertCommandFailure(new UnmarkMissionCommand(invalidIndex, new MissionId("M03")),
                model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_filteredList_updatesDisplayedStudentAndPreservesFilter() {
        Student original = model.getFilteredStudentList().get(INDEX_SECOND_STUDENT.getZeroBased());
        Student student = new StudentBuilder(original).withMissionSubmissions("M05", "M03").build();
        model.setStudent(original, student);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        showStudentAtIndex(model, INDEX_SECOND_STUDENT);
        showStudentAtIndex(expectedModel, INDEX_SECOND_STUDENT);

        Student updated = new StudentBuilder(student).withMissionSubmissions("M05").build();
        expectedModel.setStudent(student, updated);

        String feedback = String.format(UnmarkMissionCommand.MESSAGE_SUCCESS, student.getName(), "M03");

        assertCommandSuccess(new UnmarkMissionCommand(INDEX_FIRST_STUDENT, new MissionId("M03")),
                model, feedback, expectedModel);
        assertEquals(1, model.getFilteredStudentList().size());
        assertEquals(updated, model.getFilteredStudentList().get(0));
    }

    @Test
    public void execute_indexOutsideFilteredList_leavesModelUnchanged() {
        showStudentAtIndex(model, INDEX_FIRST_STUDENT);

        assertCommandFailure(new UnmarkMissionCommand(INDEX_SECOND_STUDENT, new MissionId("M03")),
                model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals_sameIndexAndMission_returnsTrue() {
        UnmarkMissionCommand command = new UnmarkMissionCommand(INDEX_FIRST_STUDENT, new MissionId("M03"));

        assertTrue(command.equals(command));
        assertTrue(command.equals(new UnmarkMissionCommand(INDEX_FIRST_STUDENT, new MissionId("M03"))));
        assertFalse(command.equals(new UnmarkMissionCommand(INDEX_SECOND_STUDENT, new MissionId("M03"))));
        assertFalse(command.equals(new UnmarkMissionCommand(INDEX_FIRST_STUDENT, new MissionId("M04"))));
        assertFalse(command.equals(null));
        assertFalse(command.equals("unmarkmission"));
    }

    @Test
    public void toString_validCommand_returnsExpectedString() {
        UnmarkMissionCommand command = new UnmarkMissionCommand(INDEX_FIRST_STUDENT, new MissionId("M03"));
        String expected = UnmarkMissionCommand.class.getCanonicalName()
                + "{targetIndex=" + INDEX_FIRST_STUDENT + ", missionId=M03}";

        assertEquals(expected, command.toString());
    }
}

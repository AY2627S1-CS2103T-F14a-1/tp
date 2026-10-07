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

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.Student;
import seedu.address.testutil.StudentBuilder;

/**
 * Tests mission submission execution and preservation of existing records.
 */
public class DeleteMissionCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_invalidArguments_throwsException() {
        assertThrows(NullPointerException.class, () -> new DeleteMissionCommand(null, 3));
        assertThrows(IllegalArgumentException.class, () -> new DeleteMissionCommand(INDEX_FIRST_STUDENT, 0));
        assertThrows(IllegalArgumentException.class, () -> new DeleteMissionCommand(INDEX_FIRST_STUDENT, 11));
    }

    @Test
    public void execute_validIndex_preservesOtherWeeksAndStudents() {
        Student original = model.getFilteredStudentList().get(0);
        Student student = new StudentBuilder(original).withMissionSubmissions(2, 3, 4).build();
        model.setStudent(original, student);

        Student updated = new StudentBuilder(student).withMissionSubmissions(2, 4).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStudent(student, updated);

        String feedback = String.format(DeleteMissionCommand.MESSAGE_SUCCESS, student.getName(), 3);

        assertCommandSuccess(new DeleteMissionCommand(INDEX_FIRST_STUDENT, 3),
                model, feedback, expectedModel);
        assertTrue(student.getMissionSubmissions().hasSubmission(3));
    }

    @Test
    public void execute_boundaryWeeks_removesBothWeeks() throws Exception {
        Student original = model.getFilteredStudentList().get(0);
        Student student = new StudentBuilder(original).withMissionSubmissions(1, 10).build();
        model.setStudent(original, student);

        CommandResult firstResult = new DeleteMissionCommand(INDEX_FIRST_STUDENT, 1).execute(model);
        CommandResult lastResult = new DeleteMissionCommand(INDEX_FIRST_STUDENT, 10).execute(model);

        assertEquals(String.format(DeleteMissionCommand.MESSAGE_SUCCESS, student.getName(), 1),
                firstResult.getFeedbackToUser());
        assertEquals(String.format(DeleteMissionCommand.MESSAGE_SUCCESS, student.getName(), 10),
                lastResult.getFeedbackToUser());

        Student expected = new StudentBuilder(student).withMissionSubmissions().build();
        assertEquals(expected, model.getFilteredStudentList().get(0));
    }

    @Test
    public void execute_missingSubmission_leavesModelUnchanged() {
        Student original = model.getFilteredStudentList().get(0);
        Student student = new StudentBuilder(original).withMissionSubmissions(2).build();
        model.setStudent(original, student);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        String feedback = String.format(DeleteMissionCommand.MESSAGE_NOT_RECORDED, student.getName(), 3);

        assertCommandSuccess(new DeleteMissionCommand(INDEX_FIRST_STUDENT, 3),
                model, feedback, expectedModel);
    }

    @Test
    public void execute_invalidIndex_leavesModelUnchanged() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);

        assertCommandFailure(new DeleteMissionCommand(invalidIndex, 3),
                model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_emptyList_reportsInvalidIndex() {
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());

        assertCommandFailure(new DeleteMissionCommand(INDEX_FIRST_STUDENT, 3),
                emptyModel, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_filteredList_updatesDisplayedStudentAndPreservesFilter() {
        Student original = model.getFilteredStudentList().get(INDEX_SECOND_STUDENT.getZeroBased());
        Student student = new StudentBuilder(original).withMissionSubmissions(2, 3).build();
        model.setStudent(original, student);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        showStudentAtIndex(model, INDEX_SECOND_STUDENT);
        showStudentAtIndex(expectedModel, INDEX_SECOND_STUDENT);

        Student updated = new StudentBuilder(student).withMissionSubmissions(2).build();
        expectedModel.setStudent(student, updated);

        String feedback = String.format(DeleteMissionCommand.MESSAGE_SUCCESS, student.getName(), 3);

        assertCommandSuccess(new DeleteMissionCommand(INDEX_FIRST_STUDENT, 3),
                model, feedback, expectedModel);
        assertEquals(1, model.getFilteredStudentList().size());
        assertEquals(updated, model.getFilteredStudentList().get(0));
    }

    @Test
    public void execute_indexOutsideFilteredList_leavesModelUnchanged() {
        showStudentAtIndex(model, INDEX_FIRST_STUDENT);

        assertCommandFailure(new DeleteMissionCommand(INDEX_SECOND_STUDENT, 3),
                model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals_sameIndexAndWeek_returnsTrue() {
        DeleteMissionCommand command = new DeleteMissionCommand(INDEX_FIRST_STUDENT, 3);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new DeleteMissionCommand(INDEX_FIRST_STUDENT, 3)));
        assertFalse(command.equals(new DeleteMissionCommand(INDEX_SECOND_STUDENT, 3)));
        assertFalse(command.equals(new DeleteMissionCommand(INDEX_FIRST_STUDENT, 4)));
        assertFalse(command.equals(null));
        assertFalse(command.equals("delmission"));
    }

    @Test
    public void toString_validCommand_returnsExpectedString() {
        DeleteMissionCommand command = new DeleteMissionCommand(INDEX_FIRST_STUDENT, 3);
        String expected = DeleteMissionCommand.class.getCanonicalName()
                + "{targetIndex=" + INDEX_FIRST_STUDENT + ", week=3}";

        assertEquals(expected, command.toString());
    }
}

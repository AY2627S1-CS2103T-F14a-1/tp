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
public class AddMissionCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_invalidArguments_throwsException() {
        assertThrows(NullPointerException.class, () -> new AddMissionCommand(null, 3));
        assertThrows(IllegalArgumentException.class, () -> new AddMissionCommand(INDEX_FIRST_STUDENT, 0));
        assertThrows(IllegalArgumentException.class, () -> new AddMissionCommand(INDEX_FIRST_STUDENT, 14));
    }

    @Test
    public void execute_validIndex_preservesOtherWeeksAndStudents() {
        Student original = model.getFilteredStudentList().get(0);
        Student student = new StudentBuilder(original).withMissionSubmissions(5, 4).build();
        model.setStudent(original, student);

        Student updated = new StudentBuilder(student).withMissionSubmissions(5, 3, 4).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStudent(student, updated);

        String feedback = String.format(AddMissionCommand.MESSAGE_SUCCESS, student.getName(), 3);

        assertCommandSuccess(new AddMissionCommand(INDEX_FIRST_STUDENT, 3),
                model, feedback, expectedModel);
        assertFalse(student.getMissionSubmissions().hasSubmission(3));
    }

    @Test
    public void execute_boundaryWeeks_recordsBothWeeks() throws Exception {
        Student original = model.getFilteredStudentList().get(0);
        Student student = new StudentBuilder(original).withMissionSubmissions().build();
        model.setStudent(original, student);

        CommandResult firstResult = new AddMissionCommand(INDEX_FIRST_STUDENT, 3).execute(model);
        CommandResult lastResult = new AddMissionCommand(INDEX_FIRST_STUDENT, 13).execute(model);

        assertEquals(String.format(AddMissionCommand.MESSAGE_SUCCESS, student.getName(), 3),
                firstResult.getFeedbackToUser());
        assertEquals(String.format(AddMissionCommand.MESSAGE_SUCCESS, student.getName(), 13),
                lastResult.getFeedbackToUser());

        Student expected = new StudentBuilder(student).withMissionSubmissions(3, 13).build();
        assertEquals(expected, model.getFilteredStudentList().get(0));
    }

    @Test
    public void execute_existingSubmission_leavesModelUnchanged() {
        Student original = model.getFilteredStudentList().get(0);
        Student student = new StudentBuilder(original).withMissionSubmissions(3).build();
        model.setStudent(original, student);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        String feedback = String.format(AddMissionCommand.MESSAGE_ALREADY_RECORDED, student.getName(), 3);

        assertCommandSuccess(new AddMissionCommand(INDEX_FIRST_STUDENT, 3),
                model, feedback, expectedModel);
    }

    @Test
    public void execute_invalidIndex_leavesModelUnchanged() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);

        assertCommandFailure(new AddMissionCommand(invalidIndex, 3),
                model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_emptyList_reportsInvalidIndex() {
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());

        assertCommandFailure(new AddMissionCommand(INDEX_FIRST_STUDENT, 3),
                emptyModel, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_filteredList_updatesDisplayedStudentAndPreservesFilter() {
        Student original = model.getFilteredStudentList().get(INDEX_SECOND_STUDENT.getZeroBased());
        Student student = new StudentBuilder(original).withMissionSubmissions(5).build();
        model.setStudent(original, student);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        showStudentAtIndex(model, INDEX_SECOND_STUDENT);
        showStudentAtIndex(expectedModel, INDEX_SECOND_STUDENT);

        Student updated = new StudentBuilder(student).withMissionSubmissions(5, 3).build();
        expectedModel.setStudent(student, updated);

        String feedback = String.format(AddMissionCommand.MESSAGE_SUCCESS, student.getName(), 3);

        assertCommandSuccess(new AddMissionCommand(INDEX_FIRST_STUDENT, 3),
                model, feedback, expectedModel);
        assertEquals(1, model.getFilteredStudentList().size());
        assertEquals(updated, model.getFilteredStudentList().get(0));
    }

    @Test
    public void execute_indexOutsideFilteredList_leavesModelUnchanged() {
        showStudentAtIndex(model, INDEX_FIRST_STUDENT);

        assertCommandFailure(new AddMissionCommand(INDEX_SECOND_STUDENT, 3),
                model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals_sameIndexAndWeek_returnsTrue() {
        AddMissionCommand command = new AddMissionCommand(INDEX_FIRST_STUDENT, 3);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new AddMissionCommand(INDEX_FIRST_STUDENT, 3)));
        assertFalse(command.equals(new AddMissionCommand(INDEX_SECOND_STUDENT, 3)));
        assertFalse(command.equals(new AddMissionCommand(INDEX_FIRST_STUDENT, 4)));
        assertFalse(command.equals(null));
        assertFalse(command.equals("addmission"));
    }

    @Test
    public void toString_validCommand_returnsExpectedString() {
        AddMissionCommand command = new AddMissionCommand(INDEX_FIRST_STUDENT, 3);
        String expected = AddMissionCommand.class.getCanonicalName()
                + "{targetIndex=" + INDEX_FIRST_STUDENT + ", week=3}";

        assertEquals(expected, command.toString());
    }
}

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
import seedu.address.model.student.RegisteredStudioRecords;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioRecord;
import seedu.address.testutil.StudentBuilder;

/**
 * Tests studio record deletion and preservation of existing studio records.
 */
public class DeleteStudioRecordCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final StudioRecord studioRecord = new StudioRecord(0);

    @Test
    public void constructor_invalidArguments_throwsException() {
        assertThrows(NullPointerException.class, () ->
                new DeleteStudioRecordCommand(null, RegisteredStudioRecords.MIN_WEEK));
        assertThrows(IllegalArgumentException.class, () ->
                new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, RegisteredStudioRecords.MIN_WEEK - 1));
        assertThrows(IllegalArgumentException.class, () ->
                new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, RegisteredStudioRecords.MAX_WEEK + 1));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        DeleteStudioRecordCommand command =
                new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, RegisteredStudioRecords.MIN_WEEK);

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_validIndex_preservesOtherWeeksAndStudents() {
        Student original = model.getFilteredStudentList().get(0);
        Student student = new StudentBuilder(original).withStudioRecords(
                new StudentBuilder.StudioRecordSet(3, studioRecord),
                new StudentBuilder.StudioRecordSet(4, studioRecord),
                new StudentBuilder.StudioRecordSet(5, studioRecord)).build();
        model.setStudent(original, student);

        Student updated = new StudentBuilder(student).withStudioRecords(
                new StudentBuilder.StudioRecordSet(4, studioRecord),
                new StudentBuilder.StudioRecordSet(5, studioRecord)).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStudent(student, updated);

        String feedback = String.format(DeleteStudioRecordCommand.MESSAGE_SUCCESS, student.getName(), 3);

        assertCommandSuccess(
                new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 3),
                model,
                new CommandResult(feedback, updated),
                expectedModel);
        assertTrue(student.getRegisteredStudioRecords().hasStudioRecord(
                3));
    }

    @Test
    public void execute_boundaryWeeks_removesBothWeeks() throws Exception {
        Student original = model.getFilteredStudentList().get(0);
        Student student = new StudentBuilder(original)
                .withStudioRecords(
                        new StudentBuilder.StudioRecordSet(RegisteredStudioRecords.MIN_WEEK, studioRecord),
                        new StudentBuilder.StudioRecordSet(RegisteredStudioRecords.MAX_WEEK, studioRecord))
                .build();
        model.setStudent(original, student);

        CommandResult firstResult = new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, RegisteredStudioRecords.MIN_WEEK)
                .execute(model);
        CommandResult lastResult = new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, RegisteredStudioRecords.MAX_WEEK)
                .execute(model);

        assertEquals(String.format(DeleteStudioRecordCommand.MESSAGE_SUCCESS,
                student.getName(), RegisteredStudioRecords.MIN_WEEK), firstResult.getFeedbackToUser());
        assertEquals(String.format(DeleteStudioRecordCommand.MESSAGE_SUCCESS,
                student.getName(), RegisteredStudioRecords.MAX_WEEK), lastResult.getFeedbackToUser());

        Student expected = new StudentBuilder(student).withStudioRecords().build();
        assertEquals(expected, model.getFilteredStudentList().get(0));
    }

    @Test
    public void execute_missingStudioRecord_leavesModelUnchanged() {
        Student original = model.getFilteredStudentList().get(0);
        Student student = new StudentBuilder(original).withStudioRecords(
                new StudentBuilder.StudioRecordSet(5, studioRecord)).build();
        model.setStudent(original, student);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        String feedback = String.format(DeleteStudioRecordCommand.MESSAGE_ALREADY_NOT_RECORDED,
                student.getName(), 3);

        Student expectedStudent =
                model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());

        assertCommandSuccess(
                new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 3),
                model,
                new CommandResult(feedback, expectedStudent),
                expectedModel);
    }

    @Test
    public void execute_invalidIndex_leavesModelUnchanged() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);

        assertCommandFailure(new DeleteStudioRecordCommand(invalidIndex, 3),
                model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_emptyList_reportsInvalidIndex() {
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());

        assertCommandFailure(new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 3),
                emptyModel, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_filteredList_updatesDisplayedStudentAndPreservesFilter() {
        Student original = model.getFilteredStudentList().get(INDEX_SECOND_STUDENT.getZeroBased());
        Student student = new StudentBuilder(original).withStudioRecords(
                new StudentBuilder.StudioRecordSet(3, studioRecord),
                new StudentBuilder.StudioRecordSet(5, studioRecord)).build();
        model.setStudent(original, student);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        showStudentAtIndex(model, INDEX_SECOND_STUDENT);
        showStudentAtIndex(expectedModel, INDEX_SECOND_STUDENT);

        Student updated = new StudentBuilder(student).withStudioRecords(
                new StudentBuilder.StudioRecordSet(5, studioRecord)).build();
        expectedModel.setStudent(student, updated);

        String feedback = String.format(DeleteStudioRecordCommand.MESSAGE_SUCCESS, student.getName(), 3);

        assertCommandSuccess(
                new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 3),
                model,
                new CommandResult(feedback, updated),
                expectedModel);
        assertEquals(1, model.getFilteredStudentList().size());
        assertEquals(updated, model.getFilteredStudentList().get(0));
    }

    @Test
    public void execute_indexOutsideFilteredList_leavesModelUnchanged() {
        showStudentAtIndex(model, INDEX_FIRST_STUDENT);

        assertCommandFailure(new DeleteStudioRecordCommand(INDEX_SECOND_STUDENT, 3),
                model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals_sameIndexAndWeek_returnsTrue() {
        DeleteStudioRecordCommand command = new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 3);

        assertTrue(command.equals(command));
        DeleteStudioRecordCommand equalCommand =
                new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 3);

        assertTrue(command.equals(equalCommand));
        assertEquals(equalCommand.hashCode(), command.hashCode());
        assertFalse(command.equals(new DeleteStudioRecordCommand(INDEX_SECOND_STUDENT, 3)));
        assertFalse(command.equals(new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 4)));
        assertFalse(command.equals(null));
        assertFalse(command.equals("delstudiorecord"));
    }

    @Test
    public void toString_validCommand_returnsExpectedString() {
        DeleteStudioRecordCommand command = new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 3);
        String expected = DeleteStudioRecordCommand.class.getCanonicalName()
                + "{targetStudentIndex=" + INDEX_FIRST_STUDENT + ", week=3}";

        assertEquals(expected, command.toString());
    }
}

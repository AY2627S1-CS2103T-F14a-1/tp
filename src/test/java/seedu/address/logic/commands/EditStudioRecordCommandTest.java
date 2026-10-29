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

/** Tests studio record editing and preservation of unrelated student details. */
public class EditStudioRecordCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final StudioRecord originalRecord = new StudioRecord(0);
    private final StudioRecord updatedRecord = new StudioRecord(50);

    @Test
    public void constructor_invalidArguments_throwsException() {
        assertThrows(NullPointerException.class, () ->
                new EditStudioRecordCommand(null, RegisteredStudioRecords.MIN_WEEK, updatedRecord));
        assertThrows(NullPointerException.class, () ->
                new EditStudioRecordCommand(INDEX_FIRST_STUDENT, RegisteredStudioRecords.MIN_WEEK, null));
        assertThrows(IllegalArgumentException.class, () ->
                new EditStudioRecordCommand(INDEX_FIRST_STUDENT, RegisteredStudioRecords.MIN_WEEK - 1, updatedRecord));
        assertThrows(IllegalArgumentException.class, () ->
                new EditStudioRecordCommand(INDEX_FIRST_STUDENT, RegisteredStudioRecords.MAX_WEEK + 1, updatedRecord));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        EditStudioRecordCommand command = new EditStudioRecordCommand(
                INDEX_FIRST_STUDENT, RegisteredStudioRecords.MIN_WEEK, updatedRecord);

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_validIndex_replacesRecordAndPreservesOtherWeeksAndStudents() {
        Student original = model.getFilteredStudentList().get(0);
        Student student = new StudentBuilder(original).withStudioRecords(
                new StudentBuilder.StudioRecordSet(3, originalRecord),
                new StudentBuilder.StudioRecordSet(4, originalRecord)).build();
        model.setStudent(original, student);
        Student updated = new StudentBuilder(student).withStudioRecords(
                new StudentBuilder.StudioRecordSet(3, updatedRecord),
                new StudentBuilder.StudioRecordSet(4, originalRecord)).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStudent(student, updated);

        assertCommandSuccess(new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 3, updatedRecord), model,
                String.format(EditStudioRecordCommand.MESSAGE_SUCCESS, student.getName(), 3), expectedModel);
        assertEquals(originalRecord, student.getRegisteredStudioRecords().getRecordsByWeek().get(3));
    }

    @Test
    public void execute_boundaryWeeks_replacesBothWeeks() throws Exception {
        Student original = model.getFilteredStudentList().get(0);
        Student student = new StudentBuilder(original).withStudioRecords(
                new StudentBuilder.StudioRecordSet(RegisteredStudioRecords.MIN_WEEK, originalRecord),
                new StudentBuilder.StudioRecordSet(RegisteredStudioRecords.MAX_WEEK, originalRecord)).build();
        model.setStudent(original, student);

        CommandResult firstResult = new EditStudioRecordCommand(
                INDEX_FIRST_STUDENT, RegisteredStudioRecords.MIN_WEEK, updatedRecord).execute(model);
        CommandResult lastResult = new EditStudioRecordCommand(
                INDEX_FIRST_STUDENT, RegisteredStudioRecords.MAX_WEEK, updatedRecord).execute(model);

        assertEquals(String.format(EditStudioRecordCommand.MESSAGE_SUCCESS,
                student.getName(), RegisteredStudioRecords.MIN_WEEK), firstResult.getFeedbackToUser());
        assertEquals(String.format(EditStudioRecordCommand.MESSAGE_SUCCESS,
                student.getName(), RegisteredStudioRecords.MAX_WEEK), lastResult.getFeedbackToUser());
        Student expected = new StudentBuilder(student).withStudioRecords(
                new StudentBuilder.StudioRecordSet(RegisteredStudioRecords.MIN_WEEK, updatedRecord),
                new StudentBuilder.StudioRecordSet(RegisteredStudioRecords.MAX_WEEK, updatedRecord)).build();
        assertEquals(expected, model.getFilteredStudentList().get(0));
    }

    @Test
    public void execute_missingStudioRecord_leavesModelUnchanged() {
        Student original = model.getFilteredStudentList().get(0);
        Student student = new StudentBuilder(original).withStudioRecords(
                new StudentBuilder.StudioRecordSet(5, originalRecord)).build();
        model.setStudent(original, student);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 3, updatedRecord), model,
                String.format(EditStudioRecordCommand.MESSAGE_WEEK_NOT_RECORDED, student.getName(), 3), expectedModel);
    }

    @Test
    public void execute_unchangedStudioRecord_leavesModelUnchanged() {
        Student original = model.getFilteredStudentList().get(0);
        Student student = new StudentBuilder(original).withStudioRecords(
                new StudentBuilder.StudioRecordSet(3, originalRecord)).build();
        model.setStudent(original, student);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 3, originalRecord), model,
                String.format(EditStudioRecordCommand.MESSAGE_ALREADY_RECORDED, student.getName(), 3), expectedModel);
    }

    @Test
    public void execute_invalidIndex_leavesModelUnchanged() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);
        assertCommandFailure(new EditStudioRecordCommand(invalidIndex, 3, updatedRecord),
                model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_emptyList_reportsInvalidIndex() {
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());
        assertCommandFailure(new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 3, updatedRecord),
                emptyModel, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_filteredList_updatesDisplayedStudentAndPreservesFilter() {
        Student original = model.getFilteredStudentList().get(INDEX_SECOND_STUDENT.getZeroBased());
        Student student = new StudentBuilder(original).withStudioRecords(
                new StudentBuilder.StudioRecordSet(3, originalRecord)).build();
        model.setStudent(original, student);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        showStudentAtIndex(model, INDEX_SECOND_STUDENT);
        showStudentAtIndex(expectedModel, INDEX_SECOND_STUDENT);
        Student updated = new StudentBuilder(student).withStudioRecords(
                new StudentBuilder.StudioRecordSet(3, updatedRecord)).build();
        expectedModel.setStudent(student, updated);

        assertCommandSuccess(new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 3, updatedRecord), model,
                String.format(EditStudioRecordCommand.MESSAGE_SUCCESS, student.getName(), 3), expectedModel);
        assertEquals(1, model.getFilteredStudentList().size());
        assertEquals(updated, model.getFilteredStudentList().get(0));
    }

    @Test
    public void execute_indexOutsideFilteredList_leavesModelUnchanged() {
        showStudentAtIndex(model, INDEX_FIRST_STUDENT);
        assertCommandFailure(new EditStudioRecordCommand(INDEX_SECOND_STUDENT, 3, updatedRecord),
                model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals_sameIndexWeekAndScore_returnsTrue() {
        EditStudioRecordCommand command = new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 3, updatedRecord);
        assertTrue(command.equals(command));
        EditStudioRecordCommand equalCommand =
                new EditStudioRecordCommand(
                        INDEX_FIRST_STUDENT,
                        3,
                        updatedRecord);

        assertTrue(command.equals(equalCommand));
        assertEquals(equalCommand.hashCode(), command.hashCode());
        assertFalse(command.equals(new EditStudioRecordCommand(INDEX_SECOND_STUDENT, 3, updatedRecord)));
        assertFalse(command.equals(new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 4, updatedRecord)));
        assertFalse(command.equals(new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 3, originalRecord)));
        assertFalse(command.equals(null));
        assertFalse(command.equals("editstudiorecord"));
    }

    @Test
    public void toString_validCommand_returnsExpectedString() {
        EditStudioRecordCommand command = new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 3, updatedRecord);
        String expected = EditStudioRecordCommand.class.getCanonicalName()
                + "{targetIndex=" + INDEX_FIRST_STUDENT + ", week=3, studioRecord=" + updatedRecord + "}";
        assertEquals(expected, command.toString());
    }
}

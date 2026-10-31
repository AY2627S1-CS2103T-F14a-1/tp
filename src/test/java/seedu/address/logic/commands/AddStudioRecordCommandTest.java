package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.getOutOfBoundsStudentIndexMessage;
import static seedu.address.logic.commands.CommandTestUtil.showStudentAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_STUDENT;
import static seedu.address.testutil.TypicalStudents.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.RegisteredStudioRecords;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioRecord;
import seedu.address.testutil.StudentBuilder;

/**
 * Tests studio recording and preservation of existing studio records.
 */
public class AddStudioRecordCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final StudioRecord studioRecord = new StudioRecord(0);

    @Test
    public void execute_validArguments_returnsSuccessFeedback() throws Exception {
        Student student = model.getFilteredStudentList().get(0);
        AddStudioRecordCommand command =
                new AddStudioRecordCommand(INDEX_FIRST_STUDENT, RegisteredStudioRecords.MIN_WEEK, studioRecord);

        CommandResult result = command.execute(model);

        assertEquals(String.format(AddStudioRecordCommand.MESSAGE_SUCCESS,
                student.getName(), RegisteredStudioRecords.MIN_WEEK), result.getFeedbackToUser());
        assertEquals(studioRecord, model.getFilteredStudentList().get(0)
                .getRegisteredStudioRecords().getRecordsByWeek().get(RegisteredStudioRecords.MIN_WEEK));
    }

    @Test
    public void constructor_nullArguments_throwsException() {
        // null index
        assertThrows(NullPointerException.class, () -> new AddStudioRecordCommand(null,
                RegisteredStudioRecords.MIN_WEEK, studioRecord));

        // null studio record
        assertThrows(NullPointerException.class, () ->
                new AddStudioRecordCommand(INDEX_FIRST_STUDENT,
                        RegisteredStudioRecords.MIN_WEEK, null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        AddStudioRecordCommand command =
                new AddStudioRecordCommand(INDEX_FIRST_STUDENT, RegisteredStudioRecords.MIN_WEEK, studioRecord);
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_validIndex_preservesOtherWeeksAndStudents() {
        Student original = model.getFilteredStudentList().get(0);
        Student student = new StudentBuilder(original).withStudioRecords(
                new StudentBuilder.StudioRecordSet(4, studioRecord),
                new StudentBuilder.StudioRecordSet(5, studioRecord)).build();
        model.setStudent(original, student);

        Student updated = new StudentBuilder(student).withStudioRecords(
                new StudentBuilder.StudioRecordSet(3, studioRecord),
                new StudentBuilder.StudioRecordSet(4, studioRecord),
                new StudentBuilder.StudioRecordSet(5, studioRecord)).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setStudent(student, updated);

        String feedback = String.format(AddStudioRecordCommand.MESSAGE_SUCCESS, student.getName(), 3);

        assertCommandSuccess(
                new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 3, studioRecord),
                model,
                new CommandResult(feedback, updated),
                expectedModel);
        assertFalse(student.getRegisteredStudioRecords().hasStudioRecord(3));
    }

    @Test
    public void execute_boundaryWeeks_recordsBothWeeks() throws Exception {
        Student original = model.getFilteredStudentList().get(0);
        Student student = new StudentBuilder(original).withStudioRecords().build();
        model.setStudent(original, student);

        CommandResult firstResult = new AddStudioRecordCommand(
                INDEX_FIRST_STUDENT, RegisteredStudioRecords.MIN_WEEK, studioRecord)
                .execute(model);
        CommandResult lastResult = new AddStudioRecordCommand(
                INDEX_FIRST_STUDENT, RegisteredStudioRecords.MAX_WEEK, studioRecord).execute(model);

        assertEquals(String.format(AddStudioRecordCommand.MESSAGE_SUCCESS, student.getName(),
                        RegisteredStudioRecords.MIN_WEEK),
                firstResult.getFeedbackToUser());
        assertEquals(String.format(AddStudioRecordCommand.MESSAGE_SUCCESS, student.getName(),
                        RegisteredStudioRecords.MAX_WEEK),
                lastResult.getFeedbackToUser());

        Student expected = new StudentBuilder(student)
                .withStudioRecords(
                        new StudentBuilder.StudioRecordSet(RegisteredStudioRecords.MIN_WEEK, studioRecord),
                        new StudentBuilder.StudioRecordSet(RegisteredStudioRecords.MAX_WEEK, studioRecord)
                )
                .build();
        assertEquals(expected, model.getFilteredStudentList().get(0));
    }

    @Test
    public void execute_existingStudioRecord_leavesModelUnchanged() {
        Student original = model.getFilteredStudentList().get(0);
        Student student = new StudentBuilder(original).withStudioRecords(
                new StudentBuilder.StudioRecordSet(3, studioRecord)).build();
        model.setStudent(original, student);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        String feedback = String.format(
                AddStudioRecordCommand.MESSAGE_ALREADY_RECORDED, student.getName(), 3);

        assertCommandSuccess(
                new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 3, studioRecord),
                model,
                new CommandResult(feedback, student),
                expectedModel);
    }

    @Test
    public void execute_invalidIndex_leavesModelUnchanged() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);

        assertCommandFailure(new AddStudioRecordCommand(invalidIndex, 3, studioRecord),
                model, getOutOfBoundsStudentIndexMessage(invalidIndex, model));
    }

    @Test
    public void execute_emptyList_reportsInvalidIndex() {
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());

        assertCommandFailure(new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 3, studioRecord),
                emptyModel, getOutOfBoundsStudentIndexMessage(INDEX_FIRST_STUDENT, emptyModel));
    }

    @Test
    public void execute_filteredList_updatesDisplayedStudentAndPreservesFilter() {
        Student original = model.getFilteredStudentList().get(INDEX_SECOND_STUDENT.getZeroBased());
        Student student = new StudentBuilder(original).withStudioRecords(
                new StudentBuilder.StudioRecordSet(5, studioRecord)).build();
        model.setStudent(original, student);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        showStudentAtIndex(model, INDEX_SECOND_STUDENT);
        showStudentAtIndex(expectedModel, INDEX_SECOND_STUDENT);

        Student updated = new StudentBuilder(student).withStudioRecords(
                new StudentBuilder.StudioRecordSet(3, studioRecord),
                new StudentBuilder.StudioRecordSet(5, studioRecord)).build();
        expectedModel.setStudent(student, updated);

        String feedback = String.format(AddStudioRecordCommand.MESSAGE_SUCCESS, student.getName(), 3);

        assertCommandSuccess(
                new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 3, studioRecord),
                model,
                new CommandResult(feedback, updated),
                expectedModel);
        assertEquals(1, model.getFilteredStudentList().size());
        assertEquals(updated, model.getFilteredStudentList().get(0));
    }

    @Test
    public void execute_indexOutsideFilteredList_leavesModelUnchanged() {
        showStudentAtIndex(model, INDEX_FIRST_STUDENT);

        assertCommandFailure(new AddStudioRecordCommand(INDEX_SECOND_STUDENT, 3, studioRecord),
                model, getOutOfBoundsStudentIndexMessage(INDEX_SECOND_STUDENT, model));
    }

    @Test
    public void equals_sameIndexWeekAndScore_returnsTrue() {
        AddStudioRecordCommand command = new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 3, studioRecord);

        assertTrue(command.equals(command));
        AddStudioRecordCommand equalCommand =
                new AddStudioRecordCommand(
                        INDEX_FIRST_STUDENT,
                        3,
                        studioRecord);

        assertTrue(command.equals(equalCommand));
        assertEquals(equalCommand.hashCode(), command.hashCode());
        assertFalse(command.equals(new AddStudioRecordCommand(INDEX_SECOND_STUDENT, 3, studioRecord)));
        assertFalse(command.equals(new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 4, studioRecord)));
        assertFalse(command.equals(new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 3, new StudioRecord(50))));
        assertFalse(command.equals(null));
        assertFalse(command.equals("addstudiorecord"));
    }

    @Test
    public void toString_validCommand_returnsExpectedString() {
        AddStudioRecordCommand command = new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 3, studioRecord);
        String expected = AddStudioRecordCommand.class.getCanonicalName()
                + "{targetIndex=" + INDEX_FIRST_STUDENT + ", week=3, studioRecord=" + studioRecord + "}";

        assertEquals(expected, command.toString());
    }
}

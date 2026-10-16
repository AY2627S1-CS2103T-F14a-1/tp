package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.AddStudioRecordCommand.MESSAGE_SUCCESS;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showStudentAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_STUDENT;
import static seedu.address.testutil.TypicalStudents.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.StudioRecord;
import seedu.address.model.student.StudioRecords;
import seedu.address.model.student.Student;
import seedu.address.testutil.StudentBuilder;

/**
 * Tests attendance recording and preservation of existing attendance.
 */
public class AddStudioRecordsCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final Index index = Index.fromZeroBased(StudioRecords.MIN_WEEK);
    private final StudioRecord studioRecord = new StudioRecord(0);

    @Test
    public void constructor_validArguments() throws CommandException {
        AddStudioRecordCommand cmd = new AddStudioRecordCommand(index, StudioRecords.MIN_WEEK, studioRecord);
        CommandResult res = cmd.execute(model);
        assertEquals(res, new CommandResult(String.format(MESSAGE_SUCCESS)), res.getFeedbackToUser());
    }

    @Test
    public void constructor_nullArguments_throwsException() {
        // null index
        assertThrows(NullPointerException.class, () -> new AddStudioRecordCommand(null,
                StudioRecords.MIN_WEEK, studioRecord));

        // null studio record
        assertThrows(NullPointerException.class, () ->
                new AddStudioRecordCommand(INDEX_FIRST_STUDENT,
                        StudioRecords.MIN_WEEK, null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        AddStudioRecordCommand command = new AddStudioRecordCommand(INDEX_FIRST_STUDENT, StudioRecords.MIN_WEEK, studioRecord);
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

        String feedback = String.format(AddStudioRecordCommand.MESSAGE_SUCCESS, student.getName(), 3, studioRecord);

        assertCommandSuccess(new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 3, studioRecord),
                model, feedback, expectedModel);
        assertFalse(student.getStudioRecords().hasStudioRecord(3));
    }

    @Test
    public void execute_boundaryWeeks_recordsBothWeeks() throws Exception {
        Student original = model.getFilteredStudentList().get(0);
        Student student = new StudentBuilder(original).withStudioRecords().build();
        model.setStudent(original, student);

        CommandResult firstResult = new AddStudioRecordCommand(INDEX_FIRST_STUDENT, StudioRecords.MIN_WEEK, studioRecord)
                .execute(model);
        CommandResult lastResult = new AddStudioRecordCommand(INDEX_FIRST_STUDENT, StudioRecords.MAX_WEEK, studioRecord)
                .execute(model);

        assertEquals(String.format(AddStudioRecordCommand.MESSAGE_SUCCESS, student.getName(), StudioRecords.MIN_WEEK),
                firstResult.getFeedbackToUser());
        assertEquals(String.format(AddStudioRecordCommand.MESSAGE_SUCCESS, student.getName(), StudioRecords.MAX_WEEK),
                lastResult.getFeedbackToUser());

        Student expected = new StudentBuilder(student)
                .withStudioRecords(
                        new StudentBuilder.StudioRecordSet(StudioRecords.MIN_WEEK, studioRecord),
                        new StudentBuilder.StudioRecordSet(StudioRecords.MAX_WEEK, studioRecord)
                )
                .build();
        assertEquals(expected, model.getFilteredStudentList().get(0));
    }

    @Test
    public void execute_existingAttendance_leavesModelUnchanged() {
        Student original = model.getFilteredStudentList().get(0);
        Student student = new StudentBuilder(original).withStudioRecords(
                new StudentBuilder.StudioRecordSet(3, studioRecord)).build();
        model.setStudent(original, student);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        String feedback = String.format(AddStudioRecordCommand.MESSAGE_ALREADY_RECORDED, student.getName(), 3, studioRecord);

        assertCommandSuccess(new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 3, studioRecord),
                model, feedback, expectedModel);
    }

    @Test
    public void execute_invalidIndex_leavesModelUnchanged() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);

        assertCommandFailure(new AddStudioRecordCommand(invalidIndex, 3, studioRecord),
                model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_emptyList_reportsInvalidIndex() {
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());

        assertCommandFailure(new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 3, studioRecord),
                emptyModel, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
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

        String feedback = String.format(AddStudioRecordCommand.MESSAGE_SUCCESS, student.getName(), 3, studioRecord);

        assertCommandSuccess(new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 3, studioRecord),
                model, feedback, expectedModel);
        assertEquals(1, model.getFilteredStudentList().size());
        assertEquals(updated, model.getFilteredStudentList().get(0));
    }

    @Test
    public void execute_indexOutsideFilteredList_leavesModelUnchanged() {
        showStudentAtIndex(model, INDEX_FIRST_STUDENT);

        assertCommandFailure(new AddStudioRecordCommand(INDEX_SECOND_STUDENT, 3, studioRecord),
                model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals_sameIndexAndWeek_returnsTrue() {
        AddStudioRecordCommand command = new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 3, studioRecord);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 3, studioRecord)));
        assertFalse(command.equals(new AddStudioRecordCommand(INDEX_SECOND_STUDENT, 3, studioRecord)));
        assertFalse(command.equals(new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 4, studioRecord)));
        assertFalse(command.equals(null));
        assertFalse(command.equals("addattendance"));
    }

    @Test
    public void toString_validCommand_returnsExpectedString() {
        AddStudioRecordCommand command = new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 3, studioRecord);
        String expected = AddStudioRecordCommand.class.getCanonicalName()
                + "{targetIndex=" + INDEX_FIRST_STUDENT + ", week=3}";

        assertEquals(expected, command.toString());
    }
}

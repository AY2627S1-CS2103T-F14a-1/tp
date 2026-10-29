package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showStudentAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_STUDENT;
import static seedu.address.testutil.TypicalStudents.getTypicalAddressBook;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.masterycheck.MasteryCheck;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioRecord;
import seedu.address.testutil.StudentBuilder;
import seedu.address.testutil.StudentBuilder.StudioRecordSet;

/** Tests mastery check registration, duplicate handling, feedback, and filtered-list targeting. */
public class AddMasteryCheckCommandTest {
    private static final LocalDateTime APPOINTMENT = LocalDateTime.of(2026, 8, 18, 14, 30);
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new AddMasteryCheckCommand(null, 1, new MasteryCheck(APPOINTMENT)));
        assertThrows(NullPointerException.class, () -> new AddMasteryCheckCommand(INDEX_FIRST_STUDENT, 1, null));
    }

    @Test
    public void constructor_invalidNumbers_throwsIllegalArgumentException() {
        for (int number : new int[] {0, -1, Integer.MIN_VALUE}) {
            assertThrows(IllegalArgumentException.class, () ->
                    new AddMasteryCheckCommand(INDEX_FIRST_STUDENT, number, new MasteryCheck(APPOINTMENT)));
        }
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        AddMasteryCheckCommand command =
                new AddMasteryCheckCommand(INDEX_FIRST_STUDENT, 1, new MasteryCheck(APPOINTMENT));
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_newAppointment_preservesOtherRecordsAndStudents() {
        Student original = model.getFilteredStudentList().getFirst();
        MasteryCheck first = new MasteryCheck(APPOINTMENT);
        MasteryCheck second = new MasteryCheck(APPOINTMENT.plusDays(1));
        Student student = new StudentBuilder(original).withMissionSubmissions("M03")
                .withStudioRecords(new StudioRecordSet(3, new StudioRecord(50)))
                .withMasteryChecks(Map.of(2, second)).build();
        model.setStudent(original, student);
        Student updated = new StudentBuilder(student).withMasteryChecks(Map.of(1, first, 2, second)).build();
        Model expected = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expected.setStudent(student, updated);
        String expectedMessage =
                "Added " + student.getName() + "'s mastery check 1 at Aug 18, 14:30.";
        assertCommandSuccess(
                new AddMasteryCheckCommand(INDEX_FIRST_STUDENT, 1, first),
                model,
                new CommandResult(expectedMessage, updated),
                expected);
        assertFalse(student.getRegisteredMasteryChecks().hasMasteryCheck(1));
    }

    @Test
    public void execute_largestNumber_registersAppointment() throws Exception {
        Student original = model.getFilteredStudentList().getFirst();
        new AddMasteryCheckCommand(INDEX_FIRST_STUDENT, Integer.MAX_VALUE, new MasteryCheck(APPOINTMENT))
                .execute(model);
        Student expected = new StudentBuilder(original).withMasteryChecks(
                Map.of(Integer.MAX_VALUE, new MasteryCheck(APPOINTMENT))).build();
        assertEquals(expected, model.getFilteredStudentList().getFirst());
    }

    @Test
    public void execute_existingNumberWithDifferentTime_leavesModelUnchanged() {
        Student original = model.getFilteredStudentList().getFirst();
        Student student = new StudentBuilder(original)
                .withMasteryChecks(Map.of(1, new MasteryCheck(APPOINTMENT))).build();
        model.setStudent(original, student);
        Model expected = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        assertCommandSuccess(new AddMasteryCheckCommand(INDEX_FIRST_STUDENT, 1,
                new MasteryCheck(APPOINTMENT.plusDays(1))), model,
                new CommandResult(String.format(AddMasteryCheckCommand.MESSAGE_ALREADY_RECORDED, student.getName(), 1),
                student), expected);
    }

    @Test
    public void execute_invalidIndex_leavesModelUnchanged() {
        Index index = Index.fromOneBased(model.getFilteredStudentList().size() + 1);
        assertCommandFailure(new AddMasteryCheckCommand(index, 1, new MasteryCheck(APPOINTMENT)), model,
                Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_emptyList_reportsInvalidIndex() {
        Model empty = new ModelManager();
        assertCommandFailure(new AddMasteryCheckCommand(INDEX_FIRST_STUDENT, 1, new MasteryCheck(APPOINTMENT)), empty,
                Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_filteredList_updatesDisplayedStudentAndPreservesFilter() {
        Student student = model.getFilteredStudentList().get(INDEX_SECOND_STUDENT.getZeroBased());
        Model expected = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        showStudentAtIndex(model, INDEX_SECOND_STUDENT);
        showStudentAtIndex(expected, INDEX_SECOND_STUDENT);
        Student updated = new StudentBuilder(student)
                .withMasteryChecks(Map.of(1, new MasteryCheck(APPOINTMENT))).build();
        expected.setStudent(student, updated);
        String expectedMessage = String.format(
                AddMasteryCheckCommand.MESSAGE_SUCCESS,
                student.getName(),
                1,
                "Aug 18, 14:30");

        assertCommandSuccess(
                new AddMasteryCheckCommand(
                        INDEX_FIRST_STUDENT,
                        1,
                        new MasteryCheck(APPOINTMENT)),
                model,
                new CommandResult(expectedMessage, updated),
                expected);
        assertEquals(1, model.getFilteredStudentList().size());
    }

    @Test
    public void execute_indexOutsideFilteredList_leavesModelUnchanged() {
        showStudentAtIndex(model, INDEX_FIRST_STUDENT);
        assertCommandFailure(new AddMasteryCheckCommand(INDEX_SECOND_STUDENT, 1, new MasteryCheck(APPOINTMENT)), model,
                Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals_indexNumberTimeAndCompletion_returnsExpectedEquality() {
        AddMasteryCheckCommand command =
                new AddMasteryCheckCommand(INDEX_FIRST_STUDENT, 1, new MasteryCheck(APPOINTMENT));
        assertEquals(new AddMasteryCheckCommand(INDEX_FIRST_STUDENT, 1, new MasteryCheck(APPOINTMENT)), command);
        assertNotEquals(new AddMasteryCheckCommand(INDEX_SECOND_STUDENT, 1, new MasteryCheck(APPOINTMENT)), command);
        assertNotEquals(new AddMasteryCheckCommand(INDEX_FIRST_STUDENT, 2, new MasteryCheck(APPOINTMENT)), command);
        assertNotEquals(command, new AddMasteryCheckCommand(INDEX_FIRST_STUDENT, 1,
                new MasteryCheck(APPOINTMENT.plusDays(1))));
        MasteryCheck complete = new MasteryCheck(APPOINTMENT);
        complete.markComplete();
        assertNotEquals(command, new AddMasteryCheckCommand(INDEX_FIRST_STUDENT, 1, complete));
        assertNotEquals(null, command);
    }

    @Test
    public void toString_validCommand_includesIndexNumberAndAppointment() {
        MasteryCheck check = new MasteryCheck(APPOINTMENT);
        AddMasteryCheckCommand command = new AddMasteryCheckCommand(INDEX_FIRST_STUDENT, 1, check);
        assertEquals(AddMasteryCheckCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_STUDENT
                + ", number=1, masteryCheck=" + check + "}", command.toString());
    }
}

package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

/** Tests mastery check deletion, argument validation, filtered targeting, and preservation of other records. */
public class DeleteMasteryCheckCommandTest {
    private static final LocalDateTime APPOINTMENT = LocalDateTime.of(2026, 8, 18, 14, 30);
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteMasteryCheckCommand(null, 1));
    }

    @Test
    public void constructor_positiveNumbers_acceptsAllValidMasteryCheckNumbers() {
        for (int number : new int[] {1, 13, 14, Integer.MAX_VALUE}) {
            assertDoesNotThrow(() -> new DeleteMasteryCheckCommand(INDEX_FIRST_STUDENT, number),
                    "Positive mastery check number " + number + " must be accepted");
        }
    }

    @Test
    public void constructor_nonPositiveNumbers_throwsIllegalArgumentException() {
        for (int number : new int[] {-1, Integer.MIN_VALUE, 0}) {
            assertThrows(IllegalArgumentException.class, () ->
                    new DeleteMasteryCheckCommand(INDEX_FIRST_STUDENT, number),
                    "Non-positive mastery check number " + number + " must be rejected");
        }
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        DeleteMasteryCheckCommand command = new DeleteMasteryCheckCommand(INDEX_FIRST_STUDENT, 1);
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_existingNumber_preservesOtherAppointmentsFieldsAndStudents() {
        Student original = model.getFilteredStudentList().getFirst();
        MasteryCheck first = new MasteryCheck(APPOINTMENT);
        MasteryCheck second = new MasteryCheck(APPOINTMENT.plusDays(1));
        second.markComplete();
        Student student = new StudentBuilder(original).withMissionSubmissions("M03")
                .withStudioRecords(new StudioRecordSet(3, new StudioRecord(50)))
                .withMasteryChecks(Map.of(1, first, 2, second)).build();
        model.setStudent(original, student);
        Student updated = new StudentBuilder(student).withMasteryChecks(Map.of(2, second)).build();
        Model expected = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expected.setStudent(student, updated);
        assertCommandSuccess(new DeleteMasteryCheckCommand(INDEX_FIRST_STUDENT, 1), model,
                "Removed " + student.getName() + "'s mastery check numbered 1.", expected);
        assertEquals(Map.of(1, first, 2, second), student.getRegisteredMasteryChecks().getMasteryCheckAppointments());
        assertTrue(model.getFilteredStudentList().getFirst().getRegisteredMasteryChecks()
                .getMasteryCheckAppointments().get(2).isComplete());
    }

    @Test
    public void execute_onlyCompletedAppointment_removesRegistration() throws Exception {
        Student original = model.getFilteredStudentList().getFirst();
        MasteryCheck complete = new MasteryCheck(APPOINTMENT);
        complete.markComplete();
        Student student = new StudentBuilder(original).withMasteryChecks(Map.of(1, complete)).build();
        model.setStudent(original, student);
        CommandResult result = new DeleteMasteryCheckCommand(INDEX_FIRST_STUDENT, 1).execute(model);
        assertEquals(String.format(DeleteMasteryCheckCommand.MESSAGE_SUCCESS, student.getName(), 1),
                result.getFeedbackToUser());
        assertEquals(new StudentBuilder(student).withMasteryChecks(Map.of()).build(),
                model.getFilteredStudentList().getFirst());
    }

    @Test
    public void execute_largestRegisteredNumber_removesAppointment() throws Exception {
        Student original = model.getFilteredStudentList().getFirst();
        Student student = new StudentBuilder(original).withMasteryChecks(
                Map.of(Integer.MAX_VALUE, new MasteryCheck(APPOINTMENT))).build();
        model.setStudent(original, student);
        new DeleteMasteryCheckCommand(INDEX_FIRST_STUDENT, Integer.MAX_VALUE).execute(model);
        assertEquals(new StudentBuilder(student).withMasteryChecks(Map.of()).build(),
                model.getFilteredStudentList().getFirst());
    }

    @Test
    public void execute_missingRegistration_leavesModelUnchanged() {
        Student original = model.getFilteredStudentList().getFirst();
        Student student = new StudentBuilder(original)
                .withMasteryChecks(Map.of(2, new MasteryCheck(APPOINTMENT))).build();
        model.setStudent(original, student);
        Model expected = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        assertCommandSuccess(new DeleteMasteryCheckCommand(INDEX_FIRST_STUDENT, 1), model,
                String.format(DeleteMasteryCheckCommand.MESSAGE_NOT_FOUND, student.getName(), 1), expected);
    }

    @Test
    public void execute_emptyRegistrations_leavesModelUnchanged() {
        Student student = model.getFilteredStudentList().getFirst();
        Model expected = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        assertCommandSuccess(new DeleteMasteryCheckCommand(INDEX_FIRST_STUDENT, 1), model,
                String.format(DeleteMasteryCheckCommand.MESSAGE_NOT_FOUND, student.getName(), 1), expected);
    }

    @Test
    public void execute_invalidIndex_leavesModelUnchanged() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);
        assertCommandFailure(new DeleteMasteryCheckCommand(invalidIndex, 1), model,
                Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_emptyStudentList_reportsInvalidIndex() {
        assertCommandFailure(new DeleteMasteryCheckCommand(INDEX_FIRST_STUDENT, 1), new ModelManager(),
                Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_filteredList_updatesDisplayedStudentAndPreservesFilter() {
        Student original = model.getFilteredStudentList().get(INDEX_SECOND_STUDENT.getZeroBased());
        MasteryCheck first = new MasteryCheck(APPOINTMENT);
        MasteryCheck second = new MasteryCheck(APPOINTMENT.plusDays(1));
        Student student = new StudentBuilder(original).withMasteryChecks(Map.of(1, first, 2, second)).build();
        model.setStudent(original, student);
        Model expected = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        showStudentAtIndex(model, INDEX_SECOND_STUDENT);
        showStudentAtIndex(expected, INDEX_SECOND_STUDENT);
        Student updated = new StudentBuilder(student).withMasteryChecks(Map.of(2, second)).build();
        expected.setStudent(student, updated);
        assertCommandSuccess(new DeleteMasteryCheckCommand(INDEX_FIRST_STUDENT, 1), model,
                String.format(DeleteMasteryCheckCommand.MESSAGE_SUCCESS, student.getName(), 1), expected);
        assertEquals(1, model.getFilteredStudentList().size());
        assertEquals(updated, model.getFilteredStudentList().getFirst());
    }

    @Test
    public void execute_indexOutsideFilteredList_leavesModelUnchanged() {
        showStudentAtIndex(model, INDEX_FIRST_STUDENT);
        assertCommandFailure(new DeleteMasteryCheckCommand(INDEX_SECOND_STUDENT, 1), model,
                Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals_indexAndNumberDifferences_returnsExpectedEquality() {
        DeleteMasteryCheckCommand command = new DeleteMasteryCheckCommand(INDEX_FIRST_STUDENT, 1);
        assertEquals(command, command);
        DeleteMasteryCheckCommand equalCommand =
                new DeleteMasteryCheckCommand(
                        INDEX_FIRST_STUDENT,
                        1);

        assertEquals(equalCommand, command);
        assertEquals(equalCommand.hashCode(), command.hashCode());
        assertNotEquals(new DeleteMasteryCheckCommand(INDEX_SECOND_STUDENT, 1), command);
        assertNotEquals(new DeleteMasteryCheckCommand(INDEX_FIRST_STUDENT, 2), command);
        assertNotEquals(null, command);
        assertNotEquals("delmc", command);
    }

    @Test
    public void toString_validCommand_includesStudentIndexAndMasteryCheckNumber() {
        DeleteMasteryCheckCommand command = new DeleteMasteryCheckCommand(INDEX_FIRST_STUDENT, 1);
        assertEquals(DeleteMasteryCheckCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_STUDENT
                + ", number=1}", command.toString());
    }
}

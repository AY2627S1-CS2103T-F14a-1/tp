package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
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
import seedu.address.model.student.RegisteredMasteryChecks;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioRecord;
import seedu.address.testutil.StudentBuilder;
import seedu.address.testutil.StudentBuilder.StudioRecordSet;

/** Tests marking checks incomplete, validation, copy behavior, and filtered-list targeting. */
public class UnmarkMasteryCheckCommandTest {
    private static final LocalDateTime APPOINTMENT = LocalDateTime.of(2026, 8, 18, 14, 30, 45, 123456789);
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new UnmarkMasteryCheckCommand(null, 1));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new UnmarkMasteryCheckCommand(INDEX_FIRST_STUDENT, 1).execute(null));
    }

    @Test
    public void constructor_invalidNumber_throwsIllegalArgumentException() {
        for (int number : new int[] {0, -1, Integer.MIN_VALUE}) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    new UnmarkMasteryCheckCommand(INDEX_FIRST_STUDENT, number));
            assertEquals(RegisteredMasteryChecks.MESSAGE_CONSTRAINTS, exception.getMessage());
        }
    }

    @Test
    public void execute_unregisteredNumber_reportsStudentAndLeavesModelUnchanged() {
        Student student = model.getFilteredStudentList().getFirst();
        assertCommandFailure(new UnmarkMasteryCheckCommand(INDEX_FIRST_STUDENT, Integer.MAX_VALUE), model,
                "Mastery check " + Integer.MAX_VALUE + " does not exist for " + student.getName()
                        + ". You can add one using the addmc command.");
    }

    @Test
    public void execute_existingAppointment_preservesOriginalAndAllOtherRecords() {
        Student original = model.getFilteredStudentList().getFirst();
        MasteryCheck target = createCheck(true);
        MasteryCheck other = createCheck(false);
        Student student = new StudentBuilder(original).withMissionSubmissions("M03")
                .withStudioRecords(new StudioRecordSet(3, new StudioRecord(50)))
                .withMasteryChecks(Map.of(1, target, 2, other)).build();
        model.setStudent(original, student);
        Student updated = new StudentBuilder(student).withMasteryChecks(Map.of(1, createCheck(false), 2, other))
                .build();
        Model expected = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expected.setStudent(student, updated);
        String expectedMessage =
                "Marked " + student.getName() + "’s mastery check 1 as incomplete.";

        assertCommandSuccess(
                new UnmarkMasteryCheckCommand(INDEX_FIRST_STUDENT, 1),
                model,
                new CommandResult(expectedMessage, updated),
                expected);
        MasteryCheck saved = model.getFilteredStudentList().getFirst().getRegisteredMasteryChecks()
                .getMasteryCheckAppointments().get(1);
        assertTrue(target.isComplete());
        assertFalse(saved.isComplete());
        assertEquals(APPOINTMENT, saved.getAppointmentTime());
        assertNotSame(target, saved);
        assertSame(other, model.getFilteredStudentList().getFirst().getRegisteredMasteryChecks()
                .getMasteryCheckAppointments().get(2));
    }

    @Test
    public void execute_alreadyIncomplete_leavesStudentAndModelUnchanged() {
        Student original = model.getFilteredStudentList().getFirst();
        Student student = new StudentBuilder(original).withMasteryChecks(Map.of(1, createCheck(false))).build();
        model.setStudent(original, student);
        Model expected = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        String expectedMessage = String.format(
                UnmarkMasteryCheckCommand.MESSAGE_ALREADY_MARKED,
                student.getName(),
                1);

        assertCommandSuccess(
                new UnmarkMasteryCheckCommand(INDEX_FIRST_STUDENT, 1),
                model,
                new CommandResult(expectedMessage, student),
                expected);
        assertSame(student, model.getFilteredStudentList().getFirst());
    }

    @Test
    public void execute_repeatedRequest_changesStatusOnlyOnce() throws Exception {
        Student original = model.getFilteredStudentList().getFirst();
        Student student = new StudentBuilder(original).withMasteryChecks(Map.of(1, createCheck(true))).build();
        model.setStudent(original, student);
        UnmarkMasteryCheckCommand command = new UnmarkMasteryCheckCommand(INDEX_FIRST_STUDENT, 1);
        command.execute(model);
        Student afterFirst = model.getFilteredStudentList().getFirst();
        assertEquals(String.format(UnmarkMasteryCheckCommand.MESSAGE_ALREADY_MARKED, student.getName(), 1),
                command.execute(model).getFeedbackToUser());
        assertSame(afterFirst, model.getFilteredStudentList().getFirst());
    }

    @Test
    public void execute_boundaryNumbers_updatesBothRegistrations() throws Exception {
        Student original = model.getFilteredStudentList().getFirst();
        Student student = new StudentBuilder(original).withMasteryChecks(Map.of(
                1, createCheck(true), Integer.MAX_VALUE, createCheck(true))).build();
        model.setStudent(original, student);
        new UnmarkMasteryCheckCommand(INDEX_FIRST_STUDENT, 1).execute(model);
        new UnmarkMasteryCheckCommand(INDEX_FIRST_STUDENT, Integer.MAX_VALUE).execute(model);
        Student expected = new StudentBuilder(student).withMasteryChecks(Map.of(
                1, createCheck(false), Integer.MAX_VALUE, createCheck(false))).build();
        assertEquals(expected, model.getFilteredStudentList().getFirst());
        assertTrue(student.getRegisteredMasteryChecks().getMasteryCheckAppointments().get(1).isComplete());
    }

    @Test
    public void execute_invalidIndex_leavesModelUnchanged() {
        Index invalid = Index.fromOneBased(model.getFilteredStudentList().size() + 1);
        assertCommandFailure(new UnmarkMasteryCheckCommand(invalid, 1), model,
                Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_emptyList_reportsInvalidIndex() {
        assertCommandFailure(new UnmarkMasteryCheckCommand(INDEX_FIRST_STUDENT, 1), new ModelManager(),
                Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_filteredList_updatesDisplayedStudentAndPreservesFilter() {
        Student original = model.getFilteredStudentList().get(INDEX_SECOND_STUDENT.getZeroBased());
        Student student = new StudentBuilder(original).withMasteryChecks(Map.of(1, createCheck(true))).build();
        model.setStudent(original, student);
        Model expected = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        showStudentAtIndex(model, INDEX_SECOND_STUDENT);
        showStudentAtIndex(expected, INDEX_SECOND_STUDENT);
        Student updated = new StudentBuilder(student).withMasteryChecks(Map.of(1, createCheck(false))).build();
        expected.setStudent(student, updated);
        String expectedMessage = String.format(
                UnmarkMasteryCheckCommand.MESSAGE_SUCCESS,
                student.getName(),
                1);

        assertCommandSuccess(
                new UnmarkMasteryCheckCommand(INDEX_FIRST_STUDENT, 1),
                model,
                new CommandResult(expectedMessage, updated),
                expected);
        assertEquals(1, model.getFilteredStudentList().size());
    }

    @Test
    public void execute_indexOutsideFilteredList_leavesModelUnchanged() {
        showStudentAtIndex(model, INDEX_FIRST_STUDENT);
        assertCommandFailure(new UnmarkMasteryCheckCommand(INDEX_SECOND_STUDENT, 1), model,
                Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals_indexNumberAndCommandType_returnsExpectedEquality() {
        UnmarkMasteryCheckCommand command = new UnmarkMasteryCheckCommand(INDEX_FIRST_STUDENT, 1);
        assertEquals(command, command);
        UnmarkMasteryCheckCommand equalCommand =
                new UnmarkMasteryCheckCommand(
                        INDEX_FIRST_STUDENT,
                        1);

        assertEquals(equalCommand, command);
        assertEquals(equalCommand.hashCode(), command.hashCode());
        assertNotEquals(new UnmarkMasteryCheckCommand(INDEX_SECOND_STUDENT, 1), command);
        assertNotEquals(new UnmarkMasteryCheckCommand(INDEX_FIRST_STUDENT, 2), command);
        assertNotEquals(new MarkMasteryCheckCommand(INDEX_FIRST_STUDENT, 1), command);
        assertNotEquals(null, command);
        assertNotEquals("unmarkmc", command);
    }

    @Test
    public void toString_validCommand_includesTargetIndexAndNumber() {
        UnmarkMasteryCheckCommand command = new UnmarkMasteryCheckCommand(INDEX_FIRST_STUDENT, 1);
        assertEquals(UnmarkMasteryCheckCommand.class.getCanonicalName() + "{targetStudentIndex=" + INDEX_FIRST_STUDENT
                + ", number=1}", command.toString());
    }

    /**
     * Creates a fresh appointment with the requested completion status.
     *
     * @param isComplete Whether the appointment should start complete.
     * @return An appointment at the shared test time with the requested status.
     */
    private static MasteryCheck createCheck(boolean isComplete) {
        MasteryCheck check = new MasteryCheck(APPOINTMENT);
        if (isComplete) {
            check.markComplete();
        }
        return check;
    }
}

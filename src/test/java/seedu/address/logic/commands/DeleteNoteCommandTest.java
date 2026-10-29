package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_NOTE;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_INVALID_UPPER_NOTE;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_NOTE;
import static seedu.address.testutil.TypicalStudents.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.Note;
import seedu.address.model.student.RegisteredNotes;
import seedu.address.model.student.Student;

/** Tests the execution and value semantics of {@link DeleteNoteCommand}. */
public class DeleteNoteCommandTest {

    private static final Note NOTE = new Note("Review lecture notes");

    @Test
    public void constructor_nullIndexOrInvalidId_throwsException() {
        assertThrows(NullPointerException.class, () -> new DeleteNoteCommand(null, INDEX_FIRST_NOTE));
        assertThrows(NullPointerException.class, () -> new DeleteNoteCommand(INDEX_FIRST_STUDENT, null));
        assertThrows(IllegalArgumentException.class, () ->
                new DeleteNoteCommand(INDEX_FIRST_STUDENT, INDEX_INVALID_UPPER_NOTE));
    }

    @Test
    public void execute_existingNote_removesNoteFromStudent() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Student studentToUpdate = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        Student studentWithNote = studentToUpdate.withRegisteredNotes(new RegisteredNotes().withNote(NOTE));
        model.setStudent(studentToUpdate, studentWithNote);
        DeleteNoteCommand command = new DeleteNoteCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_NOTE);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setStudent(studentWithNote, studentToUpdate);

        Student updatedStudent = studentToUpdate;

        assertCommandSuccess(
                command,
                model,
                new CommandResult(
                        String.format(
                                DeleteNoteCommand.MESSAGE_SUCCESS,
                                studentWithNote.getName(),
                                INDEX_FIRST_NOTE.getOneBased()),
                        updatedStudent),
                expectedModel);
    }

    @Test
    public void execute_missingNote_reportsNoChange() throws Exception {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Student studentToUpdate = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        DeleteNoteCommand command = new DeleteNoteCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_NOTE);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(
                command,
                model,
                new CommandResult(
                        String.format(
                                DeleteNoteCommand.MESSAGE_ALREADY_NOT_RECORDED,
                                studentToUpdate.getName()),
                        studentToUpdate),
                expectedModel);
    }

    @Test
    public void execute_invalidDisplayedIndex_throwsCommandException() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Index invalidIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);
        DeleteNoteCommand command = new DeleteNoteCommand(invalidIndex, INDEX_FIRST_NOTE);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        DeleteNoteCommand command = new DeleteNoteCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_NOTE);

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void equals_sameAndDifferentArguments_returnsExpectedEquality() {
        DeleteNoteCommand command = new DeleteNoteCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_NOTE);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new DeleteNoteCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_NOTE)));
        assertFalse(command.equals(new DeleteNoteCommand(INDEX_FIRST_STUDENT, INDEX_SECOND_NOTE)));
        assertFalse(command.equals(null));
        assertFalse(command.equals("delnote"));
    }

    @Test
    public void hashCode_equalCommands_returnsEqualHashes() {
        DeleteNoteCommand command = new DeleteNoteCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_NOTE);
        DeleteNoteCommand equalCommand = new DeleteNoteCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_NOTE);

        assertEquals(command.hashCode(), equalCommand.hashCode());
    }

    @Test
    public void toString_validCommand_includesCommandDetails() {
        DeleteNoteCommand command = new DeleteNoteCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_NOTE);

        assertEquals(DeleteNoteCommand.class.getCanonicalName()
                + "{targetIndex=seedu.address.commons.core.index.Index{zeroBasedIndex=0}, "
                + "noteIndex=seedu.address.commons.core.index.Index{zeroBasedIndex=0}}",
                command.toString());
    }
}

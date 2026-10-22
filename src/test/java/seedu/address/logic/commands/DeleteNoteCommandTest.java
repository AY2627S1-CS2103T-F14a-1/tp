package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
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

    private static final int NOTE_ID = 3;
    private static final Note NOTE = new Note("Review lecture notes");

    @Test
    public void constructor_nullIndexOrInvalidId_throwsException() {
        assertThrows(NullPointerException.class, () -> new DeleteNoteCommand(null, NOTE_ID));
        assertThrows(IllegalArgumentException.class, () ->
                new DeleteNoteCommand(INDEX_FIRST_STUDENT, RegisteredNotes.MIN_NOTE_ID - 1));
        assertThrows(IllegalArgumentException.class, () ->
                new DeleteNoteCommand(INDEX_FIRST_STUDENT, RegisteredNotes.MAX_NOTE_ID + 1));
    }

    @Test
    public void execute_existingNote_removesNoteFromStudent() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Student studentToUpdate = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        Student studentWithNote = studentToUpdate.withRegisteredNotes(new RegisteredNotes().withNote(NOTE_ID, NOTE));
        model.setStudent(studentToUpdate, studentWithNote);
        DeleteNoteCommand command = new DeleteNoteCommand(INDEX_FIRST_STUDENT, NOTE_ID);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setStudent(studentWithNote, studentToUpdate);

        assertCommandSuccess(command, model,
                String.format(DeleteNoteCommand.MESSAGE_SUCCESS, studentWithNote.getName(), NOTE_ID), expectedModel);
    }

    @Test
    public void execute_missingNote_reportsNoChange() throws Exception {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Student studentToUpdate = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        DeleteNoteCommand command = new DeleteNoteCommand(INDEX_FIRST_STUDENT, NOTE_ID);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(command, model,
                String.format(DeleteNoteCommand.MESSAGE_ALREADY_NOT_RECORDED,
                        studentToUpdate.getName()), expectedModel);
    }

    @Test
    public void execute_invalidDisplayedIndex_throwsCommandException() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Index invalidIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);
        DeleteNoteCommand command = new DeleteNoteCommand(invalidIndex, NOTE_ID);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        DeleteNoteCommand command = new DeleteNoteCommand(INDEX_FIRST_STUDENT, NOTE_ID);

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void equals_sameAndDifferentArguments_returnsExpectedEquality() {
        DeleteNoteCommand command = new DeleteNoteCommand(INDEX_FIRST_STUDENT, NOTE_ID);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new DeleteNoteCommand(INDEX_FIRST_STUDENT, NOTE_ID)));
        assertFalse(command.equals(new DeleteNoteCommand(INDEX_FIRST_STUDENT, NOTE_ID + 1)));
        assertFalse(command.equals(null));
        assertFalse(command.equals("delnote"));
    }

    @Test
    public void hashCode_equalCommands_returnsEqualHashes() {
        DeleteNoteCommand command = new DeleteNoteCommand(INDEX_FIRST_STUDENT, NOTE_ID);
        DeleteNoteCommand equalCommand = new DeleteNoteCommand(INDEX_FIRST_STUDENT, NOTE_ID);

        assertEquals(command.hashCode(), equalCommand.hashCode());
    }

    @Test
    public void toString_validCommand_includesCommandDetails() {
        DeleteNoteCommand command = new DeleteNoteCommand(INDEX_FIRST_STUDENT, NOTE_ID);

        assertEquals(DeleteNoteCommand.class.getCanonicalName()
                + "{targetIndex=seedu.address.commons.core.index.Index{zeroBasedIndex=0}, noteId=3}",
                command.toString());
    }
}

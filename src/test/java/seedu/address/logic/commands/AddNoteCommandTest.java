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

/** Tests the execution and value semantics of {@link AddNoteCommand}. */
public class AddNoteCommandTest {

    private static final int NOTE_ID = 3;
    private static final Note NOTE = new Note("Review lecture notes");

    @Test
    public void constructor_nullArgumentsOrInvalidId_throwsException() {
        assertThrows(NullPointerException.class, () -> new AddNoteCommand(null, NOTE_ID, NOTE));
        assertThrows(NullPointerException.class, () -> new AddNoteCommand(INDEX_FIRST_STUDENT, NOTE_ID, null));
        assertThrows(IllegalArgumentException.class, () ->
                new AddNoteCommand(INDEX_FIRST_STUDENT, RegisteredNotes.MIN_NOTE_ID - 1, NOTE));
        assertThrows(IllegalArgumentException.class, () ->
                new AddNoteCommand(INDEX_FIRST_STUDENT, RegisteredNotes.MAX_NOTE_ID + 1, NOTE));
    }

    @Test
    public void execute_newNote_updatesStudent() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Student studentToUpdate = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        AddNoteCommand command = new AddNoteCommand(INDEX_FIRST_STUDENT, NOTE_ID, NOTE);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Student updatedStudent = studentToUpdate.withRegisteredNotes(new RegisteredNotes().withNote(NOTE_ID, NOTE));
        expectedModel.setStudent(studentToUpdate, updatedStudent);

        assertCommandSuccess(command, model,
                String.format(AddNoteCommand.MESSAGE_SUCCESS, studentToUpdate.getName(), NOTE_ID), expectedModel);
    }

    @Test
    public void execute_existingNoteId_reportsNoChange() throws Exception {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Student studentToUpdate = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        model.setStudent(studentToUpdate, studentToUpdate.withRegisteredNotes(
                new RegisteredNotes().withNote(NOTE_ID, NOTE)));
        AddNoteCommand command = new AddNoteCommand(INDEX_FIRST_STUDENT, NOTE_ID, new Note("Different note"));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(command, model,
                String.format(AddNoteCommand.MESSAGE_ALREADY_RECORDED, studentToUpdate.getName()), expectedModel);
    }

    @Test
    public void execute_invalidDisplayedIndex_throwsCommandException() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Index invalidIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);
        AddNoteCommand command = new AddNoteCommand(invalidIndex, NOTE_ID, NOTE);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        AddNoteCommand command = new AddNoteCommand(INDEX_FIRST_STUDENT, NOTE_ID, NOTE);

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void equals_sameAndDifferentArguments_returnsExpectedEquality() {
        AddNoteCommand command = new AddNoteCommand(INDEX_FIRST_STUDENT, NOTE_ID, NOTE);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new AddNoteCommand(INDEX_FIRST_STUDENT, NOTE_ID, NOTE)));
        assertFalse(command.equals(new AddNoteCommand(INDEX_FIRST_STUDENT, NOTE_ID, new Note("Other note"))));
        assertFalse(command.equals(new AddNoteCommand(INDEX_FIRST_STUDENT, NOTE_ID + 1, NOTE)));
        assertFalse(command.equals(null));
        assertFalse(command.equals("addnote"));
    }

    @Test
    public void toString_validCommand_includesCommandDetails() {
        AddNoteCommand command = new AddNoteCommand(INDEX_FIRST_STUDENT, NOTE_ID, NOTE);

        assertEquals(AddNoteCommand.class.getCanonicalName()
                + "{targetIndex=seedu.address.commons.core.index.Index{zeroBasedIndex=0}, "
                + "noteId=3, note=Review lecture notes}", command.toString());
    }
}

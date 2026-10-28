package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_STUDENT;
import static seedu.address.testutil.TypicalStudents.getTypicalAddressBook;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.Note;
import seedu.address.model.student.RegisteredNotes;
import seedu.address.model.student.Student;

/** Tests the execution and value semantics of {@link AddNoteCommand}. */
public class AddNoteCommandTest {

    private static final Note NOTE = new Note("Review lecture notes");

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddNoteCommand(null, NOTE));
        assertThrows(NullPointerException.class, () -> new AddNoteCommand(INDEX_FIRST_STUDENT, null));
    }

    @Test
    public void execute_newNote_updatesStudent() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Student studentToUpdate = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        AddNoteCommand command = new AddNoteCommand(INDEX_FIRST_STUDENT, NOTE);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Student updatedStudent = studentToUpdate.withRegisteredNotes(new RegisteredNotes().withNote(NOTE));
        expectedModel.setStudent(studentToUpdate, updatedStudent);

        assertCommandSuccess(command, model,
                String.format(AddNoteCommand.MESSAGE_SUCCESS, studentToUpdate.getName()), expectedModel);
    }

    @Test
    public void execute_existingNotes_appendsNote() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Student studentToUpdate = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        RegisteredNotes existingNotes = new RegisteredNotes(List.of(new Note("Existing note")));
        Student studentWithNote = studentToUpdate.withRegisteredNotes(existingNotes);
        model.setStudent(studentToUpdate, studentWithNote);
        AddNoteCommand command = new AddNoteCommand(INDEX_FIRST_STUDENT, NOTE);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setStudent(studentWithNote,
                studentWithNote.withRegisteredNotes(existingNotes.withNote(NOTE)));

        assertCommandSuccess(command, model,
                String.format(AddNoteCommand.MESSAGE_SUCCESS, studentToUpdate.getName()), expectedModel);
    }

    @Test
    public void execute_matchingNoteText_appendsNoteAndReturnsWarning() throws CommandException {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Student studentToUpdate = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        RegisteredNotes existingNotes = new RegisteredNotes(List.of(NOTE));
        Student studentWithNote = studentToUpdate.withRegisteredNotes(existingNotes);
        model.setStudent(studentToUpdate, studentWithNote);
        AddNoteCommand command = new AddNoteCommand(INDEX_FIRST_STUDENT, NOTE);

        CommandResult result = command.execute(model);
        Student expectedStudent = studentWithNote.withRegisteredNotes(existingNotes.withNote(NOTE));

        assertEquals(String.format(AddNoteCommand.MESSAGE_SUCCESS, studentToUpdate.getName()),
                result.getFeedbackToUser());
        assertEquals(List.of(String.format(
                AddNoteCommand.MESSAGE_WARNING_MATCHING_TEXT, studentToUpdate.getName())), result.getWarnings());
        assertEquals(expectedStudent, model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased()));
    }

    @Test
    public void execute_maximumNotesReached_reportsNoChangeAndPreservesModel() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Student studentToUpdate = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        RegisteredNotes maximumNotes = new RegisteredNotes(
                Collections.nCopies(RegisteredNotes.MAX_NOTE_INDEX, NOTE));
        model.setStudent(studentToUpdate, studentToUpdate.withRegisteredNotes(maximumNotes));
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        AddNoteCommand command = new AddNoteCommand(INDEX_FIRST_STUDENT, NOTE);

        assertCommandSuccess(command, model,
                String.format(AddNoteCommand.MESSAGE_MAXIMUM_NOTES_REACHED,
                        studentToUpdate.getName(), RegisteredNotes.MAX_NOTE_INDEX), expectedModel);
    }

    @Test
    public void execute_invalidDisplayedIndex_throwsCommandException() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Index invalidIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);
        AddNoteCommand command = new AddNoteCommand(invalidIndex, NOTE);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        AddNoteCommand command = new AddNoteCommand(INDEX_FIRST_STUDENT, NOTE);

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void equals_sameAndDifferentArguments_returnsExpectedEquality() {
        AddNoteCommand command = new AddNoteCommand(INDEX_FIRST_STUDENT, NOTE);

        assertTrue(command.equals(command));
        assertTrue(command.equals(new AddNoteCommand(INDEX_FIRST_STUDENT, NOTE)));
        assertFalse(command.equals(new AddNoteCommand(INDEX_SECOND_STUDENT, NOTE)));
        assertFalse(command.equals(new AddNoteCommand(INDEX_FIRST_STUDENT, new Note("Other note"))));
        assertFalse(command.equals(null));
        assertFalse(command.equals("addnote"));
    }

    @Test
    public void hashCode_equalCommands_returnsEqualHashes() {
        AddNoteCommand command = new AddNoteCommand(INDEX_FIRST_STUDENT, NOTE);
        AddNoteCommand equalCommand = new AddNoteCommand(INDEX_FIRST_STUDENT, NOTE);

        assertEquals(command.hashCode(), equalCommand.hashCode());
    }

    @Test
    public void toString_validCommand_includesCommandDetails() {
        AddNoteCommand command = new AddNoteCommand(INDEX_FIRST_STUDENT, NOTE);

        assertEquals(AddNoteCommand.class.getCanonicalName()
                + "{targetIndex=seedu.address.commons.core.index.Index{zeroBasedIndex=0}, "
                + "note=Review lecture notes}", command.toString());
    }
}

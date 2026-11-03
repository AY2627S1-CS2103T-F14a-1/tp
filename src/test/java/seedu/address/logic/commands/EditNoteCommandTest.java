package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.getOutOfBoundsStudentIndexMessage;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_NOTE;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_INVALID_UPPER_NOTE;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_NOTE;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_STUDENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_NOTE;
import static seedu.address.testutil.TypicalStudents.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.Note;
import seedu.address.model.student.RegisteredNotes;
import seedu.address.model.student.Student;

/** Tests execution and argument validation for {@link EditNoteCommand}. */
public class EditNoteCommandTest {

    private static final Note ORIGINAL_NOTE = new Note("Review lecture notes");
    private static final Note UPDATED_NOTE = new Note("Review tutorial notes");

    @Test
    public void constructor_nullArgumentsOrInvalidIndex_throwsException() {
        assertThrows(NullPointerException.class, () -> new EditNoteCommand(null, INDEX_FIRST_NOTE, UPDATED_NOTE));
        assertThrows(NullPointerException.class, () -> new EditNoteCommand(INDEX_FIRST_STUDENT, null, UPDATED_NOTE));
        assertThrows(NullPointerException.class, () ->
                new EditNoteCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_NOTE, null));
        assertThrows(IllegalArgumentException.class, () ->
                new EditNoteCommand(INDEX_FIRST_STUDENT, INDEX_INVALID_UPPER_NOTE, UPDATED_NOTE));
    }

    @Test
    public void execute_existingNote_replacesNoteAndPreservesOtherNotes() {
        Model model = createModelWithNotes();
        Student studentToUpdate = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        EditNoteCommand command = new EditNoteCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_NOTE, UPDATED_NOTE);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        RegisteredNotes expectedNotes = new RegisteredNotes(List.of(
                UPDATED_NOTE, new Note("Prepare consultation")));

        Student updatedStudent = studentToUpdate.withRegisteredNotes(expectedNotes);
        expectedModel.setStudent(studentToUpdate, updatedStudent);

        assertCommandSuccess(
                command,
                model,
                new CommandResult(
                        String.format(
                                EditNoteCommand.MESSAGE_SUCCESS,
                                studentToUpdate.getName(),
                                INDEX_FIRST_NOTE.getOneBased()),
                        updatedStudent),
                expectedModel);
    }

    @Test
    public void execute_missingNoteIndex_reportsNoChange() {
        Model model = createModelWithNotes();
        EditNoteCommand command = new EditNoteCommand(INDEX_FIRST_STUDENT, INDEX_THIRD_NOTE, UPDATED_NOTE);

        assertCommandFailure(command, model, EditNoteCommand.MESSAGE_INDEX_NOT_RECORDED);
    }

    @Test
    public void execute_matchingNoteText_appendsNoteAndReturnsWarning() throws CommandException {
        Note note = new Note("Review lecture notes");
        Note noteToEdit = new Note("Review");
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Student studentToUpdate = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        RegisteredNotes existingNotes = new RegisteredNotes(List.of(note, noteToEdit));
        Student studentWithNote = studentToUpdate.withRegisteredNotes(existingNotes);
        model.setStudent(studentToUpdate, studentWithNote);
        Index noteToEditIndex = Index.fromOneBased(2);
        Note newNote = new Note("Review lecture notes");
        EditNoteCommand command = new EditNoteCommand(
                INDEX_FIRST_STUDENT, noteToEditIndex, newNote);

        CommandResult result = command.execute(model);
        Student expectedStudent = studentWithNote.withRegisteredNotes(existingNotes.editNote(noteToEditIndex, newNote));

        assertEquals(String.format(EditNoteCommand.MESSAGE_SUCCESS, studentToUpdate.getName(), 2),
                result.getFeedbackToUser());
        assertEquals(List.of(String.format(
                EditNoteCommand.MESSAGE_WARNING_MATCHING_TEXT,
                studentToUpdate.getName(), "[1]")), result.getWarnings());
        assertEquals(expectedStudent, model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased()));
    }

    @Test
    public void execute_multipleMatchingNoteTexts_replacesNoteAndReturnsAllMatchingIndexes() throws CommandException {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Student studentToUpdate = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        Note matchingNote = new Note("Review lecture notes");
        Index noteToEditIndex = Index.fromOneBased(2);
        RegisteredNotes existingNotes = new RegisteredNotes(List.of(
                matchingNote, new Note("Review"), new Note("review   lecture notes")));
        Student studentWithNotes = studentToUpdate.withRegisteredNotes(existingNotes);
        model.setStudent(studentToUpdate, studentWithNotes);
        EditNoteCommand command = new EditNoteCommand(INDEX_FIRST_STUDENT, noteToEditIndex, matchingNote);

        CommandResult result = command.execute(model);
        Student expectedStudent = studentWithNotes.withRegisteredNotes(
                existingNotes.editNote(noteToEditIndex, matchingNote));

        assertEquals(String.format(EditNoteCommand.MESSAGE_SUCCESS, studentToUpdate.getName(), 2),
                result.getFeedbackToUser());
        assertEquals(List.of(String.format(
                EditNoteCommand.MESSAGE_WARNING_MATCHING_TEXT, studentToUpdate.getName(), "[1, 3]")),
                result.getWarnings());
        assertEquals(expectedStudent, model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased()));
    }

    @Test
    public void execute_sameNote_reportsNoChange() {
        Model model = createModelWithNotes();
        Student studentToUpdate = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        EditNoteCommand command = new EditNoteCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_NOTE, ORIGINAL_NOTE);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(
                command,
                model,
                new CommandResult(
                        String.format(
                                EditNoteCommand.MESSAGE_SAME_NOTE,
                                studentToUpdate.getName(),
                                INDEX_FIRST_NOTE.getOneBased()),
                        studentToUpdate),
                expectedModel);
    }

    @Test
    public void execute_invalidDisplayedIndex_throwsCommandException() {
        Model model = createModelWithNotes();
        Index invalidIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);
        EditNoteCommand command = new EditNoteCommand(invalidIndex, INDEX_FIRST_NOTE, UPDATED_NOTE);

        assertCommandFailure(command, model, getOutOfBoundsStudentIndexMessage(invalidIndex, model));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        EditNoteCommand command = new EditNoteCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_NOTE, UPDATED_NOTE);

        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void equals_differentFieldsOrTypes_returnsExpectedEquality() {
        EditNoteCommand command = new EditNoteCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_NOTE, UPDATED_NOTE);

        assertEquals(command, command);
        assertEquals(new EditNoteCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_NOTE, UPDATED_NOTE), command);
        assertNotEquals(new EditNoteCommand(INDEX_SECOND_STUDENT, INDEX_FIRST_NOTE, UPDATED_NOTE), command);
        assertNotEquals(new EditNoteCommand(INDEX_FIRST_STUDENT, INDEX_SECOND_NOTE, UPDATED_NOTE), command);
        assertNotEquals(new EditNoteCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_NOTE, ORIGINAL_NOTE), command);
        assertNotEquals(new EditNoteCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_NOTE, UPDATED_NOTE), 1);
        assertNotEquals(null, command);
        assertNotEquals("editnote", command);
    }

    @Test
    public void hashCode_equalCommands_returnsEqualHashes() {
        EditNoteCommand command = new EditNoteCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_NOTE, UPDATED_NOTE);
        EditNoteCommand equalCommand = new EditNoteCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_NOTE, UPDATED_NOTE);

        assertEquals(command.hashCode(), equalCommand.hashCode());
    }

    @Test
    public void toString_validCommand_includesCommandDetails() {
        EditNoteCommand command = new EditNoteCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_NOTE, UPDATED_NOTE);

        assertEquals(EditNoteCommand.class.getCanonicalName()
                + "{targetIndex=seedu.address.commons.core.index.Index{zeroBasedIndex=0}, "
                + "noteIndex=seedu.address.commons.core.index.Index{zeroBasedIndex=0}, "
                + "note=Review tutorial notes}", command.toString());
    }

    /**
     * Creates a model whose first displayed student has two distinct registered notes.
     *
     * @return A model suitable for exercising note-editing behavior.
     */
    private Model createModelWithNotes() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Student studentToUpdate = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        RegisteredNotes notes = new RegisteredNotes(List.of(
                ORIGINAL_NOTE, new Note("Prepare consultation")));
        model.setStudent(studentToUpdate, studentToUpdate.withRegisteredNotes(notes));
        return model;
    }
}

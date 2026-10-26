package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
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
import seedu.address.logic.Messages;
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
        expectedModel.setStudent(studentToUpdate, studentToUpdate.withRegisteredNotes(expectedNotes));

        assertCommandSuccess(command, model,
                String.format(EditNoteCommand.MESSAGE_SUCCESS,
                        studentToUpdate.getName(), INDEX_FIRST_NOTE.getOneBased()), expectedModel);
    }

    @Test
    public void execute_missingNoteIndex_reportsNoChange() {
        Model model = createModelWithNotes();
        Student studentToUpdate = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        EditNoteCommand command = new EditNoteCommand(INDEX_FIRST_STUDENT, INDEX_THIRD_NOTE, UPDATED_NOTE);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(command, model,
                String.format(EditNoteCommand.MESSAGE_INDEX_NOT_RECORDED,
                        studentToUpdate.getName(), INDEX_THIRD_NOTE.getOneBased()),
                expectedModel);
    }

    @Test
    public void execute_sameNote_reportsNoChange() {
        Model model = createModelWithNotes();
        Student studentToUpdate = model.getFilteredStudentList().get(INDEX_FIRST_STUDENT.getZeroBased());
        EditNoteCommand command = new EditNoteCommand(INDEX_FIRST_STUDENT, INDEX_FIRST_NOTE, ORIGINAL_NOTE);
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(command, model,
                String.format(EditNoteCommand.MESSAGE_SAME_NOTE,
                        studentToUpdate.getName(), INDEX_FIRST_NOTE.getOneBased()), expectedModel);
    }

    @Test
    public void execute_invalidDisplayedIndex_throwsCommandException() {
        Model model = createModelWithNotes();
        Index invalidIndex = Index.fromOneBased(model.getFilteredStudentList().size() + 1);
        EditNoteCommand command = new EditNoteCommand(invalidIndex, INDEX_FIRST_NOTE, UPDATED_NOTE);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
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

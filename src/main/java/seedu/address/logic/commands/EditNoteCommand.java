package seedu.address.logic.commands;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_TEXT;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.student.Note;
import seedu.address.model.student.RegisteredNotes;
import seedu.address.model.student.Student;

/**
 * Edits a note for a student identified by a displayed index.
 */
public class EditNoteCommand extends Command {

    public static final String COMMAND_WORD = "editnote";

    public static final String MESSAGE_ID_NOT_RECORDED =
            "%1$s does not have a registered note with ID %2$d. No changes made.";
    public static final String MESSAGE_SAME_NOTE =
            "%1$s’s note with ID %2$d is already the same. No changes made.";
    public static final String MESSAGE_SUCCESS =
            "Edited %1$s's note with ID %2$d.";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits a registered note for the selected student.\n"
            + "Parameters: " + PREFIX_INDEX + "INDEX " + PREFIX_NOTE_ID + "ID " + PREFIX_NOTE_TEXT + "TEXT\n"
            + "INDEX must be a positive integer identifying a displayed student.\n"
            + "ID must be between " + RegisteredNotes.MIN_NOTE_ID
            + " and " + RegisteredNotes.MAX_NOTE_ID + " inclusive.\n"
            + "TEXT must only contain alphanumeric characters and spaces, should not be blank, and "
            + "be less than or equal to " + Note.MAX_NOTE_LENGTH + " characters.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_INDEX + "1 "
            + PREFIX_NOTE_ID + "3 " + PREFIX_NOTE_TEXT + "hello";

    private final Index targetIndex;
    private final int noteId;
    private final Note updatedNote;

    /**
     * @param targetIndex Index of the student in the filtered student list to edit.
     * @param noteId ID of note to edit.
     * @param updatedNote Updated note to register.
     */
    public EditNoteCommand(Index targetIndex, int noteId, Note updatedNote) {
        requireNonNull(targetIndex);
        requireNonNull(updatedNote);

        checkArgument(RegisteredNotes.isValidNoteId(noteId), RegisteredNotes.MESSAGE_CONSTRAINTS);

        this.targetIndex = targetIndex;
        this.noteId = noteId;
        this.updatedNote = updatedNote;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Student studentToUpdate = getStudentAtDisplayedIndex(model, targetIndex);
        RegisteredNotes registeredNotes = studentToUpdate.getRegisteredNotes();

        if (!registeredNotes.hasNoteId(noteId)) {
            return new CommandResult(String.format(MESSAGE_ID_NOT_RECORDED, studentToUpdate.getName(), noteId));
        }

        if (registeredNotes.hasMatchingNote(noteId, updatedNote)) {
            return new CommandResult(String.format(MESSAGE_SAME_NOTE, studentToUpdate.getName(), noteId));
        }

        Student updatedStudent = studentToUpdate.withRegisteredNotes(
                registeredNotes.editNote(noteId, updatedNote));
        model.setStudent(studentToUpdate, updatedStudent);
        return new CommandResult(String.format(MESSAGE_SUCCESS, studentToUpdate.getName(), noteId));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof EditNoteCommand otherEditNoteCommand)) {
            return false;
        }

        return targetIndex.equals(otherEditNoteCommand.targetIndex)
                && noteId == otherEditNoteCommand.noteId
                && updatedNote.equals(otherEditNoteCommand.updatedNote);
    }

    @Override
    public int hashCode() {
        return hash(targetIndex, noteId, updatedNote);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("noteId", noteId)
                .add("note", updatedNote)
                .toString();
    }
}

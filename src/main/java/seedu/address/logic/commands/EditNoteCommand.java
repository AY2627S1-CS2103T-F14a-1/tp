package seedu.address.logic.commands;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_INDEX;
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

    public static final String MESSAGE_INDEX_NOT_RECORDED =
            "%1$s does not have a registered note with index %2$d. No changes made.";
    public static final String MESSAGE_SAME_NOTE =
            "%1$s’s note with index %2$d is already the same. No changes made.";
    public static final String MESSAGE_SUCCESS =
            "Edited %1$s's note with index %2$d.";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits a registered note for the selected student.\n"
            + "Parameters: " + PREFIX_INDEX + "INDEX " + PREFIX_NOTE_INDEX + "index " + PREFIX_NOTE_TEXT + "TEXT\n"
            + "INDEX must be a positive integer identifying a displayed student.\n"
            + "NOTE_INDEX must be a positive integer less than or equal to "
            + RegisteredNotes.MAX_NOTE_INDEX + " inclusive.\n"
            + "TEXT must only contain alphanumeric characters and spaces, should not be blank, and "
            + "be less than or equal to " + Note.MAX_NOTE_LENGTH + " characters.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_INDEX + "1 "
            + PREFIX_NOTE_INDEX + "3 " + PREFIX_NOTE_TEXT + "hello";

    private final Index targetIndex;
    private final Index noteIndex;
    private final Note updatedNote;

    /**
     * @param targetIndex Index of the student in the filtered student list to edit.
     * @param noteIndex Index of note to edit.
     * @param updatedNote Updated note to register.
     */
    public EditNoteCommand(Index targetIndex, Index noteIndex, Note updatedNote) {
        requireNonNull(targetIndex);
        requireNonNull(updatedNote);
        checkArgument(RegisteredNotes.isValidNoteIndex(noteIndex), RegisteredNotes.MESSAGE_CONSTRAINTS);

        this.targetIndex = targetIndex;
        this.noteIndex = noteIndex;
        this.updatedNote = updatedNote;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Student studentToUpdate = getStudentAtDisplayedIndex(model, targetIndex);
        RegisteredNotes registeredNotes = studentToUpdate.getRegisteredNotes();

        if (!registeredNotes.hasNote(noteIndex)) {
            return new CommandResult(String.format(MESSAGE_INDEX_NOT_RECORDED, studentToUpdate.getName(),
                    noteIndex.getOneBased()));
        }

        if (registeredNotes.getRegisteredNotes().get(noteIndex.getZeroBased()).equals(updatedNote)) {
            return new CommandResult(String.format(MESSAGE_SAME_NOTE, studentToUpdate.getName(),
                    noteIndex.getOneBased()));
        }

        Student updatedStudent = studentToUpdate.withRegisteredNotes(
                registeredNotes.editNote(noteIndex, updatedNote));
        model.setStudent(studentToUpdate, updatedStudent);
        return new CommandResult(String.format(MESSAGE_SUCCESS, studentToUpdate.getName(), noteIndex.getOneBased()));
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
                && noteIndex.equals(otherEditNoteCommand.noteIndex)
                && updatedNote.equals(otherEditNoteCommand.updatedNote);
    }

    @Override
    public int hashCode() {
        return hash(targetIndex, noteIndex, updatedNote);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("noteIndex", noteIndex)
                .add("note", updatedNote)
                .toString();
    }
}

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
 * Registers a note for a student identified by a displayed index.
 */
public class AddNoteCommand extends Command {

    public static final String COMMAND_WORD = "addnote";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Registers a note for the selected student.\n"
            + "Parameters: " + PREFIX_INDEX + "INDEX " + PREFIX_NOTE_ID + "ID " + PREFIX_NOTE_TEXT + "TEXT\n"
            + "INDEX must be a positive integer identifying a displayed student.\n"
            + "ID must be between " + RegisteredNotes.MIN_NOTE_ID
            + " and " + RegisteredNotes.MAX_NOTE_ID + " inclusive.\n"
            + "TEXT must only contain alphanumeric characters and spaces, should not be blank, and "
            + "be less than or equal to " + Note.MAX_NOTE_LENGTH + " characters.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_INDEX + "1 "
            + PREFIX_NOTE_ID + "3 " + PREFIX_NOTE_TEXT + "hello";

    public static final String MESSAGE_SUCCESS =
            "Added %1$s's note as ID %2$d.";
    public static final String MESSAGE_ALREADY_RECORDED =
            "%1$s’s note ID has already been registered. No changes made.";

    private final Index targetIndex;
    private final int noteId;
    private final Note note;

    /**
     * @param targetIndex Index of the student in the filtered student list to edit.
     * @param noteId ID to assign note with.
     * @param note Note to register to student.
     */
    public AddNoteCommand(Index targetIndex, int noteId, Note note) {
        requireNonNull(targetIndex);
        requireNonNull(note);

        checkArgument(RegisteredNotes.isValidNoteId(noteId), RegisteredNotes.MESSAGE_CONSTRAINTS);

        this.targetIndex = targetIndex;
        this.noteId = noteId;
        this.note = note;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Student studentToUpdate = getStudentAtDisplayedIndex(model, targetIndex);
        RegisteredNotes registeredNotes = studentToUpdate.getRegisteredNotes();

        if (registeredNotes.hasNote(noteId)) {
            return new CommandResult(String.format(MESSAGE_ALREADY_RECORDED, studentToUpdate.getName()), studentToUpdate);
        }

        Student updatedStudent = studentToUpdate.withRegisteredNotes(
                registeredNotes.withNote(noteId, note));
        model.setStudent(studentToUpdate, updatedStudent);
        return new CommandResult(String.format(MESSAGE_SUCCESS, studentToUpdate.getName(), noteId), updatedStudent);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddNoteCommand otherAddNoteCommand)) {
            return false;
        }

        return targetIndex.equals(otherAddNoteCommand.targetIndex)
                && noteId == otherAddNoteCommand.noteId
                && note.equals(otherAddNoteCommand.note);
    }

    @Override
    public int hashCode() {
        return hash(targetIndex, noteId, note);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("noteId", noteId)
                .add("note", note)
                .toString();
    }
}

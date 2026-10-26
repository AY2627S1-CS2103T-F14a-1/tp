package seedu.address.logic.commands;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
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
            + "Parameters: " + PREFIX_INDEX + "INDEX " + PREFIX_NOTE_TEXT + "TEXT\n"
            + "INDEX must be a positive integer identifying a displayed student.\n"
            + "TEXT must only contain alphanumeric characters and spaces, should not be blank, and "
            + "be less than or equal to " + Note.MAX_NOTE_LENGTH + " characters.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_INDEX + "1 "
            + PREFIX_NOTE_TEXT + "hello";

    public static final String MESSAGE_SUCCESS =
            "Added %1$s's note.";
    public static final String MESSAGE_ALREADY_RECORDED =
            "%1$s’s note index has already been registered. No changes made.";

    private final Index targetIndex;
    private final Note note;

    /**
     * @param targetIndex Index of the student in the filtered student list to edit.
     * @param note        Note to register to student.
     */
    public AddNoteCommand(Index targetIndex, Note note) {
        requireNonNull(targetIndex);
        requireNonNull(note);

        this.targetIndex = targetIndex;
        this.note = note;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Student studentToUpdate = getStudentAtDisplayedIndex(model, targetIndex);
        RegisteredNotes registeredNotes = studentToUpdate.getRegisteredNotes();

        Student updatedStudent = studentToUpdate.withRegisteredNotes(
                registeredNotes.withNote(note));
        model.setStudent(studentToUpdate, updatedStudent);
        return new CommandResult(String.format(MESSAGE_SUCCESS, studentToUpdate.getName()));
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
                && note.equals(otherAddNoteCommand.note);
    }

    @Override
    public int hashCode() {
        return hash(targetIndex, note);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("note", note)
                .toString();
    }
}

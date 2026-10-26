package seedu.address.logic.commands;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_INDEX;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.student.RegisteredNotes;
import seedu.address.model.student.Student;

/**
 * Deletes a note for a student identified by a displayed index.
 */
public class DeleteNoteCommand extends Command {

    public static final String COMMAND_WORD = "delnote";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a note for the selected student.\n"
            + "Parameters: " + PREFIX_INDEX + "INDEX " + PREFIX_NOTE_INDEX + "NOTE_INDEX\n"
            + "INDEX must be a positive integer identifying a displayed student.\n"
            + "NOTE_INDEX must be a positive integer less than or equal to "
            + RegisteredNotes.MAX_NOTE_INDEX + " inclusive.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_INDEX + "1 "
            + PREFIX_NOTE_INDEX + "3";

    public static final String MESSAGE_SUCCESS =
            "Deleted %1$s's note at ID %2$d.";
    public static final String MESSAGE_ALREADY_NOT_RECORDED =
            "%1$s’s note ID does not exist. No changes made.";

    private final Index targetIndex;
    private final Index noteIndex;

    /**
     * @param targetIndex Index of the student in the filtered student list to delete a note from.
     * @param noteIndex ID of note to delete.
     */
    public DeleteNoteCommand(Index targetIndex, Index noteIndex) {
        requireNonNull(targetIndex);
        checkArgument(RegisteredNotes.isValidNoteIndex(noteIndex), RegisteredNotes.MESSAGE_CONSTRAINTS);

        this.targetIndex = targetIndex;
        this.noteIndex = noteIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Student studentToUpdate = getStudentAtDisplayedIndex(model, targetIndex);
        RegisteredNotes registeredNotes = studentToUpdate.getRegisteredNotes();

        if (!registeredNotes.hasNote(noteIndex)) {
            return new CommandResult(String.format(MESSAGE_ALREADY_NOT_RECORDED, studentToUpdate.getName()));
        }

        Student updatedStudent = studentToUpdate.withRegisteredNotes(
                registeredNotes.withoutNote(noteIndex));
        model.setStudent(studentToUpdate, updatedStudent);
        return new CommandResult(String.format(MESSAGE_SUCCESS, studentToUpdate.getName(), noteIndex.getOneBased()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof DeleteNoteCommand otherDeleteNoteCommand)) {
            return false;
        }

        return targetIndex.equals(otherDeleteNoteCommand.targetIndex)
                && noteIndex.equals(otherDeleteNoteCommand.noteIndex);
    }

    @Override
    public int hashCode() {
        return hash(targetIndex, noteIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("noteIndex", noteIndex)
                .toString();
    }
}

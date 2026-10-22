package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import seedu.address.commons.core.index.Index;
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
            + "Parameters: INDEX ni/ID\n"
            + "INDEX must be a positive integer identifying a displayed student.\n"
            + "ID must be between " + RegisteredNotes.MIN_NOTE_ID
            + " and " + RegisteredNotes.MAX_NOTE_ID + " inclusive.\n"
            + "Example: " + COMMAND_WORD + " 1 ni/3 nt/30";

    public static final String MESSAGE_SUCCESS =
            "Deleted %1$s's note at ID %2$d.";
    public static final String MESSAGE_ALREADY_NOT_RECORDED =
            "%1$s’s note ID does not exist. No changes made.";

    private final Index targetIndex;
    private final int noteId;

    /**
     * @param targetIndex Index of the student in the filtered student list to edit.
     * @param noteId ID of note to delete.
     */
    public DeleteNoteCommand(Index targetIndex, int noteId) {
        requireNonNull(targetIndex);

        checkArgument(RegisteredNotes.isValidNoteId(noteId), RegisteredNotes.MESSAGE_CONSTRAINTS);

        this.targetIndex = targetIndex;
        this.noteId = noteId;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Student studentToUpdate = getStudentAtDisplayedIndex(model, targetIndex);
        RegisteredNotes registeredNotes = studentToUpdate.getRegisteredNotes();

        if (!registeredNotes.hasNote(noteId)) {
            return new CommandResult(String.format(MESSAGE_ALREADY_NOT_RECORDED, studentToUpdate.getName()));
        }

        Student updatedStudent = studentToUpdate.withRegisteredNotes(
                registeredNotes.withoutNote(noteId));
        model.setStudent(studentToUpdate, updatedStudent);
        return new CommandResult(String.format(MESSAGE_SUCCESS, studentToUpdate.getName(), noteId));
    }
}

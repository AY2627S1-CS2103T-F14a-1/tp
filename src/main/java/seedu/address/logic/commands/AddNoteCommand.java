package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import seedu.address.commons.core.index.Index;
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
            + "Parameters: INDEX ni/ID nt/TEXT\n"
            + "INDEX must be a positive integer identifying a displayed student.\n"
            + "ID must be between " + RegisteredNotes.MIN_NOTE_ID
            + " and " + RegisteredNotes.MAX_NOTE_ID + " inclusive.\n"
            + "Example: " + COMMAND_WORD + " 1 ni/3 nt/30";

    public static final String MESSAGE_SUCCESS =
            "Added %1$s's note as ID %2$d.";
    public static final String MESSAGE_ALREADY_RECORDED =
            "%1$s’s note ID has already been registered. No changes made.";

    private final Index index;
    private final int noteId;
    private final Note note;

    public AddNoteCommand(Index targetIndex, int noteId, Note note) {
        requireNonNull(targetIndex);
        requireNonNull(note);

        checkArgument(RegisteredNotes.isValidNoteId(noteId), RegisteredNotes.MESSAGE_CONSTRAINTS);

        this.index = targetIndex;
        this.noteId = noteId;
        this.note = note;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Student studentToUpdate = getStudentAtDisplayedIndex(model, index);
        RegisteredNotes registeredNotes = studentToUpdate.getRegisteredNotes();

        if (registeredNotes.hasNote(noteId)) {
            return new CommandResult(String.format(MESSAGE_ALREADY_RECORDED, studentToUpdate.getName()));
        }

        Student updatedStudent = studentToUpdate.withRegisteredNotes(
                registeredNotes.withNote(noteId, note));
        model.setStudent(studentToUpdate, updatedStudent);
        return new CommandResult(String.format(MESSAGE_SUCCESS, studentToUpdate.getName(), noteId));
    }
}

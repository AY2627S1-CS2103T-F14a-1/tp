package seedu.address.logic.commands;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_TEXT;

import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
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
            "The note index provided doesn't exist.";
    public static final String MESSAGE_SAME_NOTE =
            "%1$s’s note with index %2$d is already the same. No changes made.";
    public static final String MESSAGE_SUCCESS =
            "Edited %1$s's note at index %2$d.";
    public static final String MESSAGE_WARNING_MATCHING_TEXT = "%1$s's note(s) with the following index(es) "
            + "may already contain the same note text: %2$s";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits a registered note for the selected student.\n"
            + "Parameters: " + PREFIX_INDEX + "INDEX " + PREFIX_NOTE_INDEX + "NOTE_INDEX " + PREFIX_NOTE_TEXT + "TEXT\n"
            + "INDEX must be a positive integer identifying a displayed student.\n"
            + "NOTE_INDEX must be a positive integer less than or equal to "
            + RegisteredNotes.MAX_NOTE_INDEX + " inclusive.\n"
            + "TEXT must only contain alphanumeric characters, punctuation, and spaces, should not be blank, and "
            + "be less than or equal to " + Note.MAX_NOTE_LENGTH + " characters.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_INDEX + "1 "
            + PREFIX_NOTE_INDEX + "3 " + PREFIX_NOTE_TEXT + "hello";

    private static final Logger logger = LogsCenter.getLogger(EditNoteCommand.class);

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
            logger.fine("Note edit rejected: note index " + noteIndex.getOneBased()
                    + " is not recorded at displayed student index " + targetIndex.getOneBased() + ".");
            throw new CommandException(MESSAGE_INDEX_NOT_RECORDED);
        }

        if (registeredNotes.hasMatchingNote(noteIndex, updatedNote)) {
            logger.fine("Note edit skipped: note index " + noteIndex.getOneBased()
                    + " is unchanged at displayed student index " + targetIndex.getOneBased() + ".");
            return new CommandResult(String.format(MESSAGE_SAME_NOTE,
                    studentToUpdate.getName(), noteIndex.getOneBased()), studentToUpdate);
        }

        List<Index> nearMatchingNotes = registeredNotes.getNearMatchingNoteIndexes(updatedNote.getNoteText());
        Student updatedStudent = studentToUpdate.withRegisteredNotes(
                registeredNotes.editNote(noteIndex, updatedNote));
        model.setStudent(studentToUpdate, updatedStudent);
        logger.info("Edited note index " + noteIndex.getOneBased()
                + " at displayed student index " + targetIndex.getOneBased() + ".");
        CommandResult result = new CommandResult(String.format(MESSAGE_SUCCESS,
                studentToUpdate.getName(),
                noteIndex.getOneBased()),
                updatedStudent);
        if (!nearMatchingNotes.isEmpty()) {
            return result.withWarning(String.format(MESSAGE_WARNING_MATCHING_TEXT, studentToUpdate.getName(),
                    nearMatchingNotes.stream().map(Index::getOneBased).toList()));
        }
        return result;
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

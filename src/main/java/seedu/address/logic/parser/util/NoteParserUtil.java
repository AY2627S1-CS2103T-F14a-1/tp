package seedu.address.logic.parser.util;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.Note;
import seedu.address.model.student.RegisteredNotes;

/**
 * Contains utility methods used for parsing common strings in the various Note Parser classes.
 */
public final class NoteParserUtil {

    public static final String MESSAGE_INVALID_NOTE_INDEX = "The note index provided is invalid: ";

    /**
     * Parses a {@code String note index} into a {@code Index} and returns it.
     *
     * @throws ParseException if the given {@code noteIndex} is invalid.
     */
    public static Index parseNoteIndex(String noteIndexStr) throws ParseException {
        requireNonNull(noteIndexStr);
        Index noteIndex;
        try {
            noteIndex = ParserUtil.parseIndex(noteIndexStr);
        } catch (ParseException pe) {
            throw new ParseException(MESSAGE_INVALID_NOTE_INDEX + pe.getMessage());
        }
        if (!RegisteredNotes.isValidNoteIndex(noteIndex)) {
            throw new ParseException(MESSAGE_INVALID_NOTE_INDEX + RegisteredNotes.MESSAGE_CONSTRAINTS);
        }
        return noteIndex;
    }

    /**
     * Parses a {@code noteText} into a {@code Note}.
     *
     * @throws ParseException if the given {@code noteText} is invalid.
     */
    public static Note parseNoteText(String noteText) throws ParseException {
        requireNonNull(noteText);
        String trimmedNoteText = noteText.trim();
        if (!Note.isValidNoteText(trimmedNoteText)) {
            throw new ParseException(Note.MESSAGE_CONSTRAINTS);
        }
        return new Note(trimmedNoteText);
    }
}

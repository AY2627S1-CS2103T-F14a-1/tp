package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;

import seedu.address.model.student.Note;
import seedu.address.model.student.RegisteredNotes;

/**
 * Contains utility methods used for parsing common strings in the various *Parser classes.
 */
public class ParserUtil {
    public static final String MESSAGE_INVALID_INDEX =
            "Index must be a positive integer without a sign, decimal point, or leading zero.";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String note index} into a {@code Index} and returns it.
     *
     * @throws ParseException if the given {@code noteIndex} is invalid.
     */
    public static Index parseNoteIndex(String noteIndexStr) throws ParseException {
        requireNonNull(noteIndexStr);
        Index noteIndex = ParserUtil.parseIndex(noteIndexStr);
        if (!RegisteredNotes.isValidNoteIndex(noteIndex)) {
            throw new ParseException(RegisteredNotes.MESSAGE_CONSTRAINTS);
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

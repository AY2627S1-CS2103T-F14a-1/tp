package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a note for a Student in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidNoteText(String)}
 */
public final class Note {

    public static final int MAX_NOTE_LENGTH = 1000;
    public static final String MESSAGE_CONSTRAINTS =
            "Notes should only contain alphanumeric characters, punctuation, and spaces, should not be blank, "
                    + "and be less than or equal to " + Note.MAX_NOTE_LENGTH + " characters";
    /**
     * Regex matching one or more consecutive Unicode whitespace characters.
     */
    public static final String WHITESPACE_RUN_REGEX = "(?U)\\s+";

    /*
     * The first character of the text must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[\\p{Alnum}\\p{Punct}][\\p{Alnum}\\p{Punct} ]*";

    private final String noteText;

    /**
     * Constructs a {@code Note}.
     *
     * @param noteText A valid note text.
     */
    public Note(String noteText) {
        requireNonNull(noteText);
        checkArgument(isValidNoteText(noteText), MESSAGE_CONSTRAINTS);
        this.noteText = noteText;
    }

    /**
     * Returns the note's text.
     *
     * @return The note's text.
     */
    public String getNoteText() {
        return noteText;
    }

    /**
     * Returns true if a given string is a valid note text.
     */
    public static boolean isValidNoteText(String test) {
        return test.matches(VALIDATION_REGEX) && test.length() <= MAX_NOTE_LENGTH;
    }

    /**
     * Returns whether the note's text is a near-match with the given text.
     *
     * @param text Note text to check.
     * @return True if matching note text has been registered.
     * @throws NullPointerException If the text is {@code null}.
     */
    public boolean hasNearMatchingNoteText(String text) {
        requireNonNull(text);
        return normalize(noteText).equals(normalize(text));
    }

    /**
     * Normalizes note text for comparison. Ignores capitalization and
     * treats each run of whitespace—including spaces, tabs, and line breaks—as
     * a single space. Leading and trailing whitespace is removed.
     *
     * @param text The note text to normalize.
     * @return The normalized note text.
     * @throws NullPointerException If the text is {@code null}.
     */
    private static String normalize(String text) {
        requireNonNull(text);
        return text.strip()
                .replaceAll(WHITESPACE_RUN_REGEX, " ")
                .toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Note otherNote)) {
            return false;
        }

        return noteText.equals(otherNote.noteText);
    }

    @Override
    public int hashCode() {
        return noteText.hashCode();
    }

    @Override
    public String toString() {
        return noteText;
    }
}

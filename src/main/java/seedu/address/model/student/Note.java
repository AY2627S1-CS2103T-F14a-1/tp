package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a note for a Student in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidNoteText(String)}
 */
public final class Note {

    public static final int MAX_NOTE_LENGTH = 100;
    public static final String MESSAGE_CONSTRAINTS =
            "Notes should only contain alphanumeric characters and spaces, should not be blank, "
                    + "and be less than or equal to " + Note.MAX_NOTE_LENGTH + " characters";

    /*
     * The first character of the text must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String VALIDATION_REGEX = "[\\p{Alnum}][\\p{Alnum} ]*";

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

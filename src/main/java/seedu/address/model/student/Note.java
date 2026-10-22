package seedu.address.model.student;

/**
 * Represents a note for a Student in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidNoteText(String)}
 */
public class Note {

    public static final int MAX_NOTE_LENGTH = 100;
    public static final String MESSAGE_CONSTRAINTS = "Note text should not be blank, "
            + "and note ID must be a non-zero unsigned integer";

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

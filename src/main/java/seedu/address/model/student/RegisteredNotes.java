package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.model.student.Note.isValidNoteText;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an immutable collection of a student's notes.
 * Each note ID is between {@value MIN_NOTE_ID} and {@value MAX_NOTE_ID}, inclusive.
 */
public class RegisteredNotes {

    public static final int MIN_NOTE_ID = 1;
    public static final int MAX_NOTE_ID = 10;

    public static final String MESSAGE_CONSTRAINTS =
            "Note ID must be between " + MIN_NOTE_ID + " and " + MAX_NOTE_ID + " inclusive";

    private final Map<Integer, Note> notesById;

    /**
     * Constructs a {@code RegisteredNotes} collection with no registered notes.
     */
    public RegisteredNotes() {
        this.notesById = Collections.emptyMap();
    }

    /**
     * Creates {@code RegisteredNotes} from the given notes.
     *
     * @param notesById Notes and their ID.
     * @throws NullPointerException If the map, any ID, or any note is {@code null}.
     */
    public RegisteredNotes(Map<Integer, Note> notesById) {
        requireNonNull(notesById);

        Map<Integer, Note> copiedNotes = new HashMap<>(notesById);

        for (Map.Entry<Integer, Note> entry : notesById.entrySet()) {
            requireNonNull(entry.getKey());
            requireNonNull(entry.getValue());
            checkArgument(isValidNoteText(entry.getValue().getNoteText()), Note.MESSAGE_CONSTRAINTS);
            checkArgument(isValidNoteId(entry.getKey()), MESSAGE_CONSTRAINTS);
        }
        this.notesById = Collections.unmodifiableMap(copiedNotes);
    }

    public boolean hasNoteId(int id) {
        return notesById.containsKey(id);
    }

    public Map<Integer, Note> getNotesById() {
        return notesById;
    }

    /**
     * Returns whether the given note ID is within the valid range.
     *
     * @param noteId Note ID to check.
     * @return True if the ID is between {@value #MIN_NOTE_ID} and {@value #MAX_NOTE_ID}, inclusive.
     */
    public static boolean isValidNoteId(Integer noteId) {
        return noteId >= MIN_NOTE_ID && noteId <= MAX_NOTE_ID;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof RegisteredNotes otherRegisteredNotes)) {
            return false;
        }

        return notesById.equals(otherRegisteredNotes.notesById);
    }

    @Override
    public int hashCode() {
        return notesById.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder summary = new StringBuilder();
        for (int id = MIN_NOTE_ID; id <= MAX_NOTE_ID; id++) {
            Note note = notesById.get(id);
            if (note == null) {
                continue;
            }
            String noteText = note.getNoteText();
            summary.append(id)
                    .append(": ").append(noteText).append('\n');
        }
        return summary.toString();
    }
}

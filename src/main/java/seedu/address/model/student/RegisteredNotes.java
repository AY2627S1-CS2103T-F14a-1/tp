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
public final class RegisteredNotes {

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
     * @throws IllegalArgumentException If any note ID is outside the valid range.
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

    /**
     * Returns whether a note with the same ID has been registered.
     *
     * @param id Note ID to check.
     * @return True if note ID has been registered.
     * @throws IllegalArgumentException If the note ID is outside the valid range.
     */
    public boolean hasNoteId(int id) {
        checkArgument(isValidNoteId(id), MESSAGE_CONSTRAINTS);
        return notesById.containsKey(id);
    }

    /**
     * Returns whether a note with the same text has been registered.
     *
     * @param text Note text to check.
     * @return True if matching note text has been registered.
     * @throws NullPointerException If the text is {@code null}.
     */
    public boolean hasNoteText(String text) {
        requireNonNull(text);
        return notesById.containsValue(new Note(text));
    }

    /**
     * Returns whether the same note has been recorded with the given ID.
     *
     * @param id ID to check.
     * @param note Note to check.
     * @return True if the same note has been recorded with the given ID.
     * @throws IllegalArgumentException If {@code id} is outside the valid ID range.
     * @throws NullPointerException If {@code note} is {@code null}.
     */
    public boolean hasMatchingNote(int id, Note note) {
        requireNonNull(note);
        checkArgument(isValidNoteId(id), MESSAGE_CONSTRAINTS);
        return notesById.containsKey(id) && notesById.get(id).equals(note);
    }

    /**
     * Returns {@code RegisteredNotes} including the given note
     * without changing this object. Returns this object if a note with
     * the same ID is already registered.
     *
     * @param id Note ID to use.
     * @param note Note to include.
     * @return {@code RegisteredNotes} containing the existing notes and given note.
     * @throws NullPointerException If the note is {@code null}.
     */
    public RegisteredNotes withNote(int id, Note note) {
        requireNonNull(note);
        if (hasNoteId(id)) {
            return this;
        }

        Map<Integer, Note> updatedNotes = new HashMap<>(notesById);
        updatedNotes.put(id, note);

        return new RegisteredNotes(updatedNotes);
    }

    /**
     * Returns {@code RegisteredNotes} not including the given note
     * without changing this object. Returns this object if a note with
     * the same ID was already not registered.
     *
     * @param id Note ID to remove.
     * @return {@code RegisteredNotes} containing the existing notes without
     *      the note with the given ID.
     */
    public RegisteredNotes withoutNote(int id) {
        if (!hasNoteId(id)) {
            return this;
        }

        Map<Integer, Note> updatedNotes = new HashMap<>(notesById);
        updatedNotes.remove(id);
        return new RegisteredNotes(updatedNotes);
    }

    /**
     * Returns {@code RegisteredNotes} with the note for the given ID replaced by the given note.
     * Returns this instance when no note is registered for the given ID or the registered note is unchanged.
     *
     * @param noteId Note ID to edit.
     * @param note Replacement note.
     * @return {@code RegisteredNotes} containing the existing notes and the replacement note for the given ID.
     * @throws NullPointerException If the note is {@code null}.
     * @throws IllegalArgumentException If the note ID is outside the valid range.
     */
    public RegisteredNotes editNote(int noteId, Note note) {
        requireNonNull(note);
        if (!hasNoteId(noteId)) {
            return this;
        }

        if (getNotesById().get(noteId).equals(note)) {
            return this;
        }

        Map<Integer, Note> updatedNotes = new HashMap<>(notesById);
        updatedNotes.put(noteId, note);

        return new RegisteredNotes(updatedNotes);
    }

    /**
     * Returns an unmodifiable map of registered notes.
     *
     * @return Registered notes, with no guaranteed iteration order.
     */
    public Map<Integer, Note> getNotesById() {
        return notesById;
    }

    /**
     * Returns whether the given note ID is within the valid range.
     *
     * @param noteId Note ID to check.
     * @return True if the ID is between {@value #MIN_NOTE_ID} and {@value #MAX_NOTE_ID}, inclusive.
     */
    public static boolean isValidNoteId(int noteId) {
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

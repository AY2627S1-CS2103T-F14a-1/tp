package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.model.student.Note.isValidNoteText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import seedu.address.commons.core.index.Index;

/**
 * Represents an immutable collection of a student's notes.
 * Each note ID is between {@value MIN_NOTE_ID} and {@value MAX_NOTE_ID}, inclusive.
 */
public final class RegisteredNotes {

    public static final int MAX_NOTE_ID = 10;
    public static final String MESSAGE_CONSTRAINTS =
            "The number of notes cannot must be at most " + MAX_NOTE_ID;

    private final List<Note> registeredNotes;

    /**
     * Constructs a {@code RegisteredNotes} collection with no registered notes.
     */
    public RegisteredNotes() {
        this.registeredNotes = Collections.emptyList();
    }

    /**
     * Creates {@code RegisteredNotes} from the given notes.
     *
     * @param registeredNotes Registered notes.
     * @throws NullPointerException If the list, any ID, or any note is {@code null}.
     * @throws IllegalArgumentException If any note ID is outside the valid range.
     */
    public RegisteredNotes(List<Note> registeredNotes) {
        requireAllNonNull(registeredNotes);

        List<Note> copiedNotes = new ArrayList<>(registeredNotes);

        checkArgument(registeredNotes.size() <= MAX_NOTE_ID, RegisteredNotes.MESSAGE_CONSTRAINTS);
        for (Note note : copiedNotes) {
            checkArgument(isValidNoteText(note.getNoteText()), Note.MESSAGE_CONSTRAINTS);
        }
        this.registeredNotes = Collections.unmodifiableList(copiedNotes);
    }

    /**
     * Returns whether a note with the same ID has been registered.
     *
     * @param id Note ID to check.
     * @return True if note ID has been registered.
     * @throws IllegalArgumentException If the note ID is outside the valid range.
     */
    public boolean hasNote(Index targetIndex) {
        checkArgument(isValidNoteId(id), MESSAGE_CONSTRAINTS);
        return notesById.containsKey(id);
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
        if (hasNote(id)) {
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
        if (!hasNote(id)) {
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
        if (!hasNote(noteId)) {
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

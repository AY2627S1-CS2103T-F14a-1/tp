package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.model.student.Note.isValidNoteText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import seedu.address.commons.core.index.Index;

/**
 * Represents an immutable collection of a student's notes.
 * Each note index is a positive integer less than or equal to {@value MAX_NOTE_INDEX}.
 */
public final class RegisteredNotes {

    public static final int MAX_NOTE_INDEX = 10;
    public static final String MESSAGE_CONSTRAINTS =
            "There must be " + MAX_NOTE_INDEX + " notes or fewer, with indexes between 1 and " + MAX_NOTE_INDEX;

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
     * @throws NullPointerException If the list or any note is {@code null}.
     * @throws IllegalArgumentException If there are too many notes or any note text is invalid.
     */
    public RegisteredNotes(List<Note> registeredNotes) {
        requireAllNonNull(registeredNotes);

        List<Note> copiedNotes = new ArrayList<>(registeredNotes);

        checkArgument(registeredNotes.size() <= MAX_NOTE_INDEX, RegisteredNotes.MESSAGE_CONSTRAINTS);
        for (Note note : copiedNotes) {
            checkArgument(isValidNoteText(note.getNoteText()), Note.MESSAGE_CONSTRAINTS);
        }
        this.registeredNotes = Collections.unmodifiableList(copiedNotes);
    }

    /**
     * Returns whether a note with the same index has been registered.
     *
     * @param targetIndex Note index to check.
     * @return True if note index has been registered.
     * @throws NullPointerException If the note index is {@code null}.
     * @throws IllegalArgumentException If the note index is outside the valid range.
     */
    public boolean hasNote(Index targetIndex) {
        requireNonNull(targetIndex);
        checkArgument(isValidNoteIndex(targetIndex), MESSAGE_CONSTRAINTS);
        return targetIndex.getOneBased() <= registeredNotes.size();
    }

    /**
     * Returns {@code RegisteredNotes} including the given note
     * without changing this object. Returns the same object if
     * the number of notes has reached capacity.
     *
     * @param note Note to include.
     * @return {@code RegisteredNotes} containing the existing notes and given note.
     * @throws NullPointerException If the note is {@code null}.
     * @throws IllegalStateException If the list of notes has reached capacity.
     */
    public RegisteredNotes withNote(Note note) {
        requireNonNull(note);
        if (registeredNotes.size() >= MAX_NOTE_INDEX) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
        }

        List<Note> updatedNotes = new ArrayList<>(registeredNotes);
        updatedNotes.add(note);

        return new RegisteredNotes(updatedNotes);
    }

    /**
     * Returns {@code RegisteredNotes} not including the given note
     * without changing this object. Returns this object if a note with
     * the same index was already not registered.
     *
     * @param targetIndex Note index to remove.
     * @return {@code RegisteredNotes} containing the existing notes without
     *     the note with the given index.
     * @throws NullPointerException If the note index is {@code null}.
     * @throws IllegalArgumentException If the note index is outside the valid range.
     */
    public RegisteredNotes withoutNote(Index targetIndex) {
        if (!hasNote(targetIndex)) {
            return this;
        }

        List<Note> updatedNotes = new ArrayList<>(registeredNotes);
        updatedNotes.remove(targetIndex.getZeroBased());
        return new RegisteredNotes(updatedNotes);
    }

    /**
     * Returns {@code RegisteredNotes} with the note for the given index replaced by the given note.
     * Returns this instance when no note is registered for the given index or the registered note is unchanged.
     *
     * @param noteIndex Note index to edit.
     * @param note Replacement note.
     * @return {@code RegisteredNotes} containing the existing notes and the replacement note for the given index.
     * @throws NullPointerException If the note is {@code null}.
     * @throws IllegalArgumentException If the note index is outside the valid range.
     */
    public RegisteredNotes editNote(Index noteIndex, Note note) {
        requireNonNull(note);
        if (!hasNote(noteIndex)) {
            return this;
        }

        if (getRegisteredNotes().get(noteIndex.getZeroBased()).equals(note)) {
            return this;
        }

        List<Note> updatedNotes = new ArrayList<>(registeredNotes);
        updatedNotes.set(noteIndex.getZeroBased(), note);

        return new RegisteredNotes(updatedNotes);
    }

    /**
     * Returns an unmodifiable list of registered notes.
     *
     * @return List of registered notes.
     */
    public List<Note> getRegisteredNotes() {
        return registeredNotes;
    }

    /**
     * Returns whether the given note index is within the valid range.
     *
     * @param noteIndex Note index to check.
     * @return True if the index is a positive integer less than or equal to {@value #MAX_NOTE_INDEX}.
     */
    public static boolean isValidNoteIndex(Index noteIndex) {
        return noteIndex.getOneBased() <= MAX_NOTE_INDEX;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof RegisteredNotes otherRegisteredNotes)) {
            return false;
        }

        return registeredNotes.equals(otherRegisteredNotes.registeredNotes);
    }

    @Override
    public int hashCode() {
        return registeredNotes.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder summary = new StringBuilder();
        for (int id = 0; id < registeredNotes.size(); id++) {
            Note note = registeredNotes.get(id);
            if (note == null) {
                continue;
            }
            String noteText = note.getNoteText();
            summary.append(id + 1)
                    .append(": ").append(noteText).append('\n');
        }
        return summary.toString();
    }
}

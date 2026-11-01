package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/** Tests note validation and value semantics. */
public class NoteTest {

    @Test
    public void constructor_validText_storesText() {
        Note note = new Note("Read chapter 2");

        assertEquals("Read chapter 2", note.getNoteText());
    }

    @Test
    public void constructor_nullText_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Note(null));
    }

    @Test
    public void constructor_invalidText_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Note(""));
        assertThrows(IllegalArgumentException.class, () -> new Note(" leading space"));
    }

    @Test
    public void isValidNoteText_boundaryAndCharacterCases_returnsExpectedValidity() {
        assertTrue(Note.isValidNoteText("A"));
        assertTrue(Note.isValidNoteText("123 notes"));
        assertTrue(Note.isValidNoteText("Don't forget: chapter 2!"));
        assertTrue(Note.isValidNoteText("a".repeat(Note.MAX_NOTE_LENGTH)));

        assertFalse(Note.isValidNoteText(""));
        assertFalse(Note.isValidNoteText(" "));
        assertFalse(Note.isValidNoteText(" leading"));
        assertTrue(Note.isValidNoteText("trailing "));
        assertFalse(Note.isValidNoteText("note\twith tab"));
        assertFalse(Note.isValidNoteText("a".repeat(Note.MAX_NOTE_LENGTH + 1)));
        assertThrows(NullPointerException.class, () -> Note.isValidNoteText(null));
    }

    @Test
    public void equals_sameAndDifferentText_returnsExpectedEquality() {
        Note note = new Note("Review lecture");

        assertEquals(note, note);
        assertEquals(note, new Note("Review lecture"));
        assertNotEquals(note, new Note("Submit assignment"));
        assertNotEquals(note, null);
        assertNotEquals(note, "Review lecture");
    }

    @Test
    public void hashCode_hashSetLookup_respectsEquality() {
        Set<Note> notes = new HashSet<>();
        notes.add(new Note("Review lecture"));

        assertTrue(notes.contains(new Note("Review lecture")));
        assertFalse(notes.contains(new Note("Submit assignment")));
        assertFalse(notes.add(new Note("Review lecture")));
        assertEquals(1, notes.size());
    }

    @Test
    public void toString_validText_returnsText() {
        assertEquals("Review lecture", new Note("Review lecture").toString());
    }
}

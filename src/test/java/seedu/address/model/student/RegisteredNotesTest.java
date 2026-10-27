package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

/** Tests registered-note validation, immutability, updates, and value semantics. */
public class RegisteredNotesTest {

    private static final Note FIRST_NOTE = new Note("Review lecture");
    private static final Note SECOND_NOTE = new Note("Submit assignment");

    @Test
    public void constructor_noArguments_createsEmptyRegisteredNotes() {
        assertTrue(new RegisteredNotes().getNotesById().isEmpty());
    }

    @Test
    public void constructor_boundaryIds_storesNotes() {
        Map<Integer, Note> notes = Map.of(
                RegisteredNotes.MIN_NOTE_ID, FIRST_NOTE,
                RegisteredNotes.MAX_NOTE_ID, SECOND_NOTE);

        assertEquals(notes, new RegisteredNotes(notes).getNotesById());
    }

    @Test
    public void constructor_nullMapKeyOrValue_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RegisteredNotes(null));

        Map<Integer, Note> nullKey = new HashMap<>();
        nullKey.put(null, FIRST_NOTE);
        assertThrows(NullPointerException.class, () -> new RegisteredNotes(nullKey));

        Map<Integer, Note> nullValue = new HashMap<>();
        nullValue.put(RegisteredNotes.MIN_NOTE_ID, null);
        assertThrows(NullPointerException.class, () -> new RegisteredNotes(nullValue));
    }

    @Test
    public void constructor_invalidId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new RegisteredNotes(
                Map.of(RegisteredNotes.MIN_NOTE_ID - 1, FIRST_NOTE)));
        assertThrows(IllegalArgumentException.class, () -> new RegisteredNotes(
                Map.of(RegisteredNotes.MAX_NOTE_ID + 1, FIRST_NOTE)));
    }

    @Test
    public void constructor_sourceMapChanged_retainsOriginalMapping() {
        Map<Integer, Note> notes = new HashMap<>(Map.of(RegisteredNotes.MIN_NOTE_ID, FIRST_NOTE));
        RegisteredNotes registeredNotes = new RegisteredNotes(notes);
        notes.clear();
        notes.put(RegisteredNotes.MAX_NOTE_ID, SECOND_NOTE);

        assertEquals(Map.of(RegisteredNotes.MIN_NOTE_ID, FIRST_NOTE), registeredNotes.getNotesById());
    }

    @Test
    public void withNote_newId_returnsUpdatedCopy() {
        RegisteredNotes original = new RegisteredNotes(Map.of(RegisteredNotes.MIN_NOTE_ID, FIRST_NOTE));
        RegisteredNotes updated = original.withNote(RegisteredNotes.MAX_NOTE_ID, SECOND_NOTE);

        assertEquals(Map.of(RegisteredNotes.MIN_NOTE_ID, FIRST_NOTE), original.getNotesById());
        assertEquals(Map.of(
                RegisteredNotes.MIN_NOTE_ID, FIRST_NOTE,
                RegisteredNotes.MAX_NOTE_ID, SECOND_NOTE), updated.getNotesById());
    }

    @Test
    public void withNote_existingId_returnsSameInstance() {
        RegisteredNotes registeredNotes = new RegisteredNotes(Map.of(RegisteredNotes.MIN_NOTE_ID, FIRST_NOTE));

        assertSame(registeredNotes, registeredNotes.withNote(RegisteredNotes.MIN_NOTE_ID, SECOND_NOTE));
    }

    @Test
    public void withNote_nullNoteOrInvalidId_throwsException() {
        RegisteredNotes registeredNotes = new RegisteredNotes();

        assertThrows(NullPointerException.class, () -> registeredNotes.withNote(RegisteredNotes.MIN_NOTE_ID, null));
        assertThrows(IllegalArgumentException.class, () ->
                registeredNotes.withNote(RegisteredNotes.MIN_NOTE_ID - 1, FIRST_NOTE));
        assertThrows(IllegalArgumentException.class, () ->
                registeredNotes.withNote(RegisteredNotes.MAX_NOTE_ID + 1, FIRST_NOTE));
        assertTrue(registeredNotes.getNotesById().isEmpty());
    }

    @Test
    public void withoutNote_existingAndMissingIds_returnsExpectedCopies() {
        RegisteredNotes registeredNotes = new RegisteredNotes(Map.of(
                RegisteredNotes.MIN_NOTE_ID, FIRST_NOTE,
                RegisteredNotes.MAX_NOTE_ID, SECOND_NOTE));

        assertEquals(Map.of(RegisteredNotes.MAX_NOTE_ID, SECOND_NOTE),
                registeredNotes.withoutNote(RegisteredNotes.MIN_NOTE_ID).getNotesById());
        assertEquals(Map.of(RegisteredNotes.MIN_NOTE_ID, FIRST_NOTE, RegisteredNotes.MAX_NOTE_ID, SECOND_NOTE),
                registeredNotes.getNotesById());
        assertSame(registeredNotes, registeredNotes.withoutNote(2));
    }

    @Test
    public void withoutNote_invalidId_throwsIllegalArgumentException() {
        RegisteredNotes registeredNotes = new RegisteredNotes();

        assertThrows(IllegalArgumentException.class, () -> registeredNotes.withoutNote(0));
        assertThrows(IllegalArgumentException.class, () -> registeredNotes.withoutNote(11));
    }

    @Test
    public void editNote_existingIdWithDifferentNote_returnsUpdatedCopy() {
        RegisteredNotes registeredNotes = new RegisteredNotes(Map.of(
                RegisteredNotes.MIN_NOTE_ID, FIRST_NOTE,
                RegisteredNotes.MAX_NOTE_ID, SECOND_NOTE));
        Note updatedNote = new Note("Prepare tutorial");

        RegisteredNotes updatedNotes = registeredNotes.editNote(RegisteredNotes.MIN_NOTE_ID, updatedNote);

        assertEquals(Map.of(
                RegisteredNotes.MIN_NOTE_ID, FIRST_NOTE,
                RegisteredNotes.MAX_NOTE_ID, SECOND_NOTE), registeredNotes.getNotesById());
        assertEquals(Map.of(
                RegisteredNotes.MIN_NOTE_ID, updatedNote,
                RegisteredNotes.MAX_NOTE_ID, SECOND_NOTE), updatedNotes.getNotesById());
    }

    @Test
    public void editNote_missingIdOrSameNote_returnsSameInstance() {
        RegisteredNotes registeredNotes = new RegisteredNotes(Map.of(RegisteredNotes.MIN_NOTE_ID, FIRST_NOTE));

        assertSame(registeredNotes, registeredNotes.editNote(2, SECOND_NOTE));
        assertSame(registeredNotes, registeredNotes.editNote(RegisteredNotes.MIN_NOTE_ID, FIRST_NOTE));
    }

    @Test
    public void editNote_nullNoteOrInvalidId_throwsException() {
        RegisteredNotes registeredNotes = new RegisteredNotes(Map.of(RegisteredNotes.MIN_NOTE_ID, FIRST_NOTE));

        assertThrows(NullPointerException.class, () -> registeredNotes.editNote(RegisteredNotes.MIN_NOTE_ID, null));
        assertThrows(IllegalArgumentException.class, () ->
                registeredNotes.editNote(RegisteredNotes.MIN_NOTE_ID - 1, SECOND_NOTE));
        assertThrows(IllegalArgumentException.class, () ->
                registeredNotes.editNote(RegisteredNotes.MAX_NOTE_ID + 1, SECOND_NOTE));
        assertEquals(Map.of(RegisteredNotes.MIN_NOTE_ID, FIRST_NOTE), registeredNotes.getNotesById());
    }

    @Test
    public void hasNote_presentAbsentAndInvalidIds_returnsExpectedResult() {
        RegisteredNotes registeredNotes = new RegisteredNotes(Map.of(RegisteredNotes.MIN_NOTE_ID, FIRST_NOTE));

        assertTrue(registeredNotes.hasNoteId(RegisteredNotes.MIN_NOTE_ID));
        assertFalse(registeredNotes.hasNoteId(RegisteredNotes.MAX_NOTE_ID));
        assertThrows(IllegalArgumentException.class, () -> registeredNotes.hasNoteId(0));
        assertThrows(IllegalArgumentException.class, () -> registeredNotes.hasNoteId(11));
    }

    @Test
    public void hasNoteText_matchingAbsentOrNullText_returnsExpectedResult() {
        RegisteredNotes registeredNotes = new RegisteredNotes(Map.of(RegisteredNotes.MIN_NOTE_ID, FIRST_NOTE));

        assertTrue(registeredNotes.hasNoteText(FIRST_NOTE.getNoteText()));
        assertFalse(registeredNotes.hasNoteText(SECOND_NOTE.getNoteText()));
        assertThrows(NullPointerException.class, () -> registeredNotes.hasNoteText(null));
    }

    @Test
    public void hasMatchingNote_matchingDifferentMissingOrInvalidNote_returnsExpectedResult() {
        RegisteredNotes registeredNotes = new RegisteredNotes(Map.of(RegisteredNotes.MIN_NOTE_ID, FIRST_NOTE));

        assertTrue(registeredNotes.hasMatchingNote(RegisteredNotes.MIN_NOTE_ID, new Note("Review lecture")));
        assertFalse(registeredNotes.hasMatchingNote(RegisteredNotes.MIN_NOTE_ID, SECOND_NOTE));
        assertFalse(registeredNotes.hasMatchingNote(RegisteredNotes.MAX_NOTE_ID, SECOND_NOTE));
        assertThrows(NullPointerException.class, () -> registeredNotes.hasMatchingNote(
                RegisteredNotes.MIN_NOTE_ID, null));
        assertThrows(IllegalArgumentException.class, () -> registeredNotes.hasMatchingNote(
                RegisteredNotes.MIN_NOTE_ID - 1, FIRST_NOTE));
        assertThrows(IllegalArgumentException.class, () -> registeredNotes.hasMatchingNote(
                RegisteredNotes.MAX_NOTE_ID + 1, FIRST_NOTE));
    }

    @Test
    public void getNotesById_modifyMap_throwsUnsupportedOperationException() {
        RegisteredNotes registeredNotes = new RegisteredNotes(Map.of(RegisteredNotes.MIN_NOTE_ID, FIRST_NOTE));
        Map<Integer, Note> notes = registeredNotes.getNotesById();

        assertThrows(UnsupportedOperationException.class, () -> notes.put(2, SECOND_NOTE));
        assertThrows(UnsupportedOperationException.class, () -> notes.remove(RegisteredNotes.MIN_NOTE_ID));
        assertThrows(UnsupportedOperationException.class, () ->
                notes.entrySet().iterator().next().setValue(SECOND_NOTE));
    }

    @Test
    public void isValidNoteId_boundaryAndOutOfRangeIds_returnsExpectedValidity() {
        assertTrue(RegisteredNotes.isValidNoteId(RegisteredNotes.MIN_NOTE_ID));
        assertTrue(RegisteredNotes.isValidNoteId(RegisteredNotes.MAX_NOTE_ID));
        assertFalse(RegisteredNotes.isValidNoteId(RegisteredNotes.MIN_NOTE_ID - 1));
        assertFalse(RegisteredNotes.isValidNoteId(RegisteredNotes.MAX_NOTE_ID + 1));
        assertFalse(RegisteredNotes.isValidNoteId(Integer.MIN_VALUE));
        assertFalse(RegisteredNotes.isValidNoteId(Integer.MAX_VALUE));
    }

    @Test
    public void equals_hashCodeAndHashSetLookup_respectValueEquality() {
        RegisteredNotes first = new RegisteredNotes(Map.of(RegisteredNotes.MIN_NOTE_ID, FIRST_NOTE));
        RegisteredNotes equal = new RegisteredNotes(Map.of(RegisteredNotes.MIN_NOTE_ID, new Note("Review lecture")));
        RegisteredNotes different = new RegisteredNotes(Map.of(RegisteredNotes.MAX_NOTE_ID, FIRST_NOTE));
        Set<RegisteredNotes> notes = new HashSet<>();
        notes.add(first);

        assertEquals(first, first);
        assertEquals(first, equal);
        assertEquals(first.hashCode(), equal.hashCode());
        assertNotEquals(first, different);
        assertNotEquals(first, null);
        assertNotEquals(first, Map.of());
        assertTrue(notes.contains(equal));
        assertFalse(notes.add(equal));
    }

    @Test
    public void hashCode_equalMappingsInDifferentOrders_returnsEqualHashes() {
        Map<Integer, Note> first = new LinkedHashMap<>();
        first.put(1, FIRST_NOTE);
        first.put(2, SECOND_NOTE);
        Map<Integer, Note> second = new LinkedHashMap<>();
        second.put(2, SECOND_NOTE);
        second.put(1, FIRST_NOTE);

        assertEquals(new RegisteredNotes(first).hashCode(), new RegisteredNotes(second).hashCode());
    }

    @Test
    public void toString_unorderedNotes_returnsAscendingIdSummary() {
        RegisteredNotes registeredNotes = new RegisteredNotes(Map.of(10, SECOND_NOTE, 2, FIRST_NOTE));

        assertEquals("2: Review lecture\n10: Submit assignment\n", registeredNotes.toString());
        assertEquals("", new RegisteredNotes().toString());
    }
}

package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;

/** Tests registered-note validation, immutability, updates, and value semantics. */
public class RegisteredNotesTest {

    private static final Note FIRST_NOTE = new Note("Review lecture");
    private static final Note SECOND_NOTE = new Note("Submit assignment");

    @Test
    public void constructor_noArguments_createsEmptyRegisteredNotes() {
        assertTrue(new RegisteredNotes().getRegisteredNotes().isEmpty());
    }

    @Test
    public void constructor_validNotes_preservesOrder() {
        List<Note> notes = List.of(FIRST_NOTE, SECOND_NOTE);

        assertEquals(notes, new RegisteredNotes(notes).getRegisteredNotes());
    }

    @Test
    public void constructor_nullListOrNote_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RegisteredNotes(null));

        List<Note> notesWithNull = new ArrayList<>();
        notesWithNull.add(FIRST_NOTE);
        notesWithNull.add(null);
        assertThrows(NullPointerException.class, () -> new RegisteredNotes(notesWithNull));
    }

    @Test
    public void constructor_tooManyNotes_throwsIllegalArgumentException() {
        List<Note> notes = Collections.nCopies(RegisteredNotes.MAX_NOTE_INDEX + 1, FIRST_NOTE);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> new RegisteredNotes(notes));
        assertEquals(RegisteredNotes.MESSAGE_CONSTRAINTS, exception.getMessage());
    }

    @Test
    public void constructor_sourceListChanged_retainsOriginalNotes() {
        List<Note> notes = new ArrayList<>(List.of(FIRST_NOTE));
        RegisteredNotes registeredNotes = new RegisteredNotes(notes);
        notes.clear();
        notes.add(SECOND_NOTE);

        assertEquals(List.of(FIRST_NOTE), registeredNotes.getRegisteredNotes());
    }

    @Test
    public void withNote_noteProvided_returnsUpdatedCopy() {
        RegisteredNotes original = new RegisteredNotes(List.of(FIRST_NOTE));
        RegisteredNotes updated = original.withNote(SECOND_NOTE);

        assertEquals(List.of(FIRST_NOTE), original.getRegisteredNotes());
        assertEquals(List.of(FIRST_NOTE, SECOND_NOTE), updated.getRegisteredNotes());
    }

    @Test
    public void withNote_maximumNotesReached_throwsIllegalStateException() {
        RegisteredNotes registeredNotes = new RegisteredNotes(
                Collections.nCopies(RegisteredNotes.MAX_NOTE_INDEX, FIRST_NOTE));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class, () -> registeredNotes.withNote(SECOND_NOTE));
        assertEquals(RegisteredNotes.MESSAGE_CONSTRAINTS, exception.getMessage());
    }

    @Test
    public void withNote_nullNote_throwsNullPointerException() {
        RegisteredNotes registeredNotes = new RegisteredNotes();

        assertThrows(NullPointerException.class, () -> registeredNotes.withNote(null));
        assertTrue(registeredNotes.getRegisteredNotes().isEmpty());
    }

    @Test
    public void withoutNote_existingAndMissingIndexes_returnsExpectedCopies() {
        RegisteredNotes registeredNotes = new RegisteredNotes(List.of(FIRST_NOTE, SECOND_NOTE));

        assertEquals(List.of(SECOND_NOTE),
                registeredNotes.withoutNote(Index.fromOneBased(1)).getRegisteredNotes());
        assertEquals(List.of(FIRST_NOTE, SECOND_NOTE), registeredNotes.getRegisteredNotes());
        assertSame(registeredNotes, registeredNotes.withoutNote(Index.fromOneBased(3)));
    }

    @Test
    public void withoutNote_nullOrInvalidIndex_throwsException() {
        RegisteredNotes registeredNotes = new RegisteredNotes();

        assertThrows(NullPointerException.class, () -> registeredNotes.withoutNote(null));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                registeredNotes.withoutNote(Index.fromOneBased(RegisteredNotes.MAX_NOTE_INDEX + 1)));
        assertEquals(RegisteredNotes.MESSAGE_CONSTRAINTS, exception.getMessage());
    }

    @Test
    public void editNote_existingIndexWithDifferentNote_returnsUpdatedCopy() {
        RegisteredNotes registeredNotes = new RegisteredNotes(List.of(FIRST_NOTE, SECOND_NOTE));
        Note updatedNote = new Note("Prepare tutorial");

        RegisteredNotes updatedNotes = registeredNotes.editNote(Index.fromOneBased(1), updatedNote);

        assertEquals(List.of(FIRST_NOTE, SECOND_NOTE), registeredNotes.getRegisteredNotes());
        assertEquals(List.of(updatedNote, SECOND_NOTE), updatedNotes.getRegisteredNotes());
    }

    @Test
    public void editNote_missingIndexOrSameNote_returnsSameInstance() {
        RegisteredNotes registeredNotes = new RegisteredNotes(List.of(FIRST_NOTE));

        assertSame(registeredNotes, registeredNotes.editNote(Index.fromOneBased(2), SECOND_NOTE));
        assertSame(registeredNotes, registeredNotes.editNote(Index.fromOneBased(1), FIRST_NOTE));
    }

    @Test
    public void editNote_nullArgumentsOrInvalidIndex_throwsException() {
        RegisteredNotes registeredNotes = new RegisteredNotes(List.of(FIRST_NOTE));

        assertThrows(NullPointerException.class, () -> registeredNotes.editNote(null, SECOND_NOTE));
        assertThrows(NullPointerException.class, () ->
                registeredNotes.editNote(Index.fromOneBased(1), null));
        assertThrows(IllegalArgumentException.class, () ->
                registeredNotes.editNote(Index.fromOneBased(RegisteredNotes.MAX_NOTE_INDEX + 1), SECOND_NOTE));
        assertEquals(List.of(FIRST_NOTE), registeredNotes.getRegisteredNotes());
    }

    @Test
    public void hasNote_presentAbsentAndInvalidIndexes_returnsExpectedResult() {
        RegisteredNotes registeredNotes = new RegisteredNotes(List.of(FIRST_NOTE));

        assertTrue(registeredNotes.hasNote(Index.fromOneBased(1)));
        assertFalse(registeredNotes.hasNote(Index.fromOneBased(2)));
        assertThrows(NullPointerException.class, () -> registeredNotes.hasNote(null));
        assertThrows(IllegalArgumentException.class, () ->
                registeredNotes.hasNote(Index.fromOneBased(RegisteredNotes.MAX_NOTE_INDEX + 1)));
    }

    @Test
    public void hasNoteText_matchingAbsentOrNullText_returnsExpectedResult() {
        RegisteredNotes registeredNotes = new RegisteredNotes(List.of(FIRST_NOTE));

        assertTrue(registeredNotes.hasNoteText(FIRST_NOTE.getNoteText()));
        assertFalse(registeredNotes.hasNoteText(SECOND_NOTE.getNoteText()));
        assertThrows(NullPointerException.class, () -> registeredNotes.hasNoteText(null));
    }

    @Test
    public void hasMatchingNote_matchingDifferentMissingOrInvalidNote_returnsExpectedResult() {
        RegisteredNotes registeredNotes = new RegisteredNotes(List.of(FIRST_NOTE));

        assertTrue(registeredNotes.hasMatchingNote(Index.fromOneBased(1), new Note("Review lecture")));
        assertFalse(registeredNotes.hasMatchingNote(Index.fromOneBased(1), SECOND_NOTE));
        assertFalse(registeredNotes.hasMatchingNote(Index.fromOneBased(2), SECOND_NOTE));
        assertThrows(NullPointerException.class, () ->
                registeredNotes.hasMatchingNote(Index.fromOneBased(1), null));
        assertThrows(NullPointerException.class, () -> registeredNotes.hasMatchingNote(null, FIRST_NOTE));
        assertThrows(IllegalArgumentException.class, () -> registeredNotes.hasMatchingNote(
                Index.fromOneBased(RegisteredNotes.MAX_NOTE_INDEX + 1), FIRST_NOTE));
    }

    @Test
    public void getRegisteredNotes_modifyList_throwsUnsupportedOperationException() {
        RegisteredNotes registeredNotes = new RegisteredNotes(List.of(FIRST_NOTE));
        List<Note> notes = registeredNotes.getRegisteredNotes();

        assertThrows(UnsupportedOperationException.class, () -> notes.add(SECOND_NOTE));
        assertThrows(UnsupportedOperationException.class, () -> notes.remove(FIRST_NOTE));
        assertThrows(UnsupportedOperationException.class, () -> notes.set(0, SECOND_NOTE));
    }

    @Test
    public void isValidNoteIndex_boundaryAndOutOfRangeIndexes_returnsExpectedValidity() {
        assertTrue(RegisteredNotes.isValidNoteIndex(Index.fromOneBased(1)));
        assertTrue(RegisteredNotes.isValidNoteIndex(Index.fromOneBased(RegisteredNotes.MAX_NOTE_INDEX)));
        assertFalse(RegisteredNotes.isValidNoteIndex(
                Index.fromOneBased(RegisteredNotes.MAX_NOTE_INDEX + 1)));
    }

    @Test
    public void equals_hashCodeAndHashSetLookup_respectValueEqualityAndOrder() {
        RegisteredNotes first = new RegisteredNotes(List.of(FIRST_NOTE, SECOND_NOTE));
        RegisteredNotes equal = new RegisteredNotes(List.of(
                new Note("Review lecture"), new Note("Submit assignment")));
        RegisteredNotes differentOrder = new RegisteredNotes(List.of(SECOND_NOTE, FIRST_NOTE));
        Set<RegisteredNotes> notes = new HashSet<>();
        notes.add(first);

        assertEquals(first, first);
        assertEquals(first, equal);
        assertEquals(first.hashCode(), equal.hashCode());
        assertNotEquals(first, differentOrder);
        assertNotEquals(first, null);
        assertNotEquals(first, List.of());
        assertTrue(notes.contains(equal));
        assertFalse(notes.add(equal));
    }

    @Test
    public void toString_orderedNotes_returnsOneBasedIndexSummary() {
        RegisteredNotes registeredNotes = new RegisteredNotes(List.of(FIRST_NOTE, SECOND_NOTE));

        assertEquals("1: Review lecture\n2: Submit assignment\n", registeredNotes.toString());
        assertEquals("", new RegisteredNotes().toString());
    }
}

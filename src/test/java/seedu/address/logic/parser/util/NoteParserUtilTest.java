package seedu.address.logic.parser.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TestUtil.WHITESPACE;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.Note;
import seedu.address.model.student.RegisteredNotes;

public class NoteParserUtilTest {

    @Test
    public void parseNoteIndex_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> NoteParserUtil.parseNoteIndex(null));
    }

    @Test
    public void parseNoteIndex_validBoundaries_returnsIndex() throws Exception {
        assertEquals(Index.fromOneBased(1), NoteParserUtil.parseNoteIndex("1"));
        assertEquals(Index.fromOneBased(RegisteredNotes.MAX_NOTE_INDEX),
                NoteParserUtil.parseNoteIndex(" " + RegisteredNotes.MAX_NOTE_INDEX + " "));
    }

    @Test
    public void parseNoteIndex_invalidValues_throwsParseException() {
        for (String noteIndex : List.of("", WHITESPACE, "0", "-1", "1.5", "abc", "1 2", "2147483648")) {
            assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_INDEX, () ->
                    NoteParserUtil.parseNoteIndex(noteIndex));
        }
        assertThrows(ParseException.class, RegisteredNotes.MESSAGE_CONSTRAINTS, () ->
                NoteParserUtil.parseNoteIndex("11"));
    }

    @Test
    public void parseNoteText_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> NoteParserUtil.parseNoteText(null));
    }

    @Test
    public void parseNoteText_validValueWithWhitespace_returnsTrimmedNote() throws Exception {
        assertEquals(new Note("Review lecture notes"),
                NoteParserUtil.parseNoteText(WHITESPACE + "Review lecture notes" + WHITESPACE));
    }

    @Test
    public void parseNoteText_invalidValues_throwsParseException() {
        for (String noteText : List.of("", WHITESPACE, "Review!", "a".repeat(Note.MAX_NOTE_LENGTH + 1))) {
            assertThrows(ParseException.class, Note.MESSAGE_CONSTRAINTS, () -> NoteParserUtil.parseNoteText(noteText));
        }
    }
}

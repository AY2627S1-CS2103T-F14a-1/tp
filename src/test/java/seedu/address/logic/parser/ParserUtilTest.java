package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_EFFORT;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_WEEK;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.Email;
import seedu.address.model.student.Name;
import seedu.address.model.student.Note;
import seedu.address.model.student.Phone;
import seedu.address.model.student.RegisteredNotes;
import seedu.address.model.student.StudioGroup;
import seedu.address.model.tag.Tag;

public class ParserUtilTest {

    protected static final String WHITESPACE = " \t\r\n";

    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";
    private static final String INVALID_STUDIO_GROUP = "#3Q";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "123456";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";
    private static final String VALID_STUDIO_GROUP = "3A";

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, ()
            -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_STUDENT, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_STUDENT, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseWeek_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseWeek(null));
    }

    @Test
    public void parseWeek_invalidInput_throwsParseException() {
        String[] invalidWeeks = {"", " ", "-1", "+1", "1.0", "one", "1 2", "14", "2147483648"};

        for (String invalidWeek : invalidWeeks) {
            assertThrows(ParseException.class, MESSAGE_INVALID_WEEK, () -> ParserUtil.parseWeek(invalidWeek));
        }
    }

    @Test
    public void parseWeek_validInput_returnsWeek() throws Exception {
        assertEquals(0, ParserUtil.parseWeek("0"));
        assertEquals(3, ParserUtil.parseWeek("3"));
        assertEquals(13, ParserUtil.parseWeek("13"));
        assertEquals(3, ParserUtil.parseWeek(" \t3 "));
    }

    @Test
    public void parseEffortScore_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEffortScore(null));
    }

    @Test
    public void parseEffortScore_invalidInput_throwsParseException() {
        String[] invalidEffortScores = {"", " ", "-1", "+1", "1.0", "one", "1 2", "2147483648"};

        for (String invalidEffortScore : invalidEffortScores) {
            assertThrows(ParseException.class, MESSAGE_INVALID_EFFORT, () ->
                    ParserUtil.parseEffortScore(invalidEffortScore));
        }
    }

    @Test
    public void parseEffortScore_validInput_returnsEffortScore() throws Exception {
        assertEquals(0, ParserUtil.parseEffortScore("0"));
        assertEquals(50, ParserUtil.parseEffortScore("50"));
        assertEquals(Integer.MAX_VALUE, ParserUtil.parseEffortScore(String.valueOf(Integer.MAX_VALUE)));
        assertEquals(50, ParserUtil.parseEffortScore(" \t50 "));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone((String) null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(VALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }

    @Test
    public void parseTag_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTag(null));
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG));
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() throws Exception {
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(VALID_TAG_1));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
        String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(tagWithWhitespace));
    }

    @Test
    public void parseTags_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTags(null));
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTags(List.of(VALID_TAG_1, INVALID_TAG)));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseTags(List.of()).isEmpty());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(List.of(VALID_TAG_1, VALID_TAG_2));
        Set<Tag> expectedTagSet = Set.of(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2));

        assertEquals(expectedTagSet, actualTagSet);
    }

    @Test
    public void parseStudioGroup_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseStudioGroup((String) null));
    }

    @Test
    public void parseStudioGroup_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseStudioGroup(INVALID_STUDIO_GROUP));
    }

    @Test
    public void parseStudioGroup_validValueWithoutWhitespace_returnsStudioGroup() throws Exception {
        StudioGroup expectedStudioGroup = new StudioGroup(VALID_STUDIO_GROUP);
        assertEquals(expectedStudioGroup, ParserUtil.parseStudioGroup(VALID_STUDIO_GROUP));
    }

    @Test
    public void parseStudioGroup_validValueWithWhitespace_returnsTrimmedStudioGroup() throws Exception {
        String studioGroupWithWhitespace = WHITESPACE + VALID_STUDIO_GROUP + WHITESPACE;
        StudioGroup expectedStudioGroup = new StudioGroup(VALID_STUDIO_GROUP);
        assertEquals(expectedStudioGroup, ParserUtil.parseStudioGroup(studioGroupWithWhitespace));
    }

    @Test
    public void parseNoteId_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseNoteId(null));
    }

    @Test
    public void parseNoteId_validBoundaries_returnsTrimmedNumber() throws Exception {
        assertEquals(RegisteredNotes.MIN_NOTE_ID, ParserUtil.parseNoteId("1"));
        assertEquals(RegisteredNotes.MAX_NOTE_ID, ParserUtil.parseNoteId(" " + RegisteredNotes.MAX_NOTE_ID + " "));
    }

    @Test
    public void parseNoteId_invalidValues_throwsParseException() {
        for (String noteId : List.of("", WHITESPACE, "0", "11", "-1", "1.5", "abc", "1 2", "2147483648")) {
            assertThrows(ParseException.class,
                    RegisteredNotes.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseNoteId(noteId));
        }
    }

    @Test
    public void parseNoteText_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseNoteText(null));
    }

    @Test
    public void parseNoteText_validValueWithWhitespace_returnsTrimmedNote() throws Exception {
        assertEquals(new Note("Review lecture notes"),
                ParserUtil.parseNoteText(WHITESPACE + "Review lecture notes" + WHITESPACE));
    }

    @Test
    public void parseNoteText_invalidValues_throwsParseException() {
        for (String noteText : List.of("", WHITESPACE, "Review!", "a".repeat(Note.MAX_NOTE_LENGTH + 1))) {
            assertThrows(ParseException.class, Note.MESSAGE_CONSTRAINTS, () -> ParserUtil.parseNoteText(noteText));
        }
    }
}

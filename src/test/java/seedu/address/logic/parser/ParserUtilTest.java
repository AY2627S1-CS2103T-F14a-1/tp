package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.Email;
import seedu.address.model.student.Name;
import seedu.address.model.student.Phone;
import seedu.address.model.student.StudioGroup;
import seedu.address.model.tag.Tag;

public class ParserUtilTest {
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

    private static final String WHITESPACE = " \t\r\n";

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
    public void parseMasteryCheckDateTime_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseMasteryCheckDateTime(null));
    }

    @Test
    public void parseMasteryCheckDateTime_validValues_returnsDateTime() throws Exception {
        assertEquals(LocalDateTime.of(2026, 10, 20, 0, 0),
                ParserUtil.parseMasteryCheckDateTime("2026-10-20|00:00"));
        assertEquals(LocalDateTime.of(2026, 12, 31, 23, 59),
                ParserUtil.parseMasteryCheckDateTime("2026-12-31|23:59"));
        assertEquals(LocalDateTime.of(2024, 2, 29, 12, 30),
                ParserUtil.parseMasteryCheckDateTime("2024-02-29|12:30"));
        assertEquals(LocalDateTime.of(2000, 2, 29, 12, 30),
                ParserUtil.parseMasteryCheckDateTime("2000-02-29|12:30"));
    }

    @Test
    public void parseMasteryCheckDateTime_surroundingWhitespace_returnsTrimmedDateTime() throws Exception {
        assertEquals(LocalDateTime.of(2026, 10, 20, 9, 5),
                ParserUtil.parseMasteryCheckDateTime(WHITESPACE + "2026-10-20|09:05" + WHITESPACE));
    }

    @Test
    public void parseMasteryCheckDateTime_impossibleDatesAndTimes_throwsParseException() {
        for (String dateTime : List.of("2026-02-29|12:30", "1900-02-29|12:30", "2026-02-30|12:30",
                "2026-04-31|12:30", "2026-00-20|12:30", "2026-13-20|12:30", "2026-10-00|12:30",
                "2026-10-32|12:30", "2026-10-20|24:00", "2026-10-20|25:00", "2026-10-20|23:60",
                "2026-10-20|-1:00", "2026-10-20|12:-1")) {
            assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_MASTERY_CHECK_DATETIME,
                    () -> ParserUtil.parseMasteryCheckDateTime(dateTime));
        }
    }

    @Test
    public void parseMasteryCheckDateTime_malformedInput_throwsParseException() {
        for (String dateTime : List.of("", WHITESPACE, "2026-10-20", "12:30", "2026-10-20|",
                "|12:30", "2026-10-20||12:30", "2026-10-20T12:30", "2026-10-20 12:30",
                "2026-1-20|12:30", "2026-10-2|12:30", "26-10-20|12:30", "2026-10-20|9:05",
                "2026-10-20|09:5", "2026-10-20 |12:30", "2026-10-20| 12:30",
                "2026-10-20|12:30:00", "2026-10-20|12:30Z", "2026-10-20|12:30extra")) {
            assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_MASTERY_CHECK_DATETIME,
                    () -> ParserUtil.parseMasteryCheckDateTime(dateTime));
        }
    }
}

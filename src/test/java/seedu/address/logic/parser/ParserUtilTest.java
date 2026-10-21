package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.Email;
import seedu.address.model.student.Name;
import seedu.address.model.student.Phone;
import seedu.address.model.student.RegisteredMasteryChecks;
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
    public void parseMasteryCheckNumber_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseMasteryCheckNumber(null));
    }

    @Test
    public void parseMasteryCheckNumber_validBoundaries_returnsTrimmedNumber() throws Exception {
        assertEquals(1, ParserUtil.parseMasteryCheckNumber("1"));
        assertEquals(1, ParserUtil.parseMasteryCheckNumber(WHITESPACE + "1" + WHITESPACE));
        assertEquals(Integer.MAX_VALUE, ParserUtil.parseMasteryCheckNumber("2147483647"));
    }

    @Test
    public void parseMasteryCheckNumber_invalidValues_throwsParseException() {
        for (String number : List.of("", WHITESPACE, "0", "-1", "1.5", "1e2", "abc", "1 2", "2147483648")) {
            assertThrows(ParseException.class, RegisteredMasteryChecks.MESSAGE_CONSTRAINTS, () ->
                    ParserUtil.parseMasteryCheckNumber(number));
        }
    }


    @Test
    public void parseMasteryCheckDateTime_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseMasteryCheckDateTime(null));
    }

    @Test
    public void parseMasteryCheckDateTime_validBoundaries_returnsCurrentYearAppointment() throws Exception {
        int year = LocalDate.now().getYear();
        assertEquals(LocalDateTime.of(year, 1, 1, 0, 0), ParserUtil.parseMasteryCheckDateTime("01-01|00:00"));
        assertEquals(LocalDateTime.of(year, 12, 31, 23, 59), ParserUtil.parseMasteryCheckDateTime("31-12|23:59"));
        assertEquals(LocalDateTime.of(year, 2, 28, 12, 30), ParserUtil.parseMasteryCheckDateTime("28-02|12:30"));
        if (Year.isLeap(year)) {
            assertEquals(LocalDateTime.of(year, 2, 29, 12, 30), ParserUtil.parseMasteryCheckDateTime("29-02|12:30"));
        } else {
            assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_MASTERY_CHECK_DATETIME, () ->
                    ParserUtil.parseMasteryCheckDateTime("29-02|12:30"));
        }
    }

    @Test
    public void parseMasteryCheckDateTime_surroundingWhitespace_returnsTrimmedAppointment() throws Exception {
        assertEquals(LocalDateTime.of(LocalDate.now().getYear(), 8, 18, 14, 30),
                ParserUtil.parseMasteryCheckDateTime(WHITESPACE + "18-08|14:30" + WHITESPACE));
    }

    @Test
    public void parseMasteryCheckDateTime_impossibleDatesAndTimes_throwsParseException() {
        for (String value : List.of("30-02|14:30", "31-04|14:30", "00-08|14:30", "32-08|14:30",
                "18-00|14:30", "18-13|14:30", "18-08|24:00", "18-08|25:00", "18-08|14:60",
                "18-08|-1:30", "18-08|14:-1")) {
            assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_MASTERY_CHECK_DATETIME, () ->
                    ParserUtil.parseMasteryCheckDateTime(value));
        }
    }

    @Test
    public void parseMasteryCheckDateTime_malformedInput_throwsParseException() {
        for (String value : List.of("", WHITESPACE, "18-08", "14:30", "18-08|", "|14:30", "18-08||14:30",
                "18-08T14:30", "18-08 14:30", "8-08|14:30", "18-8|14:30", "18-08|4:30", "18-08|14:3",
                "18-08 |14:30", "18-08| 14:30", "18-08|14:30:00", "18-08|14:30Z", "18-08|14:30extra",
                "2026-08-18|14:30")) {
            assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_MASTERY_CHECK_DATETIME, () ->
                    ParserUtil.parseMasteryCheckDateTime(value));
        }
    }

}

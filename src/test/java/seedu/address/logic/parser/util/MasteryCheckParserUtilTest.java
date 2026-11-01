package seedu.address.logic.parser.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TestUtil.WHITESPACE;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

/** Tests mastery check number validation and strict appointment parsing. */
public class MasteryCheckParserUtilTest {

    @Test
    public void parseMasteryCheckNumber_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> MasteryCheckParserUtil.parseMasteryCheckNumber(null));
    }

    @Test
    public void parseMasteryCheckNumber_validBoundaries_returnsTrimmedNumber() throws Exception {
        assertEquals(1, MasteryCheckParserUtil.parseMasteryCheckNumber("1"));
        assertEquals(1, MasteryCheckParserUtil.parseMasteryCheckNumber(WHITESPACE + "1" + WHITESPACE));
        assertEquals(Integer.MAX_VALUE, MasteryCheckParserUtil.parseMasteryCheckNumber("2147483647"));
    }

    @Test
    public void parseMasteryCheckNumber_nonPositiveValues_reportsNumberConstraint() {
        for (String number : List.of("0", "-1", "-2147483648")) {
            assertThrows(ParseException.class, MasteryCheckParserUtil.MESSAGE_ERROR_NEGATIVE_NUMBER, () ->
                    MasteryCheckParserUtil.parseMasteryCheckNumber(number));
        }
    }

    @Test
    public void parseMasteryCheckNumber_nonIntegerOrOverflowingValues_reportsNonNumberMessage() {
        for (String number : List.of("", WHITESPACE, "1.5", "1e2", "abc", "1 2", "2147483648")) {
            assertThrows(ParseException.class, MasteryCheckParserUtil.MESSAGE_ERROR_NON_NUMBER, () ->
                    MasteryCheckParserUtil.parseMasteryCheckNumber(number));
        }
    }

    @Test
    public void parseMasteryCheckDateTime_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> MasteryCheckParserUtil.parseMasteryCheckDateTime(null));
    }

    @Test
    public void parseMasteryCheckDateTime_validBoundaries_returnsSpecifiedYearAppointment() throws Exception {
        assertEquals(LocalDateTime.of(2026, 1, 1, 0, 0),
                MasteryCheckParserUtil.parseMasteryCheckDateTime("2026-01-01|00:00"));
        assertEquals(LocalDateTime.of(2026, 12, 31, 23, 59),
                MasteryCheckParserUtil.parseMasteryCheckDateTime("2026-12-31|23:59"));
        assertEquals(LocalDateTime.of(2024, 2, 29, 12, 30),
                MasteryCheckParserUtil.parseMasteryCheckDateTime("2024-02-29|12:30"));
        assertEquals(LocalDateTime.of(2000, 2, 29, 12, 30),
                MasteryCheckParserUtil.parseMasteryCheckDateTime("2000-02-29|12:30"));
    }

    @Test
    public void parseMasteryCheckDateTime_surroundingWhitespace_returnsTrimmedAppointment() throws Exception {
        assertEquals(LocalDateTime.of(2026, 8, 18, 14, 30),
                MasteryCheckParserUtil.parseMasteryCheckDateTime(WHITESPACE + "2026-08-18|14:30" + WHITESPACE));
    }

    @Test
    public void parseMasteryCheckDateTime_impossibleDates_reportsDateConstraint() {
        for (String value : List.of("2026-02-30|14:30", "2026-04-31|14:30", "2026-08-00|14:30",
                "2026-08-32|14:30", "2026-00-18|14:30", "2026-13-18|14:30", "2026-02-29|14:30",
                "1900-02-29|14:30")) {
            assertThrows(ParseException.class, MasteryCheckParserUtil.MESSAGE_INVALID_MASTERY_CHECK_DATE, () ->
                    MasteryCheckParserUtil.parseMasteryCheckDateTime(value));
        }
    }

    @Test
    public void parseMasteryCheckDateTime_impossibleTimes_reportsTimeConstraint() {
        for (String value : List.of("2026-08-18|24:00", "2026-08-18|25:00", "2026-08-18|14:60",
                "2026-08-18|99:99")) {
            assertThrows(ParseException.class, MasteryCheckParserUtil.MESSAGE_INVALID_MASTERY_CHECK_TIME, () ->
                    MasteryCheckParserUtil.parseMasteryCheckDateTime(value));
        }
    }

    @Test
    public void parseMasteryCheckDateTime_malformedInput_reportsFormatConstraint() {
        for (String value : List.of("", WHITESPACE, "2026-08-18", "14:30", "2026-08-18|", "|14:30",
                "2026-08-18||14:30", "2026-08-18T14:30", "2026-08-18 14:30", "18-08|14:30",
                "26-08-18|14:30", "2026-8-18|14:30", "2026-08-8|14:30", "2026-08-18|4:30",
                "2026-08-18|14:3", "2026-08-18 |14:30", "2026-08-18| 14:30", "2026-08-18|14:30:00",
                "2026-08-18|14:30Z", "2026-08-18|14:30extra", "2026-08-18|-1:30", "2026-08-18|14:-1",
                "2026/08/18|14:30", "2026-aa-18|14:30", "2026-08-18|aa:30")) {
            assertThrows(ParseException.class,
                    MasteryCheckParserUtil.MESSAGE_INVALID_MASTERY_CHECK_DATETIME_FORMAT, () ->
                    MasteryCheckParserUtil.parseMasteryCheckDateTime(value));
        }
    }

    @Test
    public void parseMasteryCheckDateTime_multipleInvalidParts_reportsFormatThenDateBeforeTime() {
        assertThrows(ParseException.class, MasteryCheckParserUtil.MESSAGE_INVALID_MASTERY_CHECK_DATETIME_FORMAT, () ->
                MasteryCheckParserUtil.parseMasteryCheckDateTime("2026-04-31|24:0"));
        assertThrows(ParseException.class, MasteryCheckParserUtil.MESSAGE_INVALID_MASTERY_CHECK_DATE, () ->
                MasteryCheckParserUtil.parseMasteryCheckDateTime("2026-04-31|24:00"));
    }
}

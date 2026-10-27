package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.MasteryCheckParser.MESSAGE_INVALID_MASTERY_CHECK_DATETIME;
import static seedu.address.logic.parser.MasteryCheckParser.parseMasteryCheckDateTime;
import static seedu.address.logic.parser.MasteryCheckParser.parseMasteryCheckNumber;
import static seedu.address.logic.parser.ParserUtilTest.WHITESPACE;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.RegisteredMasteryChecks;

public class MasteryCheckParserTest {


    @Test
    public void parseMasteryCheckNumber_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parseMasteryCheckNumber(null));
    }

    @Test
    public void parseMasteryCheckNumber_validBoundaries_returnsTrimmedNumber() throws Exception {
        assertEquals(1, parseMasteryCheckNumber("1"));
        assertEquals(1, parseMasteryCheckNumber(WHITESPACE + "1" + WHITESPACE));
        assertEquals(Integer.MAX_VALUE, parseMasteryCheckNumber("2147483647"));
    }

    @Test
    public void parseMasteryCheckNumber_nonPositiveValues_reportsNumberConstraint() {
        for (String number : List.of("0", "-1", "-2147483648")) {
            assertThrows(ParseException.class, RegisteredMasteryChecks.MESSAGE_CONSTRAINTS, () ->
                    parseMasteryCheckNumber(number));
        }
    }

    @Test
    public void parseMasteryCheckNumber_nonIntegerOrOverflowingValues_reportsNonNumberMessage() {
        for (String number : List.of("", WHITESPACE, "1.5", "1e2", "abc", "1 2", "2147483648")) {
            assertThrows(ParseException.class, RegisteredMasteryChecks.MESSAGE_NON_NUMBER, () ->
                    parseMasteryCheckNumber(number));
        }
    }

    @Test
    public void parseMasteryCheckDateTime_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parseMasteryCheckDateTime(null));
    }

    @Test
    public void parseMasteryCheckDateTime_validBoundaries_returnsCurrentYearAppointment() throws Exception {
        int year = LocalDate.now().getYear();
        assertEquals(LocalDateTime.of(year, 1, 1, 0, 0), parseMasteryCheckDateTime("01-01|00:00"));
        assertEquals(LocalDateTime.of(year, 12, 31, 23, 59), parseMasteryCheckDateTime("31-12|23:59"));
        assertEquals(LocalDateTime.of(year, 2, 28, 12, 30), parseMasteryCheckDateTime("28-02|12:30"));
        if (Year.isLeap(year)) {
            assertEquals(LocalDateTime.of(year, 2, 29, 12, 30), parseMasteryCheckDateTime("29-02|12:30"));
        } else {
            assertThrows(ParseException.class, MESSAGE_INVALID_MASTERY_CHECK_DATETIME, () ->
                    parseMasteryCheckDateTime("29-02|12:30"));
        }
    }

    @Test
    public void parseMasteryCheckDateTime_surroundingWhitespace_returnsTrimmedAppointment() throws Exception {
        assertEquals(LocalDateTime.of(LocalDate.now().getYear(), 8, 18, 14, 30),
                parseMasteryCheckDateTime(WHITESPACE + "18-08|14:30" + WHITESPACE));
    }

    @Test
    public void parseMasteryCheckDateTime_impossibleDatesAndTimes_throwsParseException() {
        for (String value : List.of("30-02|14:30", "31-04|14:30", "00-08|14:30", "32-08|14:30",
                "18-00|14:30", "18-13|14:30", "18-08|24:00", "18-08|25:00", "18-08|14:60",
                "18-08|-1:30", "18-08|14:-1")) {
            assertThrows(ParseException.class, MESSAGE_INVALID_MASTERY_CHECK_DATETIME, () ->
                    parseMasteryCheckDateTime(value));
        }
    }

    @Test
    public void parseMasteryCheckDateTime_malformedInput_throwsParseException() {
        for (String value : List.of("", WHITESPACE, "18-08", "14:30", "18-08|", "|14:30", "18-08||14:30",
                "18-08T14:30", "18-08 14:30", "8-08|14:30", "18-8|14:30", "18-08|4:30", "18-08|14:3",
                "18-08 |14:30", "18-08| 14:30", "18-08|14:30:00", "18-08|14:30Z", "18-08|14:30extra",
                "2026-08-18|14:30")) {
            assertThrows(ParseException.class, MESSAGE_INVALID_MASTERY_CHECK_DATETIME, () ->
                    parseMasteryCheckDateTime(value));
        }
    }

}

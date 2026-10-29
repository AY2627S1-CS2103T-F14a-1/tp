package seedu.address.logic.parser.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TestUtil.WHITESPACE;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.mission.MissionId;

public class MissionParserUtilTest {

    @Test
    public void parseMissionId_validCaseWhitespaceAndBoundary_returnsCanonicalId() throws ParseException {
        assertEquals(new MissionId("M01"), MissionParserUtil.parseMissionId(" \tm01\n"));
        assertEquals(new MissionId("M02"), MissionParserUtil.parseMissionId("m 0 2"));
        assertEquals(new MissionId("M105"), MissionParserUtil.parseMissionId("M105"));
        assertEquals(new MissionId("M9223372036854775807"),
                MissionParserUtil.parseMissionId("m9223372036854775807"));
    }

    @Test
    public void parseMissionId_invalidValues_throwsParseException() {
        for (String value : List.of("", " ", "M1", "M00", "M 0 0", "M001", "M-1", "M+1", "M1.0", "1",
                "M01 extra", "M01 x/test", "M9223372036854775808")) {
            assertThrows(ParseException.class, MissionId.MESSAGE_CONSTRAINTS, () ->
                    MissionParserUtil.parseMissionId(value));
        }
        assertThrows(NullPointerException.class, () -> MissionParserUtil.parseMissionId(null));
    }

    @Test
    public void parseMissionName_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> MissionParserUtil.parseMissionName(null));
    }

    @Test
    public void parseMissionName_blankInput_throwsParseException() {
        for (String value : List.of("", " ", WHITESPACE)) {
            assertThrows(ParseException.class, MissionParserUtil.MESSAGE_INVALID_MISSION_NAME, () ->
                    MissionParserUtil.parseMissionName(value));
        }
    }

    @Test
    public void parseMissionName_validInput_normalizesWhitespaceAndPreservesText() throws ParseException {
        assertEquals("Mission 1: Rune Reading!",
                MissionParserUtil.parseMissionName(" \tMission 1:   Rune\n Reading! \n"));
    }

    @Test
    public void parseMissionDeadline_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> MissionParserUtil.parseMissionDeadline(null));
    }

    @Test
    public void parseMissionDeadline_validDates_returnsLocalDate() throws ParseException {
        assertEquals(LocalDate.of(2024, 2, 29), MissionParserUtil.parseMissionDeadline("2024-02-29"));
        assertEquals(LocalDate.of(2026, 10, 20), MissionParserUtil.parseMissionDeadline("2026-10-20"));
        assertEquals(LocalDate.of(2026, 10, 20),
                MissionParserUtil.parseMissionDeadline(WHITESPACE + "2026-10-20" + WHITESPACE));
        assertEquals(LocalDate.of(2026, 12, 12),
                MissionParserUtil.parseMissionDeadline("2026 -12 -12"));
    }

    @Test
    public void parseMissionDeadline_malformedFormat_throwsParseException() {
        for (String value : List.of("", WHITESPACE, "2026-2-03", "2026 -2 -03", "26-10-20", "2026/10/20",
                "2026-10-20 extra", "2026-10-20T00:00")) {
            assertThrows(ParseException.class, MissionParserUtil.MESSAGE_INVALID_MISSION_DEADLINE, () ->
                    MissionParserUtil.parseMissionDeadline(value));
        }
    }

    @Test
    public void parseMissionDeadline_impossibleCalendarDate_throwsParseException() {
        for (String value : List.of("2026-02-29", "2026-04-31", "2026-13-01", "2026-00-10",
                "2026-10-00")) {
            assertThrows(ParseException.class, MissionParserUtil.MESSAGE_INVALID_MISSION_CALENDAR_DATE, () ->
                    MissionParserUtil.parseMissionDeadline(value));
        }
    }

}

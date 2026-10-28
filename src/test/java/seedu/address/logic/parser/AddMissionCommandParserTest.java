package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddMissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.util.MissionParserUtil;
import seedu.address.model.mission.MissionId;

/** Tests mission-field validation and command routing. */
public class AddMissionCommandParserTest {

    private static final String USAGE_SUFFIX = "\n\n" + AddMissionCommand.MESSAGE_USAGE;

    private final AddMissionCommandParser parser = new AddMissionCommandParser();

    @Test
    public void parse_unicodeWhitespaceOnlyName_rejectsBlankName() {
        for (String name : List.of("\u00a0", "\u2003", " \t\u00a0 ")) {
            ParseException error = assertThrows(ParseException.class, () ->
                    parser.parse("m/M01 n/" + name + " d/2026-10-20"));
            assertEquals(MissionParserUtil.MESSAGE_INVALID_MISSION_NAME + USAGE_SUFFIX,
                    error.getMessage());
        }
    }

    @Test
    public void parseMissionFields_validAndNullInput_preservesValidationContract() throws Exception {
        assertEquals("Runes: Part 1!", MissionParserUtil.parseMissionName("  Runes:  Part 1!  "));
        assertEquals(LocalDate.of(2000, 2, 29), MissionParserUtil.parseMissionDeadline(" 2000-02-29 "));
        assertThrows(NullPointerException.class, () -> MissionParserUtil.parseMissionName(null));
        assertThrows(NullPointerException.class, () -> MissionParserUtil.parseMissionDeadline(null));
    }

    @Test
    public void parse_validArguments_normalizesNameAndAcceptsPastLeapDate() throws Exception {
        AddMissionCommand expected = new AddMissionCommand(new MissionId("M01"),
                "Mission 1: Rune Reading!", LocalDate.of(2024, 2, 29));

        assertEquals(expected, parser.parse(" m/M01 n/  Mission 1:  Rune Reading!  d/ 2024-02-29 "));
        assertEquals(expected, parser.parse("d/2024-02-29\tm/M01\tn/Mission 1:  Rune Reading!"));
        assertEquals(expected, new AddressBookParser().parseCommand(
                "ADDMISSION m/M01 n/Mission 1:  Rune Reading! d/2024-02-29"));
    }

    @Test
    public void parse_validCalendarBoundaries_acceptsDeadline() throws Exception {
        for (String date : List.of("2026-01-01", "2026-12-31", "2026-04-30", "2026-02-28",
                "2024-02-29", "2000-02-29")) {
            assertEquals(new AddMissionCommand(new MissionId("M01"), "Runes", LocalDate.parse(date)),
                    parser.parse("m/M01 n/Runes d/ " + date + " "));
        }
    }

    @Test
    public void parse_invalidArguments_reportsFieldSpecificErrors() {
        String[][] cases = {
            {"", String.join("\n", CliSyntax.PREFIX_MISSION.getMissingMessage(),
                    CliSyntax.PREFIX_NAME.getMissingMessage(), CliSyntax.PREFIX_DATE.getMissingMessage())},
            {"n/A d/2026-10-20", CliSyntax.PREFIX_MISSION.getMissingMessage()},
            {"m/M1 n/A d/2026-10-20", MissionParserUtil.MESSAGE_INVALID_MISSION_ID},
            {"m/M00 n/A d/2026-10-20", MissionParserUtil.MESSAGE_INVALID_MISSION_ID},
            {"m/ n/Runes d/2026-10-20", MissionParserUtil.MESSAGE_INVALID_MISSION_ID},
            {"m/M01", String.join("\n", CliSyntax.PREFIX_NAME.getMissingMessage(),
                    CliSyntax.PREFIX_DATE.getMissingMessage())},
            {"m/M01 n/ \t d/2026-10-20", MissionParserUtil.MESSAGE_INVALID_MISSION_NAME},
            {"m/M01 n/Runes", CliSyntax.PREFIX_DATE.getMissingMessage()},
            {"m/M01 n/Runes d/", MissionParserUtil.MESSAGE_INVALID_MISSION_DEADLINE},
            {"m/M01 n/Runes d/2026-02-30", MissionParserUtil.MESSAGE_INVALID_MISSION_CALENDAR_DATE},
            {"m/M01 m/M02 n/A d/2026-10-20",
                    CliSyntax.PREFIX_MISSION.getDuplicateMessage()},
            {"m/M01 n/A n/B d/2026-10-20",
                    CliSyntax.PREFIX_NAME.getDuplicateMessage()},
            {"m/M01 n/A d/2026-10-20 d/2026-10-21",
                    CliSyntax.PREFIX_DATE.getDuplicateMessage()},
            {"extra m/M01 n/A d/2026-10-20", Messages.MESSAGE_INVALID_COMMAND}
        };

        for (String[] testCase : cases) {
            ParseException error = assertThrows(ParseException.class, () -> parser.parse(testCase[0]), testCase[0]);
            assertEquals(testCase[1] + USAGE_SUFFIX, error.getMessage());
        }
    }

    @Test
    public void parse_invalidDeadlineFormat_reportsFormatConstraint() {
        for (String date : List.of("", " ", "20/10/2026", "2026-1-01", "26-10-20",
                "2026-10-20T12:00", "2026-10-20 extra")) {
            ParseException error = assertThrows(ParseException.class, () ->
                    parser.parse("m/M01 n/Runes d/" + date));
            assertEquals(MissionParserUtil.MESSAGE_INVALID_MISSION_DEADLINE + USAGE_SUFFIX, error.getMessage());
            ParseException missingIdError = assertThrows(ParseException.class, () ->
                    parser.parse("n/Runes d/" + date));
            assertEquals(CliSyntax.PREFIX_MISSION.getMissingMessage() + USAGE_SUFFIX,
                    missingIdError.getMessage());
        }
    }

    @Test
    public void parse_invalidCalendarDate_reportsCalendarConstraint() {
        for (String date : List.of("2023-02-29", "1900-02-29", "2026-04-31", "2026-13-12",
                "2026-00-01", "2026-10-00", "2026-10-32")) {
            ParseException error = assertThrows(ParseException.class, () ->
                    parser.parse("m/M01 n/Runes d/" + date));
            assertEquals(MissionParserUtil.MESSAGE_INVALID_MISSION_CALENDAR_DATE + USAGE_SUFFIX, error.getMessage());
            ParseException missingIdError = assertThrows(ParseException.class, () ->
                    parser.parse("n/Runes d/" + date));
            assertEquals(CliSyntax.PREFIX_MISSION.getMissingMessage() + USAGE_SUFFIX,
                    missingIdError.getMessage());
        }
    }

}

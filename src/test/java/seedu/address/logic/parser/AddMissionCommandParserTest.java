package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddMissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.mission.MissionId;

/** Tests mission-field validation and command routing. */
public class AddMissionCommandParserTest {

    private final AddMissionCommandParser parser = new AddMissionCommandParser();

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
        String usage = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, AddMissionCommand.MESSAGE_USAGE);
        String[][] cases = {
            {"", String.join("\n", CliSyntax.PREFIX_MISSION.getMissingMessage(),
                    CliSyntax.PREFIX_NAME.getMissingMessage(), CliSyntax.PREFIX_DATE.getMissingMessage())},
            {"n/A d/2026-10-20", CliSyntax.PREFIX_MISSION.getMissingMessage()},
            {"m/M1 n/A d/2026-10-20", ParserUtil.MESSAGE_INVALID_MISSION_ID},
            {"m/M00 n/A d/2026-10-20", ParserUtil.MESSAGE_INVALID_MISSION_ID},
            {"m/ n/Runes d/2026-10-20", ParserUtil.MESSAGE_INVALID_MISSION_ID},
            {"m/M01", String.join("\n", CliSyntax.PREFIX_NAME.getMissingMessage(),
                    CliSyntax.PREFIX_DATE.getMissingMessage())},
            {"m/M01 n/ \t d/2026-10-20", ParserUtil.MESSAGE_INVALID_MISSION_NAME},
            {"m/M01 n/Runes", CliSyntax.PREFIX_DATE.getMissingMessage()},
            {"m/M01 n/Runes d/", ParserUtil.MESSAGE_INVALID_MISSION_DEADLINE},
            {"m/M01 n/Runes d/2026-02-30", ParserUtil.MESSAGE_INVALID_MISSION_CALENDAR_DATE},
            {"m/M01 m/M02 n/A d/2026-10-20",
                    CliSyntax.PREFIX_MISSION.getDuplicateMessage()},
            {"m/M01 n/A n/B d/2026-10-20",
                    CliSyntax.PREFIX_NAME.getDuplicateMessage()},
            {"m/M01 n/A d/2026-10-20 d/2026-10-21",
                    CliSyntax.PREFIX_DATE.getDuplicateMessage()},
            {"extra m/M01 n/A d/2026-10-20", usage}
        };

        for (String[] testCase : cases) {
            ParseException error = assertThrows(ParseException.class, () -> parser.parse(testCase[0]), testCase[0]);
            assertEquals(testCase[1], error.getMessage());
        }
    }

    @Test
    public void parse_invalidDeadlineFormat_reportsFormatConstraint() {
        for (String date : List.of("", " ", "20/10/2026", "2026-1-01", "26-10-20",
                "2026-10-20T12:00", "2026-10-20 extra")) {
            ParseException error = assertThrows(ParseException.class, () ->
                    parser.parse("m/M01 n/Runes d/" + date));
            assertEquals(ParserUtil.MESSAGE_INVALID_MISSION_DEADLINE, error.getMessage());
        }
    }

    @Test
    public void parse_invalidCalendarDate_reportsCalendarConstraint() {
        for (String date : List.of("2023-02-29", "1900-02-29", "2026-04-31", "2026-13-12",
                "2026-00-01", "2026-10-00", "2026-10-32")) {
            ParseException error = assertThrows(ParseException.class, () ->
                    parser.parse("m/M01 n/Runes d/" + date));
            assertEquals(ParserUtil.MESSAGE_INVALID_MISSION_CALENDAR_DATE, error.getMessage());
        }
    }

    @Test
    public void parseCommand_missingMissionFields_reportsCliSyntaxMessages() {
        String[][] cases = {
            {"addmission", String.join("\n", CliSyntax.PREFIX_MISSION.getMissingMessage(),
                    CliSyntax.PREFIX_NAME.getMissingMessage(), CliSyntax.PREFIX_DATE.getMissingMessage())},
            {"addmission n/Track my time d/2026-10-20", CliSyntax.PREFIX_MISSION.getMissingMessage()},
            {"addmission m/M01", String.join("\n", CliSyntax.PREFIX_NAME.getMissingMessage(),
                    CliSyntax.PREFIX_DATE.getMissingMessage())},
            {"addmission m/M01 n/ d/2026-10-20", ParserUtil.MESSAGE_INVALID_MISSION_NAME},
            {"addmission m/M01 n/Track my time", CliSyntax.PREFIX_DATE.getMissingMessage()}
        };

        for (String[] testCase : cases) {
            ParseException error = assertThrows(ParseException.class, () ->
                    new AddressBookParser().parseCommand(testCase[0]));
            assertEquals(testCase[1], error.getMessage());
        }
    }
}

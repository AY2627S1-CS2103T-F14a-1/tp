package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddMissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Tests field validation, strict dates, and command routing. */
public class AddMissionCommandParserTest {

    private final AddMissionCommandParser parser = new AddMissionCommandParser();

    @Test
    public void parse_unicodeWhitespaceOnlyName_rejectsBlankName() {
        for (String name : List.of("\u00a0", "\u2003", " \t\u00a0 ")) {
            ParseException error = assertThrows(ParseException.class, () ->
                    parser.parse("n/" + name + " d/2026-10-20"));
            assertEquals(ParserUtil.MESSAGE_INVALID_MISSION_NAME, error.getMessage());
        }
    }

    @Test
    public void parseMissionFields_validAndNullInput_preservesValidationContract() throws Exception {
        assertEquals("Runes: Part 1!", ParserUtil.parseMissionName("  Runes:  Part 1!  "));
        assertEquals(LocalDate.of(2000, 2, 29), ParserUtil.parseMissionDeadline(" 2000-02-29 "));
        assertThrows(NullPointerException.class, () -> ParserUtil.parseMissionName(null));
        assertThrows(NullPointerException.class, () -> ParserUtil.parseMissionDeadline(null));
    }

    @Test
    public void parse_validArguments_normalizesNameAndAcceptsPastLeapDate() throws Exception {
        AddMissionCommand expected = new AddMissionCommand("Mission 1: Rune Reading!", LocalDate.of(2024, 2, 29));
        assertEquals(expected, parser.parse(" n/  Mission 1:  Rune Reading!  d/ 2024-02-29 "));
        assertEquals(expected, parser.parse("d/2024-02-29\tn/Mission 1:  Rune Reading!"));
        assertEquals(expected, new AddressBookParser().parseCommand(
                "ADDMISSION n/Mission 1:  Rune Reading! d/2024-02-29"));
    }

    @Test
    public void parse_invalidDeadlineFormat_reportsFormatConstraint() {
        for (String date : List.of("", " ", "20/10/2026", "2026-1-01", "26-10-20",
                "2026-10-20T12:00", "2026-10-20 extra")) {
            ParseException error = assertThrows(ParseException.class, () -> parser.parse("n/Runes d/" + date));
            assertEquals(ParserUtil.MESSAGE_INVALID_MISSION_DEADLINE, error.getMessage());
        }
    }

    @Test
    public void parse_invalidCalendarDate_reportsCalendarConstraint() {
        for (String date : List.of("2023-02-29", "1900-02-29", "2026-04-31", "2026-13-12",
                "2026-00-01", "2026-10-00", "2026-10-32")) {
            ParseException error = assertThrows(ParseException.class, () -> parser.parse("n/Runes d/" + date));
            assertEquals(ParserUtil.MESSAGE_INVALID_MISSION_CALENDAR_DATE, error.getMessage());
        }
    }

    @Test
    public void parse_validCalendarBoundaries_acceptsDeadline() throws Exception {
        for (String date : List.of("2026-01-01", "2026-12-31", "2026-04-30", "2026-02-28",
                "2024-02-29", "2000-02-29")) {
            assertEquals(new AddMissionCommand("Runes", LocalDate.parse(date)),
                    parser.parse("n/Runes d/ " + date + " "));
        }
    }

    @Test
    public void parse_invalidFields_reportsSpecificMessage() {
        String usage = String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, AddMissionCommand.MESSAGE_USAGE);
        String[][] cases = {
            {"", ParserUtil.MESSAGE_INVALID_MISSION_NAME},
            {"d/2026-10-20", ParserUtil.MESSAGE_INVALID_MISSION_NAME},
            {"n/Runes", AddMissionCommandParser.MESSAGE_MISSING_DEADLINE},
            {"n/ \t d/2026-10-20", ParserUtil.MESSAGE_INVALID_MISSION_NAME},
            {"n/A n/A d/2026-10-20", Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NAME)},
            {"n/A d/2026-10-20 d/2026-10-21", Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_DATE)},
            {"extra n/A d/2026-10-20", usage}
        };
        for (String[] testCase : cases) {
            ParseException error = assertThrows(ParseException.class, () -> parser.parse(testCase[0]));
            assertEquals(testCase[1], error.getMessage());
        }
    }

    @Test
    public void parseCommand_missingMissionFields_reportsFirstMissingField() {
        String[][] cases = {
            {"addmission n/", ParserUtil.MESSAGE_INVALID_MISSION_NAME},
            {"addmission n/ \t", ParserUtil.MESSAGE_INVALID_MISSION_NAME},
            {"addmission n/\u2003", ParserUtil.MESSAGE_INVALID_MISSION_NAME},
            {"addmission n/ d/invalid", ParserUtil.MESSAGE_INVALID_MISSION_NAME},
            {"addmission n/Track my time", AddMissionCommandParser.MESSAGE_MISSING_DEADLINE},
            {"addmission n/ Track my time  ", AddMissionCommandParser.MESSAGE_MISSING_DEADLINE},
            {"addmission n/A n/B", Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NAME)}
        };
        for (String[] testCase : cases) {
            ParseException error = assertThrows(ParseException.class, () ->
                    new AddressBookParser().parseCommand(testCase[0]));
            assertEquals(testCase[1], error.getMessage());
        }
    }
}

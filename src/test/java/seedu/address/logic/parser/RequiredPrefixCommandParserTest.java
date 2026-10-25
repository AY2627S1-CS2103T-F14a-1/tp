package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATETIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_TEXT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NUMBER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDIO_EFFORT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddMasteryCheckCommand;
import seedu.address.logic.commands.AddMissionCommand;
import seedu.address.logic.commands.AddStudioRecordCommand;
import seedu.address.logic.commands.DeleteMasteryCheckCommand;
import seedu.address.logic.commands.DeleteMissionCommand;
import seedu.address.logic.commands.DeleteStudentCommand;
import seedu.address.logic.commands.DeleteStudioRecordCommand;
import seedu.address.logic.commands.EditStudioRecordCommand;
import seedu.address.logic.commands.ListMissionsCommand;
import seedu.address.logic.commands.MarkMasteryCheckCommand;
import seedu.address.logic.commands.MarkMissionCommand;
import seedu.address.logic.commands.UnmarkMasteryCheckCommand;
import seedu.address.logic.commands.UnmarkMissionCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Checks that every command using the shared validator supplies its required prefixes. */
public class RequiredPrefixCommandParserTest {

    @Test
    public void parse_eachRequiredPrefixMissing_reportsPrefixSpecificMessage() {
        verifyRequiredPrefixes(new AddMasteryCheckCommandParser(), AddMasteryCheckCommand.MESSAGE_USAGE,
                List.of(PREFIX_INDEX, PREFIX_NUMBER, PREFIX_DATETIME), List.of("1", "1", "18-08|14:30"));
        verifyRequiredPrefixes(new DeleteMasteryCheckCommandParser(), DeleteMasteryCheckCommand.MESSAGE_USAGE,
                List.of(PREFIX_INDEX, PREFIX_NUMBER), List.of("1", "1"));
        verifyRequiredPrefixes(new MarkMasteryCheckCommandParser(), MarkMasteryCheckCommand.MESSAGE_USAGE,
                List.of(PREFIX_INDEX, PREFIX_NUMBER), List.of("1", "1"));
        verifyRequiredPrefixes(new UnmarkMasteryCheckCommandParser(), UnmarkMasteryCheckCommand.MESSAGE_USAGE,
                List.of(PREFIX_INDEX, PREFIX_NUMBER), List.of("1", "1"));
        verifyRequiredPrefixes(new AddStudioRecordCommandParser(), AddStudioRecordCommand.MESSAGE_USAGE,
                List.of(PREFIX_INDEX, PREFIX_WEEK, PREFIX_STUDIO_EFFORT), List.of("1", "3", "50"));
        verifyRequiredPrefixes(new EditStudioRecordCommandParser(), EditStudioRecordCommand.MESSAGE_USAGE,
                List.of(PREFIX_INDEX, PREFIX_WEEK, PREFIX_STUDIO_EFFORT), List.of("1", "3", "50"));
        verifyRequiredPrefixes(new DeleteStudioRecordCommandParser(), DeleteStudioRecordCommand.MESSAGE_USAGE,
                List.of(PREFIX_INDEX, PREFIX_WEEK), List.of("1", "3"));
        verifyRequiredPrefixes(new MarkMissionCommandParser(), MarkMissionCommand.MESSAGE_USAGE,
                List.of(PREFIX_INDEX, PREFIX_MISSION), List.of("1", "M01"));
        verifyRequiredPrefixes(new UnmarkMissionCommandParser(), UnmarkMissionCommand.MESSAGE_USAGE,
                List.of(PREFIX_INDEX, PREFIX_MISSION), List.of("1", "M01"));
        verifyRequiredPrefixes(new DeleteStudentCommandParser(), DeleteStudentCommand.MESSAGE_USAGE,
                List.of(PREFIX_INDEX), List.of("1"));
        verifyRequiredPrefixes(new ViewCommandParser(), ViewCommand.MESSAGE_USAGE, List.of(PREFIX_INDEX), List.of("1"));
        verifyRequiredPrefixes(new DeleteMissionCommandParser(), DeleteMissionCommand.MESSAGE_USAGE,
                List.of(PREFIX_MISSION), List.of("M01"));
        verifyRequiredPrefixes(new ListMissionsCommandParser(), ListMissionsCommand.MESSAGE_USAGE,
                List.of(PREFIX_MISSION), List.of("M01"));
        verifyRequiredPrefixes(new AddMissionCommandParser(), AddMissionCommand.MESSAGE_USAGE,
                List.of(CliSyntax.PREFIX_NAME, CliSyntax.PREFIX_DATE), List.of("Runes", "2026-10-20"));
        verifyRequiredPrefixes(new AddNoteCommandParser(),
                List.of(PREFIX_INDEX, PREFIX_NOTE_ID, PREFIX_NOTE_TEXT), List.of("1", "3", "Review notes"));
    }

    /**
     * Interleaves successful parsing with missing and repeated prefix checks.
     *
     * @param parser Command parser being checked.
     * @param usage Usage instructions expected once at the end of each parse error.
     * @param prefixes Required prefixes in validation order.
     * @param values Valid values corresponding to the prefixes.
     */
    private void verifyRequiredPrefixes(Parser<?> parser, String usage, List<Prefix> prefixes, List<String> values) {
        StringBuilder validInput = new StringBuilder();
        for (int i = 0; i < prefixes.size(); i++) {
            validInput.append(prefixes.get(i)).append(values.get(i)).append(' ');
        }
        assertThrows(NullPointerException.class, () -> parser.parse(null));
        ParseException preambleError = assertThrows(ParseException.class, () ->
                parser.parse("unexpected " + validInput));
        assertEquals(Messages.MESSAGE_INVALID_COMMAND + "\n" + usage,
                preambleError.getMessage());
        for (int omitted = 0; omitted < prefixes.size(); omitted++) {
            StringBuilder incompleteInput = new StringBuilder();
            for (int i = 0; i < prefixes.size(); i++) {
                if (i != omitted) {
                    incompleteInput.append(prefixes.get(i)).append(values.get(i)).append(' ');
                }
            }
            Prefix prefix = prefixes.get(omitted);
            ParseException missingError = assertThrows(ParseException.class, () ->
                    parser.parse(incompleteInput.toString()));
            assertEquals(prefix.getMissingMessage() + "\n" + usage, missingError.getMessage());
            assertDoesNotThrow(() -> parser.parse(validInput.toString()));
            ParseException duplicateError = assertThrows(ParseException.class, () ->
                    parser.parse(validInput.toString() + prefix + values.get(prefixes.indexOf(prefix))));
            assertEquals(prefix.getDuplicateMessage() + "\n" + usage, duplicateError.getMessage());
        }
    }
}

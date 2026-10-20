package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.DeleteStudioRecordCommand;

/**
 * Tests argument structure and value-parser integration for delstudiorecord.
 */
public class DeleteStudioRecordCommandParserTest {

    private final DeleteStudioRecordCommandParser parser = new DeleteStudioRecordCommandParser();

    @Test
    public void parse_validArguments_returnsCommand() {
        DeleteStudioRecordCommand expected = new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 3);

        assertParseSuccess(parser, "1 w/3", expected);
        assertParseSuccess(parser, "  1   w/ 3  ", expected);
        assertParseSuccess(parser, "1\tw/3", expected);
        assertParseSuccess(parser, "1 w/3", new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 3));
        assertParseSuccess(parser, "1 w/13", new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 13));
    }

    @Test
    public void parse_missingIndex_reportsInvalidFormat() {
        String[] inputs = {"", "   ", "w/", "w/3", "w/ 3"};
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteStudioRecordCommand.MESSAGE_USAGE);

        for (String input : inputs) {
            assertParseFailure(parser, input, expected);
        }
    }

    @Test
    public void parse_missingWeekPrefix_throwsNoSuchElementException() {
        assertThrows(java.util.NoSuchElementException.class, () -> parser.parse("1"));
    }

    @Test
    public void parse_emptyWeek_reportsInvalidWeek() {
        assertParseFailure(parser, "1 w/", "Invalid week");
    }

    @Test
    public void parse_repeatedWeek_reportsDuplicateWeek() {
        String[] inputs = {"1 w/3 w/3", "1 w/ w/3"};
        String expected = Messages.getErrorMessageForDuplicatePrefixes(PREFIX_WEEK);

        for (String input : inputs) {
            assertParseFailure(parser, input, expected);
        }
    }

    @Test
    public void parse_invalidValues_reportsValidationMessages() {
        assertParseFailure(parser, "1 w/-1", "Invalid week");
        assertThrows(IllegalArgumentException.class, () -> parser.parse("1 w/14"));
    }

    @Test
    public void parse_leadingZeros_returnsCommand() {
        assertParseSuccess(parser, "01 w/03", new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 3));
    }

    @Test
    public void parse_extraArgumentsOrUnexpectedPrefixes_reportsInvalidFormat() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteStudioRecordCommand.MESSAGE_USAGE);
        String[] inputs = {"w/3 1", "W/3 1", "w/3 extra", "1 3 4", "1 x/3", "1 extra w/3", "1w/3"};

        for (String input : inputs) {
            assertParseFailure(parser, input, expected);
        }
    }

    @Test
    public void parse_extraWeekText_reportsInvalidWeek() {
        String[] inputs = {"1 w/3 W/4", "1 w/3 extra", "1 w/3 x/4", "1 w/3w/4"};

        for (String input : inputs) {
            assertParseFailure(parser, input, "Invalid week");
        }
    }
}

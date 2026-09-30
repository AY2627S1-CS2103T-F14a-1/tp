package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddMissionCommand;

/**
 * Tests argument structure and value-parser integration for addmission.
 */
public class AddMissionCommandParserTest {

    private final AddMissionCommandParser parser = new AddMissionCommandParser();

    @Test
    public void parse_validArguments_returnsCommand() {
        AddMissionCommand expected = new AddMissionCommand(INDEX_FIRST_PERSON, 3);

        assertParseSuccess(parser, "1 w/3", expected);
        assertParseSuccess(parser, "  1   w/ 3  ", expected);
        assertParseSuccess(parser, "1\tW/3", expected);
        assertParseSuccess(parser, "1 w/1", new AddMissionCommand(INDEX_FIRST_PERSON, 1));
        assertParseSuccess(parser, "1 w/10", new AddMissionCommand(INDEX_FIRST_PERSON, 10));
    }

    @Test
    public void parse_missingIndex_reportsMissingIndex() {
        String[] inputs = {"", "   ", "w/", "w/3", "w/ 3", "w/3 1"};

        for (String input : inputs) {
            assertParseFailure(parser, input, AddMissionCommandParser.MESSAGE_MISSING_INDEX);
        }
    }

    @Test
    public void parse_missingWeek_reportsMissingWeek() {
        assertParseFailure(parser, "1", AddMissionCommandParser.MESSAGE_MISSING_WEEK);
        assertParseFailure(parser, "1 w/", AddMissionCommandParser.MESSAGE_MISSING_WEEK);
    }

    @Test
    public void parse_repeatedWeek_reportsDuplicateWeek() {
        String[] inputs = {"1 w/3 w/3", "1 w/3 W/4", "1 w/ w/3"};

        for (String input : inputs) {
            assertParseFailure(parser, input, AddMissionCommandParser.MESSAGE_DUPLICATE_WEEK);
        }
    }

    @Test
    public void parse_invalidValues_reportsUtilityMessages() {
        assertParseFailure(parser, "01 w/3", ParserUtil.MESSAGE_INVALID_STUDENT_INDEX);
        assertParseFailure(parser, "1 w/03", ParserUtil.MESSAGE_INVALID_TUTORIAL_WEEK);
        assertParseFailure(parser, "1 w/11", ParserUtil.MESSAGE_INVALID_TUTORIAL_WEEK);
    }

    @Test
    public void parse_invalidIndexAndWeek_reportsIndexFirst() {
        assertParseFailure(parser, "01 w/11", ParserUtil.MESSAGE_INVALID_STUDENT_INDEX);
        assertParseFailure(parser, "01", ParserUtil.MESSAGE_INVALID_STUDENT_INDEX);
    }

    @Test
    public void parse_extraArgumentsOrUnexpectedPrefixes_reportsInvalidFormat() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddMissionCommand.MESSAGE_USAGE);
        String[] inputs = {"1 3", "1 x/3", "1 extra w/3", "1 w/3 extra", "1 w/3 x/4", "1w/3", "1 w/3w/4"};

        for (String input : inputs) {
            assertParseFailure(parser, input, expected);
        }
    }
}

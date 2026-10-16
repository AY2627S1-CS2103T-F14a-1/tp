package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import org.junit.jupiter.api.Test;

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
        assertParseSuccess(parser, "1\tW/3", expected);
        assertParseSuccess(parser, "1 w/3", new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 3));
        assertParseSuccess(parser, "1 w/13", new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 13));
    }

    @Test
    public void parse_missingIndex_reportsMissingIndex() {
        String[] inputs = {"", "   ", "w/", "w/3", "w/ 3"};

        for (String input : inputs) {
            assertParseFailure(parser, input, WeeklyTaskParser.MESSAGE_MISSING_INDEX);
        }
    }

    @Test
    public void parse_missingWeek_reportsMissingWeek() {
        assertParseFailure(parser, "1", WeeklyTaskParser.MESSAGE_MISSING_WEEK);
        assertParseFailure(parser, "1 3", WeeklyTaskParser.MESSAGE_MISSING_WEEK);
        assertParseFailure(parser, "1 w/", WeeklyTaskParser.MESSAGE_MISSING_WEEK);
    }

    @Test
    public void parse_repeatedWeek_reportsDuplicateWeek() {
        String[] inputs = {"1 w/3 w/3", "1 w/3 W/4", "1 w/ w/3"};

        for (String input : inputs) {
            assertParseFailure(parser, input, WeeklyTaskParser.MESSAGE_DUPLICATE_WEEK);
        }
    }

    @Test
    public void parse_invalidValues_reportsValidationMessages() {
        assertParseFailure(parser, "01 w/3", WeeklyTaskParser.MESSAGE_INVALID_STUDENT_INDEX);
        assertParseFailure(parser, "1 w/03", WeeklyTaskParser.MESSAGE_INVALID_TUTORIAL_WEEK);
        assertParseFailure(parser, "1 w/2", WeeklyTaskParser.MESSAGE_INVALID_TUTORIAL_WEEK);
        assertParseFailure(parser, "1 w/1", WeeklyTaskParser.MESSAGE_INVALID_TUTORIAL_WEEK);
        assertParseFailure(parser, "1 w/14", WeeklyTaskParser.MESSAGE_INVALID_TUTORIAL_WEEK);
    }

    @Test
    public void parse_invalidIndexAndWeek_reportsIndexFirst() {
        assertParseFailure(parser, "01 w/14", WeeklyTaskParser.MESSAGE_INVALID_STUDENT_INDEX);
        assertParseFailure(parser, "01", WeeklyTaskParser.MESSAGE_INVALID_STUDENT_INDEX);
        assertParseFailure(parser, "01 3", WeeklyTaskParser.MESSAGE_INVALID_STUDENT_INDEX);
    }

    @Test
    public void parse_extraArgumentsOrUnexpectedPrefixes_reportsInvalidFormat() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteStudioRecordCommand.MESSAGE_USAGE);
        String[] inputs = {"w/3 1", "W/3 1", "w/3 extra", "1 3 4", "1 x/3",
            "1 extra w/3", "1 w/3 extra", "1 w/3 x/4", "1w/3", "1 w/3w/4"};

        for (String input : inputs) {
            assertParseFailure(parser, input, expected);
        }
    }
}

package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.StudioRecordCommandParser.MESSAGE_INVALID_WEEK;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteStudioRecordCommand;

/**
 * Tests argument structure and value-parser integration for delstudiorecord.
 */
public class DeleteStudioRecordCommandParserTest {

    private static final String USAGE_SUFFIX = "\n\n" + DeleteStudioRecordCommand.MESSAGE_USAGE;

    private final DeleteStudioRecordCommandParser parser = new DeleteStudioRecordCommandParser();

    @Test
    public void parse_validArguments_returnsCommand() {
        DeleteStudioRecordCommand expected = new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 3);

        assertParseSuccess(parser, "id/1 w/3", expected);
        assertParseSuccess(parser, "  id/1   w/ 3  ", expected);
        assertParseSuccess(parser, "id/1\tw/3", expected);
        assertParseSuccess(parser, "id/1 w/3", new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 3));
        assertParseSuccess(parser, "id/1 w/13", new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 13));
    }

    @Test
    public void parse_missingIndex_reportsMissingPrefixes() {
        String bothMissing = String.join("\n", CliSyntax.PREFIX_INDEX.getMissingMessage(),
                PREFIX_WEEK.getMissingMessage());
        assertParseFailure(parser, "",
                bothMissing + USAGE_SUFFIX);
        assertParseFailure(parser, "   ",
                bothMissing + USAGE_SUFFIX);
        for (String input : new String[]{"w/", "w/3", "w/ 3"}) {
            assertParseFailure(parser, input,
                CliSyntax.PREFIX_INDEX.getMissingMessage() + USAGE_SUFFIX);
        }
    }

    @Test
    public void parse_missingWeekPrefix_reportsMissingPrefix() {
        assertParseFailure(parser, "id/1",
                PREFIX_WEEK.getMissingMessage() + USAGE_SUFFIX);
    }

    @Test
    public void parse_emptyWeek_reportsInvalidWeek() {
        assertParseFailure(parser, "id/1 w/",
                MESSAGE_INVALID_WEEK + USAGE_SUFFIX);
    }

    @Test
    public void parse_repeatedWeek_reportsDuplicateWeek() {
        String[] inputs = {"id/1 w/3 w/3", "id/1 w/ w/3"};
        String expected = CliSyntax.PREFIX_WEEK.getDuplicateMessage() + USAGE_SUFFIX;

        for (String input : inputs) {
            assertParseFailure(parser, input, expected);
        }
    }

    @Test
    public void parse_invalidValues_reportsValidationMessages() {
        assertParseFailure(parser, "id/1 w/-1",
                MESSAGE_INVALID_WEEK + USAGE_SUFFIX);
        assertParseFailure(parser, "id/1 w/14",
                MESSAGE_INVALID_WEEK + USAGE_SUFFIX);
    }

    @Test
    public void parse_leadingZeros_returnsCommand() {
        assertParseSuccess(parser, "id/01 w/03", new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 3));
        assertParseSuccess(parser, "ID/01 W/03", new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 3));
    }

    @Test
    public void parse_extraArgumentsOrUnexpectedPrefixes_reportsInvalidFormat() {
        String expected = MESSAGE_INVALID_COMMAND + USAGE_SUFFIX;
        String[] inputs = {"1 3 4", "1 x/3", "1 extra w/3"};

        for (String input : inputs) {
            assertParseFailure(parser, input, expected);
        }
        for (String input : new String[]{"w/3 1", "W/3 1"}) {
            assertParseFailure(parser, input,
                    CliSyntax.PREFIX_INDEX.getMissingMessage() + USAGE_SUFFIX);
        }
        assertParseFailure(parser, "w/3 extra",
                CliSyntax.PREFIX_INDEX.getMissingMessage() + USAGE_SUFFIX);
        assertParseFailure(parser, "id/1 extra w/3",
                ParserUtil.MESSAGE_INVALID_INDEX + USAGE_SUFFIX);
        assertParseFailure(parser, "id/1w/3",
                PREFIX_WEEK.getMissingMessage() + USAGE_SUFFIX);
    }

    @Test
    public void parse_extraWeekText_reportsInvalidWeek() {
        assertParseFailure(parser, "id/1 w/3 W/4",
                PREFIX_WEEK.getDuplicateMessage() + USAGE_SUFFIX);
        String[] inputs = {"id/1 w/3 extra", "id/1 w/3 x/4", "id/1 w/3w/4"};

        for (String input : inputs) {
            assertParseFailure(parser, input,
                MESSAGE_INVALID_WEEK + USAGE_SUFFIX);
        }
    }
}

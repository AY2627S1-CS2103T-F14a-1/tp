package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDIO_EFFORT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.util.ParserUtil.MESSAGE_SEE_USAGE;
import static seedu.address.logic.parser.util.StudioRecordParserUtil.MESSAGE_INVALID_WEEK;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteStudioRecordCommand;

/**
 * Tests argument structure and value-parser integration for delstudiorecord.
 */
public class DeleteStudioRecordCommandParserTest {

    private static final String USAGE_SUFFIX = MESSAGE_SEE_USAGE + DeleteStudioRecordCommand.MESSAGE_USAGE;

    private final DeleteStudioRecordCommandParser parser = new DeleteStudioRecordCommandParser();

    @Test
    public void parse_validArguments_returnsCommand() {
        assertParseSuccess(parser, "id/1 w/0",
                new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 0));
        assertParseSuccess(parser, "id/1 w/1",
                new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 1));
        assertParseSuccess(parser, "id/1 w/6",
                new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 6));
        assertParseSuccess(parser, "id/1 w/12",
                new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 12));
        assertParseSuccess(parser, "id/1 w/13",
                new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 13));

        DeleteStudioRecordCommand expected = new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 3);
        assertParseSuccess(parser, "  id/1   w/ 3  ", expected);
        assertParseSuccess(parser, "id/1\tw/3", expected);
    }

    @Test
    public void parse_extraArgumentsOrUnexpectedPrefixes_reportsInvalidFormat() {
        String expected = MESSAGE_INVALID_COMMAND + USAGE_SUFFIX;
        String[] inputs = {"1 3 4", "1 x/3", "1 extra w/3"};

        for (String input : inputs) {
            assertParseFailure(parser, input, expected);
        }
    }

    @Test
    public void parse_emptyArgument_throwsNullPointerException() {
        String allMissing = String.join("\n", CliSyntax.PREFIX_INDEX.getMissingMessage(),
                PREFIX_WEEK.getMissingMessage());
        assertParseFailure(parser, "",
                allMissing + USAGE_SUFFIX);
        assertParseFailure(parser, "   ",
                allMissing + USAGE_SUFFIX);
    }

    @Test
    public void parse_nullArgument_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_reorderedPrefixes_returnsCommand() {
        assertParseSuccess(parser, "w/0 id/1",
                new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 0));
    }

    @Test
    public void parse_leadingZeros_returnsCommand() {
        assertParseSuccess(parser, "id/01 w/03", new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 3));
        assertParseSuccess(parser, "ID/01 W/03", new DeleteStudioRecordCommand(INDEX_FIRST_STUDENT, 3));
    }

    @Test
    public void parse_missingIndex_reportsMissingPrefixes() {
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
    public void parse_emptyWeek_reportsMissingArgument() {
        assertParseFailure(parser, "id/1 w/",
                "Prefix w/ is missing its argument." + USAGE_SUFFIX);
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
    public void parse_invalidWeek_reportsInvalidEffort() {
        String[] weeks = {"$", "1 2", "-1", "1.5", "abc", "14", "3 x/4", "3w/4"};

        for (String week : weeks) {
            assertParseFailure(parser, "id/1 w/" + week + " ef/40",
                    MESSAGE_INVALID_WEEK + USAGE_SUFFIX);
        }
    }
}

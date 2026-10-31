package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDIO_EFFORT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.util.ParserUtil.MESSAGE_SEE_USAGE;
import static seedu.address.logic.parser.util.StudioRecordParserUtil.MESSAGE_INVALID_EFFORT;
import static seedu.address.logic.parser.util.StudioRecordParserUtil.MESSAGE_INVALID_WEEK;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditStudioRecordCommand;
import seedu.address.model.student.StudioRecord;

/**
 * Tests parsing studio weeks and effort scores when editing studio records.
 */
public class EditStudioRecordCommandParserTest {

    private static final String USAGE_SUFFIX = MESSAGE_SEE_USAGE + EditStudioRecordCommand.MESSAGE_USAGE;

    private final EditStudioRecordCommandParser parser = new EditStudioRecordCommandParser();

    @Test
    public void parse_validArguments_returnsStudioRecordCommand() {
        assertParseSuccess(parser, "id/1 w/0 ef/0",
                new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 0, new StudioRecord(0)));
        assertParseSuccess(parser, "id/1 w/1 ef/0",
                new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 1, new StudioRecord(0)));
        assertParseSuccess(parser, "id/1 w/6 ef/0",
                new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 6, new StudioRecord(0)));
        assertParseSuccess(parser, "id/1 w/12 ef/0",
                new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 12, new StudioRecord(0)));
        assertParseSuccess(parser, "id/1 w/13 ef/0",
                new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 13, new StudioRecord(0)));

        assertParseSuccess(parser, "id/1 w/3 ef/1",
                new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 3, new StudioRecord(1)));
        assertParseSuccess(parser, "id/1 w/3 ef/50",
                new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 3, new StudioRecord(50)));
        assertParseSuccess(parser, "id/1 w/3 ef/2147483646",
                new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 3, new StudioRecord(Integer.MAX_VALUE - 1)));
        assertParseSuccess(parser, "id/1 w/3 ef/2147483647",
                new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 3, new StudioRecord(Integer.MAX_VALUE)));

        EditStudioRecordCommand expected = new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 3,
                new StudioRecord(50));
        assertParseSuccess(parser, "  id/1   w/ 3  ef/50", expected);
        assertParseSuccess(parser, "id/1\tw/3 ef/50", expected);
    }

    @Test
    public void parse_invalidArguments_reportsInvalidFormat() {
        String expected = MESSAGE_INVALID_COMMAND + USAGE_SUFFIX;
        String[] inputs = {"1 3 4", "$", "1 x/3", "1 extra w/3 ef/50"};

        for (String input : inputs) {
            assertParseFailure(parser, input, expected);
        }
    }

    @Test
    public void parse_emptyArgument_throwsNullPointerException() {
        String allMissing = String.join("\n", CliSyntax.PREFIX_INDEX.getMissingMessage(),
                PREFIX_WEEK.getMissingMessage(), PREFIX_STUDIO_EFFORT.getMissingMessage());
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
        assertParseSuccess(parser, "ef/50 w/0 id/1",
                new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 0, new StudioRecord(50)));
        assertParseSuccess(parser, "w/13 id/1 ef/0",
                new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 13, new StudioRecord(0)));
    }

    @Test
    public void parse_leadingZeros_returnsCommand() {
        assertParseSuccess(parser, "id/01 w/03 ef/050", new EditStudioRecordCommand(
                INDEX_FIRST_STUDENT, 3, new StudioRecord(50)));
        assertParseSuccess(parser, "ID/01 W/03 EF/050", new EditStudioRecordCommand(
                INDEX_FIRST_STUDENT, 3, new StudioRecord(50)));
    }

    @Test
    public void parse_missingIndex_reportsMissingPrefixes() {
        for (String input : new String[]{"w/ ef/", "w/3 ef/50", "w/ 3 ef/ 3"}) {
            assertParseFailure(parser, input,
                    CliSyntax.PREFIX_INDEX.getMissingMessage()
                            + USAGE_SUFFIX);
        }
    }

    @Test
    public void parse_missingWeekPrefix_reportsMissingPrefix() {
        assertParseFailure(parser, "id/1 ef/50",
                PREFIX_WEEK.getMissingMessage() + USAGE_SUFFIX);
    }

    @Test
    public void parse_emptyWeek_reportsMissingArgument() {
        assertParseFailure(parser, "id/1 w/ ef/50",
                "Prefix w/ is missing its argument." + USAGE_SUFFIX);
    }

    @Test
    public void parse_repeatedWeek_reportsDuplicateWeek() {
        String[] inputs = {"id/1 ef/50 w/3 w/3", "id/1 ef/50 w/ w/3"};
        String expected = CliSyntax.PREFIX_WEEK.getDuplicateMessage() + USAGE_SUFFIX;

        for (String input : inputs) {
            assertParseFailure(parser, input, expected);
        }
    }

    @Test
    public void parse_invalidWeek_reportsInvalidWeek() {
        String[] weeks = {"$", "1 2", "-1", "1.5", "abc", "14", "3 x/4", "3w/4"};

        for (String week : weeks) {
            assertParseFailure(parser, "id/1 w/" + week + " ef/40",
                    MESSAGE_INVALID_WEEK + USAGE_SUFFIX);
        }
    }

    @Test
    public void parse_missingEffortPrefix_reportsMissingPrefix() {
        assertParseFailure(parser, "id/1 w/3",
                PREFIX_STUDIO_EFFORT.getMissingMessage() + USAGE_SUFFIX);
    }

    @Test
    public void parse_emptyEffort_reportsMissingArgument() {
        assertParseFailure(parser, "id/1 w/3 ef/",
                "Prefix ef/ is missing its argument." + USAGE_SUFFIX);
    }

    @Test
    public void parse_repeatedEffort_reportsDuplicateWeek() {
        String[] inputs = {"id/1 w/3 ef/50 ef/50", "id/1 w/3 ef/ ef/50"};
        String expected = CliSyntax.PREFIX_STUDIO_EFFORT.getDuplicateMessage() + USAGE_SUFFIX;

        for (String input : inputs) {
            assertParseFailure(parser, input, expected);
        }
    }

    @Test
    public void parse_invalidEffort_reportsInvalidEffort() {
        String[] effortScores = {"$", "1 2", "-1", "1.5", "abc", "50 x/40", "50ef/40"};

        for (String effortScore : effortScores) {
            assertParseFailure(parser, "id/1 w/3 ef/" + effortScore,
                    MESSAGE_INVALID_EFFORT + USAGE_SUFFIX);
        }
    }
}

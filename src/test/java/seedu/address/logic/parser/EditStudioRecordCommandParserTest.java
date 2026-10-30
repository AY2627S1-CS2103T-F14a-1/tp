package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    public void parse_missingIndex_reportsMissingPrefixes() {
        String allMissing = String.join("\n", CliSyntax.PREFIX_INDEX.getMissingMessage(),
                PREFIX_WEEK.getMissingMessage(), PREFIX_STUDIO_EFFORT.getMissingMessage());
        assertParseFailure(parser, "",
                allMissing + USAGE_SUFFIX);
        assertParseFailure(parser, "   ",
                allMissing + USAGE_SUFFIX);
        for (String input : new String[]{"w/", "w/3", "w/ 3"}) {
            assertParseFailure(parser, input,
                    CliSyntax.PREFIX_INDEX.getMissingMessage() + "\n"
                            + PREFIX_STUDIO_EFFORT.getMissingMessage()
                            + USAGE_SUFFIX);
        }
    }

    @Test
    public void parse_missingEffort_reportsMissingPrefix() {
        String missingEffort = CliSyntax.PREFIX_STUDIO_EFFORT.getMissingMessage();

        assertParseFailure(parser, "id/1 w/3",
                missingEffort + USAGE_SUFFIX);
    }

    @Test
    public void parse_nullArgument_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_invalidWeek_reportsInvalidEffort() {
        String[] weeks = {"$", "1 2", "-1", "1.5", "abc", "14"};

        for (String week : weeks) {
            assertParseFailure(parser, "id/1 w/" + week + " ef/40",
                    MESSAGE_INVALID_WEEK + USAGE_SUFFIX);
        }
    }

    @Test
    public void parse_invalidEffort_reportsInvalidEffort() {
        String[] effortScores = {"$", "1 2", "-1", "1.5", "abc", "2147483648"};

        for (String effortScore : effortScores) {
            assertParseFailure(parser, "id/1 w/3 ef/" + effortScore,
                MESSAGE_INVALID_EFFORT + USAGE_SUFFIX);
        }
    }

    @Test
    public void parse_reorderedPrefixesAndBoundaryWeek_returnsCommand() {
        assertParseSuccess(parser, "ef/50 w/0 id/1",
                new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 0, new StudioRecord(50)));
        assertParseSuccess(parser, "w/13 id/1 ef/0",
                new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 13, new StudioRecord(0)));
    }

    @Test
    public void parse_emptyRequiredPrefix_reportsMissingArgument() {
        assertParseFailure(parser, "id/ w/3 ef/50",
                "Prefix id/ is missing its argument." + USAGE_SUFFIX);
        assertParseFailure(parser, "id/1 w/ ef/50",
                "Prefix w/ is missing its argument." + USAGE_SUFFIX);
        assertParseFailure(parser, "id/1 w/3 ef/",
                "Prefix ef/ is missing its argument." + USAGE_SUFFIX);
    }
}

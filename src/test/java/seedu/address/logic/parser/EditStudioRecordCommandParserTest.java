package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_EFFORT;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditStudioRecordCommand;
import seedu.address.model.student.StudioRecord;

/**
 * Tests parsing studio weeks and effort scores when editing studio records.
 */
public class EditStudioRecordCommandParserTest {

    private static final String USAGE_SUFFIX = "\n\n" + EditStudioRecordCommand.MESSAGE_USAGE;

    private final EditStudioRecordCommandParser parser = new EditStudioRecordCommandParser();

    @Test
    public void parse_validArguments_returnsStudioRecordCommand() {
        assertParseSuccess(parser, "id/1 w/3 ef/0",
                new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 3, new StudioRecord(0)));
        assertParseSuccess(parser, "id/1 ef/2147483647 w/3",
                new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 3, new StudioRecord(Integer.MAX_VALUE)));
    }

    @Test
    public void parse_invalidEffort_reportsInvalidEffort() {
        String[] effortScores = {"", "-1", "1.5", "abc", "2147483648"};

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
    public void parse_emptyIndexOrWeek_reportsValueError() {
        assertParseFailure(parser, "id/ w/3 ef/50",
                ParserUtil.MESSAGE_INVALID_INDEX + USAGE_SUFFIX);
        assertParseFailure(parser, "id/1 w/ ef/50",
                ParserUtil.MESSAGE_INVALID_WEEK + USAGE_SUFFIX);
    }
}

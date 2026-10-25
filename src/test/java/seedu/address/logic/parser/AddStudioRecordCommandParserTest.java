package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_EFFORT;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_WEEK;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddStudioRecordCommand;
import seedu.address.model.student.StudioRecord;

/**
 * Tests parsing studio weeks and effort scores.
 */
public class AddStudioRecordCommandParserTest {

    private static final String USAGE_SUFFIX = "\n" + AddStudioRecordCommand.MESSAGE_USAGE;

    private final AddStudioRecordCommandParser parser = new AddStudioRecordCommandParser();

    @Test
    public void parse_validArguments_returnsStudioRecordCommand() {
        assertParseSuccess(parser, "id/1 w/3 ef/0",
                new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 3, new StudioRecord(0)));
        assertParseSuccess(parser, " id/1\tw/13 ef/50 ",
                new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 13, new StudioRecord(50)));
        assertParseSuccess(parser, "id/1 ef/2147483647 w/3",
                new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 3, new StudioRecord(Integer.MAX_VALUE)));
    }

    @Test
    public void parse_missingEffort_reportsMissingPrefix() {
        String missingEffort = CliSyntax.PREFIX_STUDIO_EFFORT.getMissingMessage();

        assertParseFailure(parser, "id/1 w/3",
                missingEffort + USAGE_SUFFIX);
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
    public void parse_invalidIndexOrWeek_reportsValidationMessage() {
        assertParseFailure(parser, "id/0 w/3 ef/50",
                ParserUtil.MESSAGE_INVALID_INDEX + USAGE_SUFFIX);
        assertParseFailure(parser, "id/1 w/-1 ef/50",
                MESSAGE_INVALID_WEEK + USAGE_SUFFIX);
        assertParseFailure(parser, "id/1 w/14 ef/50",
                MESSAGE_INVALID_WEEK + USAGE_SUFFIX);
    }
}



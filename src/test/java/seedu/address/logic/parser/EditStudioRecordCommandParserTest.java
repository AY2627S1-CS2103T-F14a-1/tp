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

    private final EditStudioRecordCommandParser parser = new EditStudioRecordCommandParser();

    @Test
    public void parse_validArguments_returnsStudioRecordCommand() {
        assertParseSuccess(parser, "1 w/3 ef/0",
                new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 3, new StudioRecord(0)));
        assertParseSuccess(parser, "1 ef/2147483647 w/3",
                new EditStudioRecordCommand(INDEX_FIRST_STUDENT, 3, new StudioRecord(Integer.MAX_VALUE)));
    }

    @Test
    public void parse_invalidEffort_reportsInvalidEffort() {
        String[] effortScores = {"", "-1", "1.5", "abc", "2147483648"};

        for (String effortScore : effortScores) {
            assertParseFailure(parser, "1 w/3 ef/" + effortScore,
                    MESSAGE_INVALID_EFFORT);
        }
    }
}

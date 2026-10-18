package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddStudioRecordCommand;
import seedu.address.model.student.StudioRecord;

/**
 * Tests parsing studio weeks and effort scores.
 */
public class AddStudioRecordCommandParserTest {

    private final AddStudioRecordCommandParser parser = new AddStudioRecordCommandParser();

    @Test
    public void parse_validArguments_returnsStudioRecordCommand() {
        assertParseSuccess(parser, "1 w/3 ef/0",
                new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 3, new StudioRecord(0)));
        assertParseSuccess(parser, " 1\tW/13 EF/50 ",
                new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 13, new StudioRecord(50)));
        assertParseSuccess(parser, "1 ef/2147483647 w/3",
                new AddStudioRecordCommand(INDEX_FIRST_STUDENT, 3, new StudioRecord(Integer.MAX_VALUE)));
    }

    @Test
    public void parse_missingEffort_reportsMissingEffort() {
        assertParseFailure(parser, "1 w/3", AddStudioRecordCommandParser.MESSAGE_MISSING_EFFORT);
    }

    @Test
    public void parse_invalidEffort_reportsInvalidEffort() {
        String[] effortScores = {"", "-1", "1.5", "abc", "2147483648"};
        for (String effortScore : effortScores) {
            assertParseFailure(parser, "1 w/3 ef/" + effortScore,
                    AddStudioRecordCommandParser.MESSAGE_INVALID_EFFORT);
        }
    }

    @Test
    public void parse_invalidIndexOrWeek_reportsValidationMessage() {
        assertParseFailure(parser, "0 w/3 ef/50", WeeklyTaskParser.MESSAGE_INVALID_STUDENT_INDEX);
        assertParseFailure(parser, "1 w/2 ef/50", WeeklyTaskParser.MESSAGE_INVALID_STUDIO_WEEK);
        assertParseFailure(parser, "1 w/14 ef/50", WeeklyTaskParser.MESSAGE_INVALID_STUDIO_WEEK);
    }
}



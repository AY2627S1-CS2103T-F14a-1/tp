package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.StudioRecordCommandParserUtil.MESSAGE_INVALID_EFFORT;
import static seedu.address.logic.parser.StudioRecordCommandParserUtil.MESSAGE_INVALID_WEEK;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

public class StudioRecordCommandParserUtilTest {

    @Test
    public void parseWeek_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> StudioRecordCommandParserUtil.parseWeek(null));
    }

    @Test
    public void parseWeek_invalidInput_throwsParseException() {
        String[] invalidWeeks = {"", " ", "-1", "+1", "1.0", "one", "1 2", "14", "2147483648"};

        for (String invalidWeek : invalidWeeks) {
            assertThrows(ParseException.class, MESSAGE_INVALID_WEEK, () ->
                    StudioRecordCommandParserUtil.parseWeek(invalidWeek));
        }
    }

    @Test
    public void parseWeek_validInput_returnsWeek() throws Exception {
        assertEquals(0, StudioRecordCommandParserUtil.parseWeek("0"));
        assertEquals(3, StudioRecordCommandParserUtil.parseWeek("3"));
        assertEquals(13, StudioRecordCommandParserUtil.parseWeek("13"));
        assertEquals(3, StudioRecordCommandParserUtil.parseWeek(" \t3 "));
    }

    @Test
    public void parseEffortScore_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> StudioRecordCommandParserUtil.parseEffortScore(null));
    }

    @Test
    public void parseEffortScore_invalidInput_throwsParseException() {
        String[] invalidEffortScores = {"", " ", "-1", "+1", "1.0", "one", "1 2", "2147483648"};

        for (String invalidEffortScore : invalidEffortScores) {
            assertThrows(ParseException.class, MESSAGE_INVALID_EFFORT, () ->
                    StudioRecordCommandParserUtil.parseEffortScore(invalidEffortScore));
        }
    }

    @Test
    public void parseEffortScore_validInput_returnsEffortScore() throws Exception {
        assertEquals(0, StudioRecordCommandParserUtil.parseEffortScore("0"));
        assertEquals(50, StudioRecordCommandParserUtil.parseEffortScore("50"));
        assertEquals(Integer.MAX_VALUE, StudioRecordCommandParserUtil.parseEffortScore(
                String.valueOf(Integer.MAX_VALUE)));
        assertEquals(50, StudioRecordCommandParserUtil.parseEffortScore(" \t50 "));
    }
}

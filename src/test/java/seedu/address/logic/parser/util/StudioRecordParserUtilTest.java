package seedu.address.logic.parser.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.util.StudioRecordParserUtil.MESSAGE_INVALID_EFFORT;
import static seedu.address.logic.parser.util.StudioRecordParserUtil.MESSAGE_INVALID_WEEK;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

public class StudioRecordParserUtilTest {

    @Test
    public void parseWeek_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> StudioRecordParserUtil.parseWeek(null));
    }

    @Test
    public void parseWeek_invalidInput_throwsParseException() {
        String[] invalidWeeks = {"", " ", "-1", "+1", "1.0", "one", "1 2", "14", "2147483648"};

        for (String invalidWeek : invalidWeeks) {
            assertThrows(ParseException.class, MESSAGE_INVALID_WEEK, () ->
                    StudioRecordParserUtil.parseWeek(invalidWeek));
        }
    }

    @Test
    public void parseWeek_validInput_returnsWeek() throws Exception {
        assertEquals(0, StudioRecordParserUtil.parseWeek("0"));
        assertEquals(1, StudioRecordParserUtil.parseWeek("1"));
        assertEquals(3, StudioRecordParserUtil.parseWeek("3"));
        assertEquals(12, StudioRecordParserUtil.parseWeek("12"));
        assertEquals(13, StudioRecordParserUtil.parseWeek("13"));
        assertEquals(3, StudioRecordParserUtil.parseWeek(" \t3 "));
    }

    @Test
    public void parseEffortScore_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> StudioRecordParserUtil.parseEffortScore(null));
    }

    @Test
    public void parseEffortScore_invalidInput_throwsParseException() {
        String[] invalidEffortScores = {"", " ", "-1", "+1", "1.0", "one", "1 2", "2147483648"};

        for (String invalidEffortScore : invalidEffortScores) {
            assertThrows(ParseException.class, MESSAGE_INVALID_EFFORT, () ->
                    StudioRecordParserUtil.parseEffortScore(invalidEffortScore));
        }
    }

    @Test
    public void parseEffortScore_validInput_returnsEffortScore() throws Exception {
        assertEquals(0, StudioRecordParserUtil.parseEffortScore("0"));
        assertEquals(1, StudioRecordParserUtil.parseEffortScore("1"));
        assertEquals(50, StudioRecordParserUtil.parseEffortScore("50"));
        assertEquals(Integer.MAX_VALUE, StudioRecordParserUtil.parseEffortScore(
                String.valueOf(Integer.MAX_VALUE)));
        assertEquals(Integer.MAX_VALUE - 1, StudioRecordParserUtil.parseEffortScore(
                String.valueOf(Integer.MAX_VALUE - 1)));
        assertEquals(50, StudioRecordParserUtil.parseEffortScore(" \t50 "));
    }
}

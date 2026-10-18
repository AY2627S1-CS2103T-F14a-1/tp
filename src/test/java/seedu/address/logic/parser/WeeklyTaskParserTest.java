package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDIO_EFFORT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Tests shared mission index and week validation.
 */
public class WeeklyTaskParserTest {

    @Test
    public void parseStudentIndex_validInput_returnsIndex() throws Exception {
        assertEquals(INDEX_FIRST_STUDENT, WeeklyTaskParser.parseStudentIndex("1"));
        assertEquals(INDEX_FIRST_STUDENT, WeeklyTaskParser.parseStudentIndex(" \t1 "));
        assertEquals(12, WeeklyTaskParser.parseStudentIndex("12").getOneBased());
        assertEquals(Integer.MAX_VALUE,
                WeeklyTaskParser.parseStudentIndex(String.valueOf(Integer.MAX_VALUE)).getOneBased());
    }

    @Test
    public void parseStudentIndex_invalidInput_throwsParseException() {
        String[] invalidInputs = {"", " ", "0", "-1", "+1", "01", "1.0", "one", "1 2", "2147483648"};

        for (String input : invalidInputs) {
            assertThrows(ParseException.class, WeeklyTaskParser.MESSAGE_INVALID_STUDENT_INDEX, () ->
                    WeeklyTaskParser.parseStudentIndex(input));
        }
    }

    @Test
    public void parseStudentIndex_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> WeeklyTaskParser.parseStudentIndex(null));
    }

    @Test
    public void parseStudioWeek_validInput_returnsWeek() throws Exception {
        assertEquals(3, WeeklyTaskParser.parseStudioWeek("3"));
        assertEquals(11, WeeklyTaskParser.parseStudioWeek("11"));
        assertEquals(12, WeeklyTaskParser.parseStudioWeek("12"));
        assertEquals(13, WeeklyTaskParser.parseStudioWeek("13"));
        assertEquals(3, WeeklyTaskParser.parseStudioWeek(" \t3 "));
    }

    @Test
    public void parseTutorialWeek_invalidInput_throwsParseException() {
        String[] invalidInputs = {"", " ", "0", "1", "2", "14", "-1", "+3", "03", "3.0", "three", "3 4", "2147483648"};

        for (String input : invalidInputs) {
            assertThrows(ParseException.class, WeeklyTaskParser.MESSAGE_INVALID_STUDIO_WEEK, () ->
                    WeeklyTaskParser.parseStudioWeek(input));
        }
    }

    @Test
    public void parseStudioWeek_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> WeeklyTaskParser.parseStudioWeek(null));
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new WeeklyTaskParser(null, "usage"));
        assertThrows(NullPointerException.class, () -> new WeeklyTaskParser("1 w/3", null));
    }

    @Test
    public void getPrefixValue_additionalPrefix_returnsValue() throws Exception {
        WeeklyTaskParser parser = new WeeklyTaskParser("1 w/3 ef/8", "usage", PREFIX_STUDIO_EFFORT);

        assertEquals("8", parser.getPrefixValue(PREFIX_STUDIO_EFFORT).orElseThrow());
    }
}

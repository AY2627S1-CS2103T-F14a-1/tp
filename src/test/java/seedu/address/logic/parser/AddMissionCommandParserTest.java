package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddMissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Tests argument structure and value-parser integration for addmission.
 */
public class AddMissionCommandParserTest {

    private final AddMissionCommandParser parser = new AddMissionCommandParser();

    @Test
    public void parseStudentIndex_validInput_returnsIndex() throws Exception {
        assertEquals(INDEX_FIRST_STUDENT, AddMissionCommandParser.parseStudentIndex("1"));
        assertEquals(INDEX_FIRST_STUDENT, AddMissionCommandParser.parseStudentIndex(" \t1 "));
        assertEquals(12, AddMissionCommandParser.parseStudentIndex("12").getOneBased());
        assertEquals(Integer.MAX_VALUE,
                AddMissionCommandParser.parseStudentIndex(String.valueOf(Integer.MAX_VALUE)).getOneBased());
    }

    @Test
    public void parseStudentIndex_invalidInput_throwsParseException() {
        String[] invalidInputs = {"", " ", "0", "-1", "+1", "01", "1.0", "one", "1 2", "2147483648"};

        for (String input : invalidInputs) {
            assertThrows(ParseException.class, AddMissionCommandParser.MESSAGE_INVALID_STUDENT_INDEX, () ->
                    AddMissionCommandParser.parseStudentIndex(input));
        }
    }

    @Test
    public void parseStudentIndex_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> AddMissionCommandParser.parseStudentIndex(null));
    }

    @Test
    public void parseTutorialWeek_validInput_returnsWeek() throws Exception {
        assertEquals(1, AddMissionCommandParser.parseTutorialWeek("1"));
        assertEquals(10, AddMissionCommandParser.parseTutorialWeek("10"));
        assertEquals(3, AddMissionCommandParser.parseTutorialWeek(" \t3 "));
    }

    @Test
    public void parseTutorialWeek_invalidInput_throwsParseException() {
        String[] invalidInputs = {"", " ", "0", "11", "-1", "+3", "03", "3.0", "three", "3 4", "2147483648"};

        for (String input : invalidInputs) {
            assertThrows(ParseException.class, AddMissionCommandParser.MESSAGE_INVALID_TUTORIAL_WEEK, () ->
                    AddMissionCommandParser.parseTutorialWeek(input));
        }
    }

    @Test
    public void parseTutorialWeek_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> AddMissionCommandParser.parseTutorialWeek(null));
    }

    @Test
    public void parse_validArguments_returnsCommand() {
        AddMissionCommand expected = new AddMissionCommand(INDEX_FIRST_STUDENT, 3);

        assertParseSuccess(parser, "1 w/3", expected);
        assertParseSuccess(parser, "  1   w/ 3  ", expected);
        assertParseSuccess(parser, "1\tW/3", expected);
        assertParseSuccess(parser, "1 w/1", new AddMissionCommand(INDEX_FIRST_STUDENT, 1));
        assertParseSuccess(parser, "1 w/10", new AddMissionCommand(INDEX_FIRST_STUDENT, 10));
    }

    @Test
    public void parse_missingIndex_reportsMissingIndex() {
        String[] inputs = {"", "   ", "w/", "w/3", "w/ 3"};

        for (String input : inputs) {
            assertParseFailure(parser, input, AddMissionCommandParser.MESSAGE_MISSING_INDEX);
        }
    }

    @Test
    public void parse_missingWeek_reportsMissingWeek() {
        assertParseFailure(parser, "1", AddMissionCommandParser.MESSAGE_MISSING_WEEK);
        assertParseFailure(parser, "1 3", AddMissionCommandParser.MESSAGE_MISSING_WEEK);
        assertParseFailure(parser, "1 w/", AddMissionCommandParser.MESSAGE_MISSING_WEEK);
    }

    @Test
    public void parse_repeatedWeek_reportsDuplicateWeek() {
        String[] inputs = {"1 w/3 w/3", "1 w/3 W/4", "1 w/ w/3"};

        for (String input : inputs) {
            assertParseFailure(parser, input, AddMissionCommandParser.MESSAGE_DUPLICATE_WEEK);
        }
    }

    @Test
    public void parse_invalidValues_reportsValidationMessages() {
        assertParseFailure(parser, "01 w/3", AddMissionCommandParser.MESSAGE_INVALID_STUDENT_INDEX);
        assertParseFailure(parser, "1 w/03", AddMissionCommandParser.MESSAGE_INVALID_TUTORIAL_WEEK);
        assertParseFailure(parser, "1 w/11", AddMissionCommandParser.MESSAGE_INVALID_TUTORIAL_WEEK);
    }

    @Test
    public void parse_invalidIndexAndWeek_reportsIndexFirst() {
        assertParseFailure(parser, "01 w/11", AddMissionCommandParser.MESSAGE_INVALID_STUDENT_INDEX);
        assertParseFailure(parser, "01", AddMissionCommandParser.MESSAGE_INVALID_STUDENT_INDEX);
        assertParseFailure(parser, "01 3", AddMissionCommandParser.MESSAGE_INVALID_STUDENT_INDEX);
    }

    @Test
    public void parse_extraArgumentsOrUnexpectedPrefixes_reportsInvalidFormat() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddMissionCommand.MESSAGE_USAGE);
        String[] inputs = {"w/3 1", "W/3 1", "w/3 extra", "1 3 4", "1 x/3",
            "1 extra w/3", "1 w/3 extra", "1 w/3 x/4", "1w/3", "1 w/3w/4"};

        for (String input : inputs) {
            assertParseFailure(parser, input, expected);
        }
    }
}

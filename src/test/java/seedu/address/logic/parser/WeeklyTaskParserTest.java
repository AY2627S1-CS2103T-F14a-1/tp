package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddAttendanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Tests shared mission index and week validation.
 */
public class WeeklyTaskParserTest {

    @Test
    public void constructor_validArguments_parsesIndexAndWeek() throws Exception {
        WeeklyTaskParser parser = new WeeklyTaskParser("1 w/3", "usage");

        assertEquals(INDEX_FIRST_STUDENT, parser.getIndex());
        assertEquals(3, parser.getWeek());
    }

    @Test
    public void constructor_missingIndex_throwsParseException() {
        assertThrows(ParseException.class, WeeklyTaskParser.MESSAGE_MISSING_INDEX, ()
                -> new WeeklyTaskParser(" w/3", "usage"));
    }

    @Test
    public void constructor_invalidIndex_throwsParseException() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddAttendanceCommand.MESSAGE_USAGE);

        assertThrows(ParseException.class, expected, ()
                -> new WeeklyTaskParser("0 w/3", "usage"));
    }

    @Test
    public void parseTutorialWeek_validInput_returnsWeek() throws Exception {
        assertEquals(3, WeeklyTaskParser.parseTutorialWeek(tokenizeWeek("3")));
        assertEquals(0, WeeklyTaskParser.parseTutorialWeek(tokenizeWeek("0")));
        assertEquals(-1, WeeklyTaskParser.parseTutorialWeek(tokenizeWeek("-1")));
    }

    @Test
    public void parseTutorialWeek_invalidInput_throwsParseException() {
        String[] invalidInputs = {"3.0", "three", "3 4", "2147483648"};

        for (String input : invalidInputs) {
            assertThrows(ParseException.class, WeeklyTaskParser.MESSAGE_INVALID_TUTORIAL_WEEK, () ->
                    WeeklyTaskParser.parseTutorialWeek(tokenizeWeek(input)));
        }
    }

    @Test
    public void parseTutorialWeek_missingInput_throwsParseException() {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize("1", PREFIX_WEEK);

        assertThrows(ParseException.class, WeeklyTaskParser.MESSAGE_MISSING_WEEK, ()
                -> WeeklyTaskParser.parseTutorialWeek(arguments));
    }

    @Test
    public void parseTutorialWeek_emptyInput_throwsParseException() {
        assertThrows(ParseException.class, WeeklyTaskParser.MESSAGE_MISSING_WEEK, ()
                -> WeeklyTaskParser.parseTutorialWeek(tokenizeWeek("")));
    }

    @Test
    public void parseTutorialWeek_duplicateInput_throwsParseException() {
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize("1 w/3 w/4", PREFIX_WEEK);

        assertThrows(ParseException.class, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_WEEK), ()
                -> WeeklyTaskParser.parseTutorialWeek(arguments));
    }

    @Test
    public void parseTutorialWeek_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> WeeklyTaskParser.parseTutorialWeek(null));
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new WeeklyTaskParser(null, "usage"));
        assertThrows(NullPointerException.class, () -> new WeeklyTaskParser("1 w/3", null));
    }

    /**
     * Tokenizes one week value for tests of {@link WeeklyTaskParser#parseTutorialWeek(ArgumentMultimap)}.
     *
     * @param week Week value to place after the week prefix.
     * @return Tokenized arguments containing the supplied week value.
     */
    private ArgumentMultimap tokenizeWeek(String week) {
        return ArgumentTokenizer.tokenize("1 w/" + week, PREFIX_WEEK);
    }
}

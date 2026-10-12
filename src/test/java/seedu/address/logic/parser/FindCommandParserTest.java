package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalStudents.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.NameContainsKeywordsPredicate;
import seedu.address.model.student.StudioGroupContainsKeywordsPredicate;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArgs_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, "     ", expectedMessage);
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // name-only search
        FindCommand expectedNameCommand =
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedNameCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedNameCommand);

        // studio group search
        FindCommand expectedGroupCommand =
                new FindCommand(new StudioGroupContainsKeywordsPredicate(List.of("1A")));
        assertParseSuccess(parser, "g/1A", expectedGroupCommand);
    }

    @Test
    public void parse_nameAndGroupFilters_returnsMatchingStudents() throws Exception {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        FindCommand matchingCommand = parser.parse("Alice g/1A");
        CommandResult matchingResult = matchingCommand.execute(model);
        assertEquals("1 student(s) listed!", matchingResult.getFeedbackToUser());

        FindCommand nonMatchingCommand = parser.parse("Alice g/2B");
        CommandResult nonMatchingResult = nonMatchingCommand.execute(model);
        assertEquals("0 student(s) listed!", nonMatchingResult.getFeedbackToUser());
    }


    @Test
    public void parse_nameAfterGroup_throwsParseException() {
        String expectedMessage = String.format(
                MESSAGE_INVALID_COMMAND_FORMAT,
                FindCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "g/1A Alice", expectedMessage);
    }

    @Test
    public void parse_blankGroupValue_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "g/", expectedMessage);
    }

    @Test
    public void parse_multipleGroupKeywords_returnsFindCommand() {
        assertDoesNotThrow(() ->
                parser.parse("Alice g/1A g/1B"));
    }

    @Test
    public void parse_groupValueWithWhitespace_throwsParseException() {
        String expectedMessage = String.format(
                MESSAGE_INVALID_COMMAND_FORMAT,
                FindCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "g/1A Alice", expectedMessage);
        assertParseFailure(parser, "g/1A   Bob", expectedMessage);
    }

    @Test
    public void parse_groupPrefixOnly_throwsParseException() {
        String expectedMessage = String.format(
                MESSAGE_INVALID_COMMAND_FORMAT,
                FindCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "g/", expectedMessage);
        assertParseFailure(parser, "Alice g/", expectedMessage);
    }

    @Test
    public void parse_nameAndMultipleGroups_returnsFindCommand() {
        assertDoesNotThrow(() ->
                parser.parse("Alice g/1A g/1B"));
    }

    @Test
    public void execute_multipleGroupFilters_returnsMatchingStudents()
            throws ParseException{
        Model model = new ModelManager(
                getTypicalAddressBook(),
                new UserPrefs());

        FindCommand command = parser.parse("g/1A g/1B");

        CommandResult result = command.execute(model);

        assertEquals(
                "7 student(s) listed!",
                result.getFeedbackToUser());
    }

}

package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import seedu.address.model.student.StudioGroup;
import seedu.address.model.student.StudioGroupContainsKeywordsPredicate;

public class FindCommandParserTest {

    private final FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_emptyArgs_throwsParseException() {
        String expectedMessage =
                String.format(
                        MESSAGE_INVALID_COMMAND_FORMAT,
                        FindCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, "     ", expectedMessage);
        assertParseFailure(parser, "\n\t", expectedMessage);
    }

    @Test
    public void parse_nameOnly_returnsFindCommand() {
        FindCommand expectedCommand =
                new FindCommand(
                        new NameContainsKeywordsPredicate(
                                List.of("Alice", "Bob")));

        assertParseSuccess(parser, "Alice Bob", expectedCommand);
    }

    @Test
    public void parse_nameOnlyWithExtraWhitespace_returnsFindCommand() {
        FindCommand expectedCommand =
                new FindCommand(
                        new NameContainsKeywordsPredicate(
                                List.of("Alice", "Bob")));

        assertParseSuccess(
                parser,
                " \n Alice \n \t Bob  \t",
                expectedCommand);
    }

    @Test
    public void parse_groupOnly_returnsFindCommand() {
        FindCommand expectedCommand =
                new FindCommand(
                        new StudioGroupContainsKeywordsPredicate(
                                List.of(new StudioGroup("1A"))));

        assertParseSuccess(parser, "g/1A", expectedCommand);
    }

    @Test
    public void parse_multipleGroups_returnsFindCommand() {
        FindCommand expectedCommand =
                new FindCommand(
                        new StudioGroupContainsKeywordsPredicate(
                                List.of(
                                        new StudioGroup("1A"),
                                        new StudioGroup("1B"))));

        assertParseSuccess(
                parser,
                "g/1A g/1B",
                expectedCommand);
    }

    @Test
    public void parse_nameAndGroup_returnsFindCommand() {
        assertDoesNotThrow(() -> parser.parse("Alice g/1A"));
    }

    @Test
    public void parse_lowercaseGroupValue_throwsParseException() {
        assertParseFailure(
                parser,
                "g/1a",
                StudioGroup.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidGroupValue_throwsParseException() {
        assertParseFailure(
                parser,
                "g/abc",
                StudioGroup.MESSAGE_CONSTRAINTS);

        assertParseFailure(
                parser,
                "g/1",
                StudioGroup.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_blankGroupValue_throwsParseException() {
        assertParseFailure(
                parser,
                "g/",
                StudioGroup.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_groupValueWithWhitespace_throwsParseException() {
        assertParseFailure(
                parser,
                "g/1A Alice",
                StudioGroup.MESSAGE_CONSTRAINTS);

        assertParseFailure(
                parser,
                "g/1A 1B",
                StudioGroup.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void execute_nameAndGroupFilters_returnsMatchingStudents()
            throws ParseException {

        Model model =
                new ModelManager(
                        getTypicalAddressBook(),
                        new UserPrefs());

        FindCommand matchingCommand =
                parser.parse("Alice g/1A");

        CommandResult matchingResult =
                matchingCommand.execute(model);

        assertEquals(
                "1 student(s) listed!",
                matchingResult.getFeedbackToUser());

        FindCommand nonMatchingCommand =
                parser.parse("Alice g/2B");

        CommandResult nonMatchingResult =
                nonMatchingCommand.execute(model);

        assertEquals(
                "0 student(s) listed!",
                nonMatchingResult.getFeedbackToUser());
    }

    @Test
    public void execute_multipleGroupFilters_returnsMatchingStudents()
            throws ParseException {

        Model model =
                new ModelManager(
                        getTypicalAddressBook(),
                        new UserPrefs());

        FindCommand command =
                parser.parse("g/1A g/1B");

        CommandResult result =
                command.execute(model);

        assertEquals(
                "7 student(s) listed!",
                result.getFeedbackToUser());
    }
}
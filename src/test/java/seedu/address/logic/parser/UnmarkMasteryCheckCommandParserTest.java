package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.UnmarkMasteryCheckCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.RegisteredMasteryChecks;

/** Tests completion-command syntax, caller-specific feedback, and shared parser validation. */
public class UnmarkMasteryCheckCommandParserTest {

    private static final String USAGE_SUFFIX = "\n" + UnmarkMasteryCheckCommand.MESSAGE_USAGE;
    private final UnmarkMasteryCheckCommandParser parser = new UnmarkMasteryCheckCommandParser();

    @Test
    public void parse_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validNumbersAndWhitespace_returnsExpectedCommand() {
        assertParseSuccess(parser, "id/1 num/1", new UnmarkMasteryCheckCommand(INDEX_FIRST_STUDENT, 1));
        assertParseSuccess(parser, "  id/1  num/ 14  ", new UnmarkMasteryCheckCommand(INDEX_FIRST_STUDENT, 14));
        assertParseSuccess(parser, "id/1 num/2147483647",
                new UnmarkMasteryCheckCommand(INDEX_FIRST_STUDENT, Integer.MAX_VALUE));
    }

    @Test
    public void parse_invalidIndex_reportsIndexConstraint() {
        for (String index : List.of("0", "-1", "abc", "1.5", "1 extra", "2147483648")) {
            assertParseFailure(parser, "id/" + index + " num/1",
                ParserUtil.MESSAGE_INVALID_INDEX + USAGE_SUFFIX);
        }
    }

    @Test
    public void parse_missingNumberPrefix_reportsMissingPrefix() {
        assertParseFailure(parser, "id/1",
                CliSyntax.PREFIX_NUMBER.getMissingMessage() + USAGE_SUFFIX);
    }

    @Test
    public void parse_invalidNumber_reportsMasteryCheckConstraint() {
        for (String number : List.of("", "0", "-1", "abc", "1.5", "2147483648")) {
            assertParseFailure(parser, "id/1 num/" + number,
                RegisteredMasteryChecks.MESSAGE_CONSTRAINTS + USAGE_SUFFIX);
        }
    }

    @Test
    public void parse_duplicatePrefix_reportsRepeatedNumberPrefix() {
        for (String input : List.of("id/1 num/1 num/2", "id/1 num/1 num/1", "id/1 num/ num/1")) {
            assertParseFailure(parser, input,
                CliSyntax.PREFIX_NUMBER.getDuplicateMessage() + USAGE_SUFFIX);
        }
    }

    @Test
    public void parse_extraArguments_throwsParseException() {
        for (String input : List.of("id/1 num/1 extra", "id/1 num/1 dt/18-08|14:30", "id/1num/1")) {
            assertThrows(ParseException.class, () -> parser.parse(input));
        }
    }
}

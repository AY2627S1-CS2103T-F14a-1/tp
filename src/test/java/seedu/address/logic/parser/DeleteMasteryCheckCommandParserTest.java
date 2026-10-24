package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteMasteryCheckCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.RegisteredMasteryChecks;

/** Tests deletion argument structure, number validation, and delete-specific usage feedback. */
public class DeleteMasteryCheckCommandParserTest {
    private final DeleteMasteryCheckCommandParser parser = new DeleteMasteryCheckCommandParser();

    @Test
    public void parse_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validArguments_returnsDeletionCommand() {
        assertParseSuccess(parser, "id/1 num/1", new DeleteMasteryCheckCommand(INDEX_FIRST_STUDENT, 1));
        assertParseSuccess(parser, "  id/1   num/ 2  ", new DeleteMasteryCheckCommand(INDEX_FIRST_STUDENT, 2));
        assertParseSuccess(parser, "id/1 num/13", new DeleteMasteryCheckCommand(INDEX_FIRST_STUDENT, 13));
    }

    @Test
    public void parse_positiveNumbersBeyondStudioWeeks_returnsDeletionCommand() {
        for (String number : List.of("14", "2147483647")) {
            assertDoesNotThrow(() -> parser.parse("id/1 num/" + number),
                    "Valid mastery check number " + number + " must not be restricted to studio weeks");
        }
    }

    @Test
    public void parse_invalidIndex_reportsIndexConstraint() {
        for (String index : List.of("", "0", "-1", "abc", "1 extra", "2147483648")) {
            assertParseFailure(parser, "id/" + index + " num/1",
                    ParserUtil.MESSAGE_INVALID_INDEX);
        }
    }

    @Test
    public void parse_missingNumberPrefix_reportsMissingPrefix() {
        for (String input : List.of("id/1", "id/1 x/1")) {
            assertParseFailure(parser, input,
                    CliSyntax.PREFIX_NUMBER.getMissingMessage());
        }
    }

    @Test
    public void parse_invalidNumber_reportsMasteryCheckConstraint() {
        for (String number : List.of("", "0", "-1", "1.5", "abc", "2147483648")) {
            assertParseFailure(parser, "id/1 num/" + number, RegisteredMasteryChecks.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_repeatedNumberPrefix_reportsDuplicatePrefix() {
        for (String input : List.of("id/1 num/1 num/2", "id/1 num/1 num/1", "id/1 num/ num/2")) {
            assertParseFailure(parser, input, CliSyntax.PREFIX_NUMBER.getDuplicateMessage());
        }
    }

    @Test
    public void parse_extraTrailingArguments_throwsParseException() {
        for (String input : List.of("id/1 num/1 extra", "id/1 num/1 dt/18-08|14:30", "id/1 num/1num/2")) {
            assertThrows(ParseException.class, () -> parser.parse(input));
        }
    }
}

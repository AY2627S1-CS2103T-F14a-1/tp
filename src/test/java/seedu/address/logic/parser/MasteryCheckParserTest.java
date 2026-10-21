package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.RegisteredMasteryChecks;

/** Tests the shared parser's validated index/number values and error precedence. */
public class MasteryCheckParserTest {
    private static final String USAGE = "Completion command usage";

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MasteryCheckParser(null, USAGE));
        assertThrows(NullPointerException.class, () -> new MasteryCheckParser("1 num/1", null));
    }

    @Test
    public void constructor_boundaryValues_returnsValidatedIndexAndNumber() throws Exception {
        MasteryCheckParser first = new MasteryCheckParser(" 1 num/ 1 ", USAGE);
        assertEquals(1, first.getIndex().getOneBased());
        assertEquals(1, first.getMasteryCheckNumber());
        MasteryCheckParser maximum = new MasteryCheckParser("2147483647 num/2147483647", USAGE);
        assertEquals(Integer.MAX_VALUE, maximum.getIndex().getOneBased());
        assertEquals(Integer.MAX_VALUE, maximum.getMasteryCheckNumber());
    }

    @Test
    public void constructor_missingIndex_reportsMissingIndex() {
        for (String input : List.of("", "  ", "num/", "num/1")) {
            assertThrows(ParseException.class, MasteryCheckParser.MESSAGE_MISSING_INDEX, () ->
                    new MasteryCheckParser(input, USAGE));
        }
    }

    @Test
    public void constructor_invalidIndexAndNumber_reportsIndexFirst() {
        for (String input : List.of("0 num/0", "abc num/-1", "1 extra num/0", "2147483648 num/1")) {
            assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_INDEX, () ->
                    new MasteryCheckParser(input, USAGE));
        }
    }

    @Test
    public void constructor_missingNumberPrefix_usesCallingCommandUsage() {
        for (String usage : List.of(USAGE, "Different command usage")) {
            assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage), () ->
                    new MasteryCheckParser("1", usage));
        }
    }

    @Test
    public void constructor_duplicatePrefixes_reportsRepeatedNumber() {
        assertThrows(ParseException.class, Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NUMBER), () ->
                new MasteryCheckParser("1 num/1 num/2", USAGE));
    }

    @Test
    public void constructor_invalidNumber_reportsMasteryCheckConstraint() {
        for (String number : List.of("", "0", "-1", "abc", "1.5", "2147483648", "1 extra")) {
            assertThrows(ParseException.class, RegisteredMasteryChecks.MESSAGE_CONSTRAINTS, () ->
                    new MasteryCheckParser("1 num/" + number, USAGE));
        }
    }
}

package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

/** Tests structural validation independently of command-specific value parsing. */
public class ArgumentStructureValidatorTest {

    private static final Prefix INDEX_PREFIX = new Prefix("id/", "Index missing", "Index repeated");
    private static final Prefix WEEK_PREFIX = new Prefix("w/", "Week missing", "Week repeated");
    private static final String USAGE = "command id/INDEX w/WEEK";

    @Test
    public void validateStructure_reorderedPrefixes_returnsTrimmedValues() throws ParseException {
        ArgumentMultimap arguments = ArgumentStructureValidator.validateStructure(
                "\tw/ 3 \tid/ 1  ", USAGE, INDEX_PREFIX, WEEK_PREFIX);

        assertEquals("1", arguments.getValue(INDEX_PREFIX).orElseThrow());
        assertEquals("3", arguments.getValue(WEEK_PREFIX).orElseThrow());
        assertEquals("", arguments.getPreamble());
    }

    @Test
    public void validateStructure_preambleAndMissingPrefixes_reportsUsageFirst() {
        ParseException error = assertThrows(ParseException.class, () ->
                ArgumentStructureValidator.validateStructure("1 w/3", USAGE, INDEX_PREFIX, WEEK_PREFIX));

        assertEquals(String.format(MESSAGE_INVALID_COMMAND_FORMAT, USAGE), error.getMessage());
    }

    @Test
    public void validateStructure_missingAndRepeatedPrefixes_reportsMissingFirst() {
        ParseException error = assertThrows(ParseException.class, () ->
                ArgumentStructureValidator.validateStructure("id/1 id/2", USAGE, INDEX_PREFIX, WEEK_PREFIX));

        assertEquals("Week missing", error.getMessage());
    }

    @Test
    public void validateStructure_blankArguments_aggregatesMissingMessages() {
        for (String input : new String[]{"", " \t "}) {
            ParseException error = assertThrows(ParseException.class, () ->
                    ArgumentStructureValidator.validateStructure(input, USAGE, WEEK_PREFIX, INDEX_PREFIX));
            assertEquals("Week missing\nIndex missing", error.getMessage());
        }
    }

    @Test
    public void validateStructure_duplicatePrefixes_reportsCustomMessage() {
        ParseException error = assertThrows(ParseException.class, () ->
                ArgumentStructureValidator.validateStructure("w/3 w/4 id/1 id/2", USAGE, INDEX_PREFIX, WEEK_PREFIX));

        assertEquals("Index repeated", error.getMessage());
    }

    @Test
    public void validateStructure_emptyOrInvalidValues_leavesValueValidationToCaller() throws ParseException {
        ArgumentMultimap arguments = ArgumentStructureValidator.validateStructure(
                "id/ w/not-a-week", USAGE, INDEX_PREFIX, WEEK_PREFIX);

        assertEquals("", arguments.getValue(INDEX_PREFIX).orElseThrow());
        assertEquals("not-a-week", arguments.getValue(WEEK_PREFIX).orElseThrow());
    }

    @Test
    public void validateStructure_unknownPrefixWithinValue_preservesTextForValueValidation() throws ParseException {
        ArgumentMultimap arguments = ArgumentStructureValidator.validateStructure(
                "id/1 x/extra w/3", USAGE, INDEX_PREFIX, WEEK_PREFIX);

        assertEquals("1 x/extra", arguments.getValue(INDEX_PREFIX).orElseThrow());
    }
}

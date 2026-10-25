package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

/** Tests structural validation independently of command-specific value parsing. */
public class ArgumentStructureValidatorTest {

    private static final Prefix INDEX_PREFIX = new Prefix("id/", "Index missing", "Index repeated");
    private static final Prefix WEEK_PREFIX = new Prefix("w/", "Week missing", "Week repeated");
    private static final Prefix TAG_PREFIX = new Prefix("t/", "Tag missing", "Tag repeated");

    @Test
    public void validateStructure_reorderedPrefixes_returnsTrimmedValues() throws ParseException {
        ArgumentMultimap arguments = ArgumentStructureValidator.validateStructure(
                "\tw/ 3 \tid/ 1  ", INDEX_PREFIX, WEEK_PREFIX);

        assertEquals("1", arguments.getValue(INDEX_PREFIX).orElseThrow());
        assertEquals("3", arguments.getValue(WEEK_PREFIX).orElseThrow());
        assertEquals("", arguments.getPreamble());
    }

    @Test
    public void validateStructure_preambleAndMissingPrefixes_reportsFormatFirst() {
        ParseException error = assertThrows(ParseException.class, () ->
                ArgumentStructureValidator.validateStructure("1 w/3", INDEX_PREFIX, WEEK_PREFIX));

        assertEquals(MESSAGE_INVALID_COMMAND, error.getMessage());
    }

    @Test
    public void validateStructure_missingAndRepeatedPrefixes_reportsMissingFirst() {
        ParseException error = assertThrows(ParseException.class, () ->
                ArgumentStructureValidator.validateStructure("id/1 id/2", INDEX_PREFIX, WEEK_PREFIX));

        assertEquals("Week missing", error.getMessage());
    }

    @Test
    public void validateStructure_blankArguments_aggregatesMissingMessages() {
        for (String input : new String[]{"", " \t "}) {
            ParseException error = assertThrows(ParseException.class, () ->
                    ArgumentStructureValidator.validateStructure(input, WEEK_PREFIX, INDEX_PREFIX));
            assertEquals("Week missing\nIndex missing", error.getMessage());
        }
    }

    @Test
    public void validateStructure_duplicatePrefixes_reportsCustomMessage() {
        ParseException error = assertThrows(ParseException.class, () ->
                ArgumentStructureValidator.validateStructure("w/3 w/4 id/1 id/2", INDEX_PREFIX, WEEK_PREFIX));

        assertEquals("Index repeated", error.getMessage());
    }

    @Test
    public void validateStructure_emptyOrInvalidValues_leavesValueValidationToCaller() throws ParseException {
        ArgumentMultimap arguments = ArgumentStructureValidator.validateStructure(
                "id/ w/not-a-week", INDEX_PREFIX, WEEK_PREFIX);

        assertEquals("", arguments.getValue(INDEX_PREFIX).orElseThrow());
        assertEquals("not-a-week", arguments.getValue(WEEK_PREFIX).orElseThrow());
    }

    @Test
    public void validateStructure_unknownPrefixWithinValue_preservesTextForValueValidation() throws ParseException {
        ArgumentMultimap arguments = ArgumentStructureValidator.validateStructure(
                "id/1 x/extra w/3", INDEX_PREFIX, WEEK_PREFIX);

        assertEquals("1 x/extra", arguments.getValue(INDEX_PREFIX).orElseThrow());
    }

    @Test
    public void validateStructureWithOptionals_optionalPrefixOmitted_acceptsRequiredPrefix() throws ParseException {
        ArgumentMultimap arguments = ArgumentStructureValidator.validateStructureWithOptionals(
                "id/1", new Prefix[]{INDEX_PREFIX}, new Prefix[]{INDEX_PREFIX, WEEK_PREFIX}, INDEX_PREFIX, WEEK_PREFIX);

        assertEquals("1", arguments.getValue(INDEX_PREFIX).orElseThrow());
        assertFalse(arguments.getValue(WEEK_PREFIX).isPresent());
    }

    @Test
    public void validateStructureWithOptionals_reorderedOptionalAndRepeatedTags_retainsAllValues()
            throws ParseException {
        ArgumentMultimap arguments = ArgumentStructureValidator.validateStructureWithOptionals(
                "t/first w/ 3 id/ 1 t/second", new Prefix[]{INDEX_PREFIX},
                new Prefix[]{INDEX_PREFIX, WEEK_PREFIX}, new Prefix[]{INDEX_PREFIX, WEEK_PREFIX, TAG_PREFIX});

        assertEquals("1", arguments.getValue(INDEX_PREFIX).orElseThrow());
        assertEquals("3", arguments.getValue(WEEK_PREFIX).orElseThrow());
        assertEquals(List.of("first", "second"), arguments.getAllValues(TAG_PREFIX));
        assertEquals("second", arguments.getValue(TAG_PREFIX).orElseThrow());
    }

    @Test
    public void validateStructureWithOptionals_missingRequiredPrefixes_reportsEachOnceInOrder() {
        ParseException error = assertThrows(ParseException.class, () ->
                ArgumentStructureValidator.validateStructureWithOptionals("t/optional",
                        new Prefix[]{WEEK_PREFIX, INDEX_PREFIX, WEEK_PREFIX}, new Prefix[]{INDEX_PREFIX, WEEK_PREFIX},
                        INDEX_PREFIX, WEEK_PREFIX, TAG_PREFIX));

        assertEquals("Week missing\nIndex missing", error.getMessage());
    }

    @Test
    public void validateStructureWithOptionals_repeatedOptionalPrefix_reportsDuplicateMessage() {
        ParseException error = assertThrows(ParseException.class, () ->
                ArgumentStructureValidator.validateStructureWithOptionals("id/1 w/3 w/4",
                        new Prefix[]{INDEX_PREFIX}, new Prefix[]{INDEX_PREFIX, WEEK_PREFIX}, INDEX_PREFIX,
                WEEK_PREFIX));

        assertEquals("Week repeated", error.getMessage());
    }

    @Test
    public void validateStructureWithOptionals_repeatedRequiredPrefix_reportsDuplicateMessage() {
        ParseException error = assertThrows(ParseException.class, () ->
                ArgumentStructureValidator.validateStructureWithOptionals("id/1 id/2",
                        new Prefix[]{INDEX_PREFIX}, new Prefix[]{INDEX_PREFIX}, INDEX_PREFIX, WEEK_PREFIX));

        assertEquals("Index repeated", error.getMessage());
    }

    @Test
    public void validateStructureWithOptionals_preambleAndMissingPrefix_reportsFormatFirst() {
        ParseException error = assertThrows(ParseException.class, () ->
                ArgumentStructureValidator.validateStructureWithOptionals("unexpected w/3",
                        new Prefix[]{INDEX_PREFIX}, new Prefix[]{INDEX_PREFIX}, INDEX_PREFIX, WEEK_PREFIX));

        assertEquals(MESSAGE_INVALID_COMMAND, error.getMessage());
    }

    @Test
    public void validateStructureWithOptionals_missingAndRepeatedPrefixes_reportsMissingFirst() {
        ParseException error = assertThrows(ParseException.class, () ->
                ArgumentStructureValidator.validateStructureWithOptionals("w/3 w/4",
                        new Prefix[]{INDEX_PREFIX}, new Prefix[]{WEEK_PREFIX}, INDEX_PREFIX, WEEK_PREFIX));

        assertEquals("Index missing", error.getMessage());
    }

    @Test
    public void validateStructureWithOptionals_emptyValues_leavesValueValidationToCaller() throws ParseException {
        ArgumentMultimap arguments = ArgumentStructureValidator.validateStructureWithOptionals(
                "id/ w/ t/", new Prefix[]{INDEX_PREFIX}, new Prefix[]{INDEX_PREFIX, WEEK_PREFIX},
                INDEX_PREFIX, WEEK_PREFIX, TAG_PREFIX);

        assertEquals("", arguments.getValue(INDEX_PREFIX).orElseThrow());
        assertEquals("", arguments.getValue(WEEK_PREFIX).orElseThrow());
        assertEquals(List.of(""), arguments.getAllValues(TAG_PREFIX));
    }

    @Test
    public void validateStructureWithOptionals_noRequiredOrNonrepeatablePrefixes_acceptsBlankAndRepeatedInput()
            throws ParseException {
        ArgumentMultimap blank = ArgumentStructureValidator.validateStructureWithOptionals(
                " \t ", new Prefix[]{}, new Prefix[]{});
        ArgumentMultimap repeated = ArgumentStructureValidator.validateStructureWithOptionals(
                "t/one t/two", new Prefix[]{}, new Prefix[]{}, TAG_PREFIX);

        assertEquals("", blank.getPreamble());
        assertEquals(List.of("one", "two"), repeated.getAllValues(TAG_PREFIX));
    }

    @Test
    public void validateStructureWithOptionals_unknownPrefixInValue_preservesTextForValueValidation()
            throws ParseException {
        ArgumentMultimap arguments = ArgumentStructureValidator.validateStructureWithOptionals(
                "id/1 x/extra", new Prefix[]{INDEX_PREFIX}, new Prefix[]{INDEX_PREFIX}, INDEX_PREFIX);

        assertEquals("1 x/extra", arguments.getValue(INDEX_PREFIX).orElseThrow());
    }
}

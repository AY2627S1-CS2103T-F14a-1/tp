package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

public class ArgumentMultimapTest {

    private static final Prefix NAME_PREFIX = new Prefix("n/");
    private static final Prefix PHONE_PREFIX = new Prefix("p/");

    private final ArgumentMultimap argumentMultimap = new ArgumentMultimap();

    @Test
    public void arePrefixesPresent_noPrefixes_returnsTrue() {
        assertTrue(argumentMultimap.arePrefixesPresent());
    }

    @Test
    public void arePrefixesPresent_allPrefixesPresent_returnsTrue() {
        argumentMultimap.put(NAME_PREFIX, "Alice");
        argumentMultimap.put(PHONE_PREFIX, "98765432");

        assertTrue(argumentMultimap.arePrefixesPresent(NAME_PREFIX, PHONE_PREFIX));
    }

    @Test
    public void arePrefixesPresent_prefixMissing_returnsFalse() {
        argumentMultimap.put(NAME_PREFIX, "Alice");

        assertFalse(argumentMultimap.arePrefixesPresent(NAME_PREFIX, PHONE_PREFIX));
    }

    @Test
    public void verifyPrefixesPresentFor_multipleMissing_collectsMessagesOnceInSuppliedOrder() {
        Prefix first = new Prefix("id/", "Student index is missing", "duplicate index");
        Prefix second = new Prefix("w/", "Week is missing", "duplicate week");
        argumentMultimap.put(NAME_PREFIX, "Alice");

        ParseException error = assertThrows(ParseException.class, () ->
                argumentMultimap.verifyPrefixesPresentFor(second, NAME_PREFIX, first, second));

        assertEquals("Week is missing\nStudent index is missing", error.getMessage());
        assertEquals("Alice", argumentMultimap.getValue(NAME_PREFIX).orElseThrow());
        assertFalse(argumentMultimap.getValue(first).isPresent());
    }

    @Test
    public void verifyPrefixesPresentFor_emptyValueAndNoRequirements_acceptsInput() {
        assertDoesNotThrow(() -> argumentMultimap.verifyPrefixesPresentFor());
        argumentMultimap.put(NAME_PREFIX, "");

        assertDoesNotThrow(() -> argumentMultimap.verifyPrefixesPresentFor(NAME_PREFIX, NAME_PREFIX));
        ParseException error = assertThrows(ParseException.class, () ->
                argumentMultimap.verifyPrefixesPresentFor(NAME_PREFIX, PHONE_PREFIX));
        assertEquals(PHONE_PREFIX.getMissingMessage(), error.getMessage());
    }

    @Test
    public void verifyNoDuplicatePrefixesFor_multipleDuplicates_reportsFirstSuppliedPrefix() {
        Prefix first = new Prefix("id/", "missing index", "Index is repeated");
        Prefix second = new Prefix("w/", "missing week", "Week is repeated");
        argumentMultimap.put(first, "1");
        argumentMultimap.put(first, "2");
        argumentMultimap.put(second, "3");
        argumentMultimap.put(second, "4");

        ParseException error = assertThrows(ParseException.class, () ->
                argumentMultimap.verifyNoDuplicatePrefixesFor(second, first));

        assertEquals("Week is repeated", error.getMessage());
        assertEquals(List.of("3", "4"), argumentMultimap.getAllValues(second));
    }

    @Test
    public void verifyNoDuplicatePrefixesFor_absentSingleAndUncheckedDuplicate_acceptsInput() {
        assertDoesNotThrow(() -> argumentMultimap.verifyNoDuplicatePrefixesFor());
        assertDoesNotThrow(() -> argumentMultimap.verifyNoDuplicatePrefixesFor(NAME_PREFIX));
        argumentMultimap.put(NAME_PREFIX, "");
        assertDoesNotThrow(() -> argumentMultimap.verifyNoDuplicatePrefixesFor(NAME_PREFIX, NAME_PREFIX));
        argumentMultimap.put(PHONE_PREFIX, "123");
        argumentMultimap.put(PHONE_PREFIX, "456");
        assertDoesNotThrow(() -> argumentMultimap.verifyNoDuplicatePrefixesFor(NAME_PREFIX));
    }
}

package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

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
}

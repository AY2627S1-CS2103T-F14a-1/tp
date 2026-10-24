package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.IllegalFormatException;

import org.junit.jupiter.api.Test;

/** Tests prefix identity and formatted validation messages. */
public class PrefixTest {

    @Test
    public void constructor_defaultMessages_includesPrefixText() {
        Prefix prefix = new Prefix("id/");

        assertEquals("id/", prefix.getPrefix());
        assertEquals("Missing required prefix: id/", prefix.getMissingMessage());
        assertEquals("Prefix id/ must be specified only once.", prefix.getDuplicateMessage());
    }

    @Test
    public void constructor_customFormats_insertsPrefixIntoMessages() {
        Prefix prefix = new Prefix("id/", "Use %1$sINDEX; do not omit %1$s.", "Only one %s is allowed.");

        assertEquals("Use id/INDEX; do not omit id/.", prefix.getMissingMessage());
        assertEquals("Only one id/ is allowed.", prefix.getDuplicateMessage());
    }

    @Test
    public void constructor_invalidFormats_throwsFormatException() {
        assertThrows(IllegalFormatException.class, () -> new Prefix("id/", "%d", "duplicate"));
        assertThrows(IllegalFormatException.class, () -> new Prefix("id/", "missing", "%2$s"));
        assertThrows(NullPointerException.class, () -> new Prefix("id/", null, "duplicate"));
        assertThrows(NullPointerException.class, () -> new Prefix("id/", "missing", null));
    }

    @Test
    public void equals_differentMessages_preservesMapIdentity() {
        Prefix first = new Prefix("id/", "missing A", "duplicate A");
        Prefix second = new Prefix("id/", "missing B", "duplicate B");
        ArgumentMultimap arguments = new ArgumentMultimap();
        arguments.put(first, "1");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals("1", arguments.getValue(second).orElseThrow());
    }

    @Test
    public void hashCode_nullPrefix_returnsZero() {
        assertEquals(0, new Prefix(null).hashCode());
    }

    @Test
    public void hashCode_nonNullPrefix_usesPrefixText() {
        assertEquals("id/".hashCode(), new Prefix("id/").hashCode());
    }

    @Test
    public void equals_identityNullOtherTypeAndDifferentPrefix_obeysEqualityContract() {
        Prefix prefix = new Prefix("id/");

        assertTrue(prefix.equals(prefix));
        assertFalse(prefix.equals(null));
        assertFalse(prefix.equals("id/"));
        assertNotEquals(new Prefix("w/"), prefix);
        assertEquals(new Prefix("id/"), prefix);
    }

    @Test
    public void toString_validPrefix_returnsPrefixText() {
        assertEquals("id/", new Prefix("id/").toString());
    }
}

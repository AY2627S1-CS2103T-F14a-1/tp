package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDIO_EFFORT;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import org.junit.jupiter.api.Test;

/**
 * Tests shared studio-record index, week, and additional-prefix parsing.
 */
public class WeeklyTaskParserTest {

    @Test
    public void constructor_noAdditionalPrefixes_parsesIndexAndWeek() throws Exception {
        WeeklyTaskParser parser = new WeeklyTaskParser("1 w/3", "usage");

        assertEquals(INDEX_FIRST_STUDENT, parser.getIndex());
        assertEquals(3, parser.getWeek());
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new WeeklyTaskParser(null, "usage"));
        assertThrows(NullPointerException.class, () -> new WeeklyTaskParser("1 w/3", null));
    }

    @Test
    public void constructor_additionalPrefix_parsesAllPrefixes() throws Exception {
        WeeklyTaskParser parser = new WeeklyTaskParser("1 w/3 ef/8", "usage", PREFIX_STUDIO_EFFORT);

        assertEquals(INDEX_FIRST_STUDENT, parser.getIndex());
        assertEquals(3, parser.getWeek());
        assertEquals("8", parser.getPrefixValue(PREFIX_STUDIO_EFFORT).orElseThrow());
    }

    @Test
    public void constructor_additionalPrefix_doesNotModifyPrefixArray() throws Exception {
        Prefix[] additionalPrefixes = {PREFIX_STUDIO_EFFORT};

        new WeeklyTaskParser("1 w/3 ef/8", "usage", additionalPrefixes);

        assertEquals(PREFIX_STUDIO_EFFORT, additionalPrefixes[0]);
    }
}

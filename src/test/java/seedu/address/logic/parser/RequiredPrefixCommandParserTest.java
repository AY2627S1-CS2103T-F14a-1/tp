package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATETIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NUMBER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDIO_EFFORT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

/** Checks that every command using the shared validator supplies its required prefixes. */
public class RequiredPrefixCommandParserTest {

    @Test
    public void parse_eachRequiredPrefixMissing_reportsPrefixSpecificMessage() {
        verifyRequiredPrefixes(new AddMasteryCheckCommandParser(),
                List.of(PREFIX_INDEX, PREFIX_NUMBER, PREFIX_DATETIME), List.of("1", "1", "18-08|14:30"));
        verifyRequiredPrefixes(new DeleteMasteryCheckCommandParser(),
                List.of(PREFIX_INDEX, PREFIX_NUMBER), List.of("1", "1"));
        verifyRequiredPrefixes(new MarkMasteryCheckCommandParser(),
                List.of(PREFIX_INDEX, PREFIX_NUMBER), List.of("1", "1"));
        verifyRequiredPrefixes(new UnmarkMasteryCheckCommandParser(),
                List.of(PREFIX_INDEX, PREFIX_NUMBER), List.of("1", "1"));
        verifyRequiredPrefixes(new AddStudioRecordCommandParser(),
                List.of(PREFIX_INDEX, PREFIX_WEEK, PREFIX_STUDIO_EFFORT), List.of("1", "3", "50"));
        verifyRequiredPrefixes(new EditStudioRecordCommandParser(),
                List.of(PREFIX_INDEX, PREFIX_WEEK, PREFIX_STUDIO_EFFORT), List.of("1", "3", "50"));
        verifyRequiredPrefixes(new DeleteStudioRecordCommandParser(),
                List.of(PREFIX_INDEX, PREFIX_WEEK), List.of("1", "3"));
        verifyRequiredPrefixes(new MarkMissionCommandParser(),
                List.of(PREFIX_INDEX, PREFIX_MISSION), List.of("1", "M01"));
        verifyRequiredPrefixes(new UnmarkMissionCommandParser(),
                List.of(PREFIX_INDEX, PREFIX_MISSION), List.of("1", "M01"));
        verifyRequiredPrefixes(new DeleteStudentCommandParser(), List.of(PREFIX_INDEX), List.of("1"));
        verifyRequiredPrefixes(new ViewCommandParser(), List.of(PREFIX_INDEX), List.of("1"));
        verifyRequiredPrefixes(new DeleteMissionCommandParser(), List.of(PREFIX_MISSION), List.of("M01"));
        verifyRequiredPrefixes(new ListMissionsCommandParser(), List.of(PREFIX_MISSION), List.of("M01"));
        verifyRequiredPrefixes(new AddMissionCommandParser(),
                List.of(PREFIX_MISSION, CliSyntax.PREFIX_NAME, CliSyntax.PREFIX_DATE),
                List.of("M01", "Runes", "2026-10-20"));
    }

    /**
     * Interleaves successful parsing with missing and repeated prefix checks.
     *
     * @param parser Command parser being checked.
     * @param prefixes Required prefixes in validation order.
     * @param values Valid values corresponding to the prefixes.
     */
    private void verifyRequiredPrefixes(Parser<?> parser, List<Prefix> prefixes, List<String> values) {
        StringBuilder validInput = new StringBuilder();
        for (int i = 0; i < prefixes.size(); i++) {
            validInput.append(prefixes.get(i)).append(values.get(i)).append(' ');
        }
        assertThrows(NullPointerException.class, () -> parser.parse(null));
        assertThrows(ParseException.class, () -> parser.parse("unexpected " + validInput));
        for (int omitted = 0; omitted < prefixes.size(); omitted++) {
            StringBuilder incompleteInput = new StringBuilder();
            for (int i = 0; i < prefixes.size(); i++) {
                if (i != omitted) {
                    incompleteInput.append(prefixes.get(i)).append(values.get(i)).append(' ');
                }
            }
            Prefix prefix = prefixes.get(omitted);
            ParseException missingError = assertThrows(ParseException.class, () ->
                    parser.parse(incompleteInput.toString()));
            assertEquals(prefix.getMissingMessage(), missingError.getMessage());
            assertDoesNotThrow(() -> parser.parse(validInput.toString()));
            ParseException duplicateError = assertThrows(ParseException.class, () ->
                    parser.parse(validInput.toString() + prefix + values.get(prefixes.indexOf(prefix))));
            assertEquals(prefix.getDuplicateMessage(), duplicateError.getMessage());
        }
    }
}

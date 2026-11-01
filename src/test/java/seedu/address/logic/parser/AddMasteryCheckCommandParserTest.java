package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.util.ParserUtil.MESSAGE_SEE_USAGE;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddMasteryCheckCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.util.MasteryCheckParserUtil;
import seedu.address.logic.parser.util.ParserUtil;
import seedu.address.model.masterycheck.MasteryCheck;

/** Tests mastery check argument validation and construction of executable commands. */
public class AddMasteryCheckCommandParserTest {

    private static final String USAGE_SUFFIX = MESSAGE_SEE_USAGE + AddMasteryCheckCommand.MESSAGE_USAGE;
    private final AddMasteryCheckCommandParser parser = new AddMasteryCheckCommandParser();

    @Test
    public void parse_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validArguments_returnsExpectedCommand() {
        LocalDateTime time = LocalDateTime.of(2026, 8, 18, 14, 30);
        AddMasteryCheckCommand expected = new AddMasteryCheckCommand(INDEX_FIRST_STUDENT, 1, new MasteryCheck(time));
        assertParseSuccess(parser, "id/1 num/1 dt/2026-08-18|14:30", expected);
        assertParseSuccess(parser, " id/1 dt/2026-08-18|14:30 num/1 ", expected);
        assertParseSuccess(parser, "id/1 num/2147483647 dt/2026-12-31|23:59",
                new AddMasteryCheckCommand(INDEX_FIRST_STUDENT, Integer.MAX_VALUE,
                        new MasteryCheck(LocalDateTime.of(2026, 12, 31, 23, 59))));
    }

    @Test
    public void parse_invalidIndex_reportsIndexConstraint() {
        for (String index : List.of("0", "-1", "abc", "1 extra", "2147483648")) {
            assertParseFailure(parser, "id/" + index + " num/1 dt/2026-08-18|14:30",
                ParserUtil.MESSAGE_INVALID_INDEX + USAGE_SUFFIX);
        }
    }

    @Test
    public void parse_allValuesBlank_reportsFirstRequiredArgument() {
        assertParseFailure(parser, "id/ num/ dt/",
                "Prefix id/ is missing its argument." + USAGE_SUFFIX);
    }

    @Test
    public void parse_missingPrefixes_reportsEveryMissingPrefix() {
        assertParseFailure(parser, "id/1",
                String.join("\n", CliSyntax.PREFIX_NUMBER.getMissingMessage(),
                        CliSyntax.PREFIX_DATETIME.getMissingMessage()) + USAGE_SUFFIX);
        assertParseFailure(parser, "id/1 num/1",
                CliSyntax.PREFIX_DATETIME.getMissingMessage() + USAGE_SUFFIX);
        assertParseFailure(parser, "id/1 dt/2026-08-18|14:30",
                CliSyntax.PREFIX_NUMBER.getMissingMessage() + USAGE_SUFFIX);
    }

    @Test
    public void parse_nonPositiveNumber_reportsNumberConstraint() {
        for (String number : List.of("0", "-1", "-2147483648")) {
            assertParseFailure(parser, "id/1 num/" + number + " dt/2026-08-18|14:30",
                    MasteryCheckParserUtil.MESSAGE_ERROR_NEGATIVE_NUMBER + USAGE_SUFFIX);
        }
    }

    @Test
    public void parse_nonIntegerOrOverflowingNumber_reportsNonNumberMessage() {
        for (String number : List.of("1.5", "abc", "2147483648")) {
            assertParseFailure(parser, "id/1 num/" + number + " dt/2026-08-18|14:30",
                    MasteryCheckParserUtil.MESSAGE_ERROR_NON_NUMBER + USAGE_SUFFIX);
        }
    }

    @Test
    public void parse_impossibleDate_reportsDateConstraint() {
        assertParseFailure(parser, "id/1 num/1 dt/2026-04-31|14:30",
                MasteryCheckParserUtil.MESSAGE_INVALID_MASTERY_CHECK_DATE + USAGE_SUFFIX);
    }

    @Test
    public void parse_impossibleTime_reportsTimeConstraint() {
        for (String time : List.of("2026-08-18|24:00", "2026-08-18|14:60")) {
            assertParseFailure(parser, "id/1 num/1 dt/" + time,
                    MasteryCheckParserUtil.MESSAGE_INVALID_MASTERY_CHECK_TIME + USAGE_SUFFIX);
        }
    }

    @Test
    public void parse_malformedDateTime_reportsFormatConstraint() {
        for (String time : List.of("18-08|14:30", "2026-08-18||14:30", "2026-08-18|14:30 extra")) {
            assertParseFailure(parser, "id/1 num/1 dt/" + time,
                    MasteryCheckParserUtil.MESSAGE_INVALID_MASTERY_CHECK_DATETIME_FORMAT + USAGE_SUFFIX);
        }
    }

    @Test
    public void parse_duplicatePrefixes_reportsRepeatedPrefix() {
        assertParseFailure(parser, "id/1 num/1 num/2 dt/2026-08-18|14:30",
                CliSyntax.PREFIX_NUMBER.getDuplicateMessage() + USAGE_SUFFIX);
        assertParseFailure(parser, "id/1 num/1 dt/2026-08-18|14:30 dt/2026-08-19|14:30",
                CliSyntax.PREFIX_DATETIME.getDuplicateMessage() + USAGE_SUFFIX);
    }

    @Test
    public void parse_extraTrailingText_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("id/1 num/1 dt/2026-08-18|14:30 extra"));
    }
}

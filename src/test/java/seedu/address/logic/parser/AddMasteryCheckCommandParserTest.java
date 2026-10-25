package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddMasteryCheckCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.masterycheck.MasteryCheck;
import seedu.address.model.student.RegisteredMasteryChecks;

/** Tests mastery check argument validation and construction of executable commands. */
public class AddMasteryCheckCommandParserTest {

    private static final String USAGE_SUFFIX = "\n" + AddMasteryCheckCommand.MESSAGE_USAGE;
    private final AddMasteryCheckCommandParser parser = new AddMasteryCheckCommandParser();

    @Test
    public void parse_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validArguments_returnsExpectedCommand() {
        LocalDateTime time = LocalDateTime.of(LocalDate.now().getYear(), 8, 18, 14, 30);
        AddMasteryCheckCommand expected = new AddMasteryCheckCommand(INDEX_FIRST_STUDENT, 1, new MasteryCheck(time));
        assertParseSuccess(parser, "id/1 num/1 dt/18-08|14:30", expected);
        assertParseSuccess(parser, " id/1 dt/18-08|14:30 num/1 ", expected);
        assertParseSuccess(parser, "id/1 num/2147483647 dt/31-12|23:59",
                new AddMasteryCheckCommand(INDEX_FIRST_STUDENT, Integer.MAX_VALUE,
                        new MasteryCheck(LocalDateTime.of(LocalDate.now().getYear(), 12, 31, 23, 59))));
    }

    @Test
    public void parse_invalidIndex_reportsIndexConstraint() {
        for (String index : List.of("", "0", "-1", "abc", "1 extra", "2147483648")) {
            assertParseFailure(parser, "id/" + index + " num/1 dt/18-08|14:30",
                ParserUtil.MESSAGE_INVALID_INDEX + USAGE_SUFFIX);
        }
    }

    @Test
    public void parse_missingPrefixes_reportsEveryMissingPrefix() {
        assertParseFailure(parser, "id/1",
                String.join("\n", CliSyntax.PREFIX_NUMBER.getMissingMessage(),
                        CliSyntax.PREFIX_DATETIME.getMissingMessage()) + USAGE_SUFFIX);
        assertParseFailure(parser, "id/1 num/1",
                CliSyntax.PREFIX_DATETIME.getMissingMessage() + USAGE_SUFFIX);
        assertParseFailure(parser, "id/1 dt/18-08|14:30",
                CliSyntax.PREFIX_NUMBER.getMissingMessage() + USAGE_SUFFIX);
    }

    @Test
    public void parse_nonPositiveNumber_reportsNumberConstraint() {
        for (String number : List.of("0", "-1", "-2147483648")) {
            assertParseFailure(parser, "id/1 num/" + number + " dt/18-08|14:30",
                    RegisteredMasteryChecks.MESSAGE_CONSTRAINTS + USAGE_SUFFIX);
        }
    }

    @Test
    public void parse_nonIntegerOrOverflowingNumber_reportsNonNumberMessage() {
        for (String number : List.of("", "1.5", "abc", "2147483648")) {
            assertParseFailure(parser, "id/1 num/" + number + " dt/18-08|14:30",
                    RegisteredMasteryChecks.MESSAGE_NON_NUMBER + USAGE_SUFFIX);
        }
    }

    @Test
    public void parse_invalidDateTime_reportsDateTimeConstraint() {
        for (String time : List.of("", "31-04|14:30", "18-08|24:00", "18-08|14:60", "18-08||14:30")) {
            assertParseFailure(parser, "id/1 num/1 dt/" + time,
                ParserUtil.MESSAGE_INVALID_MASTERY_CHECK_DATETIME + USAGE_SUFFIX);
        }
    }

    @Test
    public void parse_duplicatePrefixes_reportsRepeatedPrefix() {
        assertParseFailure(parser, "id/1 num/1 num/2 dt/18-08|14:30",
                CliSyntax.PREFIX_NUMBER.getDuplicateMessage() + USAGE_SUFFIX);
        assertParseFailure(parser, "id/1 num/1 dt/18-08|14:30 dt/19-08|14:30",
                CliSyntax.PREFIX_DATETIME.getDuplicateMessage() + USAGE_SUFFIX);
    }

    @Test
    public void parse_extraTrailingText_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse("id/1 num/1 dt/18-08|14:30 extra"));
    }
}

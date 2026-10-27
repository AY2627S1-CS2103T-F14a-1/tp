package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.Locale;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.RegisteredMasteryChecks;

/**
 * Contains utility methods used for parsing strings in the various Mastery Check Parser classes.
 */
public class MasteryCheckParserUtil {

    public static final String MESSAGE_INVALID_MASTERY_CHECK_DATETIME =
            "Mastery check appointment must be a valid date and 24-hour time in dd-MM|HH:mm format.";

    /** Strict input formatter whose default year is captured when this class is initialized. */
    private static final DateTimeFormatter MASTERY_CHECK_DATETIME_FORMATTER =
            new DateTimeFormatterBuilder()
                    .appendPattern("dd-MM'|'HH:mm")
                    .parseDefaulting(ChronoField.YEAR, LocalDate.now().getYear())
                    .toFormatter(Locale.ROOT)
                    .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Parses a positive mastery check number, trimming leading and trailing whitespace.
     *
     * @param number Text containing the mastery check number.
     * @return A positive integer identifying the mastery check registration.
     * @throws NullPointerException If number is null.
     * @throws ParseException If the text is not an integer from 1 to {@link Integer#MAX_VALUE}.
     */
    public static int parseMasteryCheckNumber(String number) throws ParseException {
        requireNonNull(number);
        try {
            int trimmedNumber = Integer.parseInt(number.trim());
            if (!RegisteredMasteryChecks.isValidMasteryCheckNumber(trimmedNumber)) {
                throw new ParseException(RegisteredMasteryChecks.MESSAGE_CONSTRAINTS);
            }
            return trimmedNumber;
        } catch (NumberFormatException _) {
            throw new ParseException(RegisteredMasteryChecks.MESSAGE_NON_NUMBER);
        }
    }

    /**
     * Parses an appointment in dd-MM|HH:mm format using strict date and time validation.
     * Leading and trailing whitespace is trimmed. The omitted year defaults to the system's
     * local year when this class is initialized, as captured by the shared formatter.
     *
     * @param dateTime The appointment date and 24-hour time separated by a vertical bar.
     * @return The validated appointment date and time.
     * @throws NullPointerException If dateTime is null.
     * @throws ParseException If the input format, calendar date, or time is invalid.
     */
    public static LocalDateTime parseMasteryCheckDateTime(String dateTime) throws ParseException {
        requireNonNull(dateTime);
        String trimmedDateTime = dateTime.trim();
        try {
            return LocalDateTime.parse(trimmedDateTime, MASTERY_CHECK_DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ParseException(MESSAGE_INVALID_MASTERY_CHECK_DATETIME);
        }
    }

}

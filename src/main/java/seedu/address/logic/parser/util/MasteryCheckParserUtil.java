package seedu.address.logic.parser.util;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.RegisteredMasteryChecks;

/**
 * Contains utility methods used for parsing strings in the various Mastery Check Parser classes.
 */
public final class MasteryCheckParserUtil {

    public static final String MESSAGE_ERROR_NEGATIVE_NUMBER =
            "Provided mastery check number is smaller than or equal to 0.";
    public static final String MESSAGE_ERROR_NON_NUMBER =
            "Provided mastery check number value is not a valid integer.";

    public static final String MESSAGE_INVALID_MASTERY_CHECK_DATE = "Provided date value is invalid. "
            + "Date segment must use YYYY-MM-DD format, for example 2026-10-20.";
    public static final String MESSAGE_INVALID_MASTERY_CHECK_TIME = "Provided time value is invalid. "
            + "Time segment must use HH:MM 24-hour format, for example 14:30.";
    public static final String MESSAGE_INVALID_MASTERY_CHECK_DATETIME_FORMAT = "Provided datetime format is invalid.";

    /** Strict formatter for a 24-hour appointment time without seconds. */
    private static final DateTimeFormatter MASTERY_CHECK_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm", Locale.ROOT).withResolverStyle(ResolverStyle.STRICT);

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
                throw new ParseException(MESSAGE_ERROR_NEGATIVE_NUMBER);
            }
            return trimmedNumber;
        } catch (NumberFormatException _) {
            throw new ParseException(MESSAGE_ERROR_NON_NUMBER);
        }
    }

    /**
     * Parses an appointment in yyyy-MM-dd|HH:mm format using strict ISO date and 24-hour time validation.
     * Leading and trailing whitespace is trimmed.
     * Format errors take precedence over invalid dates, which take precedence over invalid times.
     *
     * @param dateTime The appointment date and 24-hour time separated by a vertical bar.
     * @return The validated appointment date and time.
     * @throws NullPointerException If dateTime is null.
     * @throws ParseException If the input format, calendar date, or time is invalid.
     */
    public static LocalDateTime parseMasteryCheckDateTime(String dateTime) throws ParseException {
        requireNonNull(dateTime);
        String[] parts = dateTime.trim().split("\\|", -1);
        if (parts.length != 2 || !parts[0].matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")
                || !parts[1].matches("[0-9]{2}:[0-9]{2}")) {
            throw new ParseException(MESSAGE_INVALID_MASTERY_CHECK_DATETIME_FORMAT);
        }

        LocalDate date;
        try {
            date = LocalDate.parse(parts[0], DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new ParseException(MESSAGE_INVALID_MASTERY_CHECK_DATE);
        }

        try {
            LocalTime time = LocalTime.parse(parts[1], MASTERY_CHECK_TIME_FORMATTER);
            return LocalDateTime.of(date, time);
        } catch (DateTimeParseException e) {
            throw new ParseException(MESSAGE_INVALID_MASTERY_CHECK_TIME);
        }
    }
}

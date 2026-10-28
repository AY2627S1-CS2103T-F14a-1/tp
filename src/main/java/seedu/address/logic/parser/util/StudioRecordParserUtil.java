package seedu.address.logic.parser.util;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Contains utility methods used for parsing strings in the various Studio Record Parser classes.
 */
public class StudioRecordParserUtil {

    public static final String MESSAGE_INVALID_EFFORT = "Effort score must be a non-negative unsigned integer up to 2147483647.";
    public static final String MESSAGE_INVALID_WEEK = "Week must be a value between 0 and 13.";

    /**
     * Parses a {@code String} into a {@code int} week value.
     * @throws ParseException if the specified index is invalid (not a non-negative unsigned integer).
     */
    public static int parseWeek(String week) throws ParseException {
        requireNonNull(week);
        String trimmedWeek = week.trim();

        if (!StringUtil.isNonNegativeUnsignedInteger(trimmedWeek)) {
            throw new ParseException(MESSAGE_INVALID_WEEK);
        }
        int weekNumber = Integer.parseInt(trimmedWeek);
        if (weekNumber < 0 || weekNumber > 13) {
            throw new ParseException(MESSAGE_INVALID_WEEK);
        }
        return weekNumber;

    }

    /**
     * Parses a {@code String} into a {@code int} effort score.
     * @throws ParseException if the specified effort is invalid (not a non-negative unsigned integer).
     */
    public static int parseEffortScore(String effort) throws ParseException {
        requireNonNull(effort);
        String trimmedEffort = effort.trim();
        if (!StringUtil.isNonNegativeUnsignedInteger(trimmedEffort)) {
            throw new ParseException(MESSAGE_INVALID_EFFORT);
        }
        return Integer.parseInt(trimmedEffort);
    }
}

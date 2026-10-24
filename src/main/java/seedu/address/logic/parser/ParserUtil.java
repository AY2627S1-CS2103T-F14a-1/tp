package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.mission.Mission;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.Email;
import seedu.address.model.student.Name;
import seedu.address.model.student.Phone;
import seedu.address.model.student.RegisteredMasteryChecks;
import seedu.address.model.student.StudioGroup;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {
    public static final String MESSAGE_INVALID_INDEX =
            "Index must be a positive integer without a sign, decimal point, or leading zero.";

    public static final String MESSAGE_INVALID_MISSION_NAME = "Provide a mission name using n/NAME.";
    public static final String MESSAGE_INVALID_MISSION_ID = MissionId.MESSAGE_CONSTRAINTS;
    public static final String MESSAGE_INVALID_MISSION_DEADLINE =
            "Deadline must use YYYY-MM-DD format, for example 2026-10-20.";
    public static final String MESSAGE_INVALID_MISSION_CALENDAR_DATE =
            "Deadline is not a valid calendar date. Month must be 01–12, "
                    + "and day must be valid for the selected month and year.";

    public static final String MESSAGE_INVALID_MASTERY_CHECK_DATETIME =
            "Mastery check appointment must be a valid date and 24-hour time in dd-MM|HH:mm format.";

    public static final String MESSAGE_INVALID_EFFORT = "Effort score must be a positive integer.";
    public static final String MESSAGE_INVALID_WEEK = "Week must be a positive integer.";

    /** Strict input formatter whose default year is captured when this class is initialized. */
    private static final DateTimeFormatter MASTERY_CHECK_DATETIME_FORMATTER =
            new DateTimeFormatterBuilder()
                    .appendPattern("dd-MM'|'HH:mm")
                    .parseDefaulting(ChronoField.YEAR, LocalDate.now().getYear())
                    .toFormatter(Locale.ROOT)
                    .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses a {@code String studioGroup} into a {@code StudioGroup}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code studioGroup} is invalid.
     */
    public static StudioGroup parseStudioGroup(String studioGroup) throws ParseException {
        requireNonNull(studioGroup);
        String trimmedStudioGroup = studioGroup.trim();
        if (!StudioGroup.isValidStudioGroup(trimmedStudioGroup)) {
            throw new ParseException(StudioGroup.MESSAGE_CONSTRAINTS);
        }
        return new StudioGroup(trimmedStudioGroup);
    }

    /**
     * Parses a mission name, collapsing whitespace while preserving punctuation and case.
     *
     * @param name Raw mission name.
     * @return Name with surrounding whitespace removed and internal whitespace collapsed.
     * @throws ParseException If the name is blank.
     */
    public static String parseMissionName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = Mission.normalizeName(name);
        if (trimmedName.isBlank()) {
            throw new ParseException(MESSAGE_INVALID_MISSION_NAME);
        }
        return trimmedName;
    }

    /**
     * Parses a case-insensitive mission ID into its canonical uppercase form.
     *
     * @param missionIdText Mission ID text.
     * @return The validated mission ID.
     * @throws NullPointerException If the text is null.
     * @throws ParseException If the mission ID is invalid.
     */
    public static MissionId parseMissionId(String missionIdText) throws ParseException {
        requireNonNull(missionIdText);

        try {
            return new MissionId(missionIdText.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ParseException(MESSAGE_INVALID_MISSION_ID);
        }
    }

    /**
     * Parses a strict calendar deadline, including past dates.
     *
     * @param deadline Raw date text in YYYY-MM-DD format.
     * @return Valid calendar date.
     * @throws ParseException If the syntax or calendar date is invalid.
     */
    public static LocalDate parseMissionDeadline(String deadline) throws ParseException {
        requireNonNull(deadline);
        String trimmedDeadline = deadline.strip();
        if (!trimmedDeadline.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            throw new ParseException(MESSAGE_INVALID_MISSION_DEADLINE);
        }
        try {
            return LocalDate.parse(trimmedDeadline);
        } catch (DateTimeParseException e) {
            throw new ParseException(MESSAGE_INVALID_MISSION_CALENDAR_DATE);
        }
    }

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
            throw new ParseException(RegisteredMasteryChecks.MESSAGE_CONSTRAINTS);
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

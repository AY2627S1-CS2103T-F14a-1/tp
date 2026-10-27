package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.mission.Mission;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.Email;
import seedu.address.model.student.Name;
import seedu.address.model.student.Note;
import seedu.address.model.student.Phone;
import seedu.address.model.student.RegisteredNotes;
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
    public static final String MESSAGE_INVALID_MISSION_STATUS =
            "Invalid status filter. Use s/overdue or omit s/.";

    public static final String MESSAGE_INVALID_EFFORT = "Effort score must be a positive integer.";
    public static final String MESSAGE_INVALID_WEEK = "Week must be a positive integer.";

    private static final String MISSION_STATUS_OVERDUE = "overdue";



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
     * Trims and validates a mission ID; {@link MissionId} normalizes its case.
     *
     * @param missionIdText Mission ID text.
     * @return The validated mission ID.
     * @throws NullPointerException If the text is null.
     * @throws ParseException If the mission ID is invalid.
     */
    public static MissionId parseMissionId(String missionIdText) throws ParseException {
        requireNonNull(missionIdText);

        try {
            return new MissionId(missionIdText.trim());
        } catch (IllegalArgumentException e) {
            throw new ParseException(MESSAGE_INVALID_MISSION_ID);
        }
    }

    /**
     * Parses the optional mission overdue filter.
     *
     * @param status Optional status value supplied by the user.
     * @return True if the overdue filter is supplied, or false if omitted.
     * @throws NullPointerException If the optional container is null.
     * @throws ParseException If a supplied value is not overdue.
     */
    public static boolean parseMissionOverdueFilter(Optional<String> status) throws ParseException {
        requireNonNull(status);

        if (status.isEmpty()) {
            return false;
        }
        if (!MISSION_STATUS_OVERDUE.equalsIgnoreCase(status.orElseThrow().strip())) {
            throw new ParseException(MESSAGE_INVALID_MISSION_STATUS);
        }
        return true;
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
     * Parses a {@code String note index} into a {@code Index} and returns it.
     *
     * @throws ParseException if the given {@code noteIndex} is invalid.
     */
    public static Index parseNoteIndex(String noteIndexStr) throws ParseException {
        requireNonNull(noteIndexStr);
        Index noteIndex = ParserUtil.parseIndex(noteIndexStr);
        if (!RegisteredNotes.isValidNoteIndex(noteIndex)) {
            throw new ParseException(RegisteredNotes.MESSAGE_CONSTRAINTS);
        }
        return noteIndex;
    }

    /**
     * Parses a {@code noteText} into a {@code Note}.
     *
     * @throws ParseException if the given {@code noteText} is invalid.
     */
    public static Note parseNoteText(String noteText) throws ParseException {
        requireNonNull(noteText);
        String trimmedNoteText = noteText.trim();
        if (!Note.isValidNoteText(trimmedNoteText)) {
            throw new ParseException(Note.MESSAGE_CONSTRAINTS);
        }
        return new Note(trimmedNoteText);
    }
}

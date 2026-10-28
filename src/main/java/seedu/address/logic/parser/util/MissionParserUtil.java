package seedu.address.logic.parser.util;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.mission.Mission;
import seedu.address.model.mission.MissionId;

/**
 * Contains utility methods used for parsing strings in the various Mission Parser classes.
 */
public final class MissionParserUtil {

    public static final String MESSAGE_INVALID_MISSION_NAME = "Provide a mission name using n/NAME.";
    public static final String MESSAGE_INVALID_MISSION_ID = MissionId.MESSAGE_CONSTRAINTS;
    public static final String MESSAGE_INVALID_MISSION_DEADLINE =
            "Deadline must use YYYY-MM-DD format, for example 2026-10-20.";
    public static final String MESSAGE_INVALID_MISSION_CALENDAR_DATE =
            "Deadline is not a valid calendar date. Month must be 01–12, "
                    + "and day must be valid for the selected month and year.";
    public static final String MESSAGE_INVALID_MISSION_STATUS =
            "Invalid status filter. Use s/overdue or omit s/.";

    private static final String MISSION_STATUS_OVERDUE = "overdue";

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

}

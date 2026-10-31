package seedu.address.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;
import seedu.address.model.mission.Mission;
import seedu.address.model.student.Student;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_MISSION_NOT_FOUND = "Mission %1$s does not exist.";
    public static final String MESSAGE_DUPLICATE_MISSION = "This mission already exists in the mission list.";

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND = "Invalid command format!";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n\n%1$s";
    public static final String MESSAGE_STUDENTS_LISTED_OVERVIEW = "%1$d student(s) listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code student} for display to the user.
     */
    public static String format(Student student) {
        final StringBuilder builder = new StringBuilder();
        builder.append(student.getName())
                .append("; Phone: ")
                .append(student.getPhone())
                .append("; Email: ")
                .append(student.getEmail())
                .append("; Tags: ");
        student.getTags().forEach(builder::append);
        return builder.toString();
    }

    /**
     * Formats mission details without a deadline-status label.
     *
     * @param mission Mission to format.
     * @return Mission ID, name, and deadline.
     */
    public static String format(Mission mission) {
        return format(mission, false);
    }

    /**
     * Formats mission details with an optional deadline-status label.
     *
     * @param mission Mission to format.
     * @param isPastDeadline Whether the mission's deadline has passed.
     * @return Mission ID, name, and deadline with the requested status label.
     */
    public static String format(Mission mission, boolean isPastDeadline) {
        String deadlineStatus = isPastDeadline ? " — Deadline passed" : "";

        return mission.getId() + " · " + mission.getName()
                + " (Due: " + mission.getDeadline() + deadlineStatus + ")";
    }

    /**
     * Formats the details of the given {@code student} for display to the user.
     *
     * @param student the student whose details are to be formatted
     * @return the formatted student details string
     */
    public static String formatStudentDetails(Student student) {
        return String.format(
                "Name: %s%n"
                        + "Phone: %s%n"
                        + "Email: %s%n"
                        + "Studio Group: %s",
                student.getName(),
                student.getPhone(),
                student.getEmail(),
                student.getStudioGroup());
    }

}

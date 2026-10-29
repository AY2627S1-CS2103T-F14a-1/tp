package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.masterycheck.MasteryCheck;
import seedu.address.model.student.Student;

/**
 * Lists upcoming mastery check appointments for students in the current
 * filtered student list, ordered chronologically.
 */
public class ListMasteryCheckCommand extends Command {

    public static final String COMMAND_WORD = "listmc";

    public static final String MESSAGE_NO_UPCOMING_MASTERY_CHECKS =
            "No upcoming mastery checks in the current student list.";

    public static final String MESSAGE_SUCCESS =
            "Listed all upcoming mastery checks as of %s:%n%s";

    public static final String MESSAGE_USAGE =
            COMMAND_WORD
                    + ": Lists upcoming mastery checks in chronological order.\n"
                    + "Only mastery checks after the current date and time "
                    + "are shown.\n"
                    + "Example: "
                    + COMMAND_WORD;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    private final Clock clock;

    /**
     * Creates a command using the system's default time zone and current clock.
     */
    public ListMasteryCheckCommand() {
        this(Clock.systemDefaultZone());
    }

    /**
     * Creates a command using the supplied clock.
     *
     * <p>The clock is injected so that the command can be tested with a fixed
     * current date and time.</p>
     *
     * @param clock clock used to determine the current date and time
     * @throws NullPointerException if the clock is null
     */
    ListMasteryCheckCommand(Clock clock) {
        this.clock = requireNonNull(clock);
    }

    /**
     * Lists mastery checks whose appointment time is after the current date
     * and time.
     *
     * <p>Only students in the model's current filtered list are considered.
     * The appointments are sorted by appointment time, followed by student
     * name and mastery-check number.</p>
     *
     * @param model model containing the currently filtered students
     * @return feedback containing the upcoming mastery checks, or a message if
     *         no upcoming mastery checks exist
     * @throws NullPointerException if the model is null
     */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        LocalDateTime today = LocalDateTime.now(clock);
        List<String> upcomingMasteryCheckRows = getUpcomingMasteryCheckRows(
                model.getFilteredStudentList(),
                today);

        if (upcomingMasteryCheckRows.isEmpty()) {
            return new CommandResult(MESSAGE_NO_UPCOMING_MASTERY_CHECKS);
        }

        return new CommandResult(
                String.format(
                        MESSAGE_SUCCESS,
                        today.format(DATE_TIME_FORMATTER),
                        String.join(
                                System.lineSeparator(),
                                upcomingMasteryCheckRows)));
    }

    /**
     * Collects and sorts upcoming mastery checks for the supplied students.
     *
     * @param lastShownList students whose mastery checks should be checked
     * @param now current date and time
     * @return formatted upcoming mastery-check rows in chronological order
     */
    private List<String> getUpcomingMasteryCheckRows(List<Student> lastShownList,
                                                     LocalDateTime now) {
        assert lastShownList != null;
        assert now != null;

        List<MasteryCheckRow> upcomingChecks = new ArrayList<>();

        for (int index = 0; index < lastShownList.size(); index++) {
            Student student = lastShownList.get(index);


            for (Map.Entry<Integer, MasteryCheck> entry
                    : student.getRegisteredMasteryChecks()
                            .getMasteryCheckAppointments()
                            .entrySet()) {

                MasteryCheck masteryCheck = entry.getValue();

                if (masteryCheck.getAppointmentTime().isAfter(now)) {
                    upcomingChecks.add(new MasteryCheckRow(
                            student,
                            entry.getKey(),
                            masteryCheck));
                }
            }
        }

        return upcomingChecks.stream()
                .sorted(Comparator
                        .comparing((MasteryCheckRow row) ->
                                row.masteryCheck()
                                        .getAppointmentTime())
                        .thenComparing(row -> row.student().getName().fullName)
                        .thenComparingInt(MasteryCheckRow::number))
                .map(this::formatRow)
                .toList();
    }

    /**
     * Formats one mastery-check appointment for display.
     *
     * @param row mastery-check row to format
     * @return formatted appointment row
     */
    private String formatRow(MasteryCheckRow row) {
        MasteryCheck check = row.masteryCheck();

        return String.format(
                "%d | %s | %s | %s",
                row.number(),
                row.student().getName().fullName,
                check.getAppointmentTime().format(DATE_TIME_FORMATTER),
                check.isComplete()
                        ? "Completed"
                        : "Not completed");
    }

    /**
     * Stores the information required to display one mastery-check appointment.
     *
     * @param student student with the appointment
     * @param number mastery-check registration number
     * @param masteryCheck appointment details
     */
    private record MasteryCheckRow(
            Student student,
            int number,
            MasteryCheck masteryCheck) {
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof ListMasteryCheckCommand otherCommand)) {
            return false;
        }

        return clock.equals(otherCommand.clock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clock);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("clock", clock)
                .toString();
    }
}

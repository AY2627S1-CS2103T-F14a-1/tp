package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_STUDENTS;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.mission.Mission;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.Student;

/**
 * Displays submission statuses for a selected mission, optionally filtering to overdue records.
 */
public class ViewMissionCommand extends Command {

    public static final String COMMAND_WORD = "viewmission";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists submission statuses for a mission across all students.\n"
            + "Parameters: " + PREFIX_MISSION + "MISSION_ID ["
            + PREFIX_STATUS + "overdue]\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_MISSION
            + "M03 " + PREFIX_STATUS + "overdue";

    public static final String MESSAGE_SUCCESS =
            "%1$s\n%2$d student(s) listed.\n%3$s\n%4$s";

    public static final String MESSAGE_NO_RESULTS = "No students match the query.";

    public static final String MESSAGE_MANUAL_RECORDS =
            "Results reflect manually maintained AvengerHub records.\n"
                    + "No recorded submission does not confirm that no submission was made.";

    private static final String STATUS_SUBMITTED = "Submitted";
    private static final String STATUS_NOT_SUBMITTED = "Not submitted";
    private static final String STATUS_OVERDUE = "Overdue";

    private final MissionId missionId;
    private final boolean overdueOnly;
    private final Clock clock;

    /**
     * Creates a command listing every student's status using the local date.
     *
     * @param missionId Mission whose submission statuses should be displayed.
     * @throws NullPointerException If the mission ID is null.
     */
    public ViewMissionCommand(MissionId missionId) {
        this(missionId, false);
    }

    /**
     * Creates a command using the computer's local date.
     *
     * @param missionId Mission whose submission statuses should be displayed.
     * @param overdueOnly Whether to show only missing submissions past the deadline.
     * @throws NullPointerException If the mission ID is null.
     */
    public ViewMissionCommand(MissionId missionId, boolean overdueOnly) {
        this(missionId, overdueOnly, Clock.systemDefaultZone());
    }

    /**
     * Creates a command using a supplied date source.
     *
     * @param missionId Mission whose submission statuses should be displayed.
     * @param overdueOnly Whether to show only missing submissions past the deadline.
     * @param clock Clock supplying the evaluation date and time zone.
     * @throws NullPointerException If the mission ID or clock is null.
     */
    ViewMissionCommand(MissionId missionId, boolean overdueOnly, Clock clock) {
        this.missionId = requireNonNull(missionId);
        this.overdueOnly = overdueOnly;
        this.clock = requireNonNull(clock);
    }

    /**
     * Displays statuses across all students without modifying submission records.
     *
     * @param model Model containing students and configured missions.
     * @return Mission details, result count, and numbered submission statuses.
     * @throws CommandException If the mission does not exist.
     * @throws NullPointerException If the model is null.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Mission mission = model.findMission(missionId).orElseThrow(() ->
                new CommandException(String.format(Messages.MESSAGE_MISSION_NOT_FOUND, missionId)));
        LocalDate today = LocalDate.now(clock);
        boolean isPastDeadline = mission.isPastDeadline(today);

        if (overdueOnly) {
            model.updateFilteredStudentList(student -> isPastDeadline
                    && !student.getMissionSubmissions().hasSubmission(missionId));
        } else {
            model.updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);
        }
        List<Student> displayedStudents = model.getFilteredStudentList();
        String rows = formatRows(displayedStudents, isPastDeadline);

        return new CommandResult(String.format(MESSAGE_SUCCESS,
                Messages.format(mission), displayedStudents.size(), rows, MESSAGE_MANUAL_RECORDS));
    }

    /**
     * Formats statuses using the displayed student indexes.
     *
     * @param students Students in displayed order.
     * @param isPastDeadline Whether the selected mission's deadline has passed.
     * @return Numbered status rows, or an explanation when no students match.
     */
    private String formatRows(List<Student> students, boolean isPastDeadline) {
        assert students != null;

        if (students.isEmpty()) {
            return MESSAGE_NO_RESULTS;
        }

        List<String> rows = new ArrayList<>();
        for (int index = 0; index < students.size(); index++) {
            Student student = students.get(index);
            rows.add((index + 1) + ". " + student.getName()
                    + " - " + getStatus(student, isPastDeadline));
        }
        return String.join("\n", rows);
    }

    /**
     * Determines a student's status from recorded submissions and the deadline.
     *
     * @param student Student whose submission should be checked.
     * @param isPastDeadline Whether the selected mission's deadline has passed.
     * @return The recorded or derived submission status.
     */
    private String getStatus(Student student, boolean isPastDeadline) {
        assert student != null;

        if (student.getMissionSubmissions().hasSubmission(missionId)) {
            return STATUS_SUBMITTED;
        }
        return isPastDeadline ? STATUS_OVERDUE : STATUS_NOT_SUBMITTED;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof ViewMissionCommand otherCommand
                && missionId.equals(otherCommand.missionId)
                && overdueOnly == otherCommand.overdueOnly
                && clock.equals(otherCommand.clock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(missionId, overdueOnly, clock);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("missionId", missionId)
                .add("overdueOnly", overdueOnly)
                .add("clock", clock)
                .toString();
    }
}

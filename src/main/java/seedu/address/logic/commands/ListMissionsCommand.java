package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;

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
import seedu.address.model.mission.MissionSubmissionStatus;
import seedu.address.model.student.Student;

/**
 * Lists mission submission statuses for the currently displayed students.
 */
public class ListMissionsCommand extends Command {

    public static final String COMMAND_WORD = "listmissions";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists mission summaries or student submission statuses.\n"
            + "Parameters: [" + PREFIX_MISSION + "MISSION_ID]\n"
            + "Without parameters: lists all missions with submission counts.\n"
            + "With a mission ID: lists statuses within the current student list.\n"
            + "Examples: " + COMMAND_WORD + "; " + COMMAND_WORD + " " + PREFIX_MISSION + "M01";

    public static final String MESSAGE_SUCCESS =
            "Submission statuses for %1$s (current student list):\n%2$s";
    public static final String MESSAGE_NO_STUDENTS =
            "No students in the current student list.";

    private final MissionId missionId;
    private final Clock clock;

    /**
     * Creates a command using the computer's local calendar date.
     *
     * @param missionId Mission whose statuses should be listed.
     * @throws NullPointerException If the mission ID is null.
     */
    public ListMissionsCommand(MissionId missionId) {
        this(missionId, Clock.systemDefaultZone());
    }

    /**
     * Creates a command using a supplied date source.
     *
     * @param missionId Mission whose statuses should be listed.
     * @param clock Clock supplying the current date and time zone.
     * @throws NullPointerException If either argument is null.
     */
    ListMissionsCommand(MissionId missionId, Clock clock) {
        this.missionId = requireNonNull(missionId);
        this.clock = requireNonNull(clock);
    }

    /**
     * Lists displayed students' statuses without changing records or filters.
     *
     * @param model Model containing the mission and displayed students.
     * @return Mission details and numbered student submission statuses.
     * @throws CommandException If the mission does not exist.
     * @throws NullPointerException If the model is null.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Mission mission = model.findMission(missionId).orElseThrow(() ->
                new CommandException(String.format(Messages.MESSAGE_MISSION_NOT_FOUND, missionId)));
        boolean isPastDeadline = mission.isPastDeadline(LocalDate.now(clock));
        String rows = formatStudentStatuses(model.getFilteredStudentList(), isPastDeadline);

        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(mission), rows));
    }

    /**
     * Formats each student's status while preserving displayed indexes.
     *
     * @param students Students in their displayed order.
     * @param isPastDeadline Whether the selected mission's deadline has passed.
     * @return Numbered status rows, or an explanation for an empty student list.
     */
    private String formatStudentStatuses(List<Student> students, boolean isPastDeadline) {
        assert students != null;

        if (students.isEmpty()) {
            return MESSAGE_NO_STUDENTS;
        }

        List<String> rows = new ArrayList<>();
        for (int index = 0; index < students.size(); index++) {
            Student student = students.get(index);
            MissionSubmissionStatus status = MissionSubmissionStatus.from(
                    student.getMissionSubmissions().hasSubmission(missionId), isPastDeadline);
            rows.add((index + 1) + ". " + student.getName()
                    + " | Studio group: " + student.getStudioGroup()
                    + " | " + status);
        }
        return String.join("\n", rows);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ListMissionsCommand otherCommand)) {
            return false;
        }
        return missionId.equals(otherCommand.missionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(missionId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("missionId", missionId)
                .toString();
    }
}

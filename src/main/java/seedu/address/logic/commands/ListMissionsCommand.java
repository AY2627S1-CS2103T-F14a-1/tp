package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
            + "Parameters: [" + PREFIX_MISSION + "MISSION_ID] ["
            + PREFIX_STATUS + "STATUS]\n"
            + "Without parameters: lists all missions with submission counts.\n"
            + "STATUS must be submitted or overdue.\n"
            + "Omit m/ to apply the status filter across all missions.\n"
            + "Examples: " + COMMAND_WORD + " " + PREFIX_MISSION + "M01 "
            + PREFIX_STATUS + "submitted; " + COMMAND_WORD + " " + PREFIX_STATUS + "overdue";

    public static final String MESSAGE_SUCCESS =
            "Submission statuses for %1$s (current student list):\n%2$s";
    public static final String MESSAGE_NO_STUDENTS =
            "No students in the current student list.";
    public static final String MESSAGE_NO_MATCHES =
            "No submissions match the selected status.";

    /** Selected mission, or empty for a query across all missions. */
    private final Optional<MissionId> missionId;
    private final Clock clock;
    private final Optional<MissionSubmissionStatus> statusFilter;

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
     * Creates an unfiltered status query using a supplied date source.
     *
     * @param missionId Mission whose statuses should be listed.
     * @param clock Clock supplying the current date and time zone.
     * @throws NullPointerException If either argument is null.
     */
    ListMissionsCommand(MissionId missionId, Clock clock) {
        this(missionId, clock, Optional.empty());
    }

    /**
     * Creates a single-mission query with an optional status filter.
     *
     * @param missionId Mission whose statuses should be listed.
     * @param clock Clock supplying the current date and time zone.
     * @param statusFilter Required status, or empty to include every status.
     * @throws NullPointerException If any argument is null.
     */
    ListMissionsCommand(MissionId missionId, Clock clock,
                        Optional<MissionSubmissionStatus> statusFilter) {
        this(Optional.of(missionId), clock, statusFilter);
    }

    /**
     * Creates a query with an optional mission and status filter.
     *
     * @param missionId Selected mission, or empty to query all missions.
     * @param clock Clock supplying the current date and time zone.
     * @param statusFilter Required status, or empty to include every status.
     * @throws NullPointerException If any argument is null.
     */
    private ListMissionsCommand(Optional<MissionId> missionId, Clock clock,
                                Optional<MissionSubmissionStatus> statusFilter) {
        this.missionId = requireNonNull(missionId);
        this.clock = requireNonNull(clock);
        this.statusFilter = requireNonNull(statusFilter);
    }

    /**
     * Creates a filtered query using the computer's local calendar date.
     *
     * @param missionId Mission whose statuses should be listed.
     * @param status Required submission status.
     * @return Command listing students with the requested status.
     * @throws NullPointerException If either argument is null.
     */
    public static ListMissionsCommand withStatus(MissionId missionId, MissionSubmissionStatus status) {
        return new ListMissionsCommand(
                missionId, Clock.systemDefaultZone(), Optional.of(status));
    }

    /**
     * Creates a status-filtered query across all missions using the local date.
     *
     * @param status Required submission status.
     * @return Command querying the selected status across all missions.
     * @throws NullPointerException If the status is null.
     */
    public static ListMissionsCommand withStatus(MissionSubmissionStatus status) {
        return withStatus(status, Clock.systemDefaultZone());
    }

    /**
     * Creates a status-filtered query across all missions using a supplied clock.
     *
     * @param status Required submission status.
     * @param clock Clock supplying the current date and time zone.
     * @return Command querying the selected status across all missions.
     * @throws NullPointerException If either argument is null.
     */
    static ListMissionsCommand withStatus(MissionSubmissionStatus status, Clock clock) {
        return new ListMissionsCommand(Optional.empty(), clock, Optional.of(status));
    }

    /**
     * Lists matching statuses without changing records or student filters.
     *
     * @param model Model containing missions and displayed students.
     * @return Mission details and matching student submission statuses.
     * @throws CommandException If a specified mission does not exist.
     * @throws NullPointerException If the model is null.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Mission> missions = getSelectedMissions(model);
        if (missions.isEmpty()) {
            return new CommandResult(ListMissionCatalogueCommand.MESSAGE_NO_MISSIONS);
        }

        String scope = missionId.isPresent() ? Messages.format(missions.getFirst()) : "all missions";
        String rows = formatRows(model.getFilteredStudentList(), missions, LocalDate.now(clock));

        return new CommandResult(String.format(MESSAGE_SUCCESS, scope, rows));
    }

    /**
     * Resolves one mission or obtains all missions in numeric ID order.
     *
     * @param model Model containing the mission catalogue.
     * @return Selected missions in report order.
     * @throws CommandException If a specified mission does not exist.
     */
    private List<Mission> getSelectedMissions(Model model) throws CommandException {
        assert model != null;

        if (missionId.isPresent()) {
            MissionId selectedId = missionId.orElseThrow();
            Mission mission = model.findMission(selectedId).orElseThrow(() ->
                    new CommandException(String.format(Messages.MESSAGE_MISSION_NOT_FOUND, selectedId)));
            return List.of(mission);
        }

        return model.getMissionList().stream()
                .sorted(Comparator.comparingLong((Mission mission) -> mission.getId().getNumber()))
                .toList();
    }

    /**
     * Combines matching rows in mission order while handling empty results.
     *
     * @param students Students in displayed order.
     * @param missions Missions in report order.
     * @param today Local date used to evaluate deadlines.
     * @return Matching rows or an explanation when no results exist.
     */
    private String formatRows(List<Student> students, List<Mission> missions, LocalDate today) {
        assert students != null;
        assert missions != null;
        assert today != null;

        if (students.isEmpty()) {
            return MESSAGE_NO_STUDENTS;
        }

        List<String> rows = new ArrayList<>();
        for (Mission mission : missions) {
            rows.addAll(getStudentStatusRows(students, mission, today));
        }
        return rows.isEmpty() ? MESSAGE_NO_MATCHES : String.join("\n", rows);
    }

    /**
     * Builds matching rows for one mission using displayed student indexes.
     *
     * @param students Students in displayed order.
     * @param mission Mission whose submissions are checked.
     * @param today Local date used to evaluate the deadline.
     * @return Matching rows, including mission details for all-mission queries.
     */
    private List<String> getStudentStatusRows(List<Student> students, Mission mission, LocalDate today) {
        assert students != null;
        assert mission != null;
        assert today != null;

        boolean isPastDeadline = mission.isPastDeadline(today);
        String missionDetails = missionId.isEmpty() ? " | " + Messages.format(mission) : "";
        List<String> rows = new ArrayList<>();
        for (int index = 0; index < students.size(); index++) {
            Student student = students.get(index);
            MissionSubmissionStatus status = MissionSubmissionStatus.from(
                    student.getMissionSubmissions().hasSubmission(mission.getId()), isPastDeadline);
            if (statusFilter.isPresent() && statusFilter.orElseThrow() != status) {
                continue;
            }

            rows.add((index + 1) + ". " + student.getName()
                    + " | Studio group: " + student.getStudioGroup()
                    + missionDetails + " | " + status);
        }
        return rows;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ListMissionsCommand otherCommand)) {
            return false;
        }
        return missionId.equals(otherCommand.missionId)
                && statusFilter.equals(otherCommand.statusFilter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(missionId, statusFilter);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("missionId", missionId)
                .add("statusFilter", statusFilter)
                .toString();
    }
}

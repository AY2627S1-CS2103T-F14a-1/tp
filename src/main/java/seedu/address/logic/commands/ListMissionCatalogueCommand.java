package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.mission.Mission;
import seedu.address.model.student.Student;

/**
 * Lists configured missions without changing student filters or records.
 */
public class ListMissionCatalogueCommand extends Command {

    public static final String MESSAGE_SUCCESS =
            "All missions (submissions in current student list):\n%1$s";
    public static final String MESSAGE_NO_MISSIONS = "No missions configured.";

    private final Clock clock;

    /**
     * Creates a catalogue command using the computer's local calendar date.
     */
    public ListMissionCatalogueCommand() {
        this(Clock.systemDefaultZone());
    }

    /**
     * Creates a catalogue command using a supplied date source.
     *
     * @param clock Clock supplying the current date and time zone.
     * @throws NullPointerException If the clock is null.
     */
    ListMissionCatalogueCommand(Clock clock) {
        this.clock = requireNonNull(clock);
    }

    /**
     * Lists all configured missions in ascending numeric ID order.
     *
     * @param model Model containing the mission catalogue.
     * @return Mission details and submission counts, or feedback for an empty catalogue.
     * @throws NullPointerException If the model is null.
     */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        LocalDate today = LocalDate.now(clock);
        List<Student> students = model.getFilteredStudentList();

        String missionRows = model.getMissionList().stream()
                .sorted(Comparator.comparingLong((Mission mission) -> mission.getId().getNumber()))
                .map(mission -> formatMissionSummary(mission, students, today))
                .collect(Collectors.joining("\n"));

        if (missionRows.isEmpty()) {
            return new CommandResult(MESSAGE_NO_MISSIONS);
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS, missionRows));
    }

    /**
     * Formats mission details, deadline status, and filtered submission counts.
     *
     * @param mission Mission whose submissions are counted.
     * @param students Students in the current filtered list.
     * @param today Local date used to evaluate the mission deadline.
     * @return Mission summary including submitted and total student counts.
     */
    private String formatMissionSummary(Mission mission, List<Student> students, LocalDate today) {
        assert mission != null;
        assert students != null;
        assert today != null;

        long submittedCount = students.stream()
                .filter(student -> student.getMissionSubmissions().hasSubmission(mission.getId()))
                .count();

        return Messages.format(mission, mission.isPastDeadline(today))
                + " | Submitted: " + submittedCount + "/" + students.size();
    }
}

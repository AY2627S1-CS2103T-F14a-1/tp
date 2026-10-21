package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.mission.Mission;
import seedu.address.model.student.Student;

/**
 * Lists missing submissions whose mission deadlines have passed.
 */
public class ListOverdueCommand extends Command {

    public static final String COMMAND_WORD = "listoverdue";

    public static final String MESSAGE_SUCCESS =
            "Overdue submissions as of %1$s (current student list):\n%2$s";
    public static final String MESSAGE_NO_OVERDUE =
            "No overdue submissions in the current student list.";

    private final Clock clock;

    /**
     * Creates a command using the computer's local calendar date.
     */
    public ListOverdueCommand() {
        this(Clock.systemDefaultZone());
    }

    /**
     * Creates a command using a supplied date source.
     *
     * @param clock Clock supplying the current date and time zone.
     * @throws NullPointerException If the clock is null.
     */
    ListOverdueCommand(Clock clock) {
        this.clock = requireNonNull(clock);
    }

    /**
     * Lists overdue submissions without changing records or student filters.
     *
     * @param model Model containing missions and currently displayed students.
     * @return Feedback listing missing submissions after their deadlines.
     * @throws NullPointerException If the model is null.
     */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        LocalDate today = LocalDate.now(clock);
        List<Mission> overdueMissions = model.getMissionList().stream()
                .filter(mission -> mission.isPastDeadline(today))
                .sorted(Comparator.comparingLong(mission -> mission.getId().getNumber()))
                .toList();
        List<String> overdueRows = getOverdueRows(
                model.getFilteredStudentList(), overdueMissions);

        if (overdueRows.isEmpty()) {
            return new CommandResult(MESSAGE_NO_OVERDUE);
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS,
                today, String.join("\n", overdueRows)));
    }

    /**
     * Formats missing submissions in displayed-student and mission order.
     *
     * @param lastShownList Students in their displayed order.
     * @param overdueMissions Missions already determined to be overdue.
     * @return One row for each missing student-mission submission.
     */
    private List<String> getOverdueRows(List<Student> lastShownList,
                                        List<Mission> overdueMissions) {
        assert lastShownList != null;
        assert overdueMissions != null;

        List<String> overdueRows = new ArrayList<>();
        for (int index = 0; index < lastShownList.size(); index++) {
            Student student = lastShownList.get(index);
            for (Mission mission : overdueMissions) {
                if (!student.getMissionSubmissions().hasSubmission(mission.getId())) {
                    overdueRows.add((index + 1) + ". " + student.getName()
                            + " | " + Messages.format(mission));
                }
            }
        }
        return overdueRows;
    }
}

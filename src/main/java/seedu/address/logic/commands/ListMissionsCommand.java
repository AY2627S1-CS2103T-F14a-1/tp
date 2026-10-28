package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;

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
 * Lists students in the currently displayed list who submitted a mission.
 */
public class ListMissionsCommand extends Command {

    public static final String COMMAND_WORD = "listmissions";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists submitted students within the current student list.\n"
            + "Parameters: " + PREFIX_MISSION + "MISSION_ID\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_MISSION + "M01";

    public static final String MESSAGE_SUCCESS =
            "Submissions for %1$s (current student list):\n%2$s";
    public static final String MESSAGE_NO_SUBMISSIONS =
            "No submissions recorded for %1$s in the current student list.";

    private final MissionId missionId;

    /**
     * Creates a command to list submissions for a mission.
     *
     * @param missionId Mission whose submitted students should be listed.
     * @throws NullPointerException If the mission ID is null.
     */
    public ListMissionsCommand(MissionId missionId) {
        this.missionId = requireNonNull(missionId);
    }

    /**
     * Lists submitted students without modifying records or the student filter.
     *
     * @param model Model containing the mission and displayed students.
     * @return Feedback retaining the displayed student indexes.
     * @throws CommandException If the mission does not exist.
     * @throws NullPointerException If the model is null.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Mission mission = model.findMission(missionId).orElseThrow(() ->
                new CommandException(String.format(Messages.MESSAGE_MISSION_NOT_FOUND, missionId)));
        List<Student> lastShownList = model.getFilteredStudentList();
        List<String> submissionRows = new ArrayList<>();

        for (int index = 0; index < lastShownList.size(); index++) {
            Student student = lastShownList.get(index);
            if (student.getMissionSubmissions().hasSubmission(missionId)) {
                submissionRows.add((index + 1) + ". " + student.getName());
            }
        }

        if (submissionRows.isEmpty()) {
            return new CommandResult(String.format(MESSAGE_NO_SUBMISSIONS, Messages.format(mission)));
        }

        return new CommandResult(String.format(MESSAGE_SUCCESS,
                Messages.format(mission), String.join("\n", submissionRows)));
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

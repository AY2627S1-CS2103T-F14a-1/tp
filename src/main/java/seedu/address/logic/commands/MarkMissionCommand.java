package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.MissionSubmissions;
import seedu.address.model.student.Student;

/**
 * Records a mission submission for a student identified by a displayed index.
 */
public class MarkMissionCommand extends Command {

    public static final String COMMAND_WORD = "markmission";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Records a mission submission for the selected student.\n"
            + "Parameters: INDEX m/MISSION_ID\n"
            + "INDEX must be a positive integer identifying a displayed student.\n"
            + MissionId.MESSAGE_CONSTRAINTS + "\n"
            + "Example: " + COMMAND_WORD + " 1 m/M05";

    public static final String MESSAGE_SUCCESS =
            "Added %1$s’s mission submission for mission %2$s.";

    public static final String MESSAGE_ALREADY_RECORDED =
            "%1$s’s mission submission has already been recorded for mission %2$s. No changes made.";

    private static final Logger logger = LogsCenter.getLogger(MarkMissionCommand.class);

    private final Index targetIndex;
    private final MissionId missionId;

    /**
     * Creates a command to record a mission submission.
     *
     * @param targetIndex Index of the student in the displayed list.
     * @param missionId Configured mission identifier.
     * @throws NullPointerException If the index or mission ID is null.
     */
    public MarkMissionCommand(Index targetIndex, MissionId missionId) {
        this.targetIndex = requireNonNull(targetIndex);
        this.missionId = requireNonNull(missionId);
    }

    /**
     * Records the submission without changing the current list filter.
     *
     * @param model Model containing the students.
     * @return Feedback describing the addition or an existing submission.
     * @throws CommandException If the index is invalid or the mission does not exist.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.findMission(missionId).isEmpty()) {
            logger.fine("MarkMissionCommand rejected: mission " + missionId + " does not exist.");
            throw new CommandException(String.format(Messages.MESSAGE_MISSION_NOT_FOUND, missionId));
        }

        Student studentToUpdate = getStudentAtDisplayedIndex(model, targetIndex);
        MissionSubmissions submissions = studentToUpdate.getMissionSubmissions();

        if (submissions.hasSubmission(missionId)) {
            logger.fine("MarkMissionCommand made no changes for mission " + missionId
                    + " for student " + studentToUpdate.getName()
                    + " at displayed student index " + targetIndex.getOneBased() + ".");
            return new CommandResult(String.format(MESSAGE_ALREADY_RECORDED, studentToUpdate.getName(), missionId));
        }

        Student updatedStudent = studentToUpdate.withMissionSubmissions(submissions.withSubmission(missionId));
        model.setStudent(studentToUpdate, updatedStudent);
        logger.info("Recorded submission for mission " + missionId
                + " for student " + studentToUpdate.getName()
                + " at displayed student index " + targetIndex.getOneBased() + " in memory.");
        return new CommandResult(String.format(MESSAGE_SUCCESS, studentToUpdate.getName(), missionId));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof MarkMissionCommand otherMissionCommand)) {
            return false;
        }

        return targetIndex.equals(otherMissionCommand.targetIndex)
                && missionId.equals(otherMissionCommand.missionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetIndex.getZeroBased(), missionId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("missionId", missionId)
                .toString();
    }
}

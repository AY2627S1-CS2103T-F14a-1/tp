package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;

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
 * Removes a mission submission for a student identified by a displayed index.
 */
public class UnmarkMissionCommand extends Command {

    public static final String COMMAND_WORD = "unmarkmission";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes record of a mission submission for the selected student.\n"
            + "Parameters: " + PREFIX_INDEX + "INDEX " + PREFIX_MISSION + "MISSION_ID\n"
            + "INDEX must be a positive integer identifying a displayed student.\n"
            + MissionId.MESSAGE_CONSTRAINTS + "\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_INDEX + "1 " + PREFIX_MISSION + "M05";

    public static final String MESSAGE_SUCCESS =
            "Removed %1$s's mission submission for mission %2$s.";

    public static final String MESSAGE_NOT_RECORDED =
            "%1$s's mission submission was already not recorded for mission %2$s. No changes made.";

    private static final Logger logger = LogsCenter.getLogger(UnmarkMissionCommand.class);

    private final Index targetIndex;
    private final MissionId missionId;

    /**
     * Creates a command to remove a mission submission.
     *
     * @param targetIndex Index of the student in the displayed list.
     * @param missionId Configured mission identifier.
     * @throws NullPointerException If the index or mission ID is null.
     */
    public UnmarkMissionCommand(Index targetIndex, MissionId missionId) {
        this.targetIndex = requireNonNull(targetIndex);
        this.missionId = requireNonNull(missionId);
    }

    /**
     * Removes the submission without changing the current list filter.
     *
     * @param model Model containing the students.
     * @return Feedback describing the removal or an absent submission.
     * @throws CommandException If the index is invalid or the mission does not exist.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.findMission(missionId).isEmpty()) {
            logger.fine("UnmarkMissionCommand rejected: mission " + missionId + " does not exist.");
            throw new CommandException(String.format(Messages.MESSAGE_MISSION_NOT_FOUND, missionId));
        }

        Student studentToUpdate;
        try {
            studentToUpdate = getStudentAtDisplayedIndex(model, targetIndex);
        } catch (CommandException e) {
            throw new CommandException(e.getMessage() + "\n\n" + MESSAGE_USAGE);
        }
        MissionSubmissions submissions = studentToUpdate.getMissionSubmissions();

        if (!submissions.hasSubmission(missionId)) {
            logger.fine("UnmarkMissionCommand made no changes for mission " + missionId
                    + " for student " + studentToUpdate.getName()
                    + " at displayed student index " + targetIndex.getOneBased() + ".");
            return new CommandResult(String.format(MESSAGE_NOT_RECORDED, studentToUpdate.getName(), missionId));
        }

        Student updatedStudent = studentToUpdate.withMissionSubmissions(submissions.withoutSubmission(missionId));
        model.setStudent(studentToUpdate, updatedStudent);
        logger.info("Removed submission for mission " + missionId
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
        if (!(other instanceof UnmarkMissionCommand otherMissionCommand)) {
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

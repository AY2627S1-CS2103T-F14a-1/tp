package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;

import java.util.Objects;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.mission.Mission;
import seedu.address.model.mission.MissionId;

/** Deletes a shared mission and its submission records using a stable mission ID. */
public class DeleteMissionCommand extends Command {

    public static final String COMMAND_WORD = "delmission";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a mission by its ID, not its displayed position.\n"
            + "Parameters: " + PREFIX_MISSION + "MISSION_ID\n"
            + MissionId.MESSAGE_CONSTRAINTS + "\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_MISSION + "M03";

    public static final String MESSAGE_SUCCESS = "Deleted mission: %1$s.";

    private static final Logger logger = LogsCenter.getLogger(DeleteMissionCommand.class);

    private final MissionId missionId;

    /**
     * Creates a request without checking whether the mission exists.
     *
     * @param missionId Validated canonical mission ID.
     * @throws NullPointerException If the ID is null.
     */
    public DeleteMissionCommand(MissionId missionId) {
        this.missionId = requireNonNull(missionId);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (model.findMission(missionId).isEmpty()) {
            logger.fine("DeleteMissionCommand rejected: mission " + missionId + " does not exist.");
            throw new CommandException(String.format(Messages.MESSAGE_MISSION_NOT_FOUND, missionId));
        }

        Mission deleted = model.deleteMission(missionId);
        logger.info("Deleted mission " + missionId + " and its submission records from the in-memory address book.");
        return new CommandResult(
                String.format(
                        MESSAGE_SUCCESS,
                        Messages.format(deleted)),
                null,
                model.getMissionList(),
                true);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteMissionCommand otherCommand)) {
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
        return new ToStringBuilder(this).add("missionId", missionId).toString();
    }
}

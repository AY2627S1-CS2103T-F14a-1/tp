package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.time.LocalDate;
import java.util.Objects;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.mission.Mission;
import seedu.address.model.mission.MissionId;

/** Adds a shared mission with a user-selected stable ID. */
public class AddMissionCommand extends Command {

    public static final String COMMAND_WORD = "addmission";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a mission shared across students.\n"
            + "Parameters: " + PREFIX_MISSION + "MISSION_ID " + PREFIX_NAME + "NAME " + PREFIX_DATE + "DEADLINE\n"
            + MissionId.MESSAGE_CONSTRAINTS + "\n"
            + "DEADLINE must be a real date in YYYY-MM-DD format.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_MISSION + "M01 " + PREFIX_NAME + "Rune Reading "
            + PREFIX_DATE + "2026-10-20";
    public static final String MESSAGE_SUCCESS = "New mission added: %1$s.";
    public static final String MESSAGE_DUPLICATE_ID = "A mission with this ID already exists.";

    private static final Logger logger = LogsCenter.getLogger(AddMissionCommand.class);

    private final MissionId missionId;
    private final String name;
    private final LocalDate deadline;

    /**
     * Creates a command containing parsed mission details.
     *
     * @param missionId Validated mission ID.
     * @param name Validated non-blank mission name.
     * @param deadline Validated deadline, which may be in the past.
     * @throws NullPointerException If any argument is null.
     */
    public AddMissionCommand(MissionId missionId, String name, LocalDate deadline) {
        this.missionId = requireNonNull(missionId);
        this.name = requireNonNull(name);
        this.deadline = requireNonNull(deadline);
    }

    /**
     * Adds the mission after checking for duplicate IDs and names.
     *
     * @param model Model containing the catalogue.
     * @return Feedback identifying the new mission.
     * @throws NullPointerException If the model is null.
     * @throws CommandException If the ID or name is duplicated.
     * @throws IllegalArgumentException If the supplied name is blank.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (model.findMission(missionId).isPresent()) {
            logger.fine("Mission addition rejected: duplicate ID " + missionId + ".");
            throw new CommandException(MESSAGE_DUPLICATE_ID);
        }
        if (model.hasMissionName(name)) {
            logger.fine("Mission addition rejected: duplicate name.");
            throw new CommandException(Messages.MESSAGE_DUPLICATE_MISSION);
        }
        Mission added = model.addMission(missionId, name, deadline);
        logger.info("Added mission " + added.getId() + " to the in-memory catalogue.");
        return new CommandResult(
                String.format(
                        MESSAGE_SUCCESS,
                        Messages.format(added)),
                null,
                model.getMissionList(),
                true);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AddMissionCommand otherAddMissionCommand)) {
            return false;
        }
        return missionId.equals(otherAddMissionCommand.missionId)
                && name.equals(otherAddMissionCommand.name)
                && deadline.equals(otherAddMissionCommand.deadline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(missionId, name, deadline);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("missionId", missionId).add("name", name)
                .add("deadline", deadline).toString();
    }
}

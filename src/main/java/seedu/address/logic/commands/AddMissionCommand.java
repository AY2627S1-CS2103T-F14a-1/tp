package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.time.LocalDate;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.mission.Mission;

/** Adds a shared mission, allocating its ID only when the command executes. */
public class AddMissionCommand extends Command {

    public static final String COMMAND_WORD = "addmission";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a mission shared across students.\n"
            + "Parameters: " + PREFIX_NAME + "NAME " + PREFIX_DATE + "DEADLINE\n"
            + "DEADLINE must be a real date in YYYY-MM-DD format.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_NAME + "Rune Reading " + PREFIX_DATE + "2026-10-20.";
    public static final String MESSAGE_SUCCESS = "New mission added: %1$s.";
    public static final String MESSAGE_DUPLICATE_MISSION = "This mission already exists in the mission list.";

    private static final Logger logger = LogsCenter.getLogger(AddMissionCommand.class);

    private final String name;
    private final LocalDate deadline;

    /**
     * Creates a command containing parsed mission details without allocating an ID.
     *
     * @param name Validated non-blank mission name.
     * @param deadline Validated deadline, which may be in the past.
     */
    public AddMissionCommand(String name, LocalDate deadline) {
        this.name = requireNonNull(name);
        this.deadline = requireNonNull(deadline);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (model.hasMissionName(name)) {
            logger.fine("Mission addition rejected: duplicate name.");
            throw new CommandException(MESSAGE_DUPLICATE_MISSION);
        }
        Mission added = model.addMission(name, deadline);
        logger.info("Added mission " + added.getId() + " to the in-memory catalogue.");
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(added)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AddMissionCommand otherAddMissionCommand)) {
            return false;
        }
        return name.equals(otherAddMissionCommand.name) && deadline.equals(otherAddMissionCommand.deadline);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("name", name).add("deadline", deadline).toString();
    }
}

package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.mission.Mission;
import seedu.address.model.mission.MissionId;

/**
 * Edits a shared mission while preserving its ID and submission records.
 */
public class EditMissionCommand extends Command {

    public static final String COMMAND_WORD = "editmission";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits a shared mission's name or deadline.\n"
            + "Parameters: " + PREFIX_MISSION + "MISSION_ID "
            + "[" + PREFIX_NAME + "NAME] [" + PREFIX_DATE + "DEADLINE]\n"
            + "Provide at least one name or deadline.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_MISSION + "M01 "
            + PREFIX_NAME + "Rune Reading " + PREFIX_DATE + "2026-10-22";

    public static final String MESSAGE_EDIT_MISSION_SUCCESS = "Edited mission: %1$s.";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_NO_CHANGES = "Mission %1$s is unchanged. No changes made.";

    private static final Logger logger = LogsCenter.getLogger(EditMissionCommand.class);

    private final MissionId missionId;
    private final EditMissionDescriptor editMissionDescriptor;

    /**
     * Creates a command containing a defensive copy of the requested edits.
     *
     * @param missionId Mission to edit.
     * @param editMissionDescriptor Validated replacement details.
     * @throws NullPointerException If either argument is null.
     * @throws IllegalArgumentException If no replacement field is supplied.
     */
    public EditMissionCommand(MissionId missionId, EditMissionDescriptor editMissionDescriptor) {
        this.missionId = requireNonNull(missionId);
        requireNonNull(editMissionDescriptor);
        checkArgument(editMissionDescriptor.isAnyFieldEdited(), MESSAGE_NOT_EDITED);

        this.editMissionDescriptor = new EditMissionDescriptor(editMissionDescriptor);
    }

    /**
     * Replaces mission details without changing student records or filters.
     *
     * @param model Model containing the shared mission.
     * @return Feedback describing the edited or unchanged mission.
     * @throws CommandException If the mission is unknown or the name is duplicated.
     * @throws NullPointerException If the model is null.
     * @throws IllegalArgumentException If a replacement name is blank.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Mission missionToEdit = model.findMission(missionId).orElseThrow(() ->
                new CommandException(String.format(Messages.MESSAGE_MISSION_NOT_FOUND, missionId)));
        Mission editedMission = createEditedMission(missionToEdit, editMissionDescriptor);
        if (!missionToEdit.hasSameName(editedMission.getName())
                && model.hasMissionName(editedMission.getName())) {
            throw new CommandException(Messages.MESSAGE_DUPLICATE_MISSION);
        }
        if (editedMission.equals(missionToEdit)) {
            return new CommandResult(String.format(MESSAGE_NO_CHANGES, missionId));
        }

        model.setMission(missionToEdit, editedMission);
        logger.info("Edited mission " + missionId + " in the in-memory catalogue.");
        return new CommandResult(String.format(
                MESSAGE_EDIT_MISSION_SUCCESS, Messages.format(editedMission)));
    }

    /**
     * Creates a replacement retaining unspecified fields and the original ID.
     *
     * @param missionToEdit Original mission.
     * @param editMissionDescriptor Requested replacement details.
     * @return Mission containing the requested edits and original ID.
     * @throws IllegalArgumentException If the replacement name is blank.
     */
    private static Mission createEditedMission(Mission missionToEdit,
                                               EditMissionDescriptor editMissionDescriptor) {
        assert missionToEdit != null;
        assert editMissionDescriptor != null;

        String updatedName = editMissionDescriptor.getName().orElse(missionToEdit.getName());
        LocalDate updatedDeadline = editMissionDescriptor.getDeadline()
                .orElse(missionToEdit.getDeadline());
        return new Mission(missionToEdit.getId(), updatedName, updatedDeadline);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof EditMissionCommand otherCommand)) {
            return false;
        }
        return missionId.equals(otherCommand.missionId)
                && editMissionDescriptor.equals(otherCommand.editMissionDescriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(missionId, editMissionDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("missionId", missionId)
                .add("editMissionDescriptor", editMissionDescriptor)
                .toString();
    }

    /**
     * Stores parsed replacement fields, following the student-edit descriptor pattern.
     */
    public static class EditMissionDescriptor {

        private String name;
        private LocalDate deadline;

        /**
         * Creates an empty descriptor.
         */
        public EditMissionDescriptor() {
        }

        /**
         * Copies replacement fields, whose values are immutable.
         *
         * @param toCopy Descriptor to copy.
         * @throws NullPointerException If the descriptor is null.
         */
        public EditMissionDescriptor(EditMissionDescriptor toCopy) {
            requireNonNull(toCopy);
            name = toCopy.name;
            deadline = toCopy.deadline;
        }

        /**
         * Returns whether at least one replacement field is supplied.
         *
         * @return True if a name or deadline is supplied.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, deadline);
        }

        /**
         * Sets the replacement name.
         *
         * @param name Name validated by the mission-name parser.
         * @throws NullPointerException If the name is null.
         */
        public void setName(String name) {
            this.name = requireNonNull(name);
        }

        /**
         * Returns the replacement name, if supplied.
         *
         * @return Optional replacement name.
         */
        public Optional<String> getName() {
            return Optional.ofNullable(name);
        }

        /**
         * Sets the replacement deadline.
         *
         * @param deadline Validated replacement date.
         * @throws NullPointerException If the deadline is null.
         */
        public void setDeadline(LocalDate deadline) {
            this.deadline = requireNonNull(deadline);
        }

        /**
         * Returns the replacement deadline, if supplied.
         *
         * @return Optional replacement deadline.
         */
        public Optional<LocalDate> getDeadline() {
            return Optional.ofNullable(deadline);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (!(other instanceof EditMissionDescriptor otherDescriptor)) {
                return false;
            }
            return Objects.equals(name, otherDescriptor.name)
                    && Objects.equals(deadline, otherDescriptor.deadline);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, deadline);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("deadline", deadline)
                    .toString();
        }
    }
}

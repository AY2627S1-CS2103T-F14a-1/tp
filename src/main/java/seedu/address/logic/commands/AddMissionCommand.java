package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.MissionSubmissions;
import seedu.address.model.person.Person;

/**
 * Records a mission submission for a student identified by a displayed index.
 */
public class AddMissionCommand extends Command {

    public static final String COMMAND_WORD = "addmission";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Records a mission submission for the selected student.\n"
            + "Parameters: INDEX w/WEEK\n"
            + "INDEX must be a positive integer identifying a displayed student.\n"
            + "WEEK must be between " + MissionSubmissions.MIN_WEEK
            + " and " + MissionSubmissions.MAX_WEEK + " inclusive.\n"
            + "Example: " + COMMAND_WORD + " 1 w/3";

    public static final String MESSAGE_SUCCESS =
            "Added %1$s’s mission submission for tutorial week %2$d.";

    public static final String MESSAGE_ALREADY_RECORDED =
            "%1$s’s mission submission has already been recorded for tutorial week %2$d. No changes made.";

    private final Index targetIndex;
    private final int week;

    /**
     * Creates a command to record a mission submission.
     *
     * @param targetIndex Index of the student in the displayed list.
     * @param week Tutorial week for the submission.
     * @throws NullPointerException If the index is null.
     * @throws IllegalArgumentException If the week is outside the valid range.
     */
    public AddMissionCommand(Index targetIndex, int week) {
        this.targetIndex = requireNonNull(targetIndex);
        checkArgument(MissionSubmissions.isValidWeek(week), MissionSubmissions.MESSAGE_CONSTRAINTS);
        this.week = week;
    }

    /**
     * Records the submission without changing the current list filter.
     *
     * @param model Model containing the students.
     * @return Feedback describing the addition or an existing submission.
     * @throws CommandException If the index does not identify a displayed student.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Person> lastShownList = model.getFilteredPersonList();
        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToUpdate = lastShownList.get(targetIndex.getZeroBased());
        MissionSubmissions submissions = personToUpdate.getMissionSubmissions();

        if (submissions.hasSubmission(week)) {
            return new CommandResult(String.format(MESSAGE_ALREADY_RECORDED, personToUpdate.getName(), week));
        }

        Person updatedPerson = createUpdatedPerson(personToUpdate, submissions.withSubmission(week));
        model.setPerson(personToUpdate, updatedPerson);
        return new CommandResult(String.format(MESSAGE_SUCCESS, personToUpdate.getName(), week));
    }

    /**
     * Copies a person with updated mission submissions.
     *
     * @param person Person whose other details are preserved.
     * @param submissions Updated mission submissions.
     * @return A person containing the updated submissions and original details.
     */
    private Person createUpdatedPerson(Person person, MissionSubmissions submissions) {
        assert person != null;
        assert submissions != null;

        return new Person(person.getName(), person.getPhone(), person.getEmail(),
                person.getAddress(), person.getTags(), submissions);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddMissionCommand otherMissionCommand)) {
            return false;
        }

        return targetIndex.equals(otherMissionCommand.targetIndex)
                && week == otherMissionCommand.week;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("week", week)
                .toString();
    }
}

package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.student.RegisteredStudioRecords;
import seedu.address.model.student.Student;

/**
 * Removes a week's studio record for a student identified by a displayed index.
 */
public class DeleteStudioRecordCommand extends Command {

    public static final String COMMAND_WORD = "delstudiorecord";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes a week's studio record for the selected student.\n"
            + "Parameters: " + PREFIX_INDEX + "INDEX w/WEEK\n"
            + "INDEX must be a positive integer identifying a displayed student.\n"
            + "WEEK must be between " + RegisteredStudioRecords.MIN_WEEK
            + " and " + RegisteredStudioRecords.MAX_WEEK + " inclusive.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_INDEX + "1 " + PREFIX_WEEK + "3";

    public static final String MESSAGE_SUCCESS =
            "Removed %1$s's record for studio week %2$d.";

    public static final String MESSAGE_ALREADY_NOT_RECORDED =
            "%1$s’s studio record was already not marked for studio week %2$d. No changes made.";

    private final Index targetIndex;
    private final int week;

    /**
     * Creates a command to delete a week's studio record.
     *
     * @param targetIndex Index of the student in the displayed list.
     * @param week Tutorial week for the deletion.
     */
    public DeleteStudioRecordCommand(Index targetIndex, int week) {
        this.targetIndex = requireNonNull(targetIndex);
        checkArgument(RegisteredStudioRecords.isValidWeek(week), RegisteredStudioRecords.MESSAGE_CONSTRAINTS);
        this.week = week;
    }

    /**
     * Removes a week's studio record without changing the current list filter.
     *
     * @param model Model containing the students.
     * @return Feedback describing the removal or an already non-existent studio record.
     * @throws CommandException If the index does not identify a displayed student.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Student studentToUpdate = getStudentAtDisplayedIndex(model, targetIndex);
        RegisteredStudioRecords registeredStudioRecords = studentToUpdate.getRegisteredStudioRecords();

        if (!registeredStudioRecords.hasStudioRecord(week)) {
            return new CommandResult(String.format(MESSAGE_ALREADY_NOT_RECORDED, studentToUpdate.getName(), week),
                    studentToUpdate);
        }

        Student updatedStudent = studentToUpdate.withRegisteredStudioRecords(
                registeredStudioRecords.withoutStudioRecord(week));
        model.setStudent(studentToUpdate, updatedStudent);
        return new CommandResult(String.format(MESSAGE_SUCCESS, studentToUpdate.getName(), week), updatedStudent);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteStudioRecordCommand otherDeleteStudioRecordCommand)) {
            return false;
        }

        return targetIndex.equals(otherDeleteStudioRecordCommand.targetIndex)
                && week == otherDeleteStudioRecordCommand.week;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("week", week)
                .toString();
    }
}

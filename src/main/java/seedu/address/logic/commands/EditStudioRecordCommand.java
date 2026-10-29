package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDIO_EFFORT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;

import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.student.RegisteredStudioRecords;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioRecord;

/**
 * Edits a week's studio record for a student identified by a displayed index.
 */
public class EditStudioRecordCommand extends Command {

    public static final String COMMAND_WORD = "editstudiorecord";
    public static final String MESSAGE_ALREADY_RECORDED =
            "%1$s’s studio for studio week %2$d is already the same. No changes made.";
    public static final String MESSAGE_SUCCESS =
            "Edited %1$s's studio record for studio week %2$d.";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits a week's studio record for the selected student.\n"
            + "Parameters: " + PREFIX_INDEX + "INDEX " + PREFIX_WEEK + "WEEK " + PREFIX_STUDIO_EFFORT + "EFFORT\n"
            + "INDEX must be a positive integer identifying a displayed student.\n"
            + "WEEK must be between " + RegisteredStudioRecords.MIN_WEEK
            + " and " + RegisteredStudioRecords.MAX_WEEK + " inclusive.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_INDEX + "1 " + PREFIX_WEEK + "3 " + PREFIX_STUDIO_EFFORT + "30";
    public static final String MESSAGE_WEEK_NOT_RECORDED =
            "%1$s’s studio for studio week %2$d was not already recorded. No changes made.";

    private final Index targetIndex;
    private final int week;
    private final StudioRecord studioRecord;

    /**
     * Creates a command to edit a week's studio record.
     *
     * @param targetIndex Index of the student in the displayed list.
     * @param week Studio week for the record.
     * @param studioRecord Record to edit.
     */
    public EditStudioRecordCommand(Index targetIndex, int week, StudioRecord studioRecord) {
        requireNonNull(targetIndex);
        requireNonNull(studioRecord);

        checkArgument(RegisteredStudioRecords.isValidWeek(week), RegisteredStudioRecords.MESSAGE_CONSTRAINTS);

        this.targetIndex = targetIndex;
        this.week = week;
        this.studioRecord = studioRecord;
    }

    /**
     * Edits studio record without changing the current list filter.
     *
     * @param model {@code Model} containing the students.
     * @return Feedback describing the edit or an existing studio record.
     * @throws CommandException If the index does not identify a displayed student.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Student studentToUpdate = getStudentAtDisplayedIndex(model, targetIndex);
        RegisteredStudioRecords registeredStudioRecords = studentToUpdate.getRegisteredStudioRecords();

        if (!registeredStudioRecords.hasStudioRecord(week)) {
            return new CommandResult(String.format(MESSAGE_WEEK_NOT_RECORDED, studentToUpdate.getName(), week),
                    studentToUpdate);
        }

        if (registeredStudioRecords.hasMatchingStudioRecord(week, studioRecord)) {
            return new CommandResult(String.format(MESSAGE_ALREADY_RECORDED, studentToUpdate.getName(), week),
                    studentToUpdate);
        }

        Student updatedStudent = studentToUpdate.withRegisteredStudioRecords(
                registeredStudioRecords.editStudioRecord(week, studioRecord));
        model.setStudent(studentToUpdate, updatedStudent);
        return new CommandResult(String.format(MESSAGE_SUCCESS, studentToUpdate.getName(), week), updatedStudent);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditStudioRecordCommand otherEditStudioRecordCommand)) {
            return false;
        }

        return targetIndex.equals(otherEditStudioRecordCommand.targetIndex)
                && week == otherEditStudioRecordCommand.week
                && studioRecord.equals(otherEditStudioRecordCommand.studioRecord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetIndex, week, studioRecord);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("week", week)
                .add("studioRecord", studioRecord)
                .toString();
    }
}

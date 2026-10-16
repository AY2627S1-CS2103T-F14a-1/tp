package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioRecords;

/**
 * Removes a week's studio record for a student identified by a displayed index.
 */
public class DeleteStudioRecordCommand extends Command {

    public static final String COMMAND_WORD = "delstudiorecord";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes a week's studio record for the selected student.\n"
            + "Parameters: INDEX w/WEEK\n"
            + "INDEX must be a positive integer identifying a displayed student.\n"
            + "WEEK must be between " + StudioRecords.MIN_WEEK
            + " and " + StudioRecords.MAX_WEEK + " inclusive.\n"
            + "Example: " + COMMAND_WORD + " 1 w/3";

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
        checkArgument(StudioRecords.isValidWeek(week), StudioRecords.MESSAGE_CONSTRAINTS);
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

        List<Student> lastShownList = model.getFilteredStudentList();
        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        }

        Student studentToUpdate = lastShownList.get(targetIndex.getZeroBased());
        StudioRecords studioRecords = studentToUpdate.getStudioRecords();

        if (!studioRecords.hasStudioRecord(week)) {
            return new CommandResult(String.format(MESSAGE_ALREADY_NOT_RECORDED, studentToUpdate.getName(), week));
        }

        Student updatedStudent = createUpdatedStudent(studentToUpdate, studioRecords.withoutStudioRecord(week));
        model.setStudent(studentToUpdate, updatedStudent);
        return new CommandResult(String.format(MESSAGE_SUCCESS, studentToUpdate.getName(), week));
    }

    private Student createUpdatedStudent(Student student, StudioRecords studioRecords) {
        assert student != null;
        assert studioRecords != null;

        return new Student(student.getName(), student.getPhone(), student.getEmail(),
                student.getTags(), student.getStudioGroup(), student.getMissionSubmissions(),
                studioRecords);
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

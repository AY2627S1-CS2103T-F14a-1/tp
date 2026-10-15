package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.student.Attendance;
import seedu.address.model.student.Student;

/**
 * Removes a week's tutorial attendance for a student identified by a displayed index.
 */
public class DeleteAttendanceCommand extends Command {

    public static final String COMMAND_WORD = "delattendance";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes a week's attendance for the selected student.\n"
            + "Parameters: INDEX w/WEEK\n"
            + "INDEX must be a positive integer identifying a displayed student.\n"
            + "WEEK must be between " + Attendance.MIN_WEEK
            + " and " + Attendance.MAX_WEEK + " inclusive.\n"
            + "Example: " + COMMAND_WORD + " 1 w/3";

    public static final String MESSAGE_SUCCESS =
            "Removed %1$s's attendance for tutorial week %2$d.";

    public static final String MESSAGE_ALREADY_RECORDED =
            "%1$s’s attendance was already not marked for tutorial week %2$d. No changes made.";

    private final Index targetIndex;
    private final int week;

    /**
     * Creates a command to delete a week's attendance.
     *
     * @param targetIndex Index of the student in the displayed list.
     * @param week Tutorial week for the deletion.
     */
    public DeleteAttendanceCommand(Index targetIndex, int week) {
        this.targetIndex = requireNonNull(targetIndex);
        checkArgument(Attendance.isValidWeek(week), Attendance.MESSAGE_CONSTRAINTS);
        this.week = week;
    }

    /**
     * Removes a week's attendance without changing the current list filter.
     *
     * @param model Model containing the students.
     * @return Feedback describing the removal or an already non-existent attendance.
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
        Attendance attendance = studentToUpdate.getAttendance();

        if (!attendance.hasAttendance(week)) {
            return new CommandResult(String.format(MESSAGE_ALREADY_RECORDED, studentToUpdate.getName(), week));
        }

        Student updatedStudent = studentToUpdate.withAttendance(attendance.withoutAttendedWeek(week));
        model.setStudent(studentToUpdate, updatedStudent);
        return new CommandResult(String.format(MESSAGE_SUCCESS, studentToUpdate.getName(), week));
    }

    private Student createUpdatedStudent(Student student, Attendance attendance) {
        assert student != null;
        assert attendance != null;

        return new Student(student.getName(), student.getPhone(), student.getEmail(),
                student.getTags(), student.getStudioGroup(), student.getMissionSubmissions(),
                attendance);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteAttendanceCommand otherAttendanceCommand)) {
            return false;
        }

        return targetIndex.equals(otherAttendanceCommand.targetIndex)
                && week == otherAttendanceCommand.week;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("week", week)
                .toString();
    }
}

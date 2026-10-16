package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

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
            + "Parameters: INDEX m/MISSION_ID\n"
            + "INDEX must be a positive integer identifying a displayed student.\n"
            + MissionId.MESSAGE_CONSTRAINTS + "\n"
            + "Example: " + COMMAND_WORD + " 1 m/M05";

    public static final String MESSAGE_SUCCESS =
            "Removed %1$s’s mission submission for mission %2$s.";

    public static final String MESSAGE_NOT_RECORDED =
            "%1$s’s mission submission was already not recorded for mission %2$s. No changes made.";

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

        List<Student> lastShownList = model.getFilteredStudentList();
        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        }

        if (model.findMission(missionId).isEmpty()) {
            throw new CommandException("Mission does not exist: " + missionId);
        }

        Student studentToUpdate = lastShownList.get(targetIndex.getZeroBased());
        MissionSubmissions submissions = studentToUpdate.getMissionSubmissions();

        if (!submissions.hasSubmission(missionId)) {
            return new CommandResult(String.format(MESSAGE_NOT_RECORDED, studentToUpdate.getName(), missionId));
        }

        Student updatedStudent = createUpdatedStudent(studentToUpdate, submissions.withoutSubmission(missionId));
        model.setStudent(studentToUpdate, updatedStudent);
        return new CommandResult(String.format(MESSAGE_SUCCESS, studentToUpdate.getName(), missionId));
    }

    /**
     * Copies a student with updated mission submissions.
     *
     * @param student Student whose other details are preserved.
     * @param submissions Updated mission submissions.
     * @return A student containing the updated submissions and original details.
     */
    private Student createUpdatedStudent(Student student, MissionSubmissions submissions) {
        assert student != null;
        assert submissions != null;

        return new Student(student.getName(), student.getPhone(), student.getEmail(),
                student.getTags(), student.getStudioGroup(), submissions,
                student.getStudioRecords());
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
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("missionId", missionId)
                .toString();
    }
}

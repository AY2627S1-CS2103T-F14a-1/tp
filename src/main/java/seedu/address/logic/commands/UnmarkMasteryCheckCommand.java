package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NUMBER;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.masterycheck.MasteryCheck;
import seedu.address.model.student.RegisteredMasteryChecks;
import seedu.address.model.student.Student;

/**
 * Marks a registered mastery check incomplete for a student in the displayed list.
 * Existing appointment times and all other student records are preserved.
 */
public class UnmarkMasteryCheckCommand extends Command {
    public static final String COMMAND_WORD = "unmarkmc";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Revokes completion status for the numbered mastery check of the selected student.\n"
            + "Parameters: INDEX" + PREFIX_NUMBER + "NUMBER\n"
            + "INDEX must be a positive integer identifying a displayed student.\n"
            + "NUMBER must be a positive integer identifying a mastery check.\n"
            + "Example: " + COMMAND_WORD + " 1 num/2";

    public static final String MESSAGE_SUCCESS =
            "Marked %1$s’s mastery check %2$s as incomplete.";

    public static final String MESSAGE_ALREADY_MARKED =
            "%1$s’s mastery check %2$s is already incomplete. No changes made.";

    public static final String MESSAGE_MASTERY_CHECK_NOT_FOUND =
            "Mastery check %1$s does not exist for %2$s.";

    private static final Logger logger = LogsCenter.getLogger(UnmarkMasteryCheckCommand.class);

    private final Index targetIndex;
    private final int number;

    /**
     * Creates a command to mark a numbered mastery check incomplete.
     * The mastery check number is validated when the command executes.
     *
     * @param targetIndex Index of the student in the displayed list.
     * @param number Mastery check registration number to update.
     * @throws NullPointerException If the student index is null.
     */
    public UnmarkMasteryCheckCommand(Index targetIndex, int number) {
        this.targetIndex = requireNonNull(targetIndex);
        this.number = number;
    }

    /**
     * Marks the registered check incomplete without changing the current student-list filter.
     * Replaces the targeted check with a copy, preserving the original student's completion state.
     * If the check is already incomplete, leaves the model unchanged.
     *
     * @param model Model containing the students and their mastery check registrations.
     * @return Feedback describing the status change or an already incomplete check.
     * @throws NullPointerException If the model is null.
     * @throws CommandException If the student index or number is invalid, or the check is not registered.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        List<Student> lastShownList = model.getFilteredStudentList();
        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        }

        if (!RegisteredMasteryChecks.isValidMasteryCheckNumber(number)) {
            logger.fine("UnmarkMasteryCheckCommand rejected: " + number + " is not valid.");
            throw new CommandException(RegisteredMasteryChecks.MESSAGE_CONSTRAINTS);
        }

        Student studentToUpdate = lastShownList.get(targetIndex.getZeroBased());
        RegisteredMasteryChecks registeredMasteryChecks = studentToUpdate.getRegisteredMasteryChecks();
        MasteryCheck masteryCheck = registeredMasteryChecks.getMasteryCheckAppointments().get(number);

        if (masteryCheck == null) {
            logger.fine("UnmarkMasteryCheckCommand rejected: mastery check " + number + " does not exist.");
            throw new CommandException(
                    String.format(MESSAGE_MASTERY_CHECK_NOT_FOUND, number, studentToUpdate.getName()));
        }

        if (!masteryCheck.isComplete()) {
            logger.fine("UnmarkMasteryCheckCommand made no changes for mastery check " + number
                    + " for student " + studentToUpdate.getName()
                    + " at displayed student index " + targetIndex.getOneBased() + ".");
            return new CommandResult(String.format(MESSAGE_ALREADY_MARKED, studentToUpdate.getName(), number));
        }

        MasteryCheck completedCheck = new MasteryCheck(masteryCheck.getAppointmentTime());

        Map<Integer, MasteryCheck> updatedAppointments =
                new HashMap<>(registeredMasteryChecks.getMasteryCheckAppointments());
        updatedAppointments.put(number, completedCheck);

        Student updatedStudent = createUpdatedStudent(studentToUpdate,
                new RegisteredMasteryChecks(updatedAppointments));
        model.setStudent(studentToUpdate, updatedStudent);
        logger.info("Revoked completion for mastery check " + number
                + " for student " + studentToUpdate.getName()
                + " at displayed student index " + targetIndex.getOneBased() + " in memory.");
        return new CommandResult(String.format(MESSAGE_SUCCESS, studentToUpdate.getName(), number));
    }

    /**
     * Copies a student with replacement mastery check registrations, retaining all other details.
     *
     * @param student Non-null student whose profile and other activity records are preserved.
     * @param registeredMasteryChecks Non-null registrations containing the updated completion state.
     * @return A student containing the supplied registrations and original remaining details.
     */
    private Student createUpdatedStudent(Student student, RegisteredMasteryChecks registeredMasteryChecks) {
        assert student != null;
        assert registeredMasteryChecks != null;

        return new Student(student.getName(), student.getPhone(), student.getEmail(),
                student.getTags(), student.getStudioGroup(),
                student.getMissionSubmissions(), student.getRegisteredStudioRecords(), registeredMasteryChecks,
                student.getRegisteredNotes());
    }

    /**
     * Compares commands by the displayed student index and mastery check number.
     *
     * @param other Object to compare with this command.
     * @return True if the other object is the same command type with the same index and number.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UnmarkMasteryCheckCommand otherUnmarkMasteryCheckCommand)) {
            return false;
        }

        return targetIndex.equals(otherUnmarkMasteryCheckCommand.targetIndex)
                && number == otherUnmarkMasteryCheckCommand.number;
    }

    /**
     * Returns the student index and mastery check number for diagnostic output.
     *
     * @return A diagnostic representation of this command.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("number", number)
                .toString();
    }
}

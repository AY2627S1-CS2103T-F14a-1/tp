package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NUMBER;

import java.util.Objects;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
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
            + "Parameters: " + PREFIX_INDEX + "INDEX " + PREFIX_NUMBER + "NUMBER\n"
            + "INDEX must be a positive integer identifying a displayed student.\n"
            + "NUMBER must be a positive integer identifying a mastery check.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_INDEX + "1 " + PREFIX_NUMBER + "2";

    public static final String MESSAGE_SUCCESS =
            "Marked %1$s’s mastery check %2$s as incomplete.";

    public static final String MESSAGE_ALREADY_MARKED =
            "%1$s’s mastery check %2$s is already incomplete. No changes made.";

    public static final String MESSAGE_MASTERY_CHECK_NOT_FOUND =
            "Mastery check %1$s does not exist for %2$s. You can add one using the addmc command.";

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
        requireNonNull(targetIndex);
        checkArgument(RegisteredMasteryChecks.isValidMasteryCheckNumber(number),
                RegisteredMasteryChecks.MESSAGE_CONSTRAINTS);

        this.targetIndex = targetIndex;
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

        Student studentToUpdate = getStudentAtDisplayedIndex(model, targetIndex);
        RegisteredMasteryChecks registeredMasteryChecks = studentToUpdate.getRegisteredMasteryChecks();

        if (!registeredMasteryChecks.hasMasteryCheck(number)) {
            logger.fine("UnmarkMasteryCheckCommand rejected: mastery check " + number + " does not exist.");
            throw new CommandException(
                    String.format(MESSAGE_MASTERY_CHECK_NOT_FOUND, number, studentToUpdate.getName()));
        }

        if (!registeredMasteryChecks.isCompleteMasteryCheck(number)) {
            logger.fine("UnmarkMasteryCheckCommand made no changes for mastery check " + number
                    + " for student " + studentToUpdate.getName()
                    + " at displayed student index " + targetIndex.getOneBased() + ".");
            return new CommandResult(String.format(MESSAGE_ALREADY_MARKED, studentToUpdate.getName(), number));
        }

        Student updatedStudent = studentToUpdate.withRegisteredMasteryChecks(
                registeredMasteryChecks.withCompletionStatus(number, false));
        model.setStudent(studentToUpdate, updatedStudent);
        logger.info("Revoked completion for mastery check " + number
                + " for student " + studentToUpdate.getName()
                + " at displayed student index " + targetIndex.getOneBased() + " in memory.");
        return new CommandResult(String.format(MESSAGE_SUCCESS, studentToUpdate.getName(), number));
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

    @Override
    public int hashCode() {
        return Objects.hash(targetIndex, number);
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

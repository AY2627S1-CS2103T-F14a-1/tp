package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NUMBER;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.student.RegisteredMasteryChecks;
import seedu.address.model.student.Student;

/**
 * Removes a numbered mastery check registration from a student in the displayed list.
 * If the number is not registered, leaves the student's records unchanged.
 */
public class DeleteMasteryCheckCommand extends Command {
    public static final String COMMAND_WORD = "delmc";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes a numbered mastery check for the selected student.\n"
            + "Parameters: " + PREFIX_INDEX + "INDEX " + PREFIX_NUMBER + "NUMBER\n"
            + "INDEX must be a positive integer identifying a displayed student.\n"
            + "NUMBER must be a positive integer identifying a mastery check.\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_INDEX + "1 " + PREFIX_NUMBER + "2";

    public static final String MESSAGE_SUCCESS =
            "Removed %1$s's mastery check numbered %2$d.";

    public static final String MESSAGE_NOT_FOUND =
            "%1$s has no mastery check numbered %2$d. No changes made.";

    private final Index targetIndex;
    private final int number;

    /**
     * Creates a command to remove a student's numbered mastery check registration.
     *
     * @param targetIndex Index of the student in the displayed list.
     * @param number Mastery check number to remove.
     * @throws NullPointerException If the student index is null.
     * @throws IllegalArgumentException If the number fails the currently enforced studio-week range check.
     */
    public DeleteMasteryCheckCommand(Index targetIndex, int number) {
        requireNonNull(targetIndex);
        checkArgument(RegisteredMasteryChecks.isValidMasteryCheckNumber(number),
                RegisteredMasteryChecks.MESSAGE_CONSTRAINTS);

        this.targetIndex = targetIndex;
        this.number = number;
    }

    /**
     * Removes the registration without changing the current student-list filter.
     * Other appointments, activity records, and student details are preserved.
     *
     * @param model Model containing the students.
     * @return Feedback describing the removal or an unregistered mastery check number.
     * @throws NullPointerException If the model is null.
     * @throws CommandException If the index does not identify a displayed student.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Student studentToUpdate = getStudentAtDisplayedIndex(model, targetIndex);
        RegisteredMasteryChecks registeredMasteryChecks = studentToUpdate.getRegisteredMasteryChecks();

        if (!registeredMasteryChecks.hasMasteryCheck(number)) {
            return new CommandResult(String.format(MESSAGE_NOT_FOUND, studentToUpdate.getName(), number));
        }

        Student updatedStudent = studentToUpdate.withRegisteredMasteryChecks(
                registeredMasteryChecks.withoutMasteryCheck(number));
        model.setStudent(studentToUpdate, updatedStudent);
        return new CommandResult(String.format(MESSAGE_SUCCESS, studentToUpdate.getName(), number));
    }

    /**
     * Compares deletion commands by their displayed student index and mastery check number.
     *
     * @param other Object to compare with this command.
     * @return True if the other command targets the same student index and mastery check number.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteMasteryCheckCommand otherDeleteMasteryCheckCommand)) {
            return false;
        }

        return targetIndex.equals(otherDeleteMasteryCheckCommand.targetIndex)
                && number == otherDeleteMasteryCheckCommand.number;
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

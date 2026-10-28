package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;

import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.student.Student;

/**
 * Deletes a student identified using its displayed index from the address book.
 */
public class DeleteStudentCommand extends Command {

    public static final String COMMAND_WORD = "delstudent";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the student identified by the index number used in the displayed student list.\n"
            + "Parameters: " + PREFIX_INDEX + "INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_INDEX + "1";

    public static final String MESSAGE_DELETE_STUDENT_SUCCESS = "Deleted student: %1$s.";

    private final Index targetIndex;

    public DeleteStudentCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Student studentToDelete = getStudentAtDisplayedIndex(model, targetIndex);
        model.deleteStudent(studentToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_STUDENT_SUCCESS, Messages.format(studentToDelete)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteStudentCommand otherDeleteStudentCommand)) {
            return false;
        }

        return targetIndex.equals(otherDeleteStudentCommand.targetIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}

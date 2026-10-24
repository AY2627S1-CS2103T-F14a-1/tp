package seedu.address.logic.commands;

import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.student.Student;

/**
 * Displays the details of the student at the specified index.
 */
public class ViewCommand extends Command {

    public static final String COMMAND_WORD = "view";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Displays the details of the student identified by the "
            + "index number used in the displayed student list.\n"
            + "Parameters: " + PREFIX_INDEX + "INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_INDEX + "1";

    public static final String MESSAGE_VIEW_STUDENT_SUCCESS =
            "Showing profile of %1$s";

    private final Index targetIndex;

    /**
     * Creates a ViewCommand to display the student at the specified index.
     *
     * @param targetIndex the index of the student to view
     */
    public ViewCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Student studentToView =
                getStudentAtDisplayedIndex(model, targetIndex);

        return new CommandResult(
        String.format(
                MESSAGE_VIEW_STUDENT_SUCCESS,
                studentToView.getName().fullName),
                studentToView, model.getMissionList()
        );
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ViewCommand otherViewCommand)) {
            return false;
        }

        return targetIndex.equals(otherViewCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}

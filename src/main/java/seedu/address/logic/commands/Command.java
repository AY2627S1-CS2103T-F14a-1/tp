package seedu.address.logic.commands;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.student.Student;

/**
 * Represents a command with hidden internal logic and the ability to be executed.
 */
public abstract class Command {

    /**
     * Executes the command and returns the result message.
     *
     * @param model {@code Model} which the command should operate on.
     * @return feedback message of the operation result for display
     * @throws CommandException If an error occurs during command execution.
     */
    public abstract CommandResult execute(Model model) throws CommandException;


    /**
     * Returns the student at the specified index in the currently displayed list.
     *
     * @param model {@code model} that provides the currently displayed student list.
     * @param targetStudentIndex Zero-based position of the student to retrieve.
     * @return Student at the specified displayed-list index.
     * @throws CommandException If {@code targetStudentIndex} is outside the currently displayed student list.
     */
    protected Student getStudentAtDisplayedIndex(Model model, Index targetStudentIndex) throws CommandException {
        List<Student> lastShownList = model.getFilteredStudentList();
        if (targetStudentIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        }

        return lastShownList.get(targetStudentIndex.getZeroBased());
    }

}

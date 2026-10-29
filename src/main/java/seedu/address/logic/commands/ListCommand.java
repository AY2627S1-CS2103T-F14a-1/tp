package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_STUDENTS;

import seedu.address.model.Model;

/**
 * Lists all students in the address book to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Lists all students. Extra arguments are ignored with a warning.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "Listed all students.";

    public static final String MESSAGE_WARNING_EXTRA_ARGUMENTS =
            "Extra arguments were ignored. The list command takes no arguments.";

    private final boolean hasExtraArguments;

    /**
     * Creates a list command without an extra-argument warning.
     */
    public ListCommand() {
        this(false);
    }

    /**
     * Creates a list command with optional feedback about ignored arguments.
     *
     * @param hasExtraArguments Whether extra arguments were supplied.
     */
    public ListCommand(boolean hasExtraArguments) {
        this.hasExtraArguments = hasExtraArguments;
    }

    /**
     * Shows all students and warns if extra arguments were supplied.
     *
     * @param model Model containing the student list to display.
     * @return Success feedback with a warning if extra arguments were ignored.
     * @throws NullPointerException If the model is null.
     */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        model.updateFilteredStudentList(PREDICATE_SHOW_ALL_STUDENTS);
        CommandResult result = new CommandResult(MESSAGE_SUCCESS);
        if (hasExtraArguments) {
            return result.withWarning(MESSAGE_WARNING_EXTRA_ARGUMENTS);
        }
        return result;
    }
}

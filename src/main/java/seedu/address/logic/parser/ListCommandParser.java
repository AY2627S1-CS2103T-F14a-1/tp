package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.ListCommand;

/**
 * Parses arguments for listing all students.
 */
public class ListCommandParser implements Parser<ListCommand> {

    /**
     * Creates a list command that warns when extra arguments are supplied.
     *
     * @param args Arguments following the command word.
     * @return Command that lists all students and warns about extra arguments.
     * @throws NullPointerException If the arguments are null.
     */
    @Override
    public ListCommand parse(String args) {
        requireNonNull(args);

        return new ListCommand(!args.isBlank());
    }
}

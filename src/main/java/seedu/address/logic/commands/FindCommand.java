package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDIO_GROUP;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.student.Student;

/**
 * Finds students using name alternatives and optional studio-group filters.
 * Spaces require all name keywords to match; commas separate alternatives.
 * When both names and groups are supplied, both filters must match.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds students by name and/or studio group.\n"
            + "Name matching is case-insensitive and allows partial matches. "
            + "Parameters: [NAME_WORDS [, MORE_NAME_WORDS]...] [g/STUDIO_GROUP]...\n"
            + "Spaces mean AND; commas mean OR between name alternatives. "
            + "Name keywords match student names. Studio groups are specified using g/.\n"
            + "If both name keywords and studio groups are provided, students must match "
            + "both criteria.\n"
            + "If no name keyword is provided, only the studio group criterion is used.\n"
            + "If no g/ studio group is provided, the search is performed using only "
            + "the name keywords.\n"
            + "Place name alternatives before group filters.\n"
            + "Example: " + COMMAND_WORD + " david, alex " + PREFIX_STUDIO_GROUP + "1A";

    private final Predicate<Student> predicate;

    /**
     * Creates a FindCommand that filters students using the given predicate.
     *
     * @param predicate the predicate used to select matching students
     */
    public FindCommand(Predicate<Student> predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredStudentList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_STUDENTS_LISTED_OVERVIEW, model.getFilteredStudentList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}

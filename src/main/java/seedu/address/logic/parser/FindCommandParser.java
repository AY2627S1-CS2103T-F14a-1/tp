package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDIO_GROUP;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.NameContainsKeywordsPredicate;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioGroup;
import seedu.address.model.student.StudioGroupContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    @Override
    public FindCommand parse(String args) throws ParseException {
        requireNonNull(args);

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_STUDIO_GROUP);

        String preamble = argMultimap.getPreamble();

        List<String> nameKeywords = preamble.isBlank()
                ? new ArrayList<>()
                : List.of(preamble.split("\\s+"));

        List<String> groupKeywords =
                argMultimap.getAllValues(PREFIX_STUDIO_GROUP);

        List<StudioGroup> studioGroups = new ArrayList<>();

        for (String groupKeyword : groupKeywords) {
            studioGroups.add(ParserUtil.parseStudioGroup(groupKeyword));
        }

        if (nameKeywords.isEmpty() && studioGroups.isEmpty()) {
            throw new ParseException(
                    String.format(
                            MESSAGE_INVALID_COMMAND_FORMAT,
                            FindCommand.MESSAGE_USAGE));
        }

        Predicate<Student> predicate =
                buildPredicate(nameKeywords, studioGroups);

        return new FindCommand(predicate);
    }

    private Predicate<Student> buildPredicate(
            List<String> nameKeywords,
            List<StudioGroup> studioGroups) {

        if (!nameKeywords.isEmpty() && studioGroups.isEmpty()) {
            return new NameContainsKeywordsPredicate(nameKeywords);
        }

        if (nameKeywords.isEmpty() && !studioGroups.isEmpty()) {
            return new StudioGroupContainsKeywordsPredicate(studioGroups);
        }

        return new NameContainsKeywordsPredicate(nameKeywords)
                .and(new StudioGroupContainsKeywordsPredicate(studioGroups));
    }
}

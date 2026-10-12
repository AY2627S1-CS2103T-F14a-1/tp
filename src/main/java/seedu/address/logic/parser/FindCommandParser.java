package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDIO_GROUP;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.NameContainsKeywordsPredicate;
import seedu.address.model.student.Student;
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
        String normalizedArgs = normalizeArgs(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(normalizedArgs, PREFIX_STUDIO_GROUP);

        List<String> nameKeywords = extractNameKeywords(argMultimap);
        List<String> groupKeywords = extractGroupKeywords(argMultimap);

        if (nameKeywords.isEmpty() && groupKeywords.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                            FindCommand.MESSAGE_USAGE));
        }

        Predicate<Student> predicate = buildPredicate(nameKeywords, groupKeywords);
        return new FindCommand(predicate);
    }

    private String normalizeArgs(String args) {
        return " " + args.strip();
    }

    private List<String> extractNameKeywords(ArgumentMultimap argMultimap) {
        String preamble = argMultimap.getPreamble();
        return preamble.isBlank()
                ? List.of()
                : Arrays.asList(preamble.trim().split("\\s+"));
    }

    private List<String> extractGroupKeywords(ArgumentMultimap argMultimap) throws ParseException {
        List<String> groupKeywords = argMultimap.getAllValues(PREFIX_STUDIO_GROUP);
        if (groupKeywords.stream().anyMatch(String::isBlank)) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                            FindCommand.MESSAGE_USAGE));
        }
        return groupKeywords;
    }

    private Predicate<Student> buildPredicate(List<String> nameKeywords, List<String> groupKeywords) {
        if (!nameKeywords.isEmpty() && groupKeywords.isEmpty()) {
            return new NameContainsKeywordsPredicate(nameKeywords);
        }

        if (nameKeywords.isEmpty() && !groupKeywords.isEmpty()) {
            return new StudioGroupContainsKeywordsPredicate(groupKeywords);
        }

        Predicate<Student> predicate = new NameContainsKeywordsPredicate(nameKeywords);
        return predicate.and(new StudioGroupContainsKeywordsPredicate(groupKeywords));
    }
}

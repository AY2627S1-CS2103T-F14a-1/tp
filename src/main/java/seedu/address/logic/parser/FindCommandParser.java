package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDIO_GROUP;

import java.util.ArrayList;
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
        String trimmedArgs = args.strip();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                            FindCommand.MESSAGE_USAGE));
        }

        String[] tokens = trimmedArgs.split("\\s+");
        List<String> nameKeywords = new ArrayList<>();
        List<String> groupKeywords = new ArrayList<>();

        boolean groupPrefixSeen = false;

        for (String token : tokens) {
            if (token.startsWith(PREFIX_STUDIO_GROUP.getPrefix())) {
                String groupKeyword = token.substring(
                        PREFIX_STUDIO_GROUP.getPrefix().length());

                if (groupKeyword.isBlank()) {
                    throw new ParseException(
                            String.format(
                                    MESSAGE_INVALID_COMMAND_FORMAT,
                                    FindCommand.MESSAGE_USAGE));
                }

                groupKeywords.add(groupKeyword);
                groupPrefixSeen = true;
            } else {
                if (groupPrefixSeen) {
                    throw new ParseException(
                            String.format(
                                    MESSAGE_INVALID_COMMAND_FORMAT,
                                    FindCommand.MESSAGE_USAGE));
                }

                nameKeywords.add(token);
            }
        }

        if (nameKeywords.isEmpty() && groupKeywords.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                            FindCommand.MESSAGE_USAGE));
        }

        Predicate<Student> predicate = buildPredicate(nameKeywords, groupKeywords);
        return new FindCommand(predicate);
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

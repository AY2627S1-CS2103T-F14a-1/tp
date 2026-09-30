package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;

import java.util.Locale;
import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddMissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddMissionCommandParser implements Parser<AddMissionCommand> {

    public static final String MESSAGE_MISSING_INDEX = "A student index must be provided.";
    public static final String MESSAGE_MISSING_WEEK = "Specify a tutorial week using w/WEEK.";
    public static final String MESSAGE_DUPLICATE_WEEK = "Tutorial week must be specified only once.";

    /**
     * Parses the student index and tutorial week.
     *
     * @param args Arguments following the command word.
     * @return A command containing the validated index and week.
     * @throws ParseException If required information is missing or invalid.
     */
    @Override
    public AddMissionCommand parse(String args) throws ParseException {
        requireNonNull(args);

        String normalisedArgs = normaliseArguments(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(normalisedArgs, PREFIX_WEEK);

        String indexText = argMultimap.getPreamble();
        if (indexText.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_INDEX);
        }

        rejectExtraArguments(indexText);
        Index index = ParserUtil.parseStudentIndex(indexText);
        String weekText = extractWeek(argMultimap);
        int week = ParserUtil.parseTutorialWeek(weekText);

        return new AddMissionCommand(index, week);
    }

    /**
     * Normalizes whitespace and prefix case for this numeric-only command.
     * The leading space allows the tokenizer to detect an initial prefix.
     *
     * @param args Original command arguments.
     * @return Arguments suitable for the existing tokenizer.
     */
    private String normaliseArguments(String args) {
        return " " + args.strip().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /**
     * Extracts the single required tutorial week value.
     *
     * @param argMultimap Tokenized command arguments.
     * @return The non-empty week text.
     * @throws ParseException If the week is missing, repeated, or has extra arguments.
     */
    private String extractWeek(ArgumentMultimap argMultimap) throws ParseException {
        if (!arePrefixesPresent(argMultimap, PREFIX_WEEK)) {
            throw new ParseException(MESSAGE_MISSING_WEEK);
        }

        if (argMultimap.getAllValues(PREFIX_WEEK).size() > 1) {
            throw new ParseException(MESSAGE_DUPLICATE_WEEK);
        }

        String weekText = argMultimap.getValue(PREFIX_WEEK).orElseThrow();
        if (weekText.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_WEEK);
        }

        rejectExtraArguments(weekText);
        return weekText;
    }

    /**
     * Rejects extra arguments or unexpected prefixes within a numeric field.
     *
     * @param text Trimmed, whitespace-normalized field text.
     * @throws ParseException If additional argument structure is present.
     */
    private void rejectExtraArguments(String text) throws ParseException {
        if (text.contains(" ") || text.contains("/")) {
            throw new ParseException(String.format(
                    MESSAGE_INVALID_COMMAND_FORMAT, AddMissionCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Returns whether every specified prefix is present.
     * A present prefix may still have an empty value.
     *
     * @param argumentMultimap Tokenized arguments.
     * @param prefixes Required prefixes.
     * @return True if all required prefixes are present.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}

package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Validates the shared student index and week arguments for studio commands.
 */
public class WeeklyTaskParser {

    private final ArgumentMultimap argumentMultimap;
    private final String usage;
    private final Index index;
    private final int week;

    /**
     * Parses the common student index and week arguments, with any additional prefixes.
     *
     * @param args Arguments following the command word.
     * @param usage Usage instructions for the calling command.
     * @param additionalPrefixes Prefixes to recognize in addition to the week prefix.
     * @throws NullPointerException If an argument or prefix is null.
     * @throws ParseException If required information is missing or invalid.
     */
    public WeeklyTaskParser(String args, String usage, Prefix... additionalPrefixes) throws ParseException {
        requireNonNull(args);
        requireNonNull(usage);
        requireNonNull(additionalPrefixes);
        for (Prefix prefix : additionalPrefixes) {
            requireAllNonNull(prefix);
        }

        this.usage = usage;

        Prefix[] prefixes = prependWeekPrefix(additionalPrefixes);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, prefixes);
        this.argumentMultimap = arguments;

        index = extractIndex(arguments);
        week = extractWeek(arguments);
    }

    /**
     * Returns an array containing the weekly prefix followed by the supplied prefixes
     * for map parsing.
     *
     * @param prefixes Prefixes to recognize in addition to the weekly prefix.
     * @return An array containing every recognized prefix.
     */
    private Prefix[] prependWeekPrefix(Prefix... prefixes) {
        Prefix[] prefixArray = new Prefix[prefixes.length + 1];
        prefixArray[0] = PREFIX_WEEK;
        System.arraycopy(prefixes, 0, prefixArray, 1, prefixes.length);
        return prefixArray;
    }

    /**
     * Extracts the index.
     *
     * @param argMultimap Tokenized command arguments.
     * @return The validated displayed student index.
     * @throws ParseException If the index is missing or invalid.
     */
    private Index extractIndex(ArgumentMultimap argMultimap) throws ParseException {
        try {
            return ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage), pe);
        }
    }

    /**
     * Extracts the single required studio week value.
     *
     * @param argMultimap Tokenized command arguments.
     * @return The non-empty week text.
     * @throws ParseException If the week does not conform to the expected format.
     */
    private int extractWeek(ArgumentMultimap argMultimap) throws ParseException {
        requireNonNull(argumentMultimap);
        if (!argumentMultimap.getValue(PREFIX_WEEK).isPresent()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage));
        }
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_WEEK);
        return ParserUtil.parseWeek(argMultimap.getValue(PREFIX_WEEK).get());
    }

    /**
     * Returns the validated displayed student index.
     *
     * @return The student index.
     */
    public Index getIndex() {
        return index;
    }

    /**
     * Returns the validated studio week.
     *
     * @return The studio week.
     */
    public int getWeek() {
        return week;
    }

    /**
     * Returns the sole value associated with the given prefix.
     *
     * @param prefix Prefix whose value should be returned.
     * @return An {@code Optional} containing the prefix value, if any.
     * @throws NullPointerException If the prefix is null.
     * @throws ParseException If the prefix occurs more than once.
     */
    public String getPrefixValue(Prefix prefix) throws ParseException {
        requireNonNull(prefix);
        if (!argumentMultimap.getValue(prefix).isPresent()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage));
        }
        argumentMultimap.verifyNoDuplicatePrefixesFor(prefix);
        return argumentMultimap.getValue(prefix).get();
    }
}

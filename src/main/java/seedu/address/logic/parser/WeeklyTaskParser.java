package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.RegisteredStudioRecords;

/**
 * Validates the shared student index and week arguments for studio commands.
 */
public class WeeklyTaskParser {

    public static final String MESSAGE_MISSING_INDEX = "A student index must be provided.";
    public static final String MESSAGE_MISSING_WEEK = "Specify a studio week using " + PREFIX_WEEK + "WEEK.";
    public static final String MESSAGE_DUPLICATE_WEEK = "Studio week must be specified only once.";
    public static final String MESSAGE_INVALID_STUDENT_INDEX =
            "Student index must be a positive integer without a sign, decimal point, or leading zero.";
    public static final String MESSAGE_INVALID_STUDIO_WEEK = RegisteredStudioRecords.MESSAGE_CONSTRAINTS
            + " Enter an integer without a sign, decimal point, or leading zero.";
    private static final Pattern POSITIVE_INTEGER = Pattern.compile("[1-9][0-9]*");

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
        requireAllNonNull(additionalPrefixes);

        this.usage = usage;

        Prefix[] prefixes = getPrefixArray(additionalPrefixes);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, prefixes);
        this.argumentMultimap = arguments;

        index = extractIndex(arguments);
        week = extractWeek(arguments);
    }

    private Prefix[] getPrefixArray(Prefix... prefixes) {
        Prefix[] prefixArray = new Prefix[prefixes.length + 1];
        prefixes[0] = PREFIX_WEEK;
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
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_WEEK);
        return ParserUtil.parseWeek(argMultimap.getValue(PREFIX_WEEK).orElseThrow());
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
    public Optional<String> getPrefixValue(Prefix prefix) throws ParseException {
        requireNonNull(prefix);
        argumentMultimap.verifyNoDuplicatePrefixesFor(prefix);
        return argumentMultimap.getValue(prefix);
    }

    /**
     * Extracts the index while distinguishing missing inputs from misplaced arguments.
     *
     * @param argMultimap Tokenized command arguments.
     * @return The validated displayed student index.
     * @throws ParseException If the index is missing, invalid, or surrounded by extra arguments.
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
     * @throws ParseException If the week is missing, repeated, or has extra arguments.
     */
    private int extractWeek(ArgumentMultimap argMultimap) throws ParseException {
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_WEEK);
        return ParserUtil.parseWeek(argMultimap.getValue(PREFIX_WEEK).orElseThrow());
    }
}

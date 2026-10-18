package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
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
        this.usage = requireNonNull(usage);
        requireNonNull(additionalPrefixes);
        for (Prefix prefix : additionalPrefixes) {
            requireNonNull(prefix);
        }

        Prefix[] prefixes = new Prefix[additionalPrefixes.length + 1];
        prefixes[0] = PREFIX_WEEK;
        System.arraycopy(additionalPrefixes, 0, prefixes, 1, additionalPrefixes.length);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(normalizeArguments(args),
                prefixes);
        this.argumentMultimap = arguments;
        index = extractIndex(arguments);
        week = parseStudioWeek(extractWeek(arguments));
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
     * @param arguments Tokenized command arguments.
     * @return The validated displayed student index.
     * @throws ParseException If the index is missing, invalid, or surrounded by extra arguments.
     */
    private Index extractIndex(ArgumentMultimap arguments) throws ParseException {
        String indexText = arguments.getPreamble();
        if (indexText.isEmpty()) {
            for (String value : arguments.getAllValues(PREFIX_WEEK)) {
                rejectExtraArguments(value);
            }
            throw new ParseException(MESSAGE_MISSING_INDEX);
        }

        String[] fields = indexText.split(" ");
        if (!arePrefixesPresent(arguments, PREFIX_WEEK) && fields.length == 2
                && POSITIVE_INTEGER.matcher(fields[1]).matches()) {
            parseStudentIndex(fields[0]);
            throw new ParseException(MESSAGE_MISSING_WEEK);
        }

        rejectExtraArguments(indexText);
        return parseStudentIndex(indexText);
    }

    /**
     * Parses a student index without accepting signs or leading zeros.
     *
     * @param oneBasedIndex Student index text.
     * @return A valid one-based index.
     * @throws NullPointerException If the text is null.
     * @throws ParseException If the index syntax or numeric value is invalid.
     */
    public static Index parseStudentIndex(String oneBasedIndex) throws ParseException {
        requireNonNull(oneBasedIndex);

        String trimmedIndex = oneBasedIndex.trim();
        if (!POSITIVE_INTEGER.matcher(trimmedIndex).matches()) {
            throw new ParseException(MESSAGE_INVALID_STUDENT_INDEX);
        }

        try {
            return ParserUtil.parseIndex(trimmedIndex);
        } catch (ParseException e) {
            throw new ParseException(MESSAGE_INVALID_STUDENT_INDEX);
        }
    }

    /**
     * Parses a studio week within the model's valid range.
     *
     * @param week Studio week text.
     * @return The validated studio week.
     * @throws NullPointerException If the text is null.
     * @throws ParseException If the week syntax or numeric value is invalid.
     */
    public static int parseStudioWeek(String week) throws ParseException {
        requireNonNull(week);

        String trimmedWeek = week.trim();
        if (!POSITIVE_INTEGER.matcher(trimmedWeek).matches()) {
            throw new ParseException(MESSAGE_INVALID_STUDIO_WEEK);
        }

        try {
            int parsedWeek = Integer.parseInt(trimmedWeek);
            if (!RegisteredStudioRecords.isValidWeek(parsedWeek)) {
                throw new ParseException(MESSAGE_INVALID_STUDIO_WEEK);
            }
            return parsedWeek;
        } catch (NumberFormatException e) {
            throw new ParseException(MESSAGE_INVALID_STUDIO_WEEK);
        }
    }

    /**
     * Normalizes whitespace and prefix case for this numeric-only command.
     * The leading space allows the tokenizer to detect an initial prefix.
     *
     * @param args Original command arguments.
     * @return Arguments suitable for the existing tokenizer.
     */
    private String normalizeArguments(String args) {
        return " " + args.strip().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /**
     * Extracts the single required studio week value.
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
                    MESSAGE_INVALID_COMMAND_FORMAT, usage));
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

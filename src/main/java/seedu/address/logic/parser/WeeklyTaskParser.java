package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;

import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.Attendance;

/**
 * Validates the shared student index and week arguments for mission commands.
 */
public class WeeklyTaskParser {

    public static final String MESSAGE_MISSING_INDEX = "A student index must be provided.";
    public static final String MESSAGE_MISSING_WEEK = "Specify a tutorial week using w/WEEK.";
    public static final String MESSAGE_DUPLICATE_WEEK = "Tutorial week must be specified only once.";
    public static final String MESSAGE_INVALID_STUDENT_INDEX =
            "Student index must be a positive integer without a sign, decimal point, or leading zero.";
    public static final String MESSAGE_INVALID_TUTORIAL_WEEK = Attendance.MESSAGE_CONSTRAINTS
            + " Enter an integer without a sign, decimal point, or leading zero.";
    private static final Pattern POSITIVE_INTEGER = Pattern.compile("[1-9][0-9]*");

    private final String usage;
    private final Index index;
    private final int week;

    /**
     * Parses arguments using the calling command's usage in format errors.
     *
     * @param args Arguments following the command word.
     * @param usage Usage instructions for the calling command.
     * @throws NullPointerException If either argument is null.
     * @throws ParseException If required information is missing or invalid.
     */
    public WeeklyTaskParser(String args, String usage) throws ParseException {
        requireNonNull(args);
        this.usage = requireNonNull(usage);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, PREFIX_WEEK);
        index = parseIndex(arguments);
        week = parseTutorialWeek(extractWeek(arguments));
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
     * Returns the validated tutorial week.
     *
     * @return The tutorial week.
     */
    public int getWeek() {
        return week;
    }

    private Index parseIndex(ArgumentMultimap arguments) throws ParseException {
        String indexText = arguments.getPreamble();

        // if no index detected
        if (indexText.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_INDEX);
        }

        return ParserUtil.parseIndex(indexText);
    }

    /**
     * Parses a tutorial week within the model's valid range.
     *
     * @param week Tutorial week text.
     * @return The validated tutorial week.
     * @throws NullPointerException If the text is null.
     * @throws ParseException If the week syntax or numeric value is invalid.
     */
    public static int parseTutorialWeek(String week) throws ParseException {
        requireNonNull(week);
        return Integer.parseInt(week);
    }

    /**
     * Extracts the single required tutorial week value.
     *
     * @param argMultimap Tokenized command arguments.
     * @return The non-empty week text.
     * @throws ParseException If the week is missing, repeated, or has extra arguments.
     */
    private String extractWeek(ArgumentMultimap argMultimap) throws ParseException {
        if (argMultimap.getValue(PREFIX_WEEK).isEmpty()) {
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
}

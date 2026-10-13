package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddAttendanceCommand;
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
            + " Enter a single integer without a sign, decimal point, or leading zero.";

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
        week = parseTutorialWeek(arguments);
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
        requireNonNull(arguments);
        String indexText = arguments.getPreamble();

        // if no index detected
        if (indexText.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_INDEX);
        }

        try {
            return ParserUtil.parseIndex(indexText);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddAttendanceCommand.MESSAGE_USAGE), pe);
        }
    }

    /**
     * Parses a tutorial week within the model's valid range.
     *
     * @return The validated tutorial week.
     * @throws NullPointerException If the text is null.
     */
    public static int parseTutorialWeek(ArgumentMultimap argMultimap) throws ParseException {
        requireNonNull(argMultimap);
        if (argMultimap.getValue(PREFIX_WEEK).isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_WEEK);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_WEEK);

        String weekText = argMultimap.getValue(PREFIX_WEEK).orElseThrow();
        if (weekText.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_WEEK);
        }

        try {
            return Integer.parseInt(weekText);
        } catch (NumberFormatException e) {
            throw new ParseException(MESSAGE_INVALID_TUTORIAL_WEEK);
        }
    }
}

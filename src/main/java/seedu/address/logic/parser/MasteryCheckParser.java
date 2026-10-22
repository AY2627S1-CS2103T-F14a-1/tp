package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NUMBER;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the displayed student index and single positive mastery check number shared by completion commands.
 */
public class MasteryCheckParser {
    public static final String MESSAGE_MISSING_INDEX = "A student index must be provided.";

    private final String usage;
    private final Index index;
    private final int number;

    /**
     * Parses arguments using the calling command's usage in format errors.
     *
     * @param args Arguments following the command word.
     * @param usage Usage instructions for the calling command.
     * @throws NullPointerException If either argument is null.
     * @throws ParseException If required information is missing or invalid.
     */
    public MasteryCheckParser(String args, String usage) throws ParseException {
        requireNonNull(args);
        this.usage = requireNonNull(usage);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(args, PREFIX_INDEX, PREFIX_NUMBER);
        index = extractIndex(arguments);
        number = extractMasteryCheckNumber(arguments);
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
     * Returns the validated positive mastery check number.
     *
     * @return The mastery check number.
     */
    public int getMasteryCheckNumber() {
        return number;
    }

    /**
     * Extracts and validates the displayed student index from the required index prefix.
     *
     * @param arguments Tokenized command arguments.
     * @return The validated displayed student index.
     * @throws ParseException If the index prefix is missing or repeated, its value is invalid, or a preamble exists.
     */
    private Index extractIndex(ArgumentMultimap arguments) throws ParseException {
        if (!arguments.arePrefixesPresent(PREFIX_INDEX)
                || !arguments.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage));
        }

        arguments.verifyNoDuplicatePrefixesFor(PREFIX_INDEX);
        return ParserUtil.parseIndex(arguments.getValue(PREFIX_INDEX).get());
    }

    /**
     * Extracts and validates the single required mastery check number.
     *
     * @param argMultimap Tokenized command arguments.
     * @return The positive mastery check registration number.
     * @throws ParseException If the prefix is missing or repeated, or its value is not a positive integer.
     */
    private int extractMasteryCheckNumber(ArgumentMultimap argMultimap) throws ParseException {
        if (!argMultimap.arePrefixesPresent(PREFIX_NUMBER)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    usage));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NUMBER);
        return ParserUtil.parseMasteryCheckNumber(argMultimap.getValue(PREFIX_NUMBER).get());
    }
}

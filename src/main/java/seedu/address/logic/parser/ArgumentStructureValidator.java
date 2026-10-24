package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Validates arguments whose recognized prefixes must each appear exactly once, with no preamble.
 */
public final class ArgumentStructureValidator {

    /**
     * Tokenizes arguments and validates their prefix structure without parsing individual values.
     * Empty prefix values are left for the calling parser to validate.
     *
     * @param args Arguments following the command word.
     * @param prefixes Prefixes to recognize and require exactly once.
     * @return The validated argument multimap.
     * @throws ParseException If a required prefix is missing, a preamble exists, or a prefix is repeated.
     */
    public static ArgumentMultimap validateStructure(String args, Prefix... prefixes)
            throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, prefixes);
        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT);
        }
        argMultimap.verifyPrefixesPresentFor(prefixes);
        argMultimap.verifyNoDuplicatePrefixesFor(prefixes);
        return argMultimap;
    }

    public static ArgumentMultimap validateStructureWithOptionals(
            String args, Prefix[] requiredPrefixes, Prefix[] nonRepeatablePrefixes, Prefix... allPrefixes)
            throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, allPrefixes);
        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT);
        }
        argMultimap.verifyPrefixesPresentFor(requiredPrefixes);
        argMultimap.verifyNoDuplicatePrefixesFor(nonRepeatablePrefixes);
        return argMultimap;
    }
}

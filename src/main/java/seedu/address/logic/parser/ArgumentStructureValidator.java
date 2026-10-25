package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND;

import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Validates required, optional, and repeatable argument prefixes, rejecting nonempty preambles.
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
            throw new ParseException(MESSAGE_INVALID_COMMAND);
        }
        argMultimap.verifyPrefixesPresentFor(prefixes);
        argMultimap.verifyNoDuplicatePrefixesFor(prefixes);
        return argMultimap;
    }

    /**
     * Tokenizes arguments and validates required and nonrepeatable prefixes independently.
     * Prefixes in {@code allPrefixes} but not {@code requiredPrefixes} may be omitted;
     * prefixes not in {@code nonRepeatablePrefixes} may appear more than once.
     * Empty values are considered present and are left for the calling parser to validate.
     * A nonempty preamble is rejected before checking missing prefixes, followed by duplicates.
     *
     * @param args Arguments following the command word.
     * @param requiredPrefixes Prefixes that must be present, listed in missing-message order.
     * @param nonRepeatablePrefixes Prefixes allowed at most once, listed in duplicate-check order.
     * @param allPrefixes All prefixes to recognize, including every required and nonrepeatable prefix.
     * @return The validated argument multimap, retaining all values for repeatable prefixes.
     * @throws ParseException If a preamble exists, a required prefix is missing, or a nonrepeatable prefix is repeated.
     */
    public static ArgumentMultimap validateStructureWithOptionals(
            String args, Prefix[] requiredPrefixes, Prefix[] nonRepeatablePrefixes, Prefix... allPrefixes)
            throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, allPrefixes);
        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_COMMAND);
        }
        argMultimap.verifyPrefixesPresentFor(requiredPrefixes);
        argMultimap.verifyNoDuplicatePrefixesFor(nonRepeatablePrefixes);
        return argMultimap;
    }
}

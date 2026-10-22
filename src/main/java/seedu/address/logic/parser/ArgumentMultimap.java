package seedu.address.logic.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Stores mapping of prefixes to their respective arguments.
 * Each key may be associated with multiple argument values.
 * Values for a given key are stored in a list, and the insertion ordering is maintained.
 * Keys are unique, but the list of argument values may contain duplicate argument values, i.e. the same argument value
 * can be inserted multiple times for the same prefix.
 */
public class ArgumentMultimap {

    /** Prefixes mapped to their respective arguments */
    private final Map<Prefix, List<String>> argMultimap = new HashMap<>();

    /**
     * Associates the specified argument value with {@code prefix} key in this map.
     * If the map previously contained a mapping for the key, the new value is appended to the list of existing values.
     *
     * @param prefix   Prefix key with which the specified argument value is to be associated
     * @param argValue Argument value to be associated with the specified prefix key
     */
    public void put(Prefix prefix, String argValue) {
        List<String> argValues = getAllValues(prefix);
        argValues.add(argValue);
        argMultimap.put(prefix, argValues);
    }

    /**
     * Returns the last value of {@code prefix}.
     */
    public Optional<String> getValue(Prefix prefix) {
        List<String> values = getAllValues(prefix);
        return values.isEmpty() ? Optional.empty() : Optional.of(values.getLast());
    }

    /**
     * Returns all values of {@code prefix}.
     * If the prefix does not exist or has no values, this will return an empty list.
     * Modifying the returned list will not affect the underlying data structure of the ArgumentMultimap.
     */
    public List<String> getAllValues(Prefix prefix) {
        if (!argMultimap.containsKey(prefix)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(argMultimap.get(prefix));
    }

    /**
     * Returns the preamble (text before the first valid prefix). Trims any leading/trailing spaces.
     */
    public String getPreamble() {
        return getValue(new Prefix("")).orElse("");
    }

    /**
     * Verifies that each supplied prefix appears at most once, checking in the supplied order.
     *
     * @param prefixes Prefixes that must not be repeated.
     * @throws ParseException If a prefix is repeated, using the first repeated prefix's duplicate message.
     */
    public void verifyNoDuplicatePrefixesFor(Prefix... prefixes) throws ParseException {
        for (Prefix prefix : prefixes) {
            if (argMultimap.containsKey(prefix) && argMultimap.get(prefix).size() > 1) {
                throw new ParseException(prefix.getDuplicateMessage());
            }
        }
    }

    /**
     * Verifies that each supplied prefix is present, reporting each missing prefix once in the supplied order.
     * A prefix with an empty value is still considered present.
     *
     * @param prefixes Prefixes that must be present.
     * @throws ParseException If any prefixes are missing, combining their messages with line breaks.
     */
    public void verifyPrefixesPresentFor(Prefix... prefixes) throws ParseException {
        List<String> missingMessages = Stream.of(prefixes).distinct()
                .filter(prefix -> getValue(prefix).isEmpty())
                .map(Prefix::getMissingMessage)
                .toList();

        if (!missingMessages.isEmpty()) {
            throw new ParseException(String.join("\n", missingMessages));
        }
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values.
     */
    public boolean arePrefixesPresent(Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> getValue(prefix).isPresent());
    }
}

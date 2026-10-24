package seedu.address.logic.parser;

import java.util.Locale;
import java.util.Objects;

/**
 * A prefix that marks the beginning of an argument in an arguments string.
 * E.g. 't/' in 'addstudent James t/ friend'.
 */
public class Prefix {
    private final String prefix;
    private final String missingMessage;
    private final String duplicateMessage;

    /**
     * Creates a prefix with default missing and duplicate messages.
     *
     * @param prefix Text marking the beginning of an argument.
     */
    public Prefix(String prefix) {
        this(prefix, "Missing required prefix: %1$s", "Prefix %1$s must be specified only once.");
    }

    /**
     * Creates a prefix with custom messages, inserting the prefix text into each format string.
     *
     * @param prefix Text marking the beginning of an argument.
     * @param missingMessageFormat Format string for a missing prefix, with %1$s representing the prefix text.
     * @param duplicateMessageFormat Format string for a repeated prefix, with %1$s representing the prefix text.
     * @throws NullPointerException If either message format is null.
     * @throws java.util.IllegalFormatException If either message format is invalid.
     */
    public Prefix(String prefix, String missingMessageFormat, String duplicateMessageFormat) {
        this.prefix = prefix;
        this.missingMessage = missingMessageFormat.formatted(prefix);
        this.duplicateMessage = duplicateMessageFormat.formatted(prefix);
    }

    public String getPrefix() {
        return prefix;
    }

    /**
     * Returns the message for a missing prefix.
     *
     * @return The formatted missing-prefix message.
     */
    public String getMissingMessage() {
        return missingMessage;
    }

    /**
     * Returns the message for a repeated prefix.
     *
     * @return The formatted duplicate-prefix message.
     */
    public String getDuplicateMessage() {
        return duplicateMessage;
    }

    @Override
    public String toString() {
        return getPrefix();
    }

    @Override
    public int hashCode() {
        return prefix == null ? 0 : prefix.toLowerCase(Locale.ROOT).hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Prefix otherPrefix)) {
            return false;
        }

        return Objects.equals(prefix == null ? null : prefix.toLowerCase(Locale.ROOT),
                otherPrefix.prefix == null ? null : otherPrefix.prefix.toLowerCase(Locale.ROOT));
    }
}

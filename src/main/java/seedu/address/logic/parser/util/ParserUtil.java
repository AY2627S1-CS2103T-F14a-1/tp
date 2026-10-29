package seedu.address.logic.parser.util;


import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Contains utility method used for parsing common string in the various *Parser classes.
 */
public final class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX =
            "Index must be a positive integer without a sign or decimal point.";

    public static final String MESSAGE_SEE_USAGE = "\nSee proper usage below:\n\n";

    /**
     * Removes whitespace and parses {@code oneBasedIndex} into an {@code Index}.
     *
     * @param oneBasedIndex Student index entered by the user.
     * @return Parsed student index.
     * @throws NullPointerException If the input is null.
     * @throws ParseException If the specified index is not a non-zero unsigned integer.
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String compactIndex = StringUtil.removeWhitespace(oneBasedIndex);
        if (!StringUtil.isNonZeroUnsignedInteger(compactIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(compactIndex));
    }
}

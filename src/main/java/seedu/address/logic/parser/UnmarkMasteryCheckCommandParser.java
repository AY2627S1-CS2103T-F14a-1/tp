package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NUMBER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_INDEX;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.UnmarkMasteryCheckCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.util.MasteryCheckParserUtil;
import seedu.address.logic.parser.util.ParserUtil;

/**
 * Parses a student index and mastery check number into a command that marks the check incomplete.
 */
public class UnmarkMasteryCheckCommandParser implements Parser<UnmarkMasteryCheckCommand> {

    /**
     * Parses the required student index and single num/ argument using the shared mastery check parser.
     *
     * @param args Arguments following the command word.
     * @return A command containing the validated displayed index and positive mastery check number.
     * @throws NullPointerException If the arguments are null.
     * @throws ParseException If an argument is missing or invalid, or the number prefix is repeated.
     */
    @Override
    public UnmarkMasteryCheckCommand parse(String args) throws ParseException {
        requireNonNull(args);
        try {
            ArgumentMultimap argMultimap = ArgumentStructureValidator.validateStructure(
                    args, PREFIX_STUDENT_INDEX, PREFIX_NUMBER);
            Index index = ParserUtil.parseIndex(argMultimap.getRequiredValue(PREFIX_STUDENT_INDEX));
            int number = MasteryCheckParserUtil.parseMasteryCheckNumber(argMultimap.getRequiredValue(PREFIX_NUMBER));
            return new UnmarkMasteryCheckCommand(index, number);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage()
                    + ParserUtil.MESSAGE_SEE_USAGE + UnmarkMasteryCheckCommand.MESSAGE_USAGE);
        }
    }
}

package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NUMBER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_INDEX;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteMasteryCheckCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.util.MasteryCheckParserUtil;
import seedu.address.logic.parser.util.ParserUtil;

/**
 * Parses a displayed student index and mastery check number into a deletion command.
 */
public class DeleteMasteryCheckCommandParser implements Parser<DeleteMasteryCheckCommand> {

    /**
     * Parses the student index from the id/ prefix and the mastery check number from the num/ prefix.
     * Rejects preambles, missing or repeated prefixes, and invalid index or number text.
     *
     * @param args Arguments following the delete mastery check command word.
     * @return A command targeting the parsed student index and mastery check number.
     * @throws NullPointerException If the arguments are null.
     * @throws ParseException If the index or number text is invalid, or the number prefix is missing or repeated.
     * @throws IllegalArgumentException If the parsed number fails the command constructor's current range check.
     */
    @Override
    public DeleteMasteryCheckCommand parse(String args) throws ParseException {
        requireNonNull(args);
        try {
            ArgumentMultimap argMultimap = ArgumentStructureValidator.validateStructure(
                    args, PREFIX_STUDENT_INDEX, PREFIX_NUMBER);

            Index targetStudentIndex = ParserUtil.parseIndex(argMultimap.getRequiredValue(PREFIX_STUDENT_INDEX));
            int number = MasteryCheckParserUtil.parseMasteryCheckNumber(argMultimap.getRequiredValue(PREFIX_NUMBER));
            return new DeleteMasteryCheckCommand(targetStudentIndex, number);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage()
                    + ParserUtil.MESSAGE_SEE_USAGE + DeleteMasteryCheckCommand.MESSAGE_USAGE);
        }
    }
}

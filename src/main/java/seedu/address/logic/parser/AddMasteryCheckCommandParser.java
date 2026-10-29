package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATETIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NUMBER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_INDEX;

import java.time.LocalDateTime;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddMasteryCheckCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.util.MasteryCheckParserUtil;
import seedu.address.logic.parser.util.ParserUtil;
import seedu.address.model.masterycheck.MasteryCheck;

/**
 * Parses a displayed student index and the number and appointment time of a mastery check to register.
 */
public class AddMasteryCheckCommandParser implements Parser<AddMasteryCheckCommand> {

    /**
     * Parses the student index, mastery check number, and the appointment date-time.
     *
     * @param args Arguments following the command word.
     * @return A command containing the validated index, number, and date-time.
     * @throws NullPointerException If the arguments are null.
     * @throws ParseException If the index, number, or appointment time is invalid, or required prefixes
     *         are missing or repeated.
     */
    @Override
    public AddMasteryCheckCommand parse(String args) throws ParseException {
        requireNonNull(args);
        try {
            ArgumentMultimap argMultimap = ArgumentStructureValidator.validateStructure(
                    args, PREFIX_STUDENT_INDEX, PREFIX_NUMBER, PREFIX_DATETIME);

            Index index = ParserUtil.parseIndex(argMultimap.getRequiredValue(PREFIX_STUDENT_INDEX));
            int number = MasteryCheckParserUtil.parseMasteryCheckNumber(argMultimap.getRequiredValue(PREFIX_NUMBER));
            LocalDateTime dateTime = MasteryCheckParserUtil.parseMasteryCheckDateTime(argMultimap
                    .getRequiredValue(PREFIX_DATETIME));
            return new AddMasteryCheckCommand(index, number, new MasteryCheck(dateTime));
        } catch (ParseException e) {
            throw new ParseException(e.getMessage()
                    + ParserUtil.MESSAGE_SEE_USAGE + AddMasteryCheckCommand.MESSAGE_USAGE);
        }
    }
}

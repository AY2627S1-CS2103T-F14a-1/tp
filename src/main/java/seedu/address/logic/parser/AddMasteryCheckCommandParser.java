package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATETIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NUMBER;

import java.time.LocalDateTime;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddMasteryCheckCommand;
import seedu.address.logic.parser.exceptions.ParseException;
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
        ArgumentMultimap argMultimap = ArgumentStructureValidator.validateStructure(
                args, AddMasteryCheckCommand.MESSAGE_USAGE, PREFIX_INDEX, PREFIX_NUMBER, PREFIX_DATETIME);

        Index index = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_INDEX).get());
        int number = ParserUtil.parseMasteryCheckNumber(argMultimap.getValue(PREFIX_NUMBER).get());
        LocalDateTime dateTime = ParserUtil.parseMasteryCheckDateTime(argMultimap.getValue(PREFIX_DATETIME).get());
        return new AddMasteryCheckCommand(index, number, new MasteryCheck(dateTime));
    }
}

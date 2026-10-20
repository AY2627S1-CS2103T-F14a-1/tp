package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATETIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NUMBER;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddMasteryCheckCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.masterycheck.MasteryCheck;

public class AddMasteryCheckCommandParser implements Parser<AddMasteryCheckCommand> {
    /**
     * Parses the student index, studio week, and studio effort.
     *
     * @param args Arguments following the command word.
     * @return A command containing the validated index and week.
     * @throws NullPointerException If the arguments are null.
     * @throws ParseException If required information is missing or invalid.
     */
    @Override
    public AddMasteryCheckCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NUMBER, PREFIX_DATETIME);
        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddMasteryCheckCommand.MESSAGE_USAGE), pe);
        }

        if (!arePrefixesPresent(argMultimap, PREFIX_NUMBER, PREFIX_DATETIME)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddMasteryCheckCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NUMBER, PREFIX_DATETIME);
        int number = ParserUtil.parseMasteryCheckNumber(argMultimap.getValue(PREFIX_NUMBER).get());
        LocalDateTime dateTime = ParserUtil.parseMasteryCheckDateTime(argMultimap.getValue(PREFIX_DATETIME).get());

        return new AddMasteryCheckCommand(index, number, new MasteryCheck(dateTime));
    }

    /**
     * Returns whether every specified prefix is present.
     * A present prefix may still have an empty value.
     *
     * @param argumentMultimap Tokenized arguments.
     * @param prefixes Required prefixes.
     * @return True if all required prefixes are present.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}

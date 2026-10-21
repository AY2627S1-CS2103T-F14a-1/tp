package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.time.LocalDate;

import seedu.address.logic.commands.AddMissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses the name and calendar deadline of a shared mission. */
public class AddMissionCommandParser implements Parser<AddMissionCommand> {

    public static final String MESSAGE_MISSING_DEADLINE =
            "Specify a deadline in YYYY-MM-DD format using d/DEADLINE.";

    /**
     * Parses exactly one name and deadline without consulting or changing the model.
     *
     * @param args Arguments after the command word.
     * @return Command containing trimmed name and date.
     * @throws ParseException If a field is missing, repeated, empty, or invalid, or a preamble exists.
     */
    @Override
    public AddMissionCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_DATE);

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddMissionCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_DATE);
        String name = ParserUtil.parseMissionName(argMultimap.getValue(PREFIX_NAME).orElse(""));
        if (!argMultimap.arePrefixesPresent(PREFIX_DATE)) {
            throw new ParseException(MESSAGE_MISSING_DEADLINE);
        }
        LocalDate deadline = ParserUtil.parseMissionDeadline(argMultimap.getValue(PREFIX_DATE).get());

        return new AddMissionCommand(name, deadline);
    }
}

package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.time.LocalDate;

import seedu.address.logic.commands.AddMissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses the name and calendar deadline of a shared mission. */
public class AddMissionCommandParser implements Parser<AddMissionCommand> {

    /**
     * Parses exactly one name and deadline without consulting or changing the model.
     *
     * @param args Arguments after the command word.
     * @return Command containing trimmed name and date.
     * @throws ParseException If a field is missing, repeated, empty, or invalid, or a preamble exists.
     */
    @Override
    public AddMissionCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentStructureValidator.validateStructure(
                args, AddMissionCommand.MESSAGE_USAGE, PREFIX_NAME, PREFIX_DATE);

        String name = ParserUtil.parseMissionName(argMultimap.getValue(PREFIX_NAME).get());
        LocalDate deadline = ParserUtil.parseMissionDeadline(argMultimap.getValue(PREFIX_DATE).get());
        return new AddMissionCommand(name, deadline);
    }
}

package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;

import java.time.LocalDate;

import seedu.address.logic.commands.AddMissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.util.MissionParserUtil;
import seedu.address.logic.parser.util.ParserUtil;
import seedu.address.model.mission.MissionId;

/** Parses the ID, name, and calendar deadline of a shared mission. */
public class AddMissionCommandParser implements Parser<AddMissionCommand> {

    /**
     * Parses exactly one mission ID, name, and deadline without consulting or changing the model.
     *
     * @param args Arguments after the command word.
     * @return Command containing the mission ID, trimmed name, and date.
     * @throws ParseException If a field is missing, repeated, empty, or invalid, or a preamble exists.
     */
    @Override
    public AddMissionCommand parse(String args) throws ParseException {
        requireNonNull(args);
        try {
            ArgumentMultimap argMultimap = ArgumentStructureValidator.validateStructure(
                    args, PREFIX_MISSION, PREFIX_NAME, PREFIX_DATE);

            MissionId missionId = MissionParserUtil.parseMissionId(argMultimap.getRequiredValue(PREFIX_MISSION));
            String name = MissionParserUtil.parseMissionName(argMultimap.getRequiredValue(PREFIX_NAME));
            LocalDate deadline = MissionParserUtil.parseMissionDeadline(argMultimap.getRequiredValue(PREFIX_DATE));
            return new AddMissionCommand(missionId, name, deadline);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage() + ParserUtil.MESSAGE_SEE_USAGE + AddMissionCommand.MESSAGE_USAGE);
        }
    }
}

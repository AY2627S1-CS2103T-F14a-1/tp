package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.UnmarkMissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.mission.MissionId;

/**
 * Parses input arguments and creates a new {@code UnmarkMissionCommand}.
 */
public class UnmarkMissionCommandParser implements Parser<UnmarkMissionCommand> {

    /**
     * Parses the student index and mission ID.
     *
     * @param args Arguments following the command word.
     * @return A command containing the validated index and mission ID.
     * @throws NullPointerException If the arguments are null.
     * @throws ParseException If required information is missing or invalid.
     */
    @Override
    public UnmarkMissionCommand parse(String args) throws ParseException {
        requireNonNull(args);
        try {
            ArgumentMultimap argMultimap = ArgumentStructureValidator.validateStructure(
                    args, PREFIX_INDEX, PREFIX_MISSION);

            Index index = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_INDEX).get());
            MissionId missionId = ParserUtil.parseMissionId(argMultimap.getValue(PREFIX_MISSION).get());
            return new UnmarkMissionCommand(index, missionId);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage() + "\n" + UnmarkMissionCommand.MESSAGE_USAGE);
        }
    }
}

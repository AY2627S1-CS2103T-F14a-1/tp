package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.MarkMissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.util.MissionParserUtil;
import seedu.address.logic.parser.util.ParserUtil;
import seedu.address.model.mission.MissionId;

/**
 * Parses input arguments and creates a new {@code MarkMissionCommand}.
 */
public class MarkMissionCommandParser implements Parser<MarkMissionCommand> {

    /**
     * Parses the student index and mission ID.
     *
     * @param args Arguments following the command word.
     * @return A command containing the validated index and mission ID.
     * @throws NullPointerException If the arguments are null.
     * @throws ParseException If required information is missing or invalid.
     */
    @Override
    public MarkMissionCommand parse(String args) throws ParseException {
        requireNonNull(args);
        try {
            ArgumentMultimap argMultimap = ArgumentStructureValidator.validateStructure(
                    args, PREFIX_INDEX, PREFIX_MISSION);

            Index index = ParserUtil.parseIndex(argMultimap.getRequiredValue(PREFIX_INDEX));
            MissionId missionId = MissionParserUtil.parseMissionId(argMultimap.getRequiredValue(PREFIX_MISSION));
            return new MarkMissionCommand(index, missionId);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage()
                    + ParserUtil.MESSAGE_SEE_USAGE + MarkMissionCommand.MESSAGE_USAGE);
        }
    }
}

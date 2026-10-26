package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;

import seedu.address.logic.commands.ViewMissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.mission.MissionId;

/**
 * Parses the mission selected for submission status listing.
 */
public class ViewMissionCommandParser implements Parser<ViewMissionCommand> {

    /**
     * Parses exactly one mission ID without accessing application state.
     *
     * @param args Arguments following the command word.
     * @return Command containing the validated mission ID.
     * @throws ParseException If arguments are missing, repeated, or invalid.
     * @throws NullPointerException If the arguments are null.
     */
    @Override
    public ViewMissionCommand parse(String args) throws ParseException {
        requireNonNull(args);

        try {
            ArgumentMultimap arguments =
                    ArgumentStructureValidator.validateStructure(args, PREFIX_MISSION);
            MissionId missionId =
                    ParserUtil.parseMissionId(arguments.getValue(PREFIX_MISSION).orElseThrow());
            return new ViewMissionCommand(missionId);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage() + "\n\n" + ViewMissionCommand.MESSAGE_USAGE);
        }
    }
}

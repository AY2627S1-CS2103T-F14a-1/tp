package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;
import static seedu.address.logic.parser.MissionParserUtil.parseMissionId;
import static seedu.address.logic.parser.MissionParserUtil.parseMissionOverdueFilter;

import seedu.address.logic.commands.ViewMissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.mission.MissionId;

/**
 * Parses a mission ID and optional overdue filter.
 */
public class ViewMissionCommandParser implements Parser<ViewMissionCommand> {

    public static final String MESSAGE_INVALID_STATUS =
            "Invalid status filter. Use s/overdue or omit s/.";

    /**
     * Parses mission listing arguments without accessing application state.
     *
     * @param args Arguments following the command word.
     * @return Command containing the mission ID and selected filter.
     * @throws ParseException If arguments are missing, repeated, or invalid.
     * @throws NullPointerException If the arguments are null.
     */
    @Override
    public ViewMissionCommand parse(String args) throws ParseException {
        requireNonNull(args);

        try {
            ArgumentMultimap arguments = ArgumentStructureValidator.validateStructureWithOptionals(
                    args,
                    new Prefix[]{PREFIX_MISSION},
                    new Prefix[]{PREFIX_MISSION, PREFIX_STATUS},
                    PREFIX_MISSION, PREFIX_STATUS);
            MissionId missionId =
                    parseMissionId(arguments.getValue(PREFIX_MISSION).orElseThrow());
            boolean overdueOnly =
                    parseMissionOverdueFilter(arguments.getValue(PREFIX_STATUS));
            return new ViewMissionCommand(missionId, overdueOnly);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage() + "\n\n" + ViewMissionCommand.MESSAGE_USAGE);
        }
    }
}

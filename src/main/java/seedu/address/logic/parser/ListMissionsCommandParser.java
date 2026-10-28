package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;

import seedu.address.logic.commands.ListMissionsCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.util.MissionParserUtil;
import seedu.address.model.mission.MissionId;

/**
 * Parses a mission ID for listing submitted students.
 */
public class ListMissionsCommandParser implements Parser<ListMissionsCommand> {

    public static final String MESSAGE_MISSING_MISSION =
            "Specify a mission ID using " + PREFIX_MISSION + "MISSION_ID.";

    /**
     * Parses exactly one mission ID without accessing application state.
     *
     * @param args Arguments following the command word.
     * @return Command containing the canonical mission ID.
     * @throws ParseException If arguments are missing, repeated, or invalid.
     * @throws NullPointerException If the arguments are null.
     */
    @Override
    public ListMissionsCommand parse(String args) throws ParseException {
        requireNonNull(args);
        try {
            ArgumentMultimap argMultimap = ArgumentStructureValidator.validateStructure(args, PREFIX_MISSION);

            MissionId missionId = MissionParserUtil.parseMissionId(argMultimap.getValue(PREFIX_MISSION).get());
            return new ListMissionsCommand(missionId);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage() + "\n\n" + ListMissionsCommand.MESSAGE_USAGE);
        }
    }
}

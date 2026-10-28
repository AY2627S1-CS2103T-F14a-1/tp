package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;

import seedu.address.logic.commands.DeleteMissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.util.MissionParserUtil;
import seedu.address.model.mission.MissionId;

/** Parses a single canonical mission ID without accessing application state. */
public class DeleteMissionCommandParser implements Parser<DeleteMissionCommand> {

    /**
     * Validates prefix structure and delegates ID validation to ParserUtil.
     *
     * @param args Arguments following delmission.
     * @return Deletion request containing a canonical mission ID.
     * @throws ParseException If the prefix or ID is missing, repeated, or invalid.
     */
    @Override
    public DeleteMissionCommand parse(String args) throws ParseException {
        requireNonNull(args);
        try {
            ArgumentMultimap argMultimap = ArgumentStructureValidator.validateStructure(
                    args, PREFIX_MISSION);

            MissionId missionId = MissionParserUtil.parseMissionId(argMultimap.getValue(PREFIX_MISSION).get());
            return new DeleteMissionCommand(missionId);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage() + "\n\n" + DeleteMissionCommand.MESSAGE_USAGE);
        }
    }
}

package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MISSION;

import seedu.address.logic.commands.DeleteMissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.mission.MissionId;

/** Parses a single canonical mission ID without accessing application state. */
public class DeleteMissionCommandParser implements Parser<DeleteMissionCommand> {

    public static final String MESSAGE_MISSING_MISSION = "Specify a mission ID using "
            + PREFIX_MISSION + "MISSION_ID.";

    /**
     * Validates prefix structure and delegates ID validation to MissionParser.
     *
     * @param args Arguments following delmission.
     * @return Deletion request containing a canonical mission ID.
     * @throws ParseException If the prefix or ID is missing, repeated, or invalid.
     */
    @Override
    public DeleteMissionCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_MISSION);
        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteMissionCommand.MESSAGE_USAGE));
        }
        if (argMultimap.getValue(PREFIX_MISSION).isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_MISSION);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_MISSION);

        String value = argMultimap.getValue(PREFIX_MISSION).get().strip();
        if (value.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_MISSION);
        }

        MissionId missionId = MissionParser.parseMissionId(value);
        return new DeleteMissionCommand(missionId);
    }
}

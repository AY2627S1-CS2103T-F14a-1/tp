package seedu.address.logic.parser;

import seedu.address.logic.commands.DeleteMissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/** Parses a single canonical mission ID without accessing application state. */
public class DeleteMissionCommandParser implements Parser<DeleteMissionCommand> {

    /**
     * Validates prefix structure and delegates ID validation to MissionParser.
     *
     * @param args Arguments following delmission.
     * @return Deletion request containing a canonical mission ID.
     * @throws ParseException If the prefix or ID is missing, repeated, or invalid.
     */
    @Override
    public DeleteMissionCommand parse(String args) throws ParseException {
        return new DeleteMissionCommand(MissionParser.parseMissionIdArgument(args, DeleteMissionCommand.MESSAGE_USAGE));
    }
}

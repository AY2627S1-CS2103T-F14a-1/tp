package seedu.address.logic.parser;

import seedu.address.logic.commands.ListMissionsCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses a mission ID for listing submitted students.
 */
public class ListMissionsCommandParser implements Parser<ListMissionsCommand> {

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
        return new ListMissionsCommand(MissionParser.parseMissionIdArgument(args, ListMissionsCommand.MESSAGE_USAGE));
    }
}

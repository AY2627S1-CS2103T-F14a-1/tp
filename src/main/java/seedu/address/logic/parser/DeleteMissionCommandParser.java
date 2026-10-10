package seedu.address.logic.parser;

import seedu.address.logic.commands.DeleteMissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code DeleteMissionCommand}.
 */
public class DeleteMissionCommandParser implements Parser<DeleteMissionCommand> {

    /**
     * Parses the student index and tutorial week.
     *
     * @param args Arguments following the command word.
     * @return A command containing the validated index and week.
     * @throws NullPointerException If the arguments are null.
     * @throws ParseException If required information is missing or invalid.
     */
    @Override
    public DeleteMissionCommand parse(String args) throws ParseException {
        MissionParser arguments = new MissionParser(args, DeleteMissionCommand.MESSAGE_USAGE);
        return new DeleteMissionCommand(arguments.getIndex(), arguments.getWeek());
    }
}

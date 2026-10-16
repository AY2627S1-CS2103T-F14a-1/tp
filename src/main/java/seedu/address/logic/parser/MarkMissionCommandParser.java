package seedu.address.logic.parser;

import seedu.address.logic.commands.MarkMissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;

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
        MissionParser arguments = new MissionParser(args, MarkMissionCommand.MESSAGE_USAGE);
        return new MarkMissionCommand(arguments.getIndex(), arguments.getMissionId());
    }
}

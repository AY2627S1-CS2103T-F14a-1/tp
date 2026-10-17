package seedu.address.logic.parser;

import seedu.address.logic.commands.UnmarkMissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code UnmarkMissionCommand}.
 */
public class UnmarkMissionCommandParser implements Parser<UnmarkMissionCommand> {

    /**
     * Parses the student index and mission ID.
     *
     * @param args Arguments following the command word.
     * @return A command containing the validated index and mission ID.
     * @throws NullPointerException If the arguments are null.
     * @throws ParseException If required information is missing or invalid.
     */
    @Override
    public UnmarkMissionCommand parse(String args) throws ParseException {
        MissionParser arguments = new MissionParser(args, UnmarkMissionCommand.MESSAGE_USAGE);
        return new UnmarkMissionCommand(arguments.getIndex(), arguments.getMissionId());
    }
}

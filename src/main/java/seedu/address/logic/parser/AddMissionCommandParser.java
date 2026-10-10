package seedu.address.logic.parser;

import seedu.address.logic.commands.AddMissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code AddMissionCommand}.
 */
public class AddMissionCommandParser implements Parser<AddMissionCommand> {

    /**
     * Parses the student index and tutorial week.
     *
     * @param args Arguments following the command word.
     * @return A command containing the validated index and week.
     * @throws NullPointerException If the arguments are null.
     * @throws ParseException If required information is missing or invalid.
     */
    @Override
    public AddMissionCommand parse(String args) throws ParseException {
        MissionParser arguments = new MissionParser(args, AddMissionCommand.MESSAGE_USAGE);
        return new AddMissionCommand(arguments.getIndex(), arguments.getWeek());
    }
}

package seedu.address.logic.parser;

import seedu.address.logic.commands.AddStudioRecordCommand;
import seedu.address.logic.commands.AddMissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code AddStudioRecordCommand}.
 */
public class AddStudioRecordCommandParser implements Parser<AddStudioRecordCommand> {

    /**
     * Parses the student index and tutorial week.
     *
     * @param args Arguments following the command word.
     * @return A command containing the validated index and week.
     * @throws NullPointerException If the arguments are null.
     * @throws ParseException If required information is missing or invalid.
     */
    @Override
    public AddStudioRecordCommand parse(String args) throws ParseException {
        WeeklyTaskParser arguments = new WeeklyTaskParser(args, AddStudioRecordCommand.MESSAGE_USAGE);
        return new AddStudioRecordCommand(arguments.getIndex(), arguments.getWeek(), arguments.getStudioRecord());
    }
}

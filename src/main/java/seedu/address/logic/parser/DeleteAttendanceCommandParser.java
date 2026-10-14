package seedu.address.logic.parser;

import seedu.address.logic.commands.DeleteAttendanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code DeleteAttendanceCommand}.
 */
public class DeleteAttendanceCommandParser implements Parser<DeleteAttendanceCommand> {

    /**
     * Parses the student index and tutorial week.
     *
     * @param args Arguments following the command word.
     * @return A command containing the validated index and week.
     * @throws ParseException If required information is missing or invalid.
     */
    @Override
    public DeleteAttendanceCommand parse(String args) throws ParseException {
        WeeklyTaskParser arguments = new WeeklyTaskParser(args, DeleteAttendanceCommand.MESSAGE_USAGE);
        return new DeleteAttendanceCommand(arguments.getIndex(), arguments.getWeek());
    }
}

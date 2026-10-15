package seedu.address.logic.parser;

import seedu.address.logic.commands.DeleteStudioRecordCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@code DeleteStudioRecordCommand}.
 */
public class DeleteAttendanceCommandParser implements Parser<DeleteStudioRecordCommand> {

    /**
     * Parses the student index and tutorial week.
     *
     * @param args Arguments following the command word.
     * @return A command containing the validated index and week.
     * @throws ParseException If required information is missing or invalid.
     */
    @Override
    public DeleteStudioRecordCommand parse(String args) throws ParseException {
        WeeklyTaskParser arguments = new WeeklyTaskParser(args, DeleteStudioRecordCommand.MESSAGE_USAGE);
        return new DeleteStudioRecordCommand(arguments.getIndex(), arguments.getWeek());
    }
}

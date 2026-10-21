package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDIO_EFFORT;
import static seedu.address.logic.parser.ParserUtil.parseEffortScore;

import seedu.address.logic.commands.AddStudioRecordCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.StudioRecord;

/**
 * Parses input arguments and creates a new {@code AddStudioRecordCommand}.
 */
public class AddStudioRecordCommandParser implements Parser<AddStudioRecordCommand> {

    /**
     * Parses the student index, studio week, and studio effort.
     *
     * @param args Arguments following the command word.
     * @return A command containing the validated index and week.
     * @throws NullPointerException If the arguments are null.
     * @throws ParseException If required information is missing or invalid.
     */
    @Override
    public AddStudioRecordCommand parse(String args) throws ParseException {
        WeeklyTaskParser taskParser = new WeeklyTaskParser(args,
                AddStudioRecordCommand.MESSAGE_USAGE, PREFIX_STUDIO_EFFORT);
        int effortScore = parseEffortScore(
                    taskParser.getPrefixValue(PREFIX_STUDIO_EFFORT));
        return new AddStudioRecordCommand(taskParser.getIndex(),
                taskParser.getWeek(), new StudioRecord(effortScore));
    }
}

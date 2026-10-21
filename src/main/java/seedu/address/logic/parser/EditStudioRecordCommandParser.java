package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDIO_EFFORT;

import seedu.address.logic.commands.EditStudioRecordCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.StudioRecord;

/**
 * Parses input arguments and creates a new {@code EditStudioRecordCommandParser}.
 */
public class EditStudioRecordCommandParser implements Parser<EditStudioRecordCommand> {

    public static final String MESSAGE_MISSING_EFFORT =
            "Specify an effort score using " + PREFIX_STUDIO_EFFORT + "EFFORT.";
    public static final String MESSAGE_INVALID_EFFORT =
            StudioRecord.MESSAGE_CONSTRAINTS
                    + " Enter an integer without a sign or decimal point.";

    /**
     * Parses the student index, studio week, and studio effort.
     *
     * @param args Arguments following the command word.
     * @return A command containing the validated index and week.
     * @throws NullPointerException If the arguments are null.
     * @throws ParseException If required information is missing or invalid.
     */
    @Override
    public EditStudioRecordCommand parse(String args) throws ParseException {
        WeeklyTaskParser taskParser = new WeeklyTaskParser(
                args, EditStudioRecordCommand.MESSAGE_USAGE, PREFIX_STUDIO_EFFORT);
        try {
            String effortStr = taskParser.getPrefixValue(PREFIX_STUDIO_EFFORT)
                    .orElseThrow(() -> new ParseException(MESSAGE_MISSING_EFFORT));
            int effortValue = Integer.parseInt(effortStr);
            if (effortValue < 0) {
                throw new ParseException(MESSAGE_INVALID_EFFORT);
            }
            return new EditStudioRecordCommand(taskParser.getIndex(),
                    taskParser.getWeek(), new StudioRecord(effortValue));
        } catch (NumberFormatException e) {
            throw new ParseException(MESSAGE_INVALID_EFFORT);
        }
    }
}

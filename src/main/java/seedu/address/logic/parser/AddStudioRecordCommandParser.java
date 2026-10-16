package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDIO_EFFORT;

import seedu.address.logic.commands.AddStudioRecordCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.StudioRecord;

/**
 * Parses input arguments and creates a new {@code AddStudioRecordCommand}.
 */
public class AddStudioRecordCommandParser implements Parser<AddStudioRecordCommand> {

    public static final String MESSAGE_MISSING_EFFORT =
            "Specify an effort score using ef/EFFORT.";
    public static final String MESSAGE_DUPLICATE_EFFORT =
            "Effort score must be specified only once.";
    public static final String MESSAGE_INVALID_EFFORT =
            StudioRecord.MESSAGE_CONSTRAINTS
                    + " Enter an integer without a sign or decimal point.";

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
        WeeklyTaskParser taskParser = new WeeklyTaskParser(args,
                AddStudioRecordCommand.MESSAGE_USAGE, PREFIX_STUDIO_EFFORT);
        try {
            String effortStr = taskParser.getPrefixValue(PREFIX_STUDIO_EFFORT)
                .orElseThrow(() -> new ParseException(MESSAGE_MISSING_EFFORT));
            int effortValue = Integer.parseInt(effortStr);
            if (effortValue < 0) {
                throw new ParseException(MESSAGE_INVALID_EFFORT);
            }
            return new AddStudioRecordCommand(taskParser.getIndex(),
                    taskParser.getWeek(), new StudioRecord(effortValue));

        } catch (NumberFormatException e) {
            throw new ParseException(MESSAGE_INVALID_EFFORT);
        }
    }
}

package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDIO_EFFORT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditStudioRecordCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.StudioRecord;

/**
 * Parses input arguments and creates a new {@code EditStudioRecordCommandParser}.
 */
public class EditStudioRecordCommandParser implements Parser<EditStudioRecordCommand> {

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
        requireNonNull(args);
        try {
            ArgumentMultimap argMultimap = ArgumentStructureValidator.validateStructure(
                    args, PREFIX_INDEX, PREFIX_WEEK, PREFIX_STUDIO_EFFORT);

            Index index = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_INDEX).get());
            int week = StudioRecordCommandParserUtil.parseWeek(argMultimap.getValue(PREFIX_WEEK).get());
            int effortScore = StudioRecordCommandParserUtil.parseEffortScore(
                    argMultimap.getValue(PREFIX_STUDIO_EFFORT).get());
            return new EditStudioRecordCommand(index, week, new StudioRecord(effortScore));
        } catch (ParseException e) {
            throw new ParseException(e.getMessage() + "\n\n" + EditStudioRecordCommand.MESSAGE_USAGE);
        }
    }
}

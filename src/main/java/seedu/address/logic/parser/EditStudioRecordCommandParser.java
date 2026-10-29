package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDIO_EFFORT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditStudioRecordCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.util.ParserUtil;
import seedu.address.logic.parser.util.StudioRecordParserUtil;
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

            Index index = ParserUtil.parseIndex(argMultimap.getRequiredValue(PREFIX_INDEX));
            int week = StudioRecordParserUtil.parseWeek(argMultimap.getRequiredValue(PREFIX_WEEK));
            int effortScore = StudioRecordParserUtil.parseEffortScore(
                    argMultimap.getRequiredValue(PREFIX_STUDIO_EFFORT));
            return new EditStudioRecordCommand(index, week, new StudioRecord(effortScore));
        } catch (ParseException e) {
            throw new ParseException(e.getMessage()
                    + ParserUtil.MESSAGE_SEE_USAGE + EditStudioRecordCommand.MESSAGE_USAGE);
        }
    }
}

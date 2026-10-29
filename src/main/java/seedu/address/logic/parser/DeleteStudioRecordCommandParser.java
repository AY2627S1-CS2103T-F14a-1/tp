package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteStudioRecordCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.util.ParserUtil;
import seedu.address.logic.parser.util.StudioRecordParserUtil;

/**
 * Parses input arguments and creates a new {@code DeleteStudioRecordCommand}.
 */
public class DeleteStudioRecordCommandParser implements Parser<DeleteStudioRecordCommand> {

    /**
     * Parses the student index and tutorial week.
     *
     * @param args Arguments following the command word.
     * @return A command containing the validated index and week.
     * @throws ParseException If required information is missing or invalid.
     */
    @Override
    public DeleteStudioRecordCommand parse(String args) throws ParseException {
        requireNonNull(args);
        try {
            ArgumentMultimap argMultimap = ArgumentStructureValidator.validateStructure(
                    args, PREFIX_INDEX, PREFIX_WEEK);

            Index index = ParserUtil.parseIndex(argMultimap.getRequiredValue(PREFIX_INDEX));
            int week = StudioRecordParserUtil.parseWeek(argMultimap.getRequiredValue(PREFIX_WEEK));
            return new DeleteStudioRecordCommand(index, week);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage()
                    + ParserUtil.MESSAGE_SEE_USAGE + DeleteStudioRecordCommand.MESSAGE_USAGE);
        }
    }
}

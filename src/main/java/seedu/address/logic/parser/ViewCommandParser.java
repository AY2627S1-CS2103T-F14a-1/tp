package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_INDEX;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.util.ParserUtil;

/**
 * Parses input arguments and creates a new ViewCommand object
 */
public class ViewCommandParser implements Parser<ViewCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ViewCommand
     * and returns a ViewCommand object for execution.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public ViewCommand parse(String args) throws ParseException {
        requireNonNull(args);
        try {
            ArgumentMultimap argMultimap = ArgumentStructureValidator.validateStructure(
                    args, PREFIX_STUDENT_INDEX);
            Index targetStudentIndex = ParserUtil.parseIndex(argMultimap.getRequiredValue(PREFIX_STUDENT_INDEX));
            return new ViewCommand(targetStudentIndex);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage() + ParserUtil.MESSAGE_SEE_USAGE + ViewCommand.MESSAGE_USAGE);
        }
    }

}

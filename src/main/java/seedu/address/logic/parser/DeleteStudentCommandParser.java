package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_INDEX;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteStudentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.util.ParserUtil;

/**
 * Parses input arguments and creates a new DeleteStudentCommand object
 */
public class DeleteStudentCommandParser implements Parser<DeleteStudentCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteStudentCommand
     * and returns a DeleteStudentCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public DeleteStudentCommand parse(String args) throws ParseException {
        requireNonNull(args);
        try {
            ArgumentMultimap argMultimap = ArgumentStructureValidator.validateStructure(
                    args, PREFIX_STUDENT_INDEX);

            Index index = ParserUtil.parseIndex(argMultimap.getRequiredValue(PREFIX_STUDENT_INDEX));
            return new DeleteStudentCommand(index);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage()
                    + ParserUtil.MESSAGE_SEE_USAGE + DeleteStudentCommand.MESSAGE_USAGE);
        }
    }

}

package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_INDEX;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteNoteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.util.NoteParserUtil;
import seedu.address.logic.parser.util.ParserUtil;

/**
 * Parses input arguments and creates a new DeleteNoteCommand object.
 */
public class DeleteNoteCommandParser implements Parser<DeleteNoteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteNoteCommand
     * and returns a DeleteNoteCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format.
     */
    @Override
    public DeleteNoteCommand parse(String args) throws ParseException {
        requireNonNull(args);
        try {
            ArgumentMultimap argMultimap = ArgumentStructureValidator.validateStructure(
                    args, PREFIX_STUDENT_INDEX, PREFIX_NOTE_INDEX);

            Index index = ParserUtil.parseIndex(argMultimap.getRequiredValue(PREFIX_STUDENT_INDEX));
            Index noteIndex = NoteParserUtil.parseNoteIndex(argMultimap.getRequiredValue(PREFIX_NOTE_INDEX));
            return new DeleteNoteCommand(index, noteIndex);
        } catch (ParseException pe) {
            throw new ParseException(pe.getMessage() + ParserUtil.MESSAGE_SEE_USAGE + DeleteNoteCommand.MESSAGE_USAGE);
        }
    }
}

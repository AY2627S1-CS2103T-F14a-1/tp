package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_ID;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteNoteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

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
                    args, PREFIX_INDEX, PREFIX_NOTE_ID);

            Index index = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_INDEX).get());
            int noteId = ParserUtil.parseNoteId(argMultimap.getValue(PREFIX_NOTE_ID).get());
            return new DeleteNoteCommand(index, noteId);
        } catch (ParseException pe) {
            throw new ParseException(pe.getMessage() + "\n" + DeleteNoteCommand.MESSAGE_USAGE);
        }
    }
}

package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_TEXT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddNoteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.Note;

/**
 * Parses input arguments and creates a new AddNoteCommand object.
 */
public class AddNoteCommandParser implements Parser<AddNoteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddNoteCommand
     * and returns an AddNoteCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format.
     */
    @Override
    public AddNoteCommand parse(String args) throws ParseException {
        requireNonNull(args);
        try {
            ArgumentMultimap argMultimap = ArgumentStructureValidator.validateStructure(
                    args, PREFIX_INDEX, PREFIX_NOTE_TEXT);

            Index index = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_INDEX).get());
            Note note = NoteParserUtil.parseNoteText(argMultimap.getValue(PREFIX_NOTE_TEXT).get());
            return new AddNoteCommand(index, note);
        } catch (ParseException pe) {
            throw new ParseException(pe.getMessage() + "\n\n" + AddNoteCommand.MESSAGE_USAGE);
        }
    }
}

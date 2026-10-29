package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_TEXT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditNoteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.parser.util.NoteParserUtil;
import seedu.address.logic.parser.util.ParserUtil;
import seedu.address.model.student.Note;

/**
 * Parses input arguments and creates a new EditNoteCommand object.
 */
public class EditNoteCommandParser implements Parser<EditNoteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditNoteCommand
     * and returns an EditNoteCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format.
     */
    @Override
    public EditNoteCommand parse(String args) throws ParseException {
        requireNonNull(args);
        try {
            ArgumentMultimap argMultimap = ArgumentStructureValidator.validateStructure(
                    args, PREFIX_INDEX, PREFIX_NOTE_INDEX, PREFIX_NOTE_TEXT);

            Index index = ParserUtil.parseIndex(argMultimap.getValue(PREFIX_INDEX).get());
            Index noteIndex = NoteParserUtil.parseNoteIndex(argMultimap.getValue(PREFIX_NOTE_INDEX).get());
            Note note = NoteParserUtil.parseNoteText(argMultimap.getValue(PREFIX_NOTE_TEXT).get());
            return new EditNoteCommand(index, noteIndex, note);
        } catch (ParseException pe) {
            throw new ParseException(pe.getMessage() + "\n\n" + EditNoteCommand.MESSAGE_USAGE);
        }
    }
}

package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_TEXT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STUDENT_INDEX;

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
                    args, PREFIX_STUDENT_INDEX, PREFIX_NOTE_INDEX, PREFIX_NOTE_TEXT);

            Index index = ParserUtil.parseIndex(argMultimap.getRequiredValue(PREFIX_STUDENT_INDEX));
            Index noteIndex = NoteParserUtil.parseNoteIndex(argMultimap.getRequiredValue(PREFIX_NOTE_INDEX));
            Note note = NoteParserUtil.parseNoteText(argMultimap.getRequiredValue(PREFIX_NOTE_TEXT));
            return new EditNoteCommand(index, noteIndex, note);
        } catch (ParseException pe) {
            throw new ParseException(pe.getMessage() + ParserUtil.MESSAGE_SEE_USAGE + EditNoteCommand.MESSAGE_USAGE);
        }
    }
}

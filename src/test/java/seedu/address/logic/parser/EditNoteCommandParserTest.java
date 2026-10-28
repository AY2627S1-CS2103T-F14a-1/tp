package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_NOTE;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditNoteCommand;
import seedu.address.logic.parser.util.ParserUtil;
import seedu.address.model.student.Note;
import seedu.address.model.student.RegisteredNotes;

/** Tests parsing arguments for {@link EditNoteCommand}. */
public class EditNoteCommandParserTest {

    private final EditNoteCommandParser parser = new EditNoteCommandParser();

    @Test
    public void parse_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validArguments_returnsExpectedCommand() {
        EditNoteCommand expected = new EditNoteCommand(
                INDEX_FIRST_STUDENT, INDEX_THIRD_NOTE, new Note("Review lecture notes"));

        assertParseSuccess(parser, "id/1 ni/3 nt/Review lecture notes", expected);
        assertParseSuccess(parser, " ni/ 3 nt/ Review lecture notes id/ 1 ", expected);
    }

    @Test
    public void parse_invalidIndex_reportsIndexConstraint() {
        for (String index : List.of("", "0", "-1", "abc", "1 extra", "2147483648")) {
            assertParseFailure(parser, "id/" + index + " ni/3 nt/Valid note",
                    ParserUtil.MESSAGE_INVALID_INDEX + "\n\n" + EditNoteCommand.MESSAGE_USAGE);
        }
    }

    @Test
    public void parse_invalidNoteIndex_reportsApplicableConstraint() {
        for (String noteIndex : List.of("", "0", "-1", "1.5", "abc", "2147483648")) {
            assertParseFailure(parser, "id/1 ni/" + noteIndex + " nt/Valid note",
                    ParserUtil.MESSAGE_INVALID_INDEX + "\n\n" + EditNoteCommand.MESSAGE_USAGE);
        }
        assertParseFailure(parser, "id/1 ni/11 nt/Valid note",
                RegisteredNotes.MESSAGE_CONSTRAINTS + "\n\n" + EditNoteCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_invalidNoteText_reportsNoteTextConstraint() {
        for (String noteText : List.of("", "   ", "Invalid!", "a".repeat(Note.MAX_NOTE_LENGTH + 1))) {
            assertParseFailure(parser, "id/1 ni/3 nt/" + noteText,
                    Note.MESSAGE_CONSTRAINTS + "\n\n" + EditNoteCommand.MESSAGE_USAGE);
        }
    }

    @Test
    public void parse_missingAndDuplicatePrefixes_reportsPrefixSpecificMessages() {
        assertParseFailure(parser, "id/1 ni/3",
                CliSyntax.PREFIX_NOTE_TEXT.getMissingMessage() + "\n\n" + EditNoteCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "id/1 nt/Valid note",
                CliSyntax.PREFIX_NOTE_INDEX.getMissingMessage() + "\n\n" + EditNoteCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "ni/3 nt/Valid note",
                CliSyntax.PREFIX_INDEX.getMissingMessage() + "\n\n" + EditNoteCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "id/1 ni/3 nt/Valid note nt/Other note",
                CliSyntax.PREFIX_NOTE_TEXT.getDuplicateMessage() + "\n\n" + EditNoteCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_preamble_throwsParseException() {
        assertParseFailure(parser, "1 ni/3 nt/Valid note",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditNoteCommand.MESSAGE_USAGE));
    }
}

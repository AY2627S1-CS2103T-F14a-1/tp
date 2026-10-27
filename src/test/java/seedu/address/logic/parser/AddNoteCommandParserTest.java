package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddNoteCommand;
import seedu.address.model.student.Note;

/** Tests parsing arguments for {@link AddNoteCommand}. */
public class AddNoteCommandParserTest {

    private final AddNoteCommandParser parser = new AddNoteCommandParser();

    @Test
    public void parse_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validArguments_returnsExpectedCommand() {
        AddNoteCommand expected = new AddNoteCommand(INDEX_FIRST_STUDENT, new Note("Review lecture notes"));

        assertParseSuccess(parser, "id/1 nt/Review lecture notes", expected);
        assertParseSuccess(parser, " nt/ Review lecture notes id/ 1 ", expected);
    }

    @Test
    public void parse_invalidIndex_reportsIndexConstraint() {
        for (String index : List.of("", "0", "-1", "abc", "1 extra", "2147483648")) {
            assertParseFailure(parser, "id/" + index + " nt/Review lecture notes",
                    ParserUtil.MESSAGE_INVALID_INDEX + "\n\n" + AddNoteCommand.MESSAGE_USAGE);
        }
    }

    @Test
    public void parse_invalidNoteText_reportsNoteTextConstraint() {
        for (String noteText : List.of("", " ", "Review!", "a".repeat(Note.MAX_NOTE_LENGTH + 1))) {
            assertParseFailure(parser, "id/1 nt/" + noteText,
                    Note.MESSAGE_CONSTRAINTS + "\n\n" + AddNoteCommand.MESSAGE_USAGE);
        }
    }

    @Test
    public void parse_missingAndDuplicatePrefixes_reportsPrefixSpecificMessages() {
        assertParseFailure(parser, "id/1",
                CliSyntax.PREFIX_NOTE_TEXT.getMissingMessage() + "\n\n" + AddNoteCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "nt/Review lecture notes",
                CliSyntax.PREFIX_INDEX.getMissingMessage() + "\n\n" + AddNoteCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "id/1 nt/Review lecture notes nt/Other note",
                CliSyntax.PREFIX_NOTE_TEXT.getDuplicateMessage() + "\n\n" + AddNoteCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_preamble_throwsParseException() {
        assertParseFailure(parser, "1 nt/Review lecture notes",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddNoteCommand.MESSAGE_USAGE));
    }
}

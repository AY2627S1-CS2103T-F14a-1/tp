package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.util.ParserUtil.MESSAGE_SEE_USAGE;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddNoteCommand;
import seedu.address.logic.parser.util.ParserUtil;
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
        AddNoteCommand punctuationExpected = new AddNoteCommand(
                INDEX_FIRST_STUDENT, new Note("Don't forget: chapter 2!"));

        assertParseSuccess(parser, "id/1 nt/Review lecture notes", expected);
        assertParseSuccess(parser, " nt/ Review lecture notes id/ 1 ", expected);
        assertParseSuccess(parser, "id/1 nt/Don't forget: chapter 2!", punctuationExpected);
    }

    @Test
    public void parse_invalidIndex_reportsIndexConstraint() {
        for (String index : List.of("0", "-1", "abc", "1 extra", "2147483648")) {
            assertParseFailure(parser, "id/" + index + " nt/Review lecture notes",
                    ParserUtil.MESSAGE_INVALID_INDEX + MESSAGE_SEE_USAGE + AddNoteCommand.MESSAGE_USAGE);
        }
    }

    @Test
    public void parse_invalidNoteText_reportsNoteTextConstraint() {
        for (String noteText : List.of("a".repeat(Note.MAX_NOTE_LENGTH + 1))) {
            assertParseFailure(parser, "id/1 nt/" + noteText,
                    Note.MESSAGE_CONSTRAINTS + MESSAGE_SEE_USAGE + AddNoteCommand.MESSAGE_USAGE);
        }
    }

    @Test
    public void parse_missingAndDuplicatePrefixes_reportsPrefixSpecificMessages() {
        assertParseFailure(parser, "id/1",
                CliSyntax.PREFIX_NOTE_TEXT.getMissingMessage() + MESSAGE_SEE_USAGE + AddNoteCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "nt/Review lecture notes",
                CliSyntax.PREFIX_INDEX.getMissingMessage() + MESSAGE_SEE_USAGE + AddNoteCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "id/1 nt/Review lecture notes nt/Other note",
                CliSyntax.PREFIX_NOTE_TEXT.getDuplicateMessage() + MESSAGE_SEE_USAGE + AddNoteCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_preamble_throwsParseException() {
        assertParseFailure(parser, "1 nt/Review lecture notes",
                MESSAGE_INVALID_COMMAND + MESSAGE_SEE_USAGE + AddNoteCommand.MESSAGE_USAGE);
    }
}

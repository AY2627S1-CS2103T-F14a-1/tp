package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteNoteCommand;
import seedu.address.model.student.RegisteredNotes;

/** Tests parsing arguments for {@link DeleteNoteCommand}. */
public class DeleteNoteCommandParserTest {

    private final DeleteNoteCommandParser parser = new DeleteNoteCommandParser();

    @Test
    public void parse_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validArguments_returnsExpectedCommand() {
        DeleteNoteCommand expected = new DeleteNoteCommand(INDEX_FIRST_STUDENT, 3);

        assertParseSuccess(parser, "id/1 ni/3", expected);
        assertParseSuccess(parser, " ni/ 3 id/ 1 ", expected);
    }

    @Test
    public void parse_invalidIndex_reportsIndexConstraint() {
        for (String index : List.of("", "0", "-1", "abc", "1 extra", "2147483648")) {
            assertParseFailure(parser, "id/" + index + " ni/3",
                    ParserUtil.MESSAGE_INVALID_INDEX + "\n" + DeleteNoteCommand.MESSAGE_USAGE);
        }
    }

    @Test
    public void parse_invalidNoteId_reportsNoteIdConstraint() {
        for (String noteId : List.of("", "0", "11", "-1", "1.5", "abc", "2147483648")) {
            assertParseFailure(parser, "id/1 ni/" + noteId,
                    RegisteredNotes.MESSAGE_CONSTRAINTS + "\n" + DeleteNoteCommand.MESSAGE_USAGE);
        }
    }

    @Test
    public void parse_missingAndDuplicatePrefixes_reportsPrefixSpecificMessages() {
        assertParseFailure(parser, "id/1",
                CliSyntax.PREFIX_NOTE_ID.getMissingMessage() + "\n" + DeleteNoteCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "ni/3",
                CliSyntax.PREFIX_INDEX.getMissingMessage() + "\n" + DeleteNoteCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "id/1 ni/3 ni/4",
                CliSyntax.PREFIX_NOTE_ID.getDuplicateMessage() + "\n" + DeleteNoteCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_preamble_throwsParseException() {
        assertParseFailure(parser, "1 ni/3",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteNoteCommand.MESSAGE_USAGE));
    }
}

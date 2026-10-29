package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.util.ParserUtil.MESSAGE_SEE_USAGE;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_NOTE;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteNoteCommand;
import seedu.address.logic.parser.util.ParserUtil;
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
        DeleteNoteCommand expected = new DeleteNoteCommand(INDEX_FIRST_STUDENT, INDEX_THIRD_NOTE);

        assertParseSuccess(parser, "id/1 ni/3", expected);
        assertParseSuccess(parser, " ni/ 3 id/ 1 ", expected);
    }

    @Test
    public void parse_invalidIndex_reportsIndexConstraint() {
        for (String index : List.of("0", "-1", "abc", "1 extra", "2147483648")) {
            assertParseFailure(parser, "id/" + index + " ni/3",
                    ParserUtil.MESSAGE_INVALID_INDEX + MESSAGE_SEE_USAGE + DeleteNoteCommand.MESSAGE_USAGE);
        }
    }

    @Test
    public void parse_invalidNoteIndex_reportsApplicableConstraint() {
        for (String noteIndex : List.of("0", "-1", "1.5", "abc", "2147483648")) {
            assertParseFailure(parser, "id/1 ni/" + noteIndex,
                    ParserUtil.MESSAGE_INVALID_INDEX + MESSAGE_SEE_USAGE + DeleteNoteCommand.MESSAGE_USAGE);
        }
        assertParseFailure(parser, "id/1 ni/1001",
                RegisteredNotes.MESSAGE_CONSTRAINTS + MESSAGE_SEE_USAGE + DeleteNoteCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_missingAndDuplicatePrefixes_reportsPrefixSpecificMessages() {
        assertParseFailure(parser, "id/1",
                CliSyntax.PREFIX_NOTE_INDEX.getMissingMessage() + MESSAGE_SEE_USAGE + DeleteNoteCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "ni/3",
                CliSyntax.PREFIX_INDEX.getMissingMessage() + MESSAGE_SEE_USAGE + DeleteNoteCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "id/1 ni/3 ni/4",
                CliSyntax.PREFIX_NOTE_INDEX.getDuplicateMessage()
                        + MESSAGE_SEE_USAGE + DeleteNoteCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_preamble_throwsParseException() {
        assertParseFailure(parser, "1 ni/3",
                MESSAGE_INVALID_COMMAND + MESSAGE_SEE_USAGE + DeleteNoteCommand.MESSAGE_USAGE);
    }
}

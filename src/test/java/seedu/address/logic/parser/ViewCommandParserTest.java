package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ViewCommand;

public class ViewCommandParserTest {

    private static final String USAGE_SUFFIX = "\n" + ViewCommand.MESSAGE_USAGE;

    private final ViewCommandParser parser = new ViewCommandParser();

    @Test
    public void parse_validArgs_returnsViewCommand() {
        assertParseSuccess(parser, "id/1", new ViewCommand(INDEX_FIRST_STUDENT));
        assertParseSuccess(parser, "  id/1  ", new ViewCommand(INDEX_FIRST_STUDENT));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "id/0",
                ParserUtil.MESSAGE_INVALID_INDEX + USAGE_SUFFIX);
        assertParseFailure(parser, "id/-1",
                ParserUtil.MESSAGE_INVALID_INDEX + USAGE_SUFFIX);
        assertParseFailure(parser, "id/abc",
                ParserUtil.MESSAGE_INVALID_INDEX + USAGE_SUFFIX);
        assertParseFailure(parser, "",
                CliSyntax.PREFIX_INDEX.getMissingMessage() + USAGE_SUFFIX);
    }
}

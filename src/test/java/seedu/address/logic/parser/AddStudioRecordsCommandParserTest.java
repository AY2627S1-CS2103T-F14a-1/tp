package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.model.student.StudioRecords.MIN_WEEK;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddStudioRecordCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class AddStudioRecordsCommandParserTest {

    @Test
    void parse_validArgs_returnsAddAttendanceCommand() throws ParseException {
        AddAttendanceCommandParser parser = new AddAttendanceCommandParser();
        assertParseSuccess(parser, "1 " + PREFIX_WEEK + MIN_WEEK,
                new AddStudioRecordCommand(INDEX_FIRST_STUDENT, MIN_WEEK)
        );
    }
}



package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_WEEK;
import static seedu.address.model.student.Attendance.MIN_WEEK;

import org.junit.jupiter.api.Test;
import seedu.address.logic.commands.AddAttendanceCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class AddAttendanceCommandParserTest {

    @Test
    void parse_validArgs_returnsAddAttendanceCommand() throws ParseException {
        AddAttendanceCommandParser parser = new AddAttendanceCommandParser();
        assertTrue(parser.parse("1 " + PREFIX_WEEK + MIN_WEEK) instanceof AddAttendanceCommand);
    }
}



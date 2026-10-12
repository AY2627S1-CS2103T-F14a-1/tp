package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.UnmarkMissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.mission.MissionId;

/** Tests mission submission command syntax. */
public class UnmarkMissionCommandParserTest {

    @Test
    public void parse_validArguments_returnsCommand() throws Exception {
        assertEquals(new UnmarkMissionCommand(INDEX_FIRST_STUDENT, new MissionId("M05")),
                new UnmarkMissionCommandParser().parse(" 1 m/M05 "));
    }

    @Test
    public void parse_invalidArguments_rejectsInput() {
        for (String input : List.of("", "1", "m/M05", "0 m/M05", "-1 m/M05", "01 m/M05", "1.0 m/M05",
                "2147483648 m/M05", "1 m/", "1 m/M5", "1 m/M00", "1 m/M05 extra", "1 x/M05",
                "1 m/M05 m/M06", "1 m/M05 x/test")) {
            assertThrows(ParseException.class, () -> new UnmarkMissionCommandParser().parse(input));
        }
    }
}

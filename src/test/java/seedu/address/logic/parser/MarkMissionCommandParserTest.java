package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.MarkMissionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.mission.MissionId;

/** Tests mission submission command syntax. */
public class MarkMissionCommandParserTest {

    @Test
    public void parse_validArguments_returnsCommand() throws Exception {
        for (String id : List.of("M05", "m05", "M105", "m105")) {
            String canonicalId = id.startsWith("m") ? "M" + id.substring(1) : id;
            assertEquals(new MarkMissionCommand(INDEX_FIRST_STUDENT, new MissionId(canonicalId)),
                    new MarkMissionCommandParser().parse(" 1 m/" + id + " "));
        }
    }

    @Test
    public void parse_invalidArguments_rejectsInput() {
        for (String input : List.of("", "1", "m/M05", "0 m/M05", "-1 m/M05", "01 m/M05", "1.0 m/M05",
                "2147483648 m/M05", "1 m/", "1 m/M5", "1 m/M00", "1 m/M05 extra", "1 x/M05",
                "1 m/M05 m/M06", "1 m/M05 x/test", "1 m/m5", "1 m/m00", "1 m/m005", "1 m/m05 m/M05")) {
            assertThrows(ParseException.class, () -> new MarkMissionCommandParser().parse(input));
        }
    }
}

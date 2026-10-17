package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.mission.MissionId;

/** Tests the shared mission parser's normalization and null contract. */
public class MissionParserTest {

    @Test
    public void constructor_invalidArguments_reportsSpecificMessage() {
        String[][] cases = {
            {"", MissionParser.MESSAGE_MISSING_INDEX},
            {"m/M01", MissionParser.MESSAGE_MISSING_INDEX},
            {"1", MissionParser.MESSAGE_MISSING_MISSION},
            {"1 m/", MissionParser.MESSAGE_MISSING_MISSION},
            {"1 M01", MissionParser.MESSAGE_MISSING_MISSION},
            {"1 m/M01 m/m01", MissionParser.MESSAGE_DUPLICATE_MISSION},
            {"0 m/M01", MissionParser.MESSAGE_INVALID_STUDENT_INDEX},
            {"2147483648 m/M01", MissionParser.MESSAGE_INVALID_STUDENT_INDEX},
            {"1 m/m1", MissionParser.MESSAGE_INVALID_MISSION_ID},
            {"1 m/M00", MissionParser.MESSAGE_INVALID_MISSION_ID},
            {"1 extra m/M01", String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, "usage")},
            {"1 m/M01 extra", String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, "usage")},
            {"1 m/M01 x/test", String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, "usage")},
            {"m/M01 1", String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, "usage")}
        };
        for (String[] testCase : cases) {
            ParseException error = assertThrows(ParseException.class, () ->
                    new MissionParser(testCase[0], "usage"));
            assertEquals(testCase[1], error.getMessage(), testCase[0]);
        }
    }

    @Test
    public void parseStudentIndex_invalidInput_reportsIndexConstraint() {
        for (String input : List.of("", " ", "0", "-1", "+1", "01", "1.0", "one", "1 2", "2147483648")) {
            ParseException error = assertThrows(ParseException.class, () -> MissionParser.parseStudentIndex(input));
            assertEquals(MissionParser.MESSAGE_INVALID_STUDENT_INDEX, error.getMessage());
        }
    }

    @Test
    public void parseStudentIndex_validInput_returnsIndex() throws Exception {
        assertEquals(1, MissionParser.parseStudentIndex(" \t1 ").getOneBased());
        assertEquals(Integer.MAX_VALUE, MissionParser.parseStudentIndex("2147483647").getOneBased());
    }

    @Test
    public void parseMissionId_caseVariants_returnsCanonicalId() throws Exception {
        for (String input : List.of("M01", "m01", " \tm01 ")) {
            assertEquals(new MissionId("M01"), MissionParser.parseMissionId(input));
        }
        assertThrows(NullPointerException.class, () -> MissionParser.parseMissionId(null));
        assertThrows(NullPointerException.class, () -> MissionParser.parseStudentIndex(null));
    }

    @Test
    public void constructor_whitespace_parsesIndexAndMission() throws Exception {
        MissionParser parser = new MissionParser(" \t2 \tm/M105 ", "usage");
        assertEquals(2, parser.getIndex().getOneBased());
        assertEquals(new MissionId("M105"), parser.getMissionId());
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MissionParser(null, "usage"));
        assertThrows(NullPointerException.class, () -> new MissionParser("1 m/M05", null));
    }
}

package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.ViewMissionCommand;
import seedu.address.model.mission.MissionId;

/**
 * Tests mission listing arguments and command routing.
 */
public class ViewMissionCommandParserTest {

    private static final String USAGE_SUFFIX = "\n\n" + ViewMissionCommand.MESSAGE_USAGE;

    private final ViewMissionCommandParser parser = new ViewMissionCommandParser();

    @Test
    public void parse_validMission_returnsCommand() throws Exception {
        ViewMissionCommand expected = new ViewMissionCommand(new MissionId("M03"));

        assertParseSuccess(parser, " m/M03 ", expected);
        assertParseSuccess(parser, "\tM/m03\t", expected);
        assertEquals(expected, new AddressBookParser().parseCommand("VIEWMISSION m/M03"));
    }

    @Test
    public void parse_missingMission_throwsParseException() {
        assertParseFailure(parser, "",
                CliSyntax.PREFIX_MISSION.getMissingMessage() + USAGE_SUFFIX);
    }

    @Test
    public void parse_duplicateMission_throwsParseException() {
        assertParseFailure(parser, "m/M03 m/M03",
                CliSyntax.PREFIX_MISSION.getDuplicateMessage() + USAGE_SUFFIX);
    }

    @Test
    public void parse_preamble_throwsParseException() {
        assertParseFailure(parser, "extra m/M03",
                Messages.MESSAGE_INVALID_COMMAND + USAGE_SUFFIX);
    }

    @Test
    public void parse_invalidMission_throwsParseException() {
        for (String input : new String[]{"m/", "m/M3", "m/M00", "m/M03 extra", "m/M03 x/test"}) {
            assertParseFailure(parser, input, MissionId.MESSAGE_CONSTRAINTS + USAGE_SUFFIX);
        }
    }

    @Test
    public void parse_overdueFilter_returnsFilteredCommand() throws Exception {
        ViewMissionCommand expected = new ViewMissionCommand(new MissionId("M03"), true);

        assertParseSuccess(parser, "m/M03 s/overdue", expected);
        assertParseSuccess(parser, "s/OVERDUE m/m03", expected);
        assertParseSuccess(parser, "\tM/M03\tS/overdue\t", expected);
        assertEquals(expected,
                new AddressBookParser().parseCommand("VIEWMISSION m/M03 s/overdue"));
    }

    @Test
    public void parse_invalidStatus_throwsParseException() {
        for (String input : new String[]{
            "m/M03 s/", "m/M03 s/submitted", "m/M03 s/overdue extra", "m/M03 s/overdue x/test"
        }) {
            assertParseFailure(parser, input,
                    MissionParserUtil.MESSAGE_INVALID_MISSION_STATUS + USAGE_SUFFIX);
        }
    }

    @Test
    public void parse_duplicateStatus_throwsParseException() {
        assertParseFailure(parser, "m/M03 s/overdue s/overdue",
                CliSyntax.PREFIX_STATUS.getDuplicateMessage() + USAGE_SUFFIX);
    }

    @Test
    public void parse_statusWithoutMission_throwsParseException() {
        assertParseFailure(parser, "s/overdue",
                CliSyntax.PREFIX_MISSION.getMissingMessage() + USAGE_SUFFIX);
    }

    @Test
    public void parse_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}

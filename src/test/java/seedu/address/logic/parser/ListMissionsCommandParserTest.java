package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListMissionsCommand;
import seedu.address.model.mission.MissionId;

/**
 * Tests mission ID parsing and submission-list command routing.
 */
public class ListMissionsCommandParserTest {

    private final ListMissionsCommandParser parser = new ListMissionsCommandParser();

    @Test
    public void parse_validMissionId_returnsCommand() throws Exception {
        ListMissionsCommand expectedCommand = new ListMissionsCommand(new MissionId("M01"));

        assertParseSuccess(parser, " m/M01 ", expectedCommand);
        assertParseSuccess(parser, "\tm/m01\t", expectedCommand);
        assertParseSuccess(parser, " M/M01 ", expectedCommand);
        assertEquals(expectedCommand,
                new AddressBookParser().parseCommand("LISTMISSIONS m/m01"));
    }

    @Test
    public void parse_missingMissionId_reportsMissingMission() {
        for (String input : List.of("", " ")) {
            assertParseFailure(parser, input,
                    CliSyntax.PREFIX_MISSION.getMissingMessage());
        }
    }

    @Test
    public void parse_invalidMissionId_reportsIdConstraint() {
        for (String input : List.of("m/", "m/ \t", "m/3", "m/M3", "m/M00", "m/M003",
                "m/M01 extra", "m/M01 x/test", "m/M9223372036854775808")) {
            assertParseFailure(parser, input, MissionId.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_preamble_reportsUsage() {
        String expectedMessage = String.format(
                MESSAGE_INVALID_COMMAND_FORMAT, ListMissionsCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "1 m/M01", expectedMessage);
    }

    @Test
    public void parse_duplicateMissionPrefix_reportsDuplicateField() {
        assertParseFailure(parser, "m/M01 m/M02",
                CliSyntax.PREFIX_MISSION.getDuplicateMessage());
    }
}

package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND;
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

    private static final String USAGE_SUFFIX = "\n" + ListMissionsCommand.MESSAGE_USAGE;

    private final ListMissionsCommandParser parser = new ListMissionsCommandParser();

    @Test
    public void parse_validMissionId_returnsCommand() throws Exception {
        ListMissionsCommand expectedCommand = new ListMissionsCommand(new MissionId("M01"));

        assertParseSuccess(parser, " m/M01 ", expectedCommand);
        assertParseSuccess(parser, "\tm/m01\t", expectedCommand);
        assertEquals(expectedCommand,
                new AddressBookParser().parseCommand("LISTMISSIONS m/m01"));
    }

    @Test
    public void parse_missingMissionId_reportsMissingMission() {
        for (String input : List.of("", " ")) {
            assertParseFailure(parser, input,
                CliSyntax.PREFIX_MISSION.getMissingMessage() + USAGE_SUFFIX);
        }
    }

    @Test
    public void parse_invalidMissionId_reportsIdConstraint() {
        for (String input : List.of("m/", "m/ \t", "m/3", "m/M3", "m/M00", "m/M003",
                "m/M01 extra", "m/M01 x/test", "m/M9223372036854775808")) {
            assertParseFailure(parser, input,
                MissionId.MESSAGE_CONSTRAINTS + USAGE_SUFFIX);
        }
    }

    @Test
    public void parse_preambleOrUppercasePrefix_reportsUsage() {
        String expectedMessage = MESSAGE_INVALID_COMMAND + USAGE_SUFFIX;

        assertParseFailure(parser, "1 m/M01", expectedMessage);
        assertParseFailure(parser, "M/M01", expectedMessage);
    }

    @Test
    public void parse_duplicateMissionPrefix_reportsDuplicateField() {
        assertParseFailure(parser, "m/M01 m/M02",
                CliSyntax.PREFIX_MISSION.getDuplicateMessage() + USAGE_SUFFIX);
    }
}

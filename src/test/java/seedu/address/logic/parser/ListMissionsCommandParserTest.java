package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListMissionCatalogueCommand;
import seedu.address.logic.commands.ListMissionsCommand;
import seedu.address.model.mission.MissionId;
import seedu.address.model.mission.MissionSubmissionStatus;

/**
 * Tests catalogue routing and mission ID parsing for submission listing.
 */
public class ListMissionsCommandParserTest {

    private static final String USAGE_SUFFIX = "\n\n" + ListMissionsCommand.MESSAGE_USAGE;

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
    public void parse_blankArguments_returnsCatalogueCommand() throws Exception {
        for (String input : List.of("", " ", "\t")) {
            assertInstanceOf(ListMissionCatalogueCommand.class, parser.parse(input));
        }

        assertInstanceOf(ListMissionCatalogueCommand.class,
                new AddressBookParser().parseCommand("listmissions"));
        assertInstanceOf(ListMissionCatalogueCommand.class,
                new AddressBookParser().parseCommand("  LISTMISSIONS \t "));
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
    public void parse_preamble_reportsUsage() {
        String expectedMessage = MESSAGE_INVALID_COMMAND + USAGE_SUFFIX;

        assertParseFailure(parser, "1 m/M01", expectedMessage);
    }

    @Test
    public void parse_duplicateMissionPrefix_reportsDuplicateField() {
        assertParseFailure(parser, "m/M01 m/M02",
                CliSyntax.PREFIX_MISSION.getDuplicateMessage() + USAGE_SUFFIX);
    }

    @Test
    public void parse_validStatusFilter_returnsFilteredCommand() {
        MissionId missionId = new MissionId("M01");

        assertParseSuccess(parser, "m/M01 s/submitted",
                ListMissionsCommand.withStatus(missionId, MissionSubmissionStatus.SUBMITTED));
        assertParseSuccess(parser, "S/OVERDUE M/m01",
                ListMissionsCommand.withStatus(missionId, MissionSubmissionStatus.OVERDUE));
    }

    @Test
    public void parse_invalidStatusFilter_reportsStatusConstraint() {
        String expectedMessage = ListMissionsCommandParser.MESSAGE_INVALID_STATUS + USAGE_SUFFIX;

        for (String input : List.of("m/M01 s/", "m/M01 s/unknown",
                "m/M01 s/not_submitted", "m/M01 s/submitted extra")) {
            assertParseFailure(parser, input, expectedMessage);
        }
    }

    @Test
    public void parse_duplicateStatusPrefix_reportsDuplicateField() {
        assertParseFailure(parser, "m/M01 s/submitted s/overdue",
                CliSyntax.PREFIX_STATUS.getDuplicateMessage() + USAGE_SUFFIX);
    }

    @Test
    public void parse_statusWithoutMission_returnsAllMissionQuery() throws Exception {
        assertParseSuccess(parser, "s/submitted",
                ListMissionsCommand.withStatus(MissionSubmissionStatus.SUBMITTED));
        assertParseSuccess(parser, " S/OVERDUE ",
                ListMissionsCommand.withStatus(MissionSubmissionStatus.OVERDUE));
        assertEquals(
                ListMissionsCommand.withStatus(MissionSubmissionStatus.OVERDUE),
                new AddressBookParser().parseCommand("LISTMISSIONS s/overdue"));
    }

    @Test
    public void parse_invalidStatusWithoutMission_reportsStatusConstraint() {
        String expectedMessage = ListMissionsCommandParser.MESSAGE_INVALID_STATUS + USAGE_SUFFIX;

        for (String input : List.of("s/", "s/unknown", "s/submitted extra")) {
            assertParseFailure(parser, input, expectedMessage);
        }
    }

    @Test
    public void parse_statusWithEmptyMission_reportsIdConstraint() {
        assertParseFailure(parser, "s/submitted m/",
                MissionId.MESSAGE_CONSTRAINTS + USAGE_SUFFIX);
    }

    @Test
    public void parse_duplicateStatusWithoutMission_reportsDuplicateField() {
        assertParseFailure(parser, "s/submitted s/overdue",
                CliSyntax.PREFIX_STATUS.getDuplicateMessage() + USAGE_SUFFIX);
    }
}

package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditMissionCommand;
import seedu.address.logic.commands.EditMissionCommand.EditMissionDescriptor;
import seedu.address.model.mission.MissionId;

/**
 * Tests mission-edit field validation and command routing.
 */
public class EditMissionCommandParserTest {

    private final EditMissionCommandParser parser = new EditMissionCommandParser();

    @Test
    public void parse_nameAndDeadline_returnsCommand() throws Exception {
        EditMissionDescriptor descriptor = new EditMissionDescriptor();
        descriptor.setName("New Runes");
        descriptor.setDeadline(LocalDate.of(2026, 10, 22));
        EditMissionCommand expectedCommand = new EditMissionCommand(new MissionId("M01"), descriptor);

        assertParseSuccess(parser, "m/M01 n/  New   Runes  d/2026-10-22", expectedCommand);
        assertEquals(expectedCommand, new AddressBookParser().parseCommand(
                "EDITMISSION d/2026-10-22 n/New Runes m/m01"));
    }

    @Test
    public void parse_singleField_createsDescriptorWithOnlySuppliedField() {
        EditMissionDescriptor nameDescriptor = new EditMissionDescriptor();
        nameDescriptor.setName("New Runes");
        assertParseSuccess(parser, "m/M01 n/New Runes",
                new EditMissionCommand(new MissionId("M01"), nameDescriptor));

        EditMissionDescriptor deadlineDescriptor = new EditMissionDescriptor();
        deadlineDescriptor.setDeadline(LocalDate.of(2000, 2, 29));
        assertParseSuccess(parser, "d/2000-02-29 m/M01",
                new EditMissionCommand(new MissionId("M01"), deadlineDescriptor));
    }

    @Test
    public void parse_missingIdOrPreamble_reportsUsage() {
        String expectedMessage = String.format(
                MESSAGE_INVALID_COMMAND_FORMAT, EditMissionCommand.MESSAGE_USAGE);

        for (String input : List.of("", "n/New", "extra m/M01 n/New", "M/M01 n/New")) {
            assertParseFailure(parser, input, expectedMessage);
        }
    }

    @Test
    public void parse_noReplacementFields_reportsNotEdited() {
        assertParseFailure(parser, "m/M01", EditMissionCommand.MESSAGE_NOT_EDITED);
    }

    @Test
    public void parse_invalidFields_reportsSpecificConstraint() {
        assertParseFailure(parser, "m/3 n/New", MissionId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "m/ n/New", MissionId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "m/M01 n/", ParserUtil.MESSAGE_INVALID_MISSION_NAME);
        assertParseFailure(parser, "m/M01 n/\u00a0", ParserUtil.MESSAGE_INVALID_MISSION_NAME);
        assertParseFailure(parser, "m/M01 d/", ParserUtil.MESSAGE_INVALID_MISSION_DEADLINE);
        assertParseFailure(parser, "m/M01 d/2026-1-01",
                ParserUtil.MESSAGE_INVALID_MISSION_DEADLINE);
        assertParseFailure(parser, "m/M01 d/2026-02-30",
                ParserUtil.MESSAGE_INVALID_MISSION_CALENDAR_DATE);
    }

    @Test
    public void parse_duplicateFields_reportsDuplicatePrefix() {
        assertParseFailure(parser, "m/M01 m/M02 n/New",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_MISSION));
        assertParseFailure(parser, "m/M01 n/First n/Second",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NAME));
        assertParseFailure(parser, "m/M01 d/2026-10-20 d/2026-10-21",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_DATE));
    }
}

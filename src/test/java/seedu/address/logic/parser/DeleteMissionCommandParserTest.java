package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.LogicManager;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.DeleteMissionCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.mission.MissionId;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;

/** Tests numeric mission parsing and ensures pending execution cannot alter saved data. */
public class DeleteMissionCommandParserTest {

    @TempDir
    public Path directory;

    @Test
    public void parse_validNumbers_constructsCanonicalIdWithoutExistenceCheck() throws Exception {
        for (String value : List.of("M03", "m03")) {
            assertEquals(new DeleteMissionCommand(new MissionId("M03")),
                    new DeleteMissionCommandParser().parse(" m/ \t" + value + " "));
        }
        assertEquals(new DeleteMissionCommand(new MissionId("M9223372036854775807")),
                new AddressBookParser().parseCommand("DELMISSION m/M9223372036854775807"));
        assertEquals(new DeleteMissionCommand(new MissionId("M01")),
                new AddressBookParser().parseCommand("delmission m/M01"));
    }

    @Test
    public void parse_invalidNumbers_rejectsInput() {
        for (String value : List.of("0", "000", "-3", "+3", "3.0", "3e0", "abc", "3", "M3", "M003", "M00", "３",
                "M9223372036854775808", "999999999999999999999999", "3 4", "3 x/test")) {
            ParseException error = assertThrows(ParseException.class, () ->
                    new DeleteMissionCommandParser().parse("m/" + value));
            assertEquals(MissionId.MESSAGE_CONSTRAINTS,
                    error.getMessage());
        }
    }

    @Test
    public void parse_invalidStructure_reportsMissingRepeatedOrPreamble() {
        for (String input : List.of("", " ", "m/", "m/ \t")) {
            ParseException error = assertThrows(ParseException.class, () ->
                    new DeleteMissionCommandParser().parse(input));
            assertEquals(MissionParser.MESSAGE_MISSING_MISSION, error.getMessage());
        }
        assertEquals(Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_MISSION),
                assertThrows(ParseException.class, () ->
                        new DeleteMissionCommandParser().parse("m/M03 m/M03")).getMessage());
        assertEquals(String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, DeleteMissionCommand.MESSAGE_USAGE),
                assertThrows(ParseException.class, () ->
                        new DeleteMissionCommandParser().parse("extra m/M03")).getMessage());
    }

    @Test
    public void execute_unknownMission_doesNotSaveOrMutateData() throws Exception {
        Model model = new ModelManager();
        Path file = directory.resolve("book.json");
        Storage storage = new StorageManager(new JsonAddressBookStorage(file),
                new JsonUserPrefsStorage(directory.resolve("prefs.json")));
        storage.saveAddressBook(model.getAddressBook());
        String saved = Files.readString(file);
        LogicManager logic = new LogicManager(model, storage);
        CommandException error = assertThrows(CommandException.class, () -> logic.execute("delmission m/M03"));
        assertEquals("Mission M03 does not exist.", error.getMessage());
        assertEquals(saved, Files.readString(file));
        assertEquals(storage.readAddressBook().orElseThrow(), model.getAddressBook());
    }
}

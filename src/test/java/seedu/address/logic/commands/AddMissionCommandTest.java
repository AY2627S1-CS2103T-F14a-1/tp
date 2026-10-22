package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.LogicManager;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.TypicalStudents;

/** Tests command parsing through execution and the existing automatic save path. */
public class AddMissionCommandTest {

    @TempDir
    public Path directory;

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddMissionCommand(null, LocalDate.of(2026, 10, 20)));
        assertThrows(NullPointerException.class, () -> new AddMissionCommand("Runes", null));
    }

    @Test
    public void execute_newMission_reportsAllocatedIdAndPreservesStudents() throws Exception {
        Model model = new ModelManager();
        model.addStudent(TypicalStudents.ALICE);
        model.updateFilteredStudentList(student -> false);
        AddMissionCommand command = new AddMissionCommand("Rune Reading", LocalDate.of(2024, 2, 29));
        assertTrue(model.getMissionList().isEmpty());
        CommandResult result = command.execute(model);
        assertEquals("New mission added: M01 · Rune Reading (Due: 2024-02-29).", result.getFeedbackToUser());
        assertEquals(List.of(TypicalStudents.ALICE), model.getAddressBook().getStudentList());
        assertTrue(model.getFilteredStudentList().isEmpty());
        assertEquals(2, model.getAddressBook().getNextMissionNumber());
    }

    @Test
    public void execute_nullModelOrInvalidName_preservesState() {
        AddMissionCommand command = new AddMissionCommand("Runes", LocalDate.of(2026, 10, 20));
        assertThrows(NullPointerException.class, () -> command.execute(null));
        Model model = new ModelManager();
        for (String name : List.of("", " ", "\u00a0")) {
            assertThrows(IllegalArgumentException.class, () ->
                    new AddMissionCommand(name, LocalDate.of(2026, 10, 20)).execute(model));
            assertEquals(new AddressBook(), model.getAddressBook());
        }
    }

    @Test
    public void execute_exhaustedIds_preservesCatalogue() {
        AddressBook book = new AddressBook();
        book.restoreMissions(List.of(), Long.MAX_VALUE);
        Model model = new ModelManager(book, new UserPrefs());
        assertThrows(CommandException.class, () ->
                new AddMissionCommand("Runes", LocalDate.of(2026, 10, 20)).execute(model));
        assertEquals(book, model.getAddressBook());
    }

    @Test
    public void equals_sameAndDifferentArguments_comparesParsedValues() {
        LocalDate deadline = LocalDate.of(2026, 10, 20);
        AddMissionCommand command = new AddMissionCommand("Runes", deadline);
        assertTrue(command.equals(command));
        assertTrue(command.equals(new AddMissionCommand("Runes", deadline)));
        assertFalse(command.equals(new AddMissionCommand("Other", deadline)));
        assertFalse(command.equals(new AddMissionCommand("Runes", deadline.plusDays(1))));
        assertFalse(command.equals(null));
        assertFalse(command.equals("Runes"));
    }

    @Test
    public void toString_validCommand_includesParsedDetails() {
        AddMissionCommand command = new AddMissionCommand("Runes", LocalDate.of(2026, 10, 20));
        assertEquals(AddMissionCommand.class.getCanonicalName() + "{name=Runes, deadline=2026-10-20}",
                command.toString());
    }

    @Test
    public void execute_caseAndWhitespaceDuplicates_preservesNameCounterAndSavedFile() throws Exception {
        Model model = new ModelManager();
        Path file = directory.resolve("duplicates.json");
        Storage storage = new StorageManager(new JsonAddressBookStorage(file),
                new JsonUserPrefsStorage(directory.resolve("prefs.json")));
        LogicManager logic = new LogicManager(model, storage);
        logic.execute("addmission n/Rune  Reading! d/2026-10-20");
        String saved = Files.readString(file);
        for (String name : List.of("rune reading!", " RUNE   READING! ", "Rune\tReading!",
                "Rune\u00a0Reading!", "Rune\u2003Reading!", "\u00a0Rune Reading!\u00a0")) {
            CommandException error = assertThrows(CommandException.class, () ->
                    logic.execute("addmission n/" + name + " d/2027-01-01"));
            assertEquals(Messages.MESSAGE_DUPLICATE_MISSION, error.getMessage());
            assertEquals(1, model.getMissionList().size());
            assertEquals(2, model.getAddressBook().getNextMissionNumber());
            assertEquals(saved, Files.readString(file));
        }
        assertEquals("Rune Reading!", model.getMissionList().getFirst().getName());
        assertEquals("Rune Reading!", storage.readAddressBook().orElseThrow().getMissionList().getFirst().getName());
        Model reloaded = new ModelManager(storage.readAddressBook().orElseThrow(), new seedu.address.model.UserPrefs());
        assertThrows(CommandException.class, () -> new AddMissionCommand("Rune\nReading!",
                LocalDate.of(2026, 10, 21)).execute(reloaded));
        for (String distinct : List.of("Rune Reading", "RuneReading!", "Rune Reading! Part 2")) {
            logic.execute("addmission n/" + distinct + " d/2026-10-20");
        }
        assertEquals(4, model.getMissionList().size());
        assertEquals(5, model.getAddressBook().getNextMissionNumber());
    }

    @Test
    public void execute_validThenInvalidCommands_preservesSavedCatalogue() throws Exception {
        Model model = new ModelManager();
        Storage storage = new StorageManager(new JsonAddressBookStorage(directory.resolve("book.json")),
                new JsonUserPrefsStorage(directory.resolve("prefs.json")));
        LogicManager logic = new LogicManager(model, storage);
        logic.execute("addmission n/Rune Reading d/2026-10-20");
        assertEquals("M01", model.getMissionList().getFirst().getId().toString());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        assertThrows(ParseException.class, () -> logic.execute("addmission n/Other d/2026-02-30"));
        assertThrows(CommandException.class, () -> logic.execute("addmission n/Rune Reading d/2026-10-21"));
        assertEquals(1, model.getMissionList().size());
        assertEquals(2, model.getAddressBook().getNextMissionNumber());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }
}

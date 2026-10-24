package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.mission.Mission;
import seedu.address.model.mission.MissionCatalogue;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.MissionSubmissions;
import seedu.address.model.student.Student;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.TypicalStudents;

/** Regression tests for catalogue invariants and the command boundary. */
public class MissionReviewRegressionTest {
    private static final LocalDate DEADLINE = LocalDate.of(2026, 10, 20);

    @TempDir
    public Path directory;

    @Test
    public void catalogue_duplicateNames_rejectsAtomically() {
        MissionCatalogue catalogue = new MissionCatalogue();
        Mission original = catalogue.add("Rune Reading", DEADLINE);
        for (String name : List.of("rune reading", " RUNE   READING ", "Rune\u00a0Reading")) {
            assertThrows(IllegalArgumentException.class, () -> catalogue.add(name, DEADLINE));
            Mission duplicate = new Mission(new MissionId("M02"), name, DEADLINE);
            IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () ->
                    catalogue.restore(List.of(original, duplicate), 3));
            assertEquals(MissionCatalogue.MESSAGE_DUPLICATE_NAME, error.getMessage());
            assertEquals(List.of(original), catalogue.getMissions());
            assertEquals(2, catalogue.getNextMissionNumber());
        }
        assertEquals(new MissionId("M02"), catalogue.add("Rune Reading!", DEADLINE).getId());
    }

    @Test
    public void readAddressBook_duplicateNames_rejectsWithoutWriting() throws Exception {
        Path file = directory.resolve("duplicates.json");
        String json = """
                {"students":[],"missions":[
                  {"id":"M01","name":"Runes","deadline":"2026-10-20"},
                  {"id":"M02","name":" RUNES ","deadline":"2026-10-21"}
                ],"nextMissionNumber":3}
                """;
        Files.writeString(file, json);
        assertThrows(DataLoadingException.class, () -> new JsonAddressBookStorage(file).readAddressBook());
        assertEquals(json, Files.readString(file));
    }

    @Test
    public void execute_exhaustedCounter_reportsFeedbackWithoutSaving() throws Exception {
        AddressBook book = new AddressBook();
        book.restoreMissions(List.of(), Long.MAX_VALUE);
        Path file = directory.resolve("exhausted.json");
        StorageManager storage = new StorageManager(new JsonAddressBookStorage(file),
                new JsonUserPrefsStorage(directory.resolve("prefs.json")));
        storage.saveAddressBook(book);
        String saved = Files.readString(file);
        ModelManager model = new ModelManager(book, new UserPrefs());
        CommandException error = assertThrows(CommandException.class, () ->
                new LogicManager(model, storage).execute("addmission n/Runes d/2026-10-20"));
        assertEquals(MissionCatalogue.MESSAGE_IDS_EXHAUSTED, error.getMessage());
        assertEquals(book, model.getAddressBook());
        assertEquals(saved, Files.readString(file));
    }

    @Test
    public void parse_usageExample_createsValidCommand() throws Exception {
        String example = AddMissionCommand.MESSAGE_USAGE.split("Example: ", 2)[1];
        assertEquals(new AddMissionCommand("Rune Reading", DEADLINE),
                new AddressBookParser().parseCommand(example));
    }

    @Test
    public void hashCode_equalCommands_supportsHashCollections() {
        MissionId id = new MissionId("M01");
        List<Command> commands = List.of(new AddMissionCommand("Runes", DEADLINE),
                new DeleteMissionCommand(id), new MarkMissionCommand(Index.fromOneBased(1), id),
                new UnmarkMissionCommand(Index.fromOneBased(1), id));
        List<Command> equalCommands = List.of(new AddMissionCommand("Runes", DEADLINE),
                new DeleteMissionCommand(id), new MarkMissionCommand(Index.fromOneBased(1), id),
                new UnmarkMissionCommand(Index.fromOneBased(1), id));
        Set<Command> set = new HashSet<>(commands);
        for (int index = 0; index < commands.size(); index++) {
            assertEquals(commands.get(index).hashCode(), equalCommands.get(index).hashCode());
            assertTrue(set.contains(equalCommands.get(index)));
        }
    }

    @Test
    public void withMissionSubmissions_replacement_preservesOtherFieldsAndOriginal() {
        Student original = TypicalStudents.ALICE;
        MissionSubmissions originalSubmissions = original.getMissionSubmissions();
        MissionSubmissions replacement = new MissionSubmissions(Set.of(new MissionId("M01")));

        Student updated = original.withMissionSubmissions(replacement);

        assertEquals(replacement, updated.getMissionSubmissions());
        assertEquals(originalSubmissions, original.getMissionSubmissions());
        assertEquals(original, updated.withMissionSubmissions(originalSubmissions));
        assertThrows(NullPointerException.class, () -> original.withMissionSubmissions(null));
    }
}

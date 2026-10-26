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
        Mission original = catalogue.add(new MissionId("M01"), "Rune Reading", DEADLINE);
        int nextNumber = 2;
        for (String name : List.of("rune reading", " RUNE   READING ", "Rune\u00a0Reading")) {
            MissionId duplicateId = new MissionId("M%02d".formatted(nextNumber++));
            assertThrows(IllegalArgumentException.class, () -> catalogue.add(duplicateId, name, DEADLINE));
            Mission duplicate = new Mission(duplicateId, name, DEADLINE);
            IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () ->
                    catalogue.restore(List.of(original, duplicate)));
            assertEquals(MissionCatalogue.MESSAGE_DUPLICATE_NAME, error.getMessage());
            assertEquals(List.of(original), catalogue.getMissions());
        }
        assertEquals(new MissionId("M05"),
                catalogue.add(new MissionId("M05"), "Rune Reading!", DEADLINE).getId());
    }

    @Test
    public void readAddressBook_duplicateNames_rejectsWithoutWriting() throws Exception {
        Path file = directory.resolve("duplicates.json");
        String json = """
                {"students":[],"missions":[
                  {"id":"M01","name":"Runes","deadline":"2026-10-20"},
                  {"id":"M02","name":" RUNES ","deadline":"2026-10-21"}
                ]}
                """;
        Files.writeString(file, json);
        assertThrows(DataLoadingException.class, () -> new JsonAddressBookStorage(file).readAddressBook());
        assertEquals(json, Files.readString(file));
    }

    @Test
    public void execute_manualId_savesMission() throws Exception {
        AddressBook book = new AddressBook();
        Path file = directory.resolve("manual-id.json");
        StorageManager storage = new StorageManager(new JsonAddressBookStorage(file),
                new JsonUserPrefsStorage(directory.resolve("prefs.json")));
        storage.saveAddressBook(book);
        String saved = Files.readString(file);
        ModelManager model = new ModelManager(book, new UserPrefs());
        new LogicManager(model, storage).execute("addmission m/M105 n/Runes d/2026-10-20");
        assertEquals(new MissionId("M105"), model.getMissionList().getFirst().getId());
        assertTrue(!saved.equals(Files.readString(file)));
        assertEquals(model.getAddressBook(),
                new JsonAddressBookStorage(file).readAddressBook().orElseThrow());
    }

    @Test
    public void parse_usageExample_createsValidCommand() throws Exception {
        String example = AddMissionCommand.MESSAGE_USAGE.split("Example: ", 2)[1];
        assertEquals(new AddMissionCommand(new MissionId("M01"), "Rune Reading", DEADLINE),
                new AddressBookParser().parseCommand(example));
    }

    @Test
    public void hashCode_equalCommands_supportsHashCollections() {
        MissionId id = new MissionId("M01");
        List<Command> commands = List.of(new AddMissionCommand(id, "Runes", DEADLINE),
                new DeleteMissionCommand(id), new MarkMissionCommand(Index.fromOneBased(1), id),
                new UnmarkMissionCommand(Index.fromOneBased(1), id));
        List<Command> equalCommands = List.of(new AddMissionCommand(id, "Runes", DEADLINE),
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

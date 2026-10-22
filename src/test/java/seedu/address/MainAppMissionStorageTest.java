package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalStudents.ALICE;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.node.ObjectNode;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.logic.LogicManager;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;

/** Tests strict loading and the empty fallback through real command autosaving. */
public class MainAppMissionStorageTest {
    @TempDir
    public Path temporaryDirectory;

    @Test
    public void initModelManager_invalidData_preservesFileUntilSuccessfulCommand() throws Exception {
        Path file = temporaryDirectory.resolve("addressbook.json");
        Storage storage = createStorage(file);
        for (String original : invalidDocuments()) {
            Files.writeString(file, original);
            assertThrows(DataLoadingException.class, storage::readAddressBook, original);
            Model model = initialize(storage);
            assertEquals(new AddressBook(), model.getAddressBook(), original);
            assertEquals(original, Files.readString(file));
            LogicManager logic = new LogicManager(model, storage);
            assertThrows(ParseException.class, () -> logic.execute("unknowncommand"));
            assertEquals(original, Files.readString(file));
            logic.execute("list");
            assertEquals(new AddressBook(), storage.readAddressBook().orElseThrow());
            assertEquals(new AddressBook(), initialize(storage).getAddressBook());
        }
    }

    @Test
    public void initModelManager_invalidDataThenAdd_savesOnlyNewStudent() throws Exception {
        Path file = temporaryDirectory.resolve("addressbook.json");
        Files.writeString(file, "{");
        Storage storage = createStorage(file);
        Model model = initialize(storage);
        new LogicManager(model, storage).execute(
                "addstudent n/New Student p/91234567 e/new@example.com g/1A");
        assertEquals(1, model.getAddressBook().getStudentList().size());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void initModelManager_missingFile_usesSamplesWithoutSaving() throws Exception {
        Path file = temporaryDirectory.resolve("missing.json");
        assertEquals(SampleDataUtil.getSampleAddressBook(), initialize(createStorage(file)).getAddressBook());
        assertFalse(Files.exists(file));
    }

    @Test
    public void initModelManager_validFile_preservesAllDataAndFile() throws Exception {
        Path file = temporaryDirectory.resolve("valid.json");
        Storage storage = createStorage(file);
        AddressBook book = new AddressBook();
        book.addStudent(ALICE);
        book.addMission("Runes", LocalDate.of(2026, 10, 15));
        storage.saveAddressBook(book);
        String original = Files.readString(file);
        assertEquals(book, initialize(storage).getAddressBook());
        assertEquals(original, Files.readString(file));
    }

    /**
     * Builds malformed documents, including valid records followed by invalid records.
     *
     * @return Invalid documents exercising student and catalogue validation.
     * @throws Exception If fixture serialization fails.
     */
    private List<String> invalidDocuments() throws Exception {
        Path fixture = temporaryDirectory.resolve("fixture.json");
        AddressBook book = new AddressBook();
        book.addStudent(ALICE);
        book.addMission("Runes", LocalDate.of(2026, 10, 15));
        new JsonAddressBookStorage(fixture).saveAddressBook(book);
        ObjectNode valid = JsonUtil.fromJsonString(Files.readString(fixture), ObjectNode.class);
        String student = valid.path("students").get(0).toString();
        String mission = valid.path("missions").get(0).toString();
        List<String> documents = new ArrayList<>(List.of("{", "null", "[]", "{}"));
        for (String students : List.of("null", "{}", "[" + student + ",null]",
                "[" + student + "," + student + "]",
                "[" + student + "," + student.replace(ALICE.getEmail().value, "invalid") + "]",
                "[" + student + ",{}]")) {
            documents.add("{\"students\":" + students + ",\"missions\":[],\"nextMissionNumber\":1}");
        }
        ObjectNode nullTag = valid.deepCopy();
        ObjectNode studentWithNullTag = (ObjectNode) nullTag.path("students").get(0);
        studentWithNullTag.putArray("tags").addNull();
        documents.add(nullTag.toString());
        for (String missions : List.of("null", "{}", "[" + mission + ",null]",
                "[" + mission + "," + mission + "]",
                "[" + mission.replace("2026-10-15", "2026-02-30") + "]")) {
            documents.add("{\"students\":[" + student + "],\"missions\":" + missions
                    + ",\"nextMissionNumber\":2}");
        }
        documents.add(valid.deepCopy().put("nextMissionNumber", 0).toString());
        String duplicateName = mission.replace("M01", "M02").replace("Runes", " RUNES ");
        documents.add("{\"students\":[" + student + "],\"missions\":[" + mission + "," + duplicateName
                + "],\"nextMissionNumber\":3}");
        return documents;
    }

    /**
     * Creates isolated storage for a startup test.
     *
     * @param file Saved address book path.
     * @return Storage using temporary data and preferences paths.
     */
    private Storage createStorage(Path file) {
        return new StorageManager(new JsonAddressBookStorage(file),
                new JsonUserPrefsStorage(temporaryDirectory.resolve("prefs.json")));
    }

    /**
     * Exercises startup model initialization without opening a JavaFX window.
     *
     * @param storage Storage to read.
     * @return Initialized application model.
     * @throws Exception If initialization cannot be invoked.
     */
    private Model initialize(Storage storage) throws Exception {
        Method initialize = MainApp.class.getDeclaredMethod("initModelManager", Storage.class, ReadOnlyUserPrefs.class);
        initialize.setAccessible(true);
        return (Model) initialize.invoke(new MainApp(), storage, new UserPrefs());
    }
}

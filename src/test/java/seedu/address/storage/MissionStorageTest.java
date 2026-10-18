package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalStudents.ALICE;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.MissionSubmissions;
import seedu.address.model.student.Student;

/** Tests real JSON persistence, invalid catalogue data, and saved-data validation. */
public class MissionStorageTest {

    @TempDir
    public Path temporaryDirectory;

    @Test
    public void saveAndLoad_catalogueWithMissionSubmissions_preservesAllRecords() throws Exception {
        AddressBook original = new AddressBook();
        Student student = new Student(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
                ALICE.getTags(), ALICE.getStudioGroup(), new MissionSubmissions(Set.of(new MissionId("M105"))),
                ALICE.getRegisteredStudioRecords());
        original.addStudent(student);
        original.restoreMissions(List.of(), 105);
        original.addMission("Curve Manipulation", LocalDate.of(2026, 10, 15));
        Path file = temporaryDirectory.resolve("nested/addressbook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        storage.saveAddressBook(original);
        AddressBook restored = new AddressBook(new JsonAddressBookStorage(file).readAddressBook().orElseThrow());
        assertEquals(original, restored);
        assertEquals(new MissionId("M106"), restored.addMission("Runes", LocalDate.of(2026, 10, 16)).getId());
        assertEquals(Set.of(new MissionId("M105")),
                restored.getStudentList().getFirst().getMissionSubmissions().getSubmittedMissionIds());
    }

    @Test
    public void readAddressBook_missingCatalogue_rejectsWithoutChangingFile() throws Exception {
        Path file = temporaryDirectory.resolve("missingCatalogue.json");
        String incompleteData = "{\"students\":[]}";
        Files.writeString(file, incompleteData);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        assertThrows(DataLoadingException.class, storage::readAddressBook);
        assertEquals(incompleteData, Files.readString(file));
    }

    @Test
    public void readAddressBook_danglingSubmission_rejectsWithoutChangingFile() throws Exception {
        Student student = new Student(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
                ALICE.getTags(), ALICE.getStudioGroup(), new MissionSubmissions(Set.of(new MissionId("M05"))),
                ALICE.getRegisteredStudioRecords());
        String json = "{\"students\":[" + JsonUtil.toJsonString(new JsonAdaptedStudent(student))
                + "],\"missions\":[],\"nextMissionNumber\":1}";
        Path file = temporaryDirectory.resolve("dangling.json");
        Files.writeString(file, json);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        assertThrows(DataLoadingException.class, storage::readAddressBook);
        assertEquals(json, Files.readString(file));
    }

    @Test
    public void toModelType_invalidMissionFields_rejectsRecord() {
        for (String deadline : List.of("2026-02-30", "2026-10-15T23:59", "15/10/2026", "")) {
            assertThrows(IllegalValueException.class, () ->
                    new JsonAdaptedMission("M01", "Runes", deadline).toModelType());
        }
        assertThrows(IllegalValueException.class, () ->
                new JsonAdaptedMission("M1", "Runes", "2026-10-15").toModelType());
        assertThrows(IllegalValueException.class, () ->
                new JsonAdaptedMission("M01", " ", "2026-10-15").toModelType());
        assertThrows(IllegalValueException.class, () ->
                new JsonAdaptedMission("M01", "Runes", null).toModelType());
    }

    @Test
    public void readAddressBook_invalidCounter_preservesSourceFile() throws Exception {
        for (String counter : List.of("0", "-1", "1.5", "\"2\"", "true", "null", "9223372036854775808")) {
            String json = "{\"students\":[],\"missions\":[],\"nextMissionNumber\":" + counter + "}";
            Path file = temporaryDirectory.resolve("invalid.json");
            Files.writeString(file, json);
            JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
            assertThrows(DataLoadingException.class, storage::readAddressBook);
            assertEquals(json, Files.readString(file));
        }
    }

    @Test
    public void toModelType_invalidCatalogue_rejectsReplacement() throws Exception {
        String mission = "{\"id\":\"M01\",\"description\":\"Runes\",\"deadline\":\"2026-10-15\"}";
        for (String json : List.of(
                "{\"students\":[],\"missions\":[" + mission + "," + mission + "],\"nextMissionNumber\":2}",
                "{\"students\":[],\"missions\":[" + mission + "],\"nextMissionNumber\":1}",
                "{\"students\":[],\"missions\":[null],\"nextMissionNumber\":2}",
                "{\"students\":[],\"missions\":[]}",
                "{\"students\":[],\"nextMissionNumber\":2}")) {
            JsonSerializableAddressBook saved = JsonUtil.fromJsonString(json, JsonSerializableAddressBook.class);
            assertThrows(IllegalValueException.class, saved::toModelType);
        }
    }

    @Test
    public void saveAndLoad_emptyCatalogueWithUsedIds_preservesCounter() throws Exception {
        AddressBook original = new AddressBook();
        original.restoreMissions(List.of(), 9);
        Path file = temporaryDirectory.resolve("empty.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        storage.saveAddressBook(original);
        AddressBook restored = new AddressBook(storage.readAddressBook().orElseThrow());
        assertEquals(new MissionId("M09"), restored.addMission("Runes", LocalDate.of(2026, 10, 15)).getId());
    }
}

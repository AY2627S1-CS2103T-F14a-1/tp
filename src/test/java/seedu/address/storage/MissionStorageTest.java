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
        Student student = new Student(ALICE.getName(), ALICE.getMatricNumber(), ALICE.getPhone(), ALICE.getEmail(),
                ALICE.getTags(), ALICE.getStudioGroup(), new MissionSubmissions(Set.of(new MissionId("M105"))),
                ALICE.getRegisteredStudioRecords(),
                ALICE.getRegisteredMasteryChecks(), ALICE.getRegisteredNotes());
        original.addStudent(student);
        original.addMission(new MissionId("M105"), "Curve Manipulation", LocalDate.of(2026, 10, 15));
        Path file = temporaryDirectory.resolve("nested/addressbook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        storage.saveAddressBook(original);
        AddressBook restored = new AddressBook(new JsonAddressBookStorage(file).readAddressBook().orElseThrow());
        assertEquals(original, restored);
        assertEquals(new MissionId("M106"),
                restored.addMission(new MissionId("M106"), "Runes", LocalDate.of(2026, 10, 16)).getId());
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
        Student student = new Student(ALICE.getName(), ALICE.getMatricNumber(), ALICE.getPhone(), ALICE.getEmail(),
                ALICE.getTags(), ALICE.getStudioGroup(), new MissionSubmissions(Set.of(new MissionId("M05"))),
                ALICE.getRegisteredStudioRecords(),
                ALICE.getRegisteredMasteryChecks(), ALICE.getRegisteredNotes());
        String json = "{\"students\":[" + JsonUtil.toJsonString(new JsonAdaptedStudent(student))
                + "],\"missions\":[]}";
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
    public void toModelType_invalidCatalogue_rejectsReplacement() throws Exception {
        String mission = "{\"id\":\"M01\",\"name\":\"Runes\",\"deadline\":\"2026-10-15\"}";
        String catalogueMessage = "Mission catalogue is required.";
        String[][] cases = {
            {"{\"students\":[],\"missions\":[" + mission + "," + mission + "]}",
                "Duplicate mission ID: M01"},
            {"{\"students\":[],\"missions\":[null]}", "Mission records must not be null."},
            {"{\"students\":[]}", catalogueMessage}
        };
        for (String[] testCase : cases) {
            JsonSerializableAddressBook saved = JsonUtil.fromJsonString(testCase[0], JsonSerializableAddressBook.class);
            IllegalValueException error = assertThrows(IllegalValueException.class, saved::toModelType);
            assertEquals(testCase[1], error.getMessage());
        }
    }

    @Test
    public void saveAndLoad_emptyCatalogue_doesNotPersistIdCounter() throws Exception {
        AddressBook original = new AddressBook();
        Path file = temporaryDirectory.resolve("empty.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        storage.saveAddressBook(original);
        AddressBook restored = new AddressBook(storage.readAddressBook().orElseThrow());
        String savedJson = Files.readString(file);
        org.junit.jupiter.api.Assertions.assertFalse(savedJson.contains("nextMissionNumber"));
        MissionId id = new MissionId("M09");
        assertEquals(id, restored.addMission(id, "Runes", LocalDate.of(2026, 10, 15)).getId());
    }
}

package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.HOON;
import static seedu.address.testutil.TypicalStudents.IDA;
import static seedu.address.testutil.TypicalStudents.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.student.Student;
import seedu.address.testutil.StudentBuilder;

public class JsonAddressBookStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonAddressBookStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readAddressBook(null));
    }

    private java.util.Optional<ReadOnlyAddressBook> readAddressBook(String filePath) throws Exception {
        return new JsonAddressBookStorage(Paths.get(filePath)).readAddressBook(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readAddressBook("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("notJsonFormatAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidStudentAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("invalidStudentAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidAndValidStudentAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("invalidAndValidStudentAddressBook.json"));
    }

    @Test
    public void readAndSaveAddressBook_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempAddressBook.json");
        AddressBook original = getTypicalAddressBook();
        JsonAddressBookStorage jsonAddressBookStorage = new JsonAddressBookStorage(filePath);

        // Save in new file and read back
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        ReadOnlyAddressBook readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Modify data, overwrite existing file, and read back
        original.addStudent(HOON);
        original.removeStudent(ALICE);
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Save and read without specifying file path
        original.addStudent(IDA);
        jsonAddressBookStorage.saveAddressBook(original); // file path not specified
        readBack = jsonAddressBookStorage.readAddressBook().get(); // file path not specified
        assertEquals(original, new AddressBook(readBack));

    }

    @Test
    public void saveAddressBook_nullAddressBook_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(null, "SomeFile.json"));
    }

    /**
     * Saves {@code addressBook} at the specified {@code filePath}.
     */
    private void saveAddressBook(ReadOnlyAddressBook addressBook, String filePath) {
        try {
            new JsonAddressBookStorage(Paths.get(filePath))
                    .saveAddressBook(addressBook, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(new AddressBook(), null));
    }

    @Test
    public void readAndSaveAddressBook_missionSubmissions_preservesWeeks() throws Exception {
        Path filePath = testFolder.resolve("missionSubmissions.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);
        Student student = new StudentBuilder(ALICE).withMissionSubmissions(3, 4, 11, 12, 13).build();
        AddressBook original = new AddressBook();
        original.addStudent(student);

        storage.saveAddressBook(original);

        JsonAddressBookStorage reopenedStorage = new JsonAddressBookStorage(filePath);
        ReadOnlyAddressBook loaded = reopenedStorage.readAddressBook().orElseThrow();
        assertEquals(original, new AddressBook(loaded));
        assertEquals(student.getMissionSubmissions(),
                loaded.getStudentList().get(0).getMissionSubmissions());

        Student updatedStudent = new StudentBuilder(student).withMissionSubmissions(3, 4, 7, 11, 12, 13).build();
        original.setStudent(student, updatedStudent);
        reopenedStorage.saveAddressBook(original);

        ReadOnlyAddressBook reloaded = new JsonAddressBookStorage(filePath)
                .readAddressBook().orElseThrow();
        assertEquals(original, new AddressBook(reloaded));
        assertEquals(updatedStudent.getMissionSubmissions(),
                reloaded.getStudentList().get(0).getMissionSubmissions());
    }

    @Test
    public void readAddressBook_invalidMissionSubmissions_throwsDataLoadingException() throws Exception {
        Path filePath = testFolder.resolve("invalidMissionSubmissions.json");
        String json = """
                {
                  "students": [{
                    "name": "Amy Bee",
                    "phone": "85355255",
                    "email": "amy@gmail.com",
                    "address": "123, Jurong West Ave 6, #08-111",
                    "tags": [],
                    "studioGroup": "1A",
                    "missionSubmissions": [3, 14]
                  }]
                }
                """;
        Files.writeString(filePath, json);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);

        assertThrows(DataLoadingException.class, storage::readAddressBook);
        assertEquals(json, Files.readString(filePath));
    }
}

package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.testutil.TypicalStudents.AMY;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.masterycheck.MasteryCheck;
import seedu.address.model.student.Student;
import seedu.address.testutil.StudentBuilder;

/** Tests mastery check persistence through real storage files and malformed-data rejection. */
public class MasteryCheckStorageTest {
    @TempDir
    public Path temporaryDirectory;

    @Test
    public void saveAndLoad_missingParentFolder_preservesAppointmentDetailsAndCompletion() throws Exception {
        LocalDateTime time = LocalDateTime.of(2026, 8, 18, 14, 30, 45, 123456789);
        MasteryCheck complete = new MasteryCheck(time);
        complete.markComplete();
        Student student = new StudentBuilder(AMY).withMasteryChecks(Map.of(1, complete,
                Integer.MAX_VALUE, new MasteryCheck(time.plusDays(1)))).build();
        AddressBook original = new AddressBook();
        original.addStudent(student);
        Path file = temporaryDirectory.resolve("nested/addressbook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
        assertFalse(storage.readAddressBook().isPresent());
        storage.saveAddressBook(original);
        assertEquals(original, new AddressBook(new JsonAddressBookStorage(file).readAddressBook().orElseThrow()));
        assertEquals(original, new AddressBook(storage.readAddressBook().orElseThrow()));
    }

    @Test
    public void readAddressBook_invalidMasteryCheckRecords_preservesSourceFile() throws Exception {
        for (String record : List.of("[0, \"2026-08-18T14:30\", false]",
                "[1, \"2026-02-30T14:30\", false]", "[1, \"2026-08-18T14:30\", \"true\"]", "null")) {
            String studentJson = JsonUtil.toJsonString(new JsonAdaptedStudent(AMY)).replaceFirst(
                    "\"registeredMasteryChecks\"\\s*:\\s*\\[\\s*]", "\"registeredMasteryChecks\": [" + record + "]");
            String json = "{\"students\":[" + studentJson + "],\"missions\":[],\"nextMissionNumber\":1}";
            Path file = temporaryDirectory.resolve("invalid.json");
            Files.writeString(file, json);
            JsonAddressBookStorage storage = new JsonAddressBookStorage(file);
            assertThrows(DataLoadingException.class, storage::readAddressBook);
            assertEquals(json, Files.readString(file));
        }
    }

    @Test
    public void saveAndLoad_removedRegistration_persistsRemovalWithoutChangingOtherChecks() throws Exception {
        MasteryCheck first = new MasteryCheck(LocalDateTime.of(2026, 8, 18, 14, 30));
        MasteryCheck second = new MasteryCheck(first.getAppointmentTime().plusDays(1));
        Student original = new StudentBuilder(AMY).withMasteryChecks(Map.of(1, first, 2, second)).build();
        AddressBook book = new AddressBook();
        book.addStudent(original);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryDirectory.resolve("addressbook.json"));
        storage.saveAddressBook(book);
        Student updated = new StudentBuilder(original).withMasteryChecks(
                original.getRegisteredMasteryChecks().withoutSubmission(1).getMasteryCheckAppointments()).build();
        book.setStudent(original, updated);
        storage.saveAddressBook(book);
        assertEquals(List.of(updated), storage.readAddressBook().orElseThrow().getStudentList());
        assertEquals(Map.of(1, first, 2, second), original.getRegisteredMasteryChecks().getMasteryCheckAppointments());
    }
}

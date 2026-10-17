package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BENSON;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.Student;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.StudentBuilder;

/** Tests deletion across hidden students, persistent records, and allocation state. */
public class DeleteMissionCommandTest {

    @TempDir
    public Path directory;

    @Test
    public void execute_existingMission_cleansAllStudentsAndPersistsWithoutReusingIds() throws Exception {
        AddressBook book = new AddressBook();
        LocalDate deadline = LocalDate.of(2026, 10, 20);
        book.addMission("Runes", deadline);
        book.addMission("Curves", deadline);
        Student alice = new StudentBuilder(ALICE).withMissionSubmissions("M01", "M02").build();
        Student benson = new StudentBuilder(BENSON).withMissionSubmissions("M01").build();
        book.addStudent(alice);
        book.addStudent(benson);
        Model model = new ModelManager(book, new UserPrefs());
        model.updateFilteredStudentList(student -> student.isSameStudent(alice));
        Path file = directory.resolve("book.json");
        Storage storage = new StorageManager(new JsonAddressBookStorage(file),
                new JsonUserPrefsStorage(directory.resolve("prefs.json")));
        LogicManager logic = new LogicManager(model, storage);
        assertEquals("Deleted mission: M01 · Runes (Due: 2026-10-20).",
                logic.execute("delmission m/m01").getFeedbackToUser());
        assertEquals(List.of(new StudentBuilder(alice).withMissionSubmissions("M02").build(),
                new StudentBuilder(benson).withMissionSubmissions().build()), model.getAddressBook().getStudentList());
        assertEquals(1, model.getFilteredStudentList().size());
        assertEquals(new MissionId("M02"), model.getMissionList().getFirst().getId());
        assertEquals(3, model.getAddressBook().getNextMissionNumber());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
        String saved = Files.readString(file);
        CommandException error = assertThrows(CommandException.class, () -> logic.execute("delmission m/M01"));
        assertEquals("Mission M01 does not exist.", error.getMessage());
        assertEquals(saved, Files.readString(file));
        logic.execute("delmission m/M02");
        AddressBook restored = new AddressBook(storage.readAddressBook().orElseThrow());
        assertTrue(restored.getMissionList().isEmpty());
        assertEquals(new MissionId("M03"), restored.addMission("Next", deadline).getId());
    }

    @Test
    public void deleteMission_missingOrNullId_preservesState() {
        AddressBook book = new AddressBook();
        assertThrows(IllegalArgumentException.class, () -> book.deleteMission(new MissionId("M01")));
        assertThrows(NullPointerException.class, () -> book.deleteMission(null));
        assertEquals(new AddressBook(), book);
        assertThrows(NullPointerException.class, () -> new DeleteMissionCommand(null));
        assertThrows(NullPointerException.class, () -> new DeleteMissionCommand(new MissionId("M01")).execute(null));
    }

    @Test
    public void equals_andToString_useMissionIdentity() {
        DeleteMissionCommand command = new DeleteMissionCommand(new MissionId("M01"));
        assertTrue(command.equals(command));
        assertTrue(command.equals(new DeleteMissionCommand(new MissionId("M01"))));
        assertFalse(command.equals(new DeleteMissionCommand(new MissionId("M02"))));
        assertFalse(command.equals(null));
        assertFalse(command.equals("M01"));
        assertEquals(DeleteMissionCommand.class.getCanonicalName() + "{missionId=M01}", command.toString());
    }
}

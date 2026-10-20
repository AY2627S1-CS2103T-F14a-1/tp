package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.EditMissionCommand.EditMissionDescriptor;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.mission.Mission;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.Student;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.StudentBuilder;

/**
 * Tests mission editing, descriptor copying, and automatic persistence.
 */
public class EditMissionCommandTest {

    @TempDir
    public Path directory;

    private Model model;
    private Student john;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        model.addMission("Runes", LocalDate.of(2026, 10, 20));
        model.addMission("Curves", LocalDate.of(2026, 10, 21));
        john = new StudentBuilder().withName("John Doe")
                .withMissionSubmissions("M01").build();
        model.addStudent(john);
        model.updateFilteredStudentList(student -> false);
    }

    @Test
    public void execute_nameAndDeadline_preservesUnrelatedState() throws Exception {
        EditMissionDescriptor descriptor = new EditMissionDescriptor();
        descriptor.setName("New Runes");
        descriptor.setDeadline(LocalDate.of(2026, 10, 22));

        CommandResult result = new EditMissionCommand(new MissionId("M01"), descriptor).execute(model);

        assertEquals("Edited mission: M01 · New Runes (Due: 2026-10-22).",
                result.getFeedbackToUser());
        assertEquals(new Mission(new MissionId("M01"), "New Runes",
                LocalDate.of(2026, 10, 22)), model.getMissionList().getFirst());
        assertEquals("Curves", model.getMissionList().get(1).getName());
        assertEquals(List.of(john), model.getAddressBook().getStudentList());
        assertTrue(model.getFilteredStudentList().isEmpty());
        assertEquals(3, model.getAddressBook().getNextMissionNumber());
    }

    @Test
    public void execute_singleField_preservesUnspecifiedField() throws Exception {
        EditMissionDescriptor nameDescriptor = new EditMissionDescriptor();
        nameDescriptor.setName("New Runes");
        new EditMissionCommand(new MissionId("M01"), nameDescriptor).execute(model);

        assertEquals("New Runes", model.getMissionList().getFirst().getName());
        assertEquals(LocalDate.of(2026, 10, 20),
                model.getMissionList().getFirst().getDeadline());

        EditMissionDescriptor deadlineDescriptor = new EditMissionDescriptor();
        deadlineDescriptor.setDeadline(LocalDate.of(2000, 2, 29));
        new EditMissionCommand(new MissionId("M01"), deadlineDescriptor).execute(model);

        assertEquals("New Runes", model.getMissionList().getFirst().getName());
        assertEquals(LocalDate.of(2000, 2, 29),
                model.getMissionList().getFirst().getDeadline());
    }

    @Test
    public void execute_duplicateNameAndUnknownId_preservesState() {
        EditMissionDescriptor descriptor = new EditMissionDescriptor();
        descriptor.setName("  CURVES  ");

        assertCommandFailure(new EditMissionCommand(new MissionId("M01"), descriptor),
                model, EditMissionCommand.MESSAGE_DUPLICATE_MISSION);
        assertCommandFailure(new EditMissionCommand(new MissionId("M99"), descriptor),
                model, "Mission M99 does not exist.");
    }

    @Test
    public void execute_sameValues_reportsNoChanges() throws Exception {
        AddressBook original = new AddressBook(model.getAddressBook());
        EditMissionDescriptor descriptor = new EditMissionDescriptor();
        descriptor.setName("Runes");

        CommandResult result = new EditMissionCommand(new MissionId("M01"), descriptor).execute(model);

        assertEquals("Mission M01 is unchanged. No changes made.", result.getFeedbackToUser());
        assertEquals(original, model.getAddressBook());
        assertTrue(model.getFilteredStudentList().isEmpty());
    }

    @Test
    public void constructor_descriptorChangedLater_preservesOriginalEdits() throws Exception {
        EditMissionDescriptor descriptor = new EditMissionDescriptor();
        descriptor.setName("New Runes");
        EditMissionCommand command = new EditMissionCommand(new MissionId("M01"), descriptor);
        descriptor.setName("Unexpected Name");

        command.execute(model);

        assertEquals("New Runes", model.getMissionList().getFirst().getName());
    }

    @Test
    public void execute_validThenInvalidEdits_preservesSavedStateAndSubmissions() throws Exception {
        Path file = directory.resolve("book.json");
        Storage storage = new StorageManager(new JsonAddressBookStorage(file),
                new JsonUserPrefsStorage(directory.resolve("prefs.json")));
        LogicManager logic = new LogicManager(model, storage);

        logic.execute("editmission m/M01 n/New Runes d/2026-10-22");
        String saved = Files.readString(file);
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());

        assertThrows(ParseException.class, () -> logic.execute("editmission m/M01 d/2026-02-30"));
        assertEquals(saved, Files.readString(file));

        assertThrows(CommandException.class, () -> logic.execute("editmission m/M01 n/Curves"));
        assertEquals(saved, Files.readString(file));

        AddressBook reloaded = new AddressBook(storage.readAddressBook().orElseThrow());
        assertEquals(model.getAddressBook(), reloaded);
        assertTrue(reloaded.getStudentList().getFirst().getMissionSubmissions()
                .hasSubmission(new MissionId("M01")));
        assertEquals(3, reloaded.getNextMissionNumber());
    }

    @Test
    public void constructorAndExecute_invalidArguments_rejectsInvalidConstruction() {
        MissionId missionId = new MissionId("M01");
        EditMissionDescriptor descriptor = new EditMissionDescriptor();
        descriptor.setName("New Runes");

        assertThrows(NullPointerException.class, () ->
                new EditMissionCommand(null, descriptor));
        assertThrows(NullPointerException.class, () ->
                new EditMissionCommand(missionId, null));
        assertThrows(IllegalArgumentException.class, () ->
                new EditMissionCommand(missionId, new EditMissionDescriptor()));
        assertThrows(NullPointerException.class, () ->
                new EditMissionCommand(missionId, descriptor).execute(null));
    }

    @Test
    public void equals_sameAndDifferentEdits_comparesParsedValues() {
        EditMissionDescriptor descriptor = new EditMissionDescriptor();
        descriptor.setName("New Runes");
        EditMissionCommand command = new EditMissionCommand(new MissionId("M01"), descriptor);
        EditMissionCommand equalCommand = new EditMissionCommand(new MissionId("M01"),
                new EditMissionDescriptor(descriptor));

        assertEquals(command, command);
        assertEquals(command, equalCommand);
        assertEquals(command.hashCode(), equalCommand.hashCode());
        assertNotEquals(command, new EditMissionCommand(new MissionId("M02"), descriptor));

        descriptor.setName("Other Name");
        assertNotEquals(command, new EditMissionCommand(new MissionId("M01"), descriptor));
        assertNotEquals(command, null);
        assertNotEquals(command, "M01");
    }

    @Test
    public void parse_invalidEdit_doesNotChangeModel() {
        AddressBook original = new AddressBook(model.getAddressBook());

        assertThrows(ParseException.class, () -> new AddressBookParser().parseCommand("editmission m/M01 n/"));

        assertEquals(original, model.getAddressBook());
        assertTrue(model.getFilteredStudentList().isEmpty());
    }

    @Test
    public void execute_equivalentOwnName_reportsNoChanges() throws Exception {
        AddressBook original = new AddressBook(model.getAddressBook());
        EditMissionDescriptor descriptor = new EditMissionDescriptor();
        descriptor.setName("  Runes  ");

        CommandResult result =
                new EditMissionCommand(new MissionId("M01"), descriptor).execute(model);

        assertEquals("Mission M01 is unchanged. No changes made.",
                result.getFeedbackToUser());
        assertEquals(original, model.getAddressBook());
        assertTrue(model.getFilteredStudentList().isEmpty());
    }

    @Test
    public void execute_caseOnlyNameChange_updatesDisplayWithoutDuplicateError() throws Exception {
        EditMissionDescriptor descriptor = new EditMissionDescriptor();
        descriptor.setName("RUNES");

        CommandResult result =
                new EditMissionCommand(new MissionId("M01"), descriptor).execute(model);

        assertEquals("Edited mission: M01 · RUNES (Due: 2026-10-20).",
                result.getFeedbackToUser());
        assertEquals("RUNES", model.getMissionList().getFirst().getName());
        assertEquals(LocalDate.of(2026, 10, 20),
                model.getMissionList().getFirst().getDeadline());
        assertEquals(List.of(john), model.getAddressBook().getStudentList());
        assertTrue(model.getFilteredStudentList().isEmpty());
        assertEquals(3, model.getAddressBook().getNextMissionNumber());
    }
}

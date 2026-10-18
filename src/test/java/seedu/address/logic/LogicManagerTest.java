package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.STUDIO_GROUP_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.AMY;
import static seedu.address.testutil.TypicalStudents.BOB;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddStudentCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddStudioRecordCommandParser;
import seedu.address.logic.parser.WeeklyTaskParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudioRecord;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.StudentBuilder;
import seedu.address.testutil.StudentBuilder.StudioRecordSet;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteStudentCommand = "delstudent 9";
        assertCommandException(deleteStudentCommand, MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getFilteredStudentList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredStudentList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addStudentCommand = AddStudentCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + STUDIO_GROUP_DESC_AMY;
        Student expectedStudent = new StudentBuilder(AMY).withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addStudent(expectedStudent);
        assertCommandFailure(addStudentCommand, CommandException.class, expectedMessage, expectedModel);
    }

    @Test
    public void execute_studioSequenceAndReload_preservesStateAfterAcceptedAndRejectedCommands() throws Exception {
        model.addMission("Mission 1", java.time.LocalDate.of(2026, 10, 15));
        Student original = new StudentBuilder(AMY).withMissionSubmissions("M01").withStudioRecords().build();
        model.addStudent(original);
        model.addStudent(BOB);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));

        assertEquals("Added Amy Bee's studio record for tutorial week 3.",
                logic.execute("addstudiorecord 1 w/3 ef/50").getFeedbackToUser());
        Student withWeekThree = new StudentBuilder(original)
                .withStudioRecords(new StudioRecordSet(3, new StudioRecord(50))).build();
        assertStudioState(model, storage, withWeekThree);

        String savedJson = Files.readString(storage.getAddressBookFilePath());
        assertThrows(ParseException.class, AddStudioRecordCommandParser.MESSAGE_INVALID_EFFORT, () ->
                logic.execute("addstudiorecord 1 w/4 ef/-1"));
        assertEquals(savedJson, Files.readString(storage.getAddressBookFilePath()));
        assertStudioState(model, storage, withWeekThree);

        assertEquals("Added Amy Bee's studio record for tutorial week 13.",
                logic.execute("addstudiorecord 1 w/13 ef/0").getFeedbackToUser());
        Student withBothWeeks = new StudentBuilder(original).withStudioRecords(
                new StudioRecordSet(3, new StudioRecord(50)), new StudioRecordSet(13, new StudioRecord(0))).build();
        assertStudioState(model, storage, withBothWeeks);

        Model reloadedModel = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        Logic reloadedLogic = new LogicManager(reloadedModel, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("reloadedPrefs.json"))));
        savedJson = Files.readString(storage.getAddressBookFilePath());
        assertEquals("Amy Bee’s studio has already been recorded for tutorial week 3. No changes made.",
                reloadedLogic.execute("addstudiorecord 1 w/3 ef/100").getFeedbackToUser());
        assertEquals(savedJson, Files.readString(storage.getAddressBookFilePath()));
        assertStudioState(reloadedModel, storage, withBothWeeks);

        assertEquals("Removed Amy Bee's record for studio week 3.",
                reloadedLogic.execute("delstudiorecord 1 w/3").getFeedbackToUser());
        Student withWeekThirteen = new StudentBuilder(original)
                .withStudioRecords(new StudioRecordSet(13, new StudioRecord(0))).build();
        assertStudioState(reloadedModel, storage, withWeekThirteen);

        String savedAfterDeletion = Files.readString(storage.getAddressBookFilePath());
        assertThrows(ParseException.class, WeeklyTaskParser.MESSAGE_INVALID_STUDIO_WEEK, () ->
                reloadedLogic.execute("delstudiorecord 1 w/14"));
        assertEquals(savedAfterDeletion, Files.readString(storage.getAddressBookFilePath()));
        assertStudioState(reloadedModel, storage, withWeekThirteen);
    }

    /**
     * Verifies all student fields in memory and after reloading, including the untouched second student.
     *
     * @param currentModel Model used by the current logic instance.
     * @param storage Storage containing the saved address book.
     * @param expectedStudent Expected first student after the latest command.
     * @throws Exception If the saved address book cannot be read.
     */
    private void assertStudioState(Model currentModel, JsonAddressBookStorage storage, Student expectedStudent)
            throws Exception {
        List<Student> expectedStudents = List.of(expectedStudent, BOB);
        assertEquals(expectedStudents, currentModel.getAddressBook().getStudentList());
        Model reloadedModel = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        assertEquals(expectedStudents, reloadedModel.getAddressBook().getStudentList());
    }

    @Test
    public void execute_markMissionAndReload_preservesSubmissionsAfterRejectedCommands() throws Exception {
        for (int number = 1; number <= 13; number++) {
            model.addMission("Mission " + number, java.time.LocalDate.of(2026, 10, 15));
        }
        model.addStudent(new StudentBuilder(AMY).withMissionSubmissions("M05").build());
        assertEquals("Added Amy Bee’s mission submission for mission M03.",
                logic.execute("MARKMISSION 1 m/M03").getFeedbackToUser());
        Path filePath = temporaryFolder.resolve("addressBook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);
        Model reloadedModel = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        Logic reloadedLogic = new LogicManager(reloadedModel, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("reloadedPrefs.json"))));

        assertEquals(Set.of(new MissionId("M05"), new MissionId("M03")), reloadedModel.getFilteredStudentList().get(0)
                .getMissionSubmissions().getSubmittedMissionIds());
        assertEquals("Amy Bee’s mission submission has already been recorded for mission M03. No changes made.",
                reloadedLogic.execute("markmission 1 m/M03").getFeedbackToUser());

        String savedJson = Files.readString(filePath);
        assertThrows(ParseException.class, MissionId.MESSAGE_CONSTRAINTS, () ->
                reloadedLogic.execute("markmission 1 m/invalid"));
        assertEquals(savedJson, Files.readString(filePath));
        assertEquals(Set.of(new MissionId("M05"), new MissionId("M03")), reloadedModel.getFilteredStudentList().get(0)
                .getMissionSubmissions().getSubmittedMissionIds());

        assertEquals("Added Amy Bee’s mission submission for mission M04.",
                reloadedLogic.execute("markmission 1 m/M04").getFeedbackToUser());
        savedJson = Files.readString(filePath);
        assertThrows(CommandException.class, MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX, () ->
                reloadedLogic.execute("markmission 2 m/M05"));
        assertEquals(savedJson, Files.readString(filePath));
        assertEquals(Set.of(new MissionId("M05"), new MissionId("M03"), new MissionId("M04")),
                storage.readAddressBook().orElseThrow()
                .getStudentList().get(0).getMissionSubmissions().getSubmittedMissionIds());
    }

    @Test
    public void execute_unmarkMissionAndReload_preservesDeletionAndRejectsInvalidInput() throws Exception {
        for (int number = 1; number <= 13; number++) {
            model.addMission("Mission " + number, java.time.LocalDate.of(2026, 10, 15));
        }
        model.addStudent(new StudentBuilder(AMY).withMissionSubmissions("M03", "M13").build());
        assertEquals("Removed Amy Bee’s mission submission for mission M03.",
                logic.execute("UNMARKMISSION 1 m/M03").getFeedbackToUser());
        Path filePath = temporaryFolder.resolve("addressBook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);
        Model reloadedModel = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        Logic reloadedLogic = new LogicManager(reloadedModel, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("reloadedPrefs.json"))));
        assertEquals(Set.of(new MissionId("M13")), reloadedModel.getFilteredStudentList().get(0)
                .getMissionSubmissions().getSubmittedMissionIds());

        String savedJson = Files.readString(filePath);
        assertThrows(ParseException.class, MissionId.MESSAGE_CONSTRAINTS, () ->
                reloadedLogic.execute("unmarkmission 1 m/invalid"));
        assertEquals(savedJson, Files.readString(filePath));
        assertEquals(Set.of(new MissionId("M13")), reloadedModel.getFilteredStudentList().get(0)
                .getMissionSubmissions().getSubmittedMissionIds());
        assertEquals("Amy Bee’s mission submission was already not recorded for mission M03. No changes made.",
                reloadedLogic.execute("unmarkmission 1 m/M03").getFeedbackToUser());
        assertEquals(savedJson, Files.readString(filePath));

        assertEquals("Removed Amy Bee’s mission submission for mission M13.",
                reloadedLogic.execute("unmarkmission 1 m/M13").getFeedbackToUser());
        Student expected = new StudentBuilder(AMY).withMissionSubmissions().build();
        assertEquals(expected, storage.readAddressBook().orElseThrow().getStudentList().get(0));
        savedJson = Files.readString(filePath);
        assertThrows(CommandException.class, MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX, () ->
                reloadedLogic.execute("unmarkmission 2 m/M13"));
        assertEquals(savedJson, Files.readString(filePath));
        assertEquals(expected, reloadedModel.getFilteredStudentList().get(0));
    }
}

package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.AMY;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.MissionParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.Student;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.StudentBuilder;

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
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
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
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        Student expectedStudent = new StudentBuilder(AMY).withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addStudent(expectedStudent);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }

    @Test
    public void execute_addMissionAndReload_preservesSubmissionsAfterRejectedCommands() throws Exception {
        model.addStudent(new StudentBuilder(AMY).withMissionSubmissions(5).build());
        assertEquals("Added Amy Bee’s mission submission for tutorial week 3.",
                logic.execute("ADDMISSION 1 W/3").getFeedbackToUser());
        Path filePath = temporaryFolder.resolve("addressBook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);
        Model reloadedModel = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        Logic reloadedLogic = new LogicManager(reloadedModel, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("reloadedPrefs.json"))));

        assertEquals(Set.of(5, 3), reloadedModel.getFilteredStudentList().get(0)
                .getMissionSubmissions().getSubmittedWeeks());
        assertEquals("Amy Bee’s mission submission has already been recorded for tutorial week 3. No changes made.",
                reloadedLogic.execute("addmission 1 w/3").getFeedbackToUser());

        String savedJson = Files.readString(filePath);
        assertThrows(ParseException.class, MissionParser.MESSAGE_INVALID_TUTORIAL_WEEK, () ->
                reloadedLogic.execute("addmission 1 w/14"));
        assertEquals(savedJson, Files.readString(filePath));
        assertEquals(Set.of(5, 3), reloadedModel.getFilteredStudentList().get(0)
                .getMissionSubmissions().getSubmittedWeeks());

        assertEquals("Added Amy Bee’s mission submission for tutorial week 4.",
                reloadedLogic.execute("addmission 1 w/4").getFeedbackToUser());
        savedJson = Files.readString(filePath);
        assertThrows(CommandException.class, MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX, () ->
                reloadedLogic.execute("addmission 2 w/5"));
        assertEquals(savedJson, Files.readString(filePath));
        assertEquals(Set.of(5, 3, 4), storage.readAddressBook().orElseThrow()
                .getStudentList().get(0).getMissionSubmissions().getSubmittedWeeks());
    }

    @Test
    public void execute_deleteMissionAndReload_preservesDeletionAndRejectsInvalidInput() throws Exception {
        model.addStudent(new StudentBuilder(AMY).withMissionSubmissions(3, 13).build());
        assertEquals("Removed Amy Bee’s mission submission for tutorial week 3.",
                logic.execute("DELMISSION 1 W/3").getFeedbackToUser());
        Path filePath = temporaryFolder.resolve("addressBook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);
        Model reloadedModel = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs());
        Logic reloadedLogic = new LogicManager(reloadedModel, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("reloadedPrefs.json"))));
        assertEquals(Set.of(13), reloadedModel.getFilteredStudentList().get(0)
                .getMissionSubmissions().getSubmittedWeeks());

        String savedJson = Files.readString(filePath);
        assertThrows(ParseException.class, MissionParser.MESSAGE_INVALID_TUTORIAL_WEEK, () ->
                reloadedLogic.execute("delmission 1 w/14"));
        assertEquals(savedJson, Files.readString(filePath));
        assertEquals(Set.of(13), reloadedModel.getFilteredStudentList().get(0)
                .getMissionSubmissions().getSubmittedWeeks());
        assertEquals("Amy Bee’s mission submission was already not recorded for tutorial week 3. No changes made.",
                reloadedLogic.execute("delmission 1 w/3").getFeedbackToUser());
        assertEquals(savedJson, Files.readString(filePath));

        assertEquals("Removed Amy Bee’s mission submission for tutorial week 13.",
                reloadedLogic.execute("delmission 1 w/13").getFeedbackToUser());
        Student expected = new StudentBuilder(AMY).withMissionSubmissions().build();
        assertEquals(expected, storage.readAddressBook().orElseThrow().getStudentList().get(0));
        savedJson = Files.readString(filePath);
        assertThrows(CommandException.class, MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX, () ->
                reloadedLogic.execute("delmission 2 w/13"));
        assertEquals(savedJson, Files.readString(filePath));
        assertEquals(expected, reloadedModel.getFilteredStudentList().get(0));
    }
}

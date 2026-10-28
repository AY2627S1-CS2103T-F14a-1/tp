package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static seedu.address.testutil.TypicalStudents.AMY;
import static seedu.address.testutil.TypicalStudents.getTypicalAddressBook;

import java.nio.file.Path;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.stage.Stage;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.Student;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.StudentBuilder;

/**
 * Tests MainWindow listeners that keep the student details panel synchronised
 * with the student list.
 */
public class MainWindowTest {

    @BeforeAll
    public static void setUpJavaFx() throws Exception {
        FutureTask<Void> startup = new FutureTask<>(() -> {
            Platform.setImplicitExit(false);
            return null;
        });

        try {
            Platform.startup(startup);
        } catch (IllegalStateException exception) {
            Platform.runLater(startup);
        }

        startup.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void filteredStudentList_updateDisplayedStudent_refreshesDetailsPanel()
            throws Exception {
        runOnJavaFxThread(() -> {
            TestContext context = createTestContext();
            Logic logic = context.logic;
            Model model = context.model;

            MainWindow mainWindow = createMainWindow(logic);
            mainWindow.fillInnerParts();

            Student original = model.getFilteredStudentList().getFirst();

            mainWindow.getStudentListPanel()
                    .getStudentListView()
                    .getSelectionModel()
                    .select(original);

            Student updated = new StudentBuilder(original)
                    .withPhone("99999999")
                    .build();

            model.setStudent(original, updated);

            try {
                assertEquals(
                        updated,
                        getStudentDetailsPanel(mainWindow).getDisplayedStudent());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void filteredStudentList_updateWithoutDisplayedStudent_doesNothing()
            throws Exception {
        runOnJavaFxThread(() -> {
            TestContext context = createTestContext();
            Logic logic = context.logic;
            Model model = context.model;

            MainWindow mainWindow = createMainWindow(logic);
            mainWindow.fillInnerParts();

            Student original = model.getFilteredStudentList().getFirst();
            Student updated = new StudentBuilder(original)
                    .withPhone("99999999")
                    .build();

            try {
                assertNull(
                        getStudentDetailsPanel(mainWindow).getDisplayedStudent());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            model.setStudent(original, updated);

            try {
                assertNull(
                        getStudentDetailsPanel(mainWindow).getDisplayedStudent());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void filteredStudentList_updateWithoutMatchingStudent_clearsDetailsPanel()
            throws Exception {
        runOnJavaFxThread(() -> {
            TestContext context = createTestContext();
            Logic logic = context.logic;
            Model model = context.model;

            MainWindow mainWindow = createMainWindow(logic);
            mainWindow.fillInnerParts();

            Student original = model.getFilteredStudentList().getFirst();

            try {
                getStudentDetailsPanel(mainWindow)
                        .showStudent(original, logic.getMissions());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            model.deleteStudent(original);

            try {
                assertNull(
                        getStudentDetailsPanel(mainWindow).getDisplayedStudent());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void studentSelection_displaysSelectedStudent()
            throws Exception {
        runOnJavaFxThread(() -> {
            Logic logic = createLogic();
            MainWindow mainWindow = createMainWindow(logic);
            mainWindow.fillInnerParts();

            Student student = logic.getFilteredStudentList().getFirst();

            mainWindow.getStudentListPanel()
                    .getStudentListView()
                    .getSelectionModel()
                    .select(student);

            try {
                assertEquals(
                        student,
                        getStudentDetailsPanel(mainWindow).getDisplayedStudent());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Creates a logic component using the same typical students used by the
     * existing command tests.
     *
     * @return logic component for the test
     */
    private static Logic createLogic() {
        Model model = new ModelManager(
                getTypicalAddressBook(),
                new UserPrefs());

        Storage storage = new StorageManager(
                new JsonAddressBookStorage(
                        Path.of("build", "test-addressbook.json")),
                new JsonUserPrefsStorage(
                        Path.of("build", "test-preferences.json")));

        return new LogicManager(model, storage);
    }

    /**
     * Creates a MainWindow using the same student fixture style as the other
     * tests.
     *
     * @param logic logic component used by the window
     * @return configured main window
     */
    private static MainWindow createMainWindow(Logic logic) {
        Student student = new StudentBuilder(AMY)
                .withPhone("91234567")
                .withEmail("amy@example.com")
                .withStudioGroup("1A")
                .build();

        // The student is created using the existing test fixture pattern.
        // The logic instance already contains the typical-address-book data.
        assertEquals(AMY.getName(), student.getName());

        return new MainWindow(
                new Stage(),
                logic,
                Path.of("build", "test-addressbook.json"));
    }

    /**
     * Retrieves the details panel from the MainWindow under test.
     *
     * @param mainWindow window under test
     * @return details panel
     * @throws Exception if the private field cannot be accessed
     */
    private static StudentDetailsPanel getStudentDetailsPanel(
            MainWindow mainWindow) throws Exception {
        java.lang.reflect.Field field =
                MainWindow.class.getDeclaredField("studentDetailsPanel");
        field.setAccessible(true);
        return (StudentDetailsPanel) field.get(mainWindow);
    }

    /**
     * Runs assertions on the JavaFX application thread.
     *
     * @param assertions assertions to execute
     * @throws Exception if the JavaFX task fails
     */
    private static void runOnJavaFxThread(Runnable assertions)
            throws Exception {
        FutureTask<Void> task = new FutureTask<>(assertions, null);
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }

    private static TestContext createTestContext() {
        Model model = new ModelManager(
                getTypicalAddressBook(),
                new UserPrefs());

        Storage storage = new StorageManager(
                new JsonAddressBookStorage(
                        Path.of("build", "test-addressbook.json")),
                new JsonUserPrefsStorage(
                        Path.of("build", "test-preferences.json")));

        Logic logic = new LogicManager(model, storage);

        return new TestContext(model, logic);
    }

    private static class TestContext {
        private final Model model;
        private final Logic logic;

        TestContext(Model model, Logic logic) {
            this.model = model;
            this.logic = logic;
        }
    }
}
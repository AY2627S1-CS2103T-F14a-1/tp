package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static seedu.address.testutil.TypicalStudents.AMY;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddMissionCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteMissionCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.student.MissionSubmissions;
import seedu.address.model.student.Student;
import seedu.address.testutil.StudentBuilder;

/**
 * Tests the result display's FXML and mission table on the JavaFX thread.
 */
public class ResultDisplayTest {

    @BeforeAll
    public static void setUpJavaFx() throws Exception {
        FutureTask<Void> startup = new FutureTask<>(() -> {
            Platform.setImplicitExit(false);
            return null;
        });
        try {
            Platform.startup(startup);
        } catch (IllegalStateException e) {
            Platform.runLater(startup);
        }
        startup.get(10, TimeUnit.SECONDS);
    }

    @Test
    public void setCommandResult_studentView_displaysEveryWeekAndStatus() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay display = new ResultDisplay();
            Student student = new StudentBuilder(AMY).withMissionSubmissions(
                    MissionSubmissions.MIN_WEEK, MissionSubmissions.MAX_WEEK).build();
            display.setCommandResult(new CommandResult(Messages.formatStudentDetails(student), student));
            GridPane table = getTable(display);
            int weekCount = MissionSubmissions.MAX_WEEK - MissionSubmissions.MIN_WEEK + 1;
            assertEquals((weekCount + 1) * 2, table.getChildren().size());
            assertEquals("Week", ((Label) table.getChildren().get(0)).getText());
            assertEquals("Status", ((Label) table.getChildren().get(1)).getText());
            for (int row = 1; row <= weekCount; row++) {
                int week = MissionSubmissions.MIN_WEEK + row - 1;
                assertEquals(String.valueOf(week), ((Label) table.getChildren().get(row * 2)).getText());
                String expected = student.getMissionSubmissions().hasSubmission(week)
                        ? "Submitted" : "Not submitted";
                assertEquals(expected, ((Label) table.getChildren().get(row * 2 + 1)).getText());
            }
            assertTrue(display.getRoot().lookup("#studentDetails").isVisible());
            assertFalse(display.getRoot().lookup("#resultDisplay").isManaged());
        });
    }

    @Test
    public void setCommandResult_switchStudentAndFeedback_clearsPreviousView() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay display = new ResultDisplay();
            Student submitted = new StudentBuilder(AMY).withMissionSubmissions(MissionSubmissions.MIN_WEEK).build();
            display.setCommandResult(new CommandResult("First student", submitted));
            Student empty = new StudentBuilder(AMY).withMissionSubmissions().build();
            display.setCommandResult(new CommandResult("Second student", empty));
            GridPane table = getTable(display);
            for (int index = 3; index < table.getChildren().size(); index += 2) {
                assertEquals("Not submitted", ((Label) table.getChildren().get(index)).getText());
            }
            assertEquals("Second student", ((Label) display.getRoot().lookup("#studentSummary")).getText());
            assertEquals(0, ((ScrollPane) display.getRoot().lookup("#studentDetails")).getVvalue());
            display.setFeedbackToUser("Invalid index");
            assertFalse(display.getRoot().lookup("#studentDetails").isVisible());
            assertTrue(table.getChildren().isEmpty());
            TextArea feedback = (TextArea) display.getRoot().lookup("#resultDisplay");
            assertTrue(feedback.isVisible());
            assertEquals("Invalid index", feedback.getText());
            display.setCommandResult(new CommandResult("Listed students"));
            assertEquals("Listed students", feedback.getText());
            assertFalse(display.getRoot().lookup("#studentDetails").isManaged());
        });
    }

    @Test
    public void setCommandResult_viewAfterAddAndDelete_reflectsUpdatedSubmissions() throws Exception {
        Model model = new ModelManager();
        model.addStudent(new StudentBuilder(AMY).withMissionSubmissions().build());
        new AddMissionCommand(INDEX_FIRST_STUDENT, MissionSubmissions.MAX_WEEK).execute(model);
        CommandResult added = new ViewCommand(INDEX_FIRST_STUDENT).execute(model);
        new DeleteMissionCommand(INDEX_FIRST_STUDENT, MissionSubmissions.MAX_WEEK).execute(model);
        CommandResult deleted = new ViewCommand(INDEX_FIRST_STUDENT).execute(model);
        runOnJavaFxThread(() -> {
            ResultDisplay display = new ResultDisplay();
            display.setCommandResult(added);
            GridPane table = getTable(display);
            assertEquals("Submitted", ((Label) table.getChildren().getLast()).getText());
            display.setCommandResult(deleted);
            assertEquals("Not submitted", ((Label) table.getChildren().getLast()).getText());
        });
    }

    /**
     * Creates the scroll pane's skin before locating its table.
     *
     * @param display Display to attach to a scene.
     * @return The mission table loaded from FXML.
     */
    private static GridPane getTable(ResultDisplay display) {
        new Scene(display.getRoot());
        display.getRoot().applyCss();
        display.getRoot().layout();
        return (GridPane) display.getRoot().lookup("#missionSubmissions");
    }

    /**
     * Runs UI assertions on the JavaFX thread and propagates failures to JUnit.
     *
     * @param assertions UI operations and assertions to run.
     * @throws Exception If execution fails or times out.
     */
    private static void runOnJavaFxThread(Runnable assertions) throws Exception {
        FutureTask<Void> task = new FutureTask<>(assertions, null);
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }
}

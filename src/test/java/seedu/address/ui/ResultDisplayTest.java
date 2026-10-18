package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_STUDENT;
import static seedu.address.testutil.TypicalStudents.AMY;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import guitests.guihandles.ResultDisplayHandle;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteMissionCommand;
import seedu.address.logic.commands.MarkMissionCommand;
import seedu.address.logic.commands.UnmarkMissionCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.mission.Mission;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.Student;
import seedu.address.testutil.StudentBuilder;

/**
 * Tests the result display's FXML and mission table on the JavaFX thread.
 */
public class ResultDisplayTest extends GuiUnitTest {

    private static final List<Mission> MISSIONS = List.of(
            new Mission(new MissionId("M01"), "Runes", LocalDate.of(2026, 10, 15)),
            new Mission(new MissionId("M02"), "Curves", LocalDate.of(2026, 10, 16)));

    private ResultDisplay resultDisplay;
    private ResultDisplayHandle resultDisplayHandle;

    @BeforeEach
    public void setUp() {
        resultDisplay = new ResultDisplay();
        uiPartExtension.setUiPart(resultDisplay);

        resultDisplayHandle = new ResultDisplayHandle(getChildNode(resultDisplay.getRoot(),
                ResultDisplayHandle.RESULT_DISPLAY_ID));
    }

    @Test
    public void display() {
        // default result text
        guiRobot.pauseForHuman();
        assertEquals("", resultDisplayHandle.getText());

        // new result received
        guiRobot.interact(() -> resultDisplay.setFeedbackToUser("Dummy feedback to user"));
        guiRobot.pauseForHuman();
        assertEquals("Dummy feedback to user", resultDisplayHandle.getText());
    }

    @Test
    public void setCommandResult_studentView_displaysConfiguredMissionsAndStatus() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay display = new ResultDisplay();
            Student student = new StudentBuilder(AMY).withMissionSubmissions("M01").build();
            display.setCommandResult(new CommandResult(Messages.formatStudentDetails(student), student, MISSIONS));
            GridPane table = getTable(display);
            assertEquals(6, table.getChildren().size());
            assertEquals("Mission", ((Label) table.getChildren().get(0)).getText());
            assertEquals("M01 · Runes\nDue: 2026-10-15", ((Label) table.getChildren().get(2)).getText());
            assertEquals("Submitted", ((Label) table.getChildren().get(3)).getText());
            assertEquals("Not submitted", ((Label) table.getChildren().get(5)).getText());
            assertTrue(display.getRoot().lookup("#studentDetails").isVisible());
            assertFalse(display.getRoot().lookup("#resultDisplay").isManaged());
        });
    }

    @Test
    public void setCommandResult_wrappedMissionRows_alignsCellBorders() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay display = new ResultDisplay();
            List<Mission> missions = List.of(
                    new Mission(new MissionId("M01"), "Understanding the Force", LocalDate.of(2026, 10, 15)),
                    new Mission(new MissionId("M02"), "Beyond the Second Dimension", LocalDate.of(2026, 10, 29)));
            Student student = new StudentBuilder(AMY).withMissionSubmissions("M01").build();
            display.setCommandResult(new CommandResult("Student", student, missions));
            GridPane table = getTable(display);
            table.getStylesheets().add(ResultDisplay.class.getResource("/view/DarkTheme.css").toExternalForm());
            table.applyCss();
            for (double width : new double[] {240, 900}) {
                table.resize(width, table.prefHeight(width));
                table.layout();
                for (int index = 0; index < table.getChildren().size(); index += 2) {
                    Label description = (Label) table.getChildren().get(index);
                    Label status = (Label) table.getChildren().get(index + 1);
                    assertTrue(description.getHeight() > 0);
                    assertEquals(description.getBoundsInParent().getMinY(),
                            status.getBoundsInParent().getMinY(), 0.01);
                    assertEquals(description.getBoundsInParent().getMaxY(),
                            status.getBoundsInParent().getMaxY(), 0.01);
                }
            }
        });
    }

    @Test
    public void setCommandResult_emptyCatalogue_displaysEmptyMessage() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay display = new ResultDisplay();
            display.setCommandResult(new CommandResult("Student", AMY));
            assertEquals("No missions configured.", ((Label) getTable(display).getChildren().getFirst()).getText());
        });
    }

    @Test
    public void setCommandResult_switchStudentAndFeedback_clearsPreviousView() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay display = new ResultDisplay();
            Student submitted = new StudentBuilder(AMY).withMissionSubmissions("M01").build();
            display.setCommandResult(new CommandResult("First student", submitted, MISSIONS));
            Student empty = new StudentBuilder(AMY).withMissionSubmissions().build();
            display.setCommandResult(new CommandResult("Second student", empty, MISSIONS));
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
    public void setCommandResult_viewAfterMarkAndUnmark_reflectsUpdatedSubmissions() throws Exception {
        Model model = new ModelManager();
        model.addStudent(new StudentBuilder(AMY).withMissionSubmissions().build());
        model.addMission("Runes", LocalDate.of(2026, 10, 15));
        new MarkMissionCommand(INDEX_FIRST_STUDENT, new MissionId("M01")).execute(model);
        CommandResult added = new ViewCommand(INDEX_FIRST_STUDENT).execute(model);
        new UnmarkMissionCommand(INDEX_FIRST_STUDENT, new MissionId("M01")).execute(model);
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

    @Test
    public void setCommandResult_missionDeleted_clearsStaleProfileAndShowsUpdatedCatalogue() throws Exception {
        Model model = new ModelManager();
        model.addStudent(new StudentBuilder(AMY).withMissionSubmissions("M01").build());
        model.addMission("Runes", LocalDate.of(2026, 10, 20));
        CommandResult before = new ViewCommand(INDEX_FIRST_STUDENT).execute(model);
        CommandResult deletion = new DeleteMissionCommand(new MissionId("M01")).execute(model);
        CommandResult after = new ViewCommand(INDEX_FIRST_STUDENT).execute(model);
        runOnJavaFxThread(() -> {
            ResultDisplay display = new ResultDisplay();
            display.setCommandResult(before);
            GridPane table = getTable(display);
            assertEquals(4, table.getChildren().size());
            display.setCommandResult(deletion);
            assertFalse(display.getRoot().lookup("#studentDetails").isVisible());
            assertTrue(table.getChildren().isEmpty());
            TextArea feedback = (TextArea) display.getRoot().lookup("#resultDisplay");
            assertEquals(deletion.getFeedbackToUser(), feedback.getText());
            display.setCommandResult(after);
            assertEquals("No missions configured.", ((Label) table.getChildren().getFirst()).getText());
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

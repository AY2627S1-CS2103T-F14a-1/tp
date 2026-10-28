package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import guitests.guihandles.ResultDisplayHandle;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import seedu.address.model.mission.Mission;
import seedu.address.model.mission.MissionId;

/**
 * Tests the result display's FXML, mission table, and studio table on the JavaFX thread.
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
    public void setFeedbackToUser_withWarnings_displaysYellowWarningSeparately() {
        guiRobot.interact(() -> resultDisplay.setFeedbackToUser(
                "Note added successfully.", List.of("A matching note text exists.")));
        guiRobot.pauseForHuman();

        Label resultLabel = getChildNode(resultDisplay.getRoot(), "#resultDisplay");
        Label warningLabel = getChildNode(resultDisplay.getRoot(), "#warningDisplay");
        assertEquals("Note added successfully.", resultLabel.getText());
        assertEquals("WARNING:\nA matching note text exists.", warningLabel.getText());
        assertTrue(warningLabel.isVisible());
        assertTrue(warningLabel.isManaged());
        assertTrue(warningLabel.getStyleClass().contains("command-warning"));
        assertEquals(Color.web("#ffd54f"), warningLabel.getTextFill());

        guiRobot.interact(() -> resultDisplay.setFeedbackToUser("Next result."));
        assertFalse(warningLabel.isVisible());
        assertFalse(warningLabel.isManaged());
        assertEquals("", warningLabel.getText());
    }

    private static GridPane getMissionTable(StudentDetailsPanel panel) {
        new Scene(panel.getRoot());
        panel.getRoot().applyCss();
        panel.getRoot().layout();

        return (GridPane) panel.getRoot().lookup("#missionSubmissionsGrid");
    }

    private static String getMissionStatus(GridPane table, MissionId missionId) {
        for (Node node : table.getChildren()) {
            Integer row = GridPane.getRowIndex(node);
            Integer column = GridPane.getColumnIndex(node);

            if (row == null || column == null || column != 0) {
                continue;
            }

            Label missionLabel = (Label) node;

            if (missionLabel.getText().startsWith(missionId + " · ")) {
                int missionRow = row;

                return table.getChildren().stream()
                        .filter(child -> GridPane.getRowIndex(child) != null
                                && GridPane.getRowIndex(child) == missionRow
                                && GridPane.getColumnIndex(child) != null
                                && GridPane.getColumnIndex(child) == 1)
                        .map(Label.class::cast)
                        .findFirst()
                        .orElseThrow()
                        .getText();
            }
        }

        throw new AssertionError("Mission not found: " + missionId);
    }

    private static GridPane getAttendanceTable(ResultDisplay display) {
        new Scene(display.getRoot());
        display.getRoot().applyCss();
        display.getRoot().layout();

        return (GridPane) display.getRoot().lookup("#attendance");
    }

    private static String getStatus(GridPane table, int week) {
        for (int row = 1; row <= table.getRowCount(); row++) {
            int currentRow = row;

            Label weekLabel = (Label) table.getChildren().stream()
                    .filter(node -> GridPane.getRowIndex(node) != null
                            && GridPane.getRowIndex(node) == currentRow
                            && GridPane.getColumnIndex(node) != null
                            && GridPane.getColumnIndex(node) == 0)
                    .findFirst()
                    .orElseThrow();

            if (weekLabel.getText().equals(String.valueOf(week))) {
                return table.getChildren().stream()
                        .filter(node -> GridPane.getRowIndex(node) != null
                                && GridPane.getRowIndex(node) == currentRow
                                && GridPane.getColumnIndex(node) != null
                                && GridPane.getColumnIndex(node) == 1)
                        .map(Label.class::cast)
                        .findFirst()
                        .orElseThrow()
                        .getText();
            }
        }

        throw new AssertionError("No status found for week " + week);
    }

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
    public void setFeedbackToUser_displaysFeedback() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay resultDisplay = new ResultDisplay();

            resultDisplay.setFeedbackToUser(
                    "Invalid command format.");

            Scene scene = new Scene(resultDisplay.getRoot());
            scene.getRoot().applyCss();
            scene.getRoot().layout();

            Label resultLabel =
                    (Label) resultDisplay.getRoot()
                            .lookup("#resultDisplay");

            assertNotNull(resultLabel);
            assertEquals(
                    "Invalid command format.",
                    resultLabel.getText());
        });
    }

    @Test
    public void setFeedbackToUser_displaysLongFeedback()
            throws Exception {

        runOnJavaFxThread(() -> {
            ResultDisplay resultDisplay =
                    new ResultDisplay();

            String feedback =
                    "Invalid command format! "
                            + "Parameters: INDEX "
                            + "Example: view 1";

            resultDisplay.setFeedbackToUser(feedback);

            Scene scene = new Scene(resultDisplay.getRoot());
            scene.getRoot().applyCss();
            scene.getRoot().layout();

            Label resultLabel =
                    (Label) resultDisplay.getRoot()
                            .lookup("#resultDisplay");

            assertNotNull(resultLabel);
            assertEquals(feedback, resultLabel.getText());
        });
    }

    private static void runOnJavaFxThread(
            Runnable assertions) throws Exception {

        FutureTask<Void> task =
                new FutureTask<>(assertions, null);

        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }
}

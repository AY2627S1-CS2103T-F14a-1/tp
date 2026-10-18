package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalStudents.AMY;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import seedu.address.model.mission.Mission;
import seedu.address.model.mission.MissionId;
import seedu.address.model.student.Student;
import seedu.address.testutil.StudentBuilder;

public class StudentDetailsPanelTest {

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
    public void showStudent_displaysMissionStatus() throws Exception {
        Mission mission = new Mission(
                new MissionId("M01"),
                "Runes",
                LocalDate.of(2026, 10, 20));

        runOnJavaFxThread(() -> {
            StudentDetailsPanel panel = new StudentDetailsPanel();
            Student student = new StudentBuilder(AMY)
                    .withMissionSubmissions("M01")
                    .build();

            panel.showStudent(student, List.of(mission));

            GridPane missionGrid = getMissionGrid(panel);

            assertEquals(4, missionGrid.getChildren().size());
            assertEquals("Submitted", (
                    (Label) missionGrid.getChildren().get(3)).getText());
            assertTrue(panel.getRoot().lookup("#studentDetailsScrollPane").isVisible());
        });
    }

    @Test
    public void showStudent_nullStudent_clearsPanel() throws Exception {
        runOnJavaFxThread(() -> {
            StudentDetailsPanel panel = new StudentDetailsPanel();

            panel.showStudent(null, List.of());

            assertTrue(panel.getRoot().lookup("#emptyMessage").isVisible());
            assertFalse(panel.getRoot().lookup("#studentDetailsScrollPane").isVisible());
        });
    }

    private static GridPane getMissionGrid(StudentDetailsPanel panel) {
        new Scene(panel.getRoot());
        panel.getRoot().applyCss();
        panel.getRoot().layout();

        return (GridPane) panel.getRoot().lookup("#missionSubmissionsGrid");
    }

    private static void runOnJavaFxThread(Runnable assertions) throws Exception {
        FutureTask<Void> task = new FutureTask<>(assertions, null);
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }
}

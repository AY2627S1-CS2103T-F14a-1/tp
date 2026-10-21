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
import javafx.scene.layout.VBox;
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
    public void showStudent_displaysStudentDetails() throws Exception {
        runOnJavaFxThread(() -> {
            StudentDetailsPanel panel =
                    new StudentDetailsPanel();

            Student student = new StudentBuilder()
                    .withName("Amy")
                    .withPhone("91234567")
                    .withEmail("amy@example.com")
                    .withStudioGroup("1A")
                    .build();

            panel.showStudent(student, List.of());

            assertEquals(
                    student.getName().fullName,
                    getLabel(panel, "#studentName").getText());

            assertEquals(
                    "Studio group: "
                            + student.getStudioGroup().studioGroup,
                    getLabel(panel, "#studioGroup").getText());

            assertEquals(
                    "Phone: " + student.getPhone().value,
                    getLabel(panel, "#phone").getText());

            assertEquals(
                    "Email: " + student.getEmail().value,
                    getLabel(panel, "#email").getText());

            assertEquals(
                    "Tags: " + student.getTags(),
                    getLabel(panel, "#tags").getText());

            assertTrue(
                    panel.getRoot()
                            .lookup("#studentDetailsScrollPane")
                            .isVisible());
        });
    }

    @Test
    public void showStudent_displaysMissionStatusAndDeadline()
            throws Exception {

        runOnJavaFxThread(() -> {
            Mission mission = new Mission(
                    new MissionId("M01"),
                    "Runes",
                    LocalDate.of(2026, 10, 20));

            Student student = new StudentBuilder(AMY)
                    .withMissionSubmissions("M01")
                    .build();

            StudentDetailsPanel panel =
                    new StudentDetailsPanel();

            panel.showStudent(student, List.of(mission));

            GridPane missionGrid =
                    getMissionGrid(panel);

            assertEquals(4, missionGrid.getChildren().size());

            Label status =
                    (Label) missionGrid.getChildren().get(3);

            assertEquals("Submitted", status.getText());

            VBox missionDetails =
                    (VBox) missionGrid.getChildren().get(2);

            assertEquals(2, missionDetails.getChildren().size());

            Label missionName =
                    (Label) missionDetails.getChildren().get(0);

            Label deadline =
                    (Label) missionDetails.getChildren().get(1);

            assertEquals(
                    "M01 · Runes",
                    missionName.getText());

            assertEquals(
                    "Due: 2026-10-20",
                    deadline.getText());
        });
    }

    @Test
    public void showStudent_withNoMissions_displaysMessage()
            throws Exception {

        runOnJavaFxThread(() -> {
            StudentDetailsPanel panel =
                    new StudentDetailsPanel();

            panel.showStudent(
                    new StudentBuilder(AMY).build(),
                    List.of());

            GridPane missionGrid =
                    getMissionGrid(panel);

            assertEquals(1, missionGrid.getChildren().size());

            Label message =
                    (Label) missionGrid.getChildren().get(0);

            assertEquals(
                    "No missions configured.",
                    message.getText());
        });
    }

    @Test
    public void showStudent_nullStudent_clearsPanel()
            throws Exception {

        runOnJavaFxThread(() -> {
            StudentDetailsPanel panel =
                    new StudentDetailsPanel();

            panel.showStudent(null, List.of());

            assertTrue(
                    panel.getRoot()
                            .lookup("#emptyMessage")
                            .isVisible());

            assertFalse(
                    panel.getRoot()
                            .lookup("#studentDetailsScrollPane")
                            .isVisible());
        });
    }

    private static GridPane getMissionGrid(
            StudentDetailsPanel panel) {

        prepareRoot(panel);

        return (GridPane) panel.getRoot()
                .lookup("#missionSubmissionsGrid");
    }

    private static Label getLabel(
            StudentDetailsPanel panel,
            String selector) {

        prepareRoot(panel);

        return (Label) panel.getRoot().lookup(selector);
    }

    private static void prepareRoot(
            StudentDetailsPanel panel) {

        if (panel.getRoot().getScene() == null) {
            new Scene(panel.getRoot());
        }

        panel.getRoot().applyCss();
        panel.getRoot().layout();
    }

    private static void runOnJavaFxThread(
            Runnable assertions) throws Exception {

        FutureTask<Void> task =
                new FutureTask<>(assertions, null);

        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }
}
